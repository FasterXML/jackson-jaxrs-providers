package com.fasterxml.jackson.jaxrs.base;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.util.LRUMap;

import com.fasterxml.jackson.jaxrs.cfg.AnnotationBundleKey;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;
import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;
import com.fasterxml.jackson.jaxrs.util.ClassKey;

public abstract class ProviderBase<
    THIS extends ProviderBase<THIS, MAPPER, EP_CONFIG, MAPPER_CONFIG>,
    MAPPER extends ObjectMapper,
    EP_CONFIG extends EndpointConfigBase<EP_CONFIG>,
    MAPPER_CONFIG extends MapperConfiguratorBase<MAPPER_CONFIG,MAPPER>
>
    implements
        MessageBodyReader<Object>,
        MessageBodyWriter<Object>,
        Versioned
{
    /**
     * Looks like we need to worry about accidental
     *   data binding for types we shouldn't be handling. This is
     *   probably not a very good way to do it, but let's start by
     *   blacklisting things we are not to handle.
     *<p>
     *  (why ClassKey? since plain old Class has no hashCode() defined,
     *  lookups are painfully slow)
     */
    protected final static HashSet<ClassKey> DEFAULT_UNTOUCHABLES = new HashSet<ClassKey>();
    static {
        // First, I/O things (direct matches)
        DEFAULT_UNTOUCHABLES.add(new ClassKey(java.io.InputStream.class));
        DEFAULT_UNTOUCHABLES.add(new ClassKey(java.io.Reader.class));
        DEFAULT_UNTOUCHABLES.add(new ClassKey(java.io.OutputStream.class));
        DEFAULT_UNTOUCHABLES.add(new ClassKey(java.io.Writer.class));

        // then some primitive types
        DEFAULT_UNTOUCHABLES.add(new ClassKey(char[].class));

        /* 28-Jan-2012, tatu: 1.x excluded some additional types;
         *   but let's relax these a bit:
         */
        /* 27-Apr-2012, tatu: Ugh. As per
         *   [https://github.com/FasterXML/jackson-jaxrs-json-provider/issues/12]
         *  better revert this back, to make them untouchable again.
         */
        DEFAULT_UNTOUCHABLES.add(new ClassKey(String.class));
        DEFAULT_UNTOUCHABLES.add(new ClassKey(byte[].class));
    }

    /**
     * These are classes that we never use for reading
     * (never try to deserialize instances of these types).
     */
    public final static Class<?>[] DEFAULT_UNREADABLES = new Class<?>[] {
        InputStream.class, Reader.class
    };

    /**
     * These are classes that we never use for writing
     * (never try to serialize instances of these types).
     */
    public final static Class<?>[] DEFAULT_UNWRITABLES = new Class<?>[] {
        OutputStream.class, Writer.class,
        StreamingOutput.class, Response.class
    };

    /*
    /**********************************************************
    /* General configuration
    /**********************************************************
     */

    /**
     * Set of types (classes) that provider should ignore for data binding
     */
    protected HashSet<ClassKey> _cfgCustomUntouchables;

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
    /* Excluded types
    /**********************************************************
     */
    
    public final static HashSet<ClassKey> _untouchables = DEFAULT_UNTOUCHABLES;

    public final static Class<?>[] _unreadableClasses = DEFAULT_UNREADABLES;

    public final static Class<?>[] _unwritableClasses = DEFAULT_UNWRITABLES;

    /*
    /**********************************************************
    /* Bit of caching
    /**********************************************************
     */

    /**
     * Cache for resolved endpoint configurations when reading JSON data
     */
    protected final LRUMap<AnnotationBundleKey, EP_CONFIG> _readers
        = new LRUMap<AnnotationBundleKey, EP_CONFIG>(16, 120);

    /**
     * Cache for resolved endpoint configurations when writing JSON data
     */
    protected final LRUMap<AnnotationBundleKey, EP_CONFIG> _writers
        = new LRUMap<AnnotationBundleKey, EP_CONFIG>(16, 120);
    
    /*
    /**********************************************************
    /* General configuration
    /**********************************************************
     */
    
    /**
     * Helper object used for encapsulating configuration aspects
     * of {@link ObjectMapper}
     */
    protected final MAPPER_CONFIG _mapperConfig;

    protected ProviderBase(MAPPER_CONFIG mconfig) {
        _mapperConfig = mconfig;
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
     * Method that can be used to directly define {@link ObjectMapper} to use
     * for serialization and deserialization; if null, will use the standard
     * provider discovery from context instead. Default setting is null.
     */
    public void setMapper(MAPPER m) {
        _mapperConfig.setMapper(m);
    }

    public THIS configure(DeserializationFeature f, boolean state) {
        _mapperConfig.configure(f, state);
        return _this();
    }

    public THIS configure(SerializationFeature f, boolean state) {
        _mapperConfig.configure(f, state);
        return _this();
    }

    public THIS configure(JsonParser.Feature f, boolean state) {
        _mapperConfig.configure(f, state);
        return _this();
    }

    public THIS configure(JsonGenerator.Feature f, boolean state) {
        _mapperConfig.configure(f, state);
        return _this();
    }

    public THIS enable(DeserializationFeature f, boolean state) {
        _mapperConfig.configure(f, true);
        return _this();
    }

    public THIS enable(SerializationFeature f, boolean state) {
        _mapperConfig.configure(f, true);
        return _this();
    }

    public THIS enable(JsonParser.Feature f, boolean state) {
        _mapperConfig.configure(f, true);
        return _this();
    }

    public THIS enable(JsonGenerator.Feature f, boolean state) {
        _mapperConfig.configure(f, true);
        return _this();
    }

    public THIS disable(DeserializationFeature f, boolean state) {
        _mapperConfig.configure(f, false);
        return _this();
    }

    public THIS disable(SerializationFeature f, boolean state) {
        _mapperConfig.configure(f, false);
        return _this();
    }

    public THIS disable(JsonParser.Feature f, boolean state) {
        _mapperConfig.configure(f, false);
        return _this();
    }

    public THIS disable(JsonGenerator.Feature f, boolean state) {
        _mapperConfig.configure(f, false);
        return _this();
    }

    /*
    /**********************************************************
    /* Abstract methods sub-classes need to implement
    /**********************************************************
     */
    
    /**
     * Helper method used to check whether given media type
     * is JSON type or sub type.
     * Current implementation essentially checks to see whether
     * {@link MediaType#getSubtype} returns "json" or something
     * ending with "+json".
     * 
     * @since 2.2
     */
    protected abstract boolean hasMatchingMediaType(MediaType mediaType);

    protected abstract MAPPER _locateMapperViaProvider(Class<?> type, MediaType mediaType);

    protected abstract EP_CONFIG _configForReading(MAPPER mapper, Annotation[] annotations);

    protected abstract EP_CONFIG _configForWriting(MAPPER mapper, Annotation[] annotations);
    
    /*
    /**********************************************************
    /* Partial MessageBodyWriter impl
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
    @Override
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
     * a JSON type (via call to {@link #isJsonType}; then verify
     * that type is not one of "untouchable" types (types we will never
     * automatically handle), and finally that there is a serializer
     * for type (iff {@link #checkCanSerialize} has been called with
     * true argument -- otherwise assumption is there will be a handler)
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        if (!hasMatchingMediaType(mediaType)) {
            return false;
        }

        /* Ok: looks like we must weed out some core types here; ones that
         * make no sense to try to bind from JSON:
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
    @Override
    public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String,Object> httpHeaders, OutputStream entityStream) 
        throws IOException
    {
        AnnotationBundleKey key = new AnnotationBundleKey(annotations);
        EP_CONFIG endpoint;
        synchronized (_writers) {
            endpoint = _writers.get(key);
        }
        // not yet resolved (or not cached any more)? Resolve!
        if (endpoint == null) {
            MAPPER mapper = locateMapper(type, mediaType);
            endpoint = _configForWriting(mapper, annotations);
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
        JsonGenerator jg = writer.getFactory().createGenerator(entityStream, enc);

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
        value = endpoint.modifyBeforeWrite(value);
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
    /* MessageBodyReader impl
    /**********************************************************
     */
    
    /**
     * Method that JAX-RS container calls to try to check whether
     * values of given type (and media type) can be deserialized by
     * this provider.
     * Implementation will first check that expected media type is
     * a JSON type (via call to {@link #isJsonType}; then verify
     * that type is not one of "untouchable" types (types we will never
     * automatically handle), and finally that there is a deserializer
     * for type (iff {@link #checkCanDeserialize} has been called with
     * true argument -- otherwise assumption is there will be a handler)
     */
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        if (!hasMatchingMediaType(mediaType)) {
            return false;
        }

        /* Ok: looks like we must weed out some core types here; ones that
         * make no sense to try to bind from JSON:
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

        // Finally: if we really want to verify that we can serialize, we'll check:
        if (_cfgCheckCanSerialize) {
            ObjectMapper mapper = locateMapper(type, mediaType);
            if (!mapper.canDeserialize(mapper.constructType(type))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Method that JAX-RS container calls to deserialize given value.
     */
    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String,String> httpHeaders, InputStream entityStream) 
        throws IOException
    {
        AnnotationBundleKey key = new AnnotationBundleKey(annotations);
        EP_CONFIG endpoint;
        synchronized (_readers) {
            endpoint = _readers.get(key);
        }
        // not yet resolved (or not cached any more)? Resolve!
        if (endpoint == null) {
            MAPPER mapper = locateMapper(type, mediaType);
            endpoint = _configForReading(mapper, annotations);
            // and cache for future reuse
            synchronized (_readers) {
                _readers.put(key.immutableKey(), endpoint);
            }
        }
        ObjectReader reader = endpoint.getReader();
        JsonParser jp = _createParser(reader, entityStream);
        // If null is returned, considered to be empty stream
        if (jp == null || jp.nextToken() == null) {
            return null;
        }
        return reader.withType(genericType).readValue(jp);
    }

    /**
     * Overridable helper method called to create a {@link JsonParser} for reading
     * contents of given raw {@link InputStream}.
     * May return null to indicate that Stream is empty; that is, contains no
     * content.
     */
    protected JsonParser _createParser(ObjectReader reader, InputStream rawStream)
        throws IOException
    {
        return reader.getFactory().createParser(rawStream);
    }
    
    /*
    /**********************************************************
    /* Private/sub-class helper methods
    /**********************************************************
     */
    
    /**
     * Method called to locate {@link ObjectMapper} to use for serialization
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
    public MAPPER locateMapper(Class<?> type, MediaType mediaType)
    {
        // First: were we configured with a specific instance?
        MAPPER m = _mapperConfig.getConfiguredMapper();
        if (m == null) {
            // If not, maybe we can get one configured via context?
            m = _locateMapperViaProvider(type, mediaType);
            if (m == null) {
                // If not, let's get the fallback default instance
                m = _mapperConfig.getDefaultMapper();
            }
        }
        return m;
    }
    
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

    protected static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore)
    {
        return findSuperTypes(cls, endBefore, new ArrayList<Class<?>>(8));
    }

    protected static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore, List<Class<?>> result)
    {
        _addSuperTypes(cls, endBefore, result, false);
        return result;
    }
    
    protected static void _addSuperTypes(Class<?> cls, Class<?> endBefore, Collection<Class<?>> result, boolean addClassItself)
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

    @SuppressWarnings("unchecked")
    private final THIS _this() {
        return (THIS) this;
    }
}
