package com.fasterxml.jackson.jaxrs.xml;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.util.LRUMap;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.jaxrs.xml.annotation.EndpointConfig;
import com.fasterxml.jackson.jaxrs.xml.cfg.MapperConfigurator;
import com.fasterxml.jackson.jaxrs.xml.util.AnnotationBundleKey;
import com.fasterxml.jackson.jaxrs.xml.util.ClassKey;

/**
 * Basic implementation of JAX-RS abstractions ({@link MessageBodyReader},
 * {@link MessageBodyWriter}) needed for binding
 * JSON ("application/json") content to and from Java Objects ("POJO"s).
 *<p>
 * Actual data binding functionality is implemented by {@link ObjectMapper}:
 * mapper to use can be configured in multiple ways:
 * <ul>
 *  <li>By explicitly passing mapper to use in constructor
 *  <li>By explictly setting mapper to use by {@link #setMapper}
 *  <li>By defining JAX-RS <code>Provider</code> that returns {@link XmlMapper}s.
 *  <li>By doing none of above, in which case a default mapper instance is
 *     constructed (and configured if configuration methods are called)
 * </ul>
 * The last method ("do nothing specific") is often good enough; explicit passing
 * of Mapper is simple and explicit; and Provider-based method may make sense
 * with Depedency Injection frameworks, or if Mapper has to be configured differently
 * for different media types.
 *<p>
 * Note that the default mapper instance will be automatically created if
 * one of explicit configuration methods (like {@link #configure})
 * is called: if so, Provider-based introspection is <b>NOT</b> used, but the
 * resulting Mapper is used as configured.
 *<p>
 * Note that there is also a sub-class -- ({@link JacksonJaxbXMLProvider}) -- that
 * is configured by default to use both Jackson and JAXB annotations for configuration
 * (base class when used as-is defaults to using just Jackson annotations)
 *
 * @author Tatu Saloranta
 */
@Provider
@Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
public class JacksonXMLProvider
    implements
        MessageBodyReader<Object>,
        MessageBodyWriter<Object>,
        Versioned
{
    /**
     * Default annotation sets to use, if not explicitly defined during
     * construction: only Jackson annotations are used for the base
     * class. Sub-classes can use other settings.
     */
    public final static Annotations[] BASIC_ANNOTATIONS = {
        Annotations.JACKSON
    };

    /**
     * Looks like we need to worry about accidental
     *   data binding for types we shouldn't be handling. This is
     *   probably not a very good way to do it, but let's start by
     *   blacklisting things we are not to handle.
     *<p>
     *  (why ClassKey? since plain old Class has no hashCode() defined,
     *  lookups are painfully slow)
     */
    public final static HashSet<ClassKey> _untouchables = new HashSet<ClassKey>();
    static {
        // First, I/O things (direct matches)
        _untouchables.add(new ClassKey(java.io.InputStream.class));
        _untouchables.add(new ClassKey(java.io.Reader.class));
        _untouchables.add(new ClassKey(java.io.OutputStream.class));
        _untouchables.add(new ClassKey(java.io.Writer.class));

        // then some primitive types
        _untouchables.add(new ClassKey(char[].class));

        /* 27-Apr-2012, tatu: Grrrh. Looks like we need to consider these
         *   untouchables, still, to allow users to access "raw" responses
         *   if and as necessary. Latter is unfortunate in a way as it
         *   prevents use of automatic base64 decoding but...
         */
        _untouchables.add(new ClassKey(String.class));
        _untouchables.add(new ClassKey(byte[].class));

        // Then core JAX-RS things
        _untouchables.add(new ClassKey(StreamingOutput.class));
        _untouchables.add(new ClassKey(Response.class));
    }

    /**
     * These are classes that we never use for reading
     * (never try to deserialize instances of these types).
     */
    public final static Class<?>[] _unreadableClasses = new Class<?>[] {
        InputStream.class, Reader.class
    };

    /**
     * These are classes that we never use for writing
     * (never try to serialize instances of these types).
     */
    public final static Class<?>[] _unwritableClasses = new Class<?>[] {
        OutputStream.class, Writer.class,
        StreamingOutput.class, Response.class
    };

    /*
    /**********************************************************
    /* Bit of caching
    /**********************************************************
     */

    /**
     * Cache for resolved endpoint configurations when reading XML
     */
    protected final LRUMap<AnnotationBundleKey, EndpointConfig> _readers
        = new LRUMap<AnnotationBundleKey, EndpointConfig>(16, 120);

    /**
     * Cache for resolved endpoint configurations when writing XML
     */
    protected final LRUMap<AnnotationBundleKey, EndpointConfig> _writers
        = new LRUMap<AnnotationBundleKey, EndpointConfig>(16, 120);
    
    /*
    /**********************************************************
    /* General configuration
    /**********************************************************
     */
    
    /**
     * Helper object used for encapsulating configuration aspects
     * of {@link XmlMapper}
     */
    protected final MapperConfigurator _mapperConfig;

    /**
     * Set of types (classes) that provider should ignore for data binding
     */
    protected HashSet<ClassKey> _cfgCustomUntouchables;
    
    /*
    /**********************************************************
    /* Context configuration
    /**********************************************************
     */

    /**
     * Injectable context object used to locate configured
     * instance of {@link XmlMapper} to use for actual
     * serialization.
     */
    @Context
    protected Providers _providers;

    /*
    /**********************************************************
    /* Configuration
    /**********************************************************
     */

    /**
     * Whether we want to actually check that Jackson has
     * a serializer for given type. Since this should generally
     * be the case (due to auto-discovery) and since the call
     * to check availability can be bit expensive, defaults to false.
     */
    protected boolean _cfgCheckCanSerialize = false;

    /**
     * Whether we want to actually check that Jackson has
     * a deserializer for given type. Since this should generally
     * be the case (due to auto-discovery) and since the call
     * to check availability can be bit expensive, defaults to false.
     */
    protected boolean _cfgCheckCanDeserialize = false;

    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */

    /**
     * Default constructor, usually used when provider is automatically
     * configured to be used with JAX-RS implementation.
     */
    public JacksonXMLProvider()
    {
        this(null, BASIC_ANNOTATIONS);
    }

    /**
     * @param annotationsToUse Annotation set(s) to use for configuring
     *    data binding
     */
    public JacksonXMLProvider(Annotations... annotationsToUse)
    {
        this(null, annotationsToUse);
    }

    public JacksonXMLProvider(XmlMapper mapper)
    {
        this(mapper, BASIC_ANNOTATIONS);
    }
    
    /**
     * Constructor to use when a custom mapper (usually components
     * like serializer/deserializer factories that have been configured)
     * is to be used.
     * 
     * @param annotationsToUse Sets of annotations (Jackson, JAXB) that provider should
     *   support
     */
    public JacksonXMLProvider(XmlMapper mapper, Annotations[] annotationsToUse)
    {
        _mapperConfig = new MapperConfigurator(mapper, annotationsToUse);
    }

    /**
     * Method that will return version information stored in and read from jar
     * that contains this class.
     */
    public Version version() {
        return ModuleVersion.instance.version();
    }
    
    /*
    /**********************************************************
    /* Configuring
    /**********************************************************
     */

    /**
     * Method for defining whether actual detection for existence of
     * a deserializer for type should be done when {@link #isReadable}
     * is called.
     */
    public void checkCanDeserialize(boolean state) { _cfgCheckCanDeserialize = state; }

    /**
     * Method for defining whether actual detection for existence of
     * a serializer for type should be done when {@link #isWriteable}
     * is called.
     */
    public void checkCanSerialize(boolean state) { _cfgCheckCanSerialize = state; }

    /**
     * Method for configuring which annotation sets to use (including none).
     * Annotation sets are defined in order decreasing precedence; that is,
     * first one has the priority over following ones.
     * 
     * @param annotationsToUse Ordered list of annotation sets to use; if null,
     *    default
     */
    public void setAnnotationsToUse(Annotations[] annotationsToUse) {
        _mapperConfig.setAnnotationsToUse(annotationsToUse);
    }
    
    /**
     * Method that can be used to directly define {@link XmlMapper} to use
     * for serialization and deserialization; if null, will use the standard
     * provider discovery from context instead. Default setting is null.
     */
    public void setMapper(XmlMapper m) {
        _mapperConfig.setMapper(m);
    }

    public JacksonXMLProvider configure(DeserializationFeature f, boolean state) {
        _mapperConfig.configure(f, state);
        return this;
    }

    public JacksonXMLProvider configure(SerializationFeature f, boolean state) {
        _mapperConfig.configure(f, state);
        return this;
    }

    public JacksonXMLProvider configure(JsonParser.Feature f, boolean state) {
        _mapperConfig.configure(f, state);
        return this;
    }

    public JacksonXMLProvider configure(JsonGenerator.Feature f, boolean state) {
        _mapperConfig.configure(f, state);
        return this;
    }

    public JacksonXMLProvider enable(DeserializationFeature f, boolean state) {
        _mapperConfig.configure(f, true);
        return this;
    }

    public JacksonXMLProvider enable(SerializationFeature f, boolean state) {
        _mapperConfig.configure(f, true);
        return this;
    }

    public JacksonXMLProvider enable(JsonParser.Feature f, boolean state) {
        _mapperConfig.configure(f, true);
        return this;
    }

    public JacksonXMLProvider enable(JsonGenerator.Feature f, boolean state) {
        _mapperConfig.configure(f, true);
        return this;
    }

    public JacksonXMLProvider disable(DeserializationFeature f, boolean state) {
        _mapperConfig.configure(f, false);
        return this;
    }

    public JacksonXMLProvider disable(SerializationFeature f, boolean state) {
        _mapperConfig.configure(f, false);
        return this;
    }

    public JacksonXMLProvider disable(JsonParser.Feature f, boolean state) {
        _mapperConfig.configure(f, false);
        return this;
    }

    public JacksonXMLProvider disable(JsonGenerator.Feature f, boolean state) {
        _mapperConfig.configure(f, false);
        return this;
    }

    /**
     * Method for marking specified type as "untouchable", meaning that provider
     * will not try to read or write values of this type (or its subtypes).
     * 
     * @param type Type to consider untouchable; can be any kind of class,
     *   including abstract class or interface. No instance of this type
     *   (including subtypes, i.e. types assignable to this type) will
     *   be read or written by provider
     */
    public void addUntouchable(Class<?> type)
    {
        if (_cfgCustomUntouchables == null) {
            _cfgCustomUntouchables = new HashSet<ClassKey>();
        }
        _cfgCustomUntouchables.add(new ClassKey(type));
    }
    
    /*
    /**********************************************************
    /* MessageBodyReader impl
    /**********************************************************
     */

    /**
     * Method that JAX-RS container calls to try to check whether
     * values of given type (and media type) can be deserialized by
     * this provider.
     * Implementation will first check that expected media type is
     * an XML type (via call to {@link #isXMLType}; then verify
     * that type is not one of "untouchable" types (types we will never
     * automatically handle), and finally that there is a deserializer
     * for type (iff {@link #checkCanDeserialize} has been called with
     * true argument -- otherwise assumption is there will be a handler)
     */
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        if (!isXMLType(mediaType) || !isReadableType(type)) {
            return false;
        }
        // if we really want to verify that we can serialize, we'll check:
        if (_cfgCheckCanSerialize) {
            XmlMapper mapper = locateMapper(type, mediaType);
            if (!mapper.canDeserialize(mapper.constructType(type))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method that JAX-RS container calls to deserialize given value.
     */
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String,String> httpHeaders, InputStream entityStream) 
        throws IOException
    {
        AnnotationBundleKey key = new AnnotationBundleKey(annotations);
        EndpointConfig endpoint;
        synchronized (_readers) {
            endpoint = _readers.get(key);
        }
        // not yet resolved (or not cached any more)? Resolve!
        if (endpoint == null) {
            XmlMapper mapper = locateMapper(type, mediaType);
            endpoint = EndpointConfig.forReading(mapper, annotations);
            // and cache for future reuse
            synchronized (_readers) {
                _readers.put(key.immutableKey(), endpoint);
            }
        }
        ObjectReader reader = endpoint.getReader();
        // Fix for [Issue#4]: note, can not try to advance parser, XML parser complains
        PushbackInputStream wrappedStream = new PushbackInputStream(entityStream);
        int firstByte = wrappedStream.read(); 
        if (firstByte == -1) {
            return null;
        }
        wrappedStream.unread(firstByte);
        JsonParser jp = reader.getJsonFactory().createJsonParser(wrappedStream);
        return reader.withType(genericType).readValue(jp);
    }

    /*
    /**********************************************************
    /* MessageBodyWriter impl
    /**********************************************************
     */

    /**
     * Method that JAX-RS container calls to try to figure out
     * serialized length of given value. Since computation of
     * this length is about as expensive as serialization itself,
     * implementation will return -1 to denote "not known", so
     * that container will determine length from actual serialized
     * output (if needed).
     */
    public long getSize(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        /* In general figuring output size requires actual writing; usually not
         * worth it to write everything twice.
         */
        return -1;
    }

    /**
     * Method that JAX-RS container calls to try to check whether
     * given value (of specified type) can be serialized by
     * this provider.
     * Implementation will first check that expected media type is
     * an XML type (via call to {@link #isXMLType}; then verify
     * that type is not one of "untouchable" types (types we will never
     * automatically handle), and finally that there is a serializer
     * for type (iff {@link #checkCanSerialize} has been called with
     * true argument -- otherwise assumption is there will be a handler)
     */
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        if (!isXMLType(mediaType) || !isWritableType(type)) {
            return false;
        }
        // Also: if we really want to verify that we can deserialize, we'll check:
        if (_cfgCheckCanSerialize) {
            if (!locateMapper(type, mediaType).canSerialize(type)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method that JAX-RS container calls to serialize given value.
     */
    public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String,Object> httpHeaders, OutputStream entityStream) 
        throws IOException
    {
        AnnotationBundleKey key = new AnnotationBundleKey(annotations);
        EndpointConfig endpoint;
        synchronized (_writers) {
            endpoint = _writers.get(key);
        }
        // not yet resolved (or not cached any more)? Resolve!
        if (endpoint == null) {
            XmlMapper mapper = locateMapper(type, mediaType);
            endpoint = EndpointConfig.forWriting(mapper, annotations);
            // and cache for future reuse
            synchronized (_writers) {
                _writers.put(key.immutableKey(), endpoint);
            }
        }

        ObjectWriter writer = endpoint.getWriter();
        
        /* 27-Feb-2009, tatu: Where can we find desired encoding? Within
         *   HTTP headers?
         */
        JsonEncoding enc = findEncoding(mediaType, httpHeaders);
        JsonGenerator jg = writer.getJsonFactory().createJsonGenerator(entityStream, enc);

        // Want indentation?
        if (writer.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jg.useDefaultPrettyPrinter();
        }
        // 04-Mar-2010, tatu: How about type we were given? (if any)
        JavaType rootType = null;
        
        if (genericType != null && value != null) {
            /* 10-Jan-2011, tatu: as per [JACKSON-456], it's not safe to just force root
             *    type since it prevents polymorphic type serialization. Since we really
             *    just need this for generics, let's only use generic type if it's truly
             *    generic.
             */
            if (genericType.getClass() != Class.class) { // generic types are other impls of 'java.lang.reflect.Type'
                /* This is still not exactly right; should root type be further
                 * specialized with 'value.getClass()'? Let's see how well this works before
                 * trying to come up with more complete solution.
                 */
                rootType = writer.getTypeFactory().constructType(genericType);
                /* 26-Feb-2011, tatu: To help with [JACKSON-518], we better recognize cases where
                 *    type degenerates back into "Object.class" (as is the case with plain TypeVariable,
                 *    for example), and not use that.
                 */
                if (rootType.getRawClass() == Object.class) {
                    rootType = null;
                }
            }
        }
        // Most of the configuration now handled through EndpointConfig, ObjectWriter
        // but we may need to force root type:
        if (rootType != null) {
            writer = writer.withType(rootType);
        }
        writer.writeValue(jg, value);
    }

    /**
     * Helper method to use for determining desired output encoding.
     * For now, will always just use UTF-8...
     */
    protected JsonEncoding findEncoding(MediaType mediaType, MultivaluedMap<String,Object> httpHeaders)
    {
        return JsonEncoding.UTF8;
    }
    
    /*
    /**********************************************************
    /* Overridable helper methods
    /**********************************************************
     */

    /**
     * Helper method used to check whether given media type
     * is XML type or sub type.
     * Current implementation essentially checks to see whether
     * {@link MediaType#getSubtype} returns "xml" or something
     * ending with "+xml".
     */
    protected boolean isXMLType(MediaType mediaType)
    {
        /* As suggested by Stephen D, there are 2 ways to check: either
         * being as inclusive as possible (if subtype is "xml"), or
         * exclusive (major type "application", minor type "xml").
         * Let's start with inclusive one, hard to know which major
         * types we should cover aside from "application".
         */
        if (mediaType != null) {
            // Ok: there are also "xxx+xml" subtypes, which count as well
            String subtype = mediaType.getSubtype();
            return "xml".equalsIgnoreCase(subtype) || subtype.endsWith("+xml");
        }
        /* Not sure if this can happen; but it seems reasonable
         * that we can at least produce XML without media type?
         */
        return true;
    }
    
    /**
     * Helper method called to see if given type is readable, that is,
     * acceptable Java type to bind from XML
     */
    protected boolean isReadableType(Class<?> type)
    {
        /* Ok: looks like we must weed out some core types here; ones that
         * make no sense to try to bind from XML
         */
        if (_untouchables.contains(new ClassKey(type))) {
            return false;
        }
        // and there are some other abstract/interface types to exclude too:
        for (Class<?> cls : _unreadableClasses) {
            if (cls.isAssignableFrom(type)) {
                return false;
            }
        }
        // as well as possible custom exclusions
        if (_containedIn(type, _cfgCustomUntouchables)) {
            return false;
        }
        return true;
    }

    
    /**
     * Helper method called to see if given type is writable, that is,
     * acceptable Java type to write out as XML.
     */
    protected boolean isWritableType(Class<?> type)
    {
        /* Ok: looks like we must weed out some core types here; ones that
         * make no sense to try to write out as XML:
         */
        if (_untouchables.contains(new ClassKey(type))) {
            return false;
        }
        // but some are interface/abstract classes, so
        for (Class<?> cls : _unwritableClasses) {
            if (cls.isAssignableFrom(type)) {
                return false;
            }
        }
        // and finally, may have additional custom types to exclude
        if (_containedIn(type, _cfgCustomUntouchables)) {
            return false;
        }
        return true;
    }

    /**
     * Method called to locate {@link XmlMapper} to use for serialization
     * and deserialization. If an instance has been explicitly defined by
     * {@link #setMapper} (or non-null instance passed in constructor), that
     * will be used. 
     * If not, will try to locate it using standard JAX-RS
     * {@link ContextResolver} mechanism, if it has been properly configured
     * to access it (by JAX-RS runtime).
     * Finally, if no mapper is found, will return a default unconfigured
     * {@link ObjectMapper} instance (one constructed with default constructor
     * and not modified in any way)
     *
     * @param type Class of object being serialized or deserialized;
     *   not checked at this point, since it is assumed that unprocessable
     *   classes have been already weeded out,
     *   but will be passed to {@link ContextResolver} as is.
     * @param mediaType Declared media type for the instance to process:
     *   not used by this method,
     *   but will be passed to {@link ContextResolver} as is.
     */
    public XmlMapper locateMapper(Class<?> type, MediaType mediaType)
    {
        // First: were we configured with a specific instance?
        XmlMapper m = _mapperConfig.getConfiguredMapper();
        if (m == null) {
            // If not, maybe we can get one configured via context?
            if (_providers != null) {
                ContextResolver<XmlMapper> resolver = _providers.getContextResolver(XmlMapper.class, mediaType);
                /* Above should work as is, but due to this bug
                 *   [https://jersey.dev.java.net/issues/show_bug.cgi?id=288]
                 * in Jersey, it doesn't. But this works until resolution of
                 * the issue:
                 */
                if (resolver == null) {
                    resolver = _providers.getContextResolver(XmlMapper.class, null);
                }
                if (resolver != null) {
                    m = resolver.getContext(type);
                }
            }
            if (m == null) {
                // If not, let's get the fallback default instance
                m = _mapperConfig.getDefaultMapper();
            }
        }
        return m;
    }

    /*
    /**********************************************************
    /* Private/sub-class helper methods
    /**********************************************************
     */

    protected static boolean _containedIn(Class<?> mainType, HashSet<ClassKey> set)
    {
        if (set != null) {
            ClassKey key = new ClassKey(mainType);
            // First: type itself?
            if (set.contains(key)) return true;
            // Then supertypes (note: will not contain Object.class)
            for (Class<?> cls : findSuperTypes(mainType, null)) {
                key.reset(cls);
                if (set.contains(key)) return true;
            }
        }
        return false;
    }

    private static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore)
    {
        return findSuperTypes(cls, endBefore, new ArrayList<Class<?>>(8));
    }

    private static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore, List<Class<?>> result)
    {
        _addSuperTypes(cls, endBefore, result, false);
        return result;
    }
    
    private static void _addSuperTypes(Class<?> cls, Class<?> endBefore, Collection<Class<?>> result, boolean addClassItself)
    {
        if (cls == endBefore || cls == null || cls == Object.class) {
            return;
        }
        if (addClassItself) {
            if (result.contains(cls)) { // already added, no need to check supers
                return;
            }
            result.add(cls);
        }
        for (Class<?> intCls : cls.getInterfaces()) {
            _addSuperTypes(intCls, endBefore, result, true);
        }
        _addSuperTypes(cls.getSuperclass(), endBefore, result, true);
    }

}
