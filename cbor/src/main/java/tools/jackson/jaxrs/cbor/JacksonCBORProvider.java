package tools.jackson.jaxrs.cbor;

import java.lang.annotation.Annotation;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

import tools.jackson.core.*;

import tools.jackson.databind.*;

import tools.jackson.dataformat.cbor.databind.CBORMapper;
import tools.jackson.jaxrs.base.ProviderBase;
import tools.jackson.jaxrs.cbor.PackageVersion;

/**
 * Basic implementation of JAX-RS abstractions ({@link MessageBodyReader},
 * {@link MessageBodyWriter}) needed for binding
 * CBOR ("application/cbor") content to and from Java Objects ("POJO"s).
 *<p>
 * Actual data binding functionality is implemented by {@link ObjectMapper}:
 * mapper to use can be configured in multiple ways:
 * <ul>
 *  <li>By explicitly passing mapper to use in constructor
 *  <li>By explictly setting mapper to use by {@link #setMapper}
 *  <li>By defining JAX-RS <code>Provider</code> that returns {@link ObjectMapper}s.
 *  <li>By doing none of above, in which case a default mapper instance is
 *     constructed (and configured if configuration methods are called)
 * </ul>
 * The last method ("do nothing specific") is often good enough; explicit passing
 * of Mapper is simple and explicit; and Provider-based method may make sense
 * with Dependency Injection frameworks, or if Mapper has to be configured differently
 * for different media types.
 *<p>
 * Note that the default mapper instance will be automatically created if
 * one of explicit configuration methods (like {@link #configure})
 * is called: if so, Provider-based introspection is <b>NOT</b> used, but the
 * resulting Mapper is used as configured.
 *<p>
 * Note that there is also a sub-class -- ({@link JacksonJaxbCBORProvider}) -- that
 * is configured by default to use both Jackson and JAXB annotations for configuration
 * (base class when used as-is defaults to using just Jackson annotations)
 *
 * @author Tatu Saloranta
 */
@Provider
@Consumes(MediaType.WILDCARD)
// https://datatracker.ietf.org/doc/html/rfc8949
@Produces({ "application/cbor", MediaType.WILDCARD })
public class JacksonCBORProvider
extends ProviderBase<JacksonCBORProvider,
    CBORMapper,
    CBOREndpointConfig,
    CBORMapperConfigurator
>
{
    /*
    /**********************************************************************
    /* Context configuration
    /**********************************************************************
     */

    /**
     * Injectable context object used to locate configured
     * instance of {@link ObjectMapper} to use for actual
     * serialization.
     */
    @Context
    protected Providers _providers;

    /*
    /**********************************************************************
    /* Construction
    /**********************************************************************
     */

    /**
     * Default constructor, usually used when provider is automatically
     * configured to be used with JAX-RS implementation.
     */
    public JacksonCBORProvider() {
        this(null, null);
    }

    public JacksonCBORProvider(CBORMapper mapper)
    {
        this(mapper, null);
    }

    /**
     * Constructor to use when a custom mapper (usually components
     * like serializer/deserializer factories that have been configured)
     * is to be used.
     *
     * @param aiOverride AnnotationIntrospector to override default with, if any
     */
    public JacksonCBORProvider(CBORMapper mapper,
            AnnotationIntrospector aiOverride)
    {
        super(new CBORMapperConfigurator(mapper, aiOverride));
    }

    /**
     * Method that will return version information stored in and read from jar
     * that contains this class.
     */
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    /*
    /**********************************************************************
    /* Abstract method impls
    /**********************************************************************
     */

    /**
     * Helper method used to check whether given media type
     * is CBOR type or sub type.
     * Current implementation essentially checks to see whether
     * {@link MediaType#getSubtype} returns
     * "cbor" or something ending with "+cbor".
     */
    @Override
    protected boolean hasMatchingMediaType(MediaType mediaType)
    {
        /* As suggested by Stephen D, there are 2 ways to check: either
         * being as inclusive as possible (if subtype is "cbor"), or
         * exclusive (major type "application", minor type "cbor").
         * Let's start with inclusive one, hard to know which major
         * types we should cover aside from "application".
         */
        if (mediaType != null) {
            // Ok: there are also "xxx+cbor" subtypes, which count as well
            String subtype = mediaType.getSubtype();
            return CBORMediaTypes.APPLICATION_JACKSON_CBOR_TYPE.getSubtype().equalsIgnoreCase(subtype) || 
            		"cbor".equalsIgnoreCase(subtype) || subtype.endsWith("+cbor");
        }
        // Without a media type, let JAX-RS deal with mapping if it can.
        return false;
    }

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
    @Override
    protected CBORMapper _locateMapperViaProvider(Class<?> type, MediaType mediaType)
    {
        CBORMapper m = _mapperConfig.getConfiguredMapper();
        if (m == null) {
            if (_providers != null) {
                ContextResolver<CBORMapper> resolver = _providers.getContextResolver(CBORMapper.class, mediaType);
                // Above should work as is, but due to this bug
                //   [https://jersey.dev.java.net/issues/show_bug.cgi?id=288]
                // in Jersey, it doesn't. But this works until resolution of
                // the issue:
                if (resolver == null) {
                    resolver = _providers.getContextResolver(CBORMapper.class, null);
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

    @Override
    protected CBOREndpointConfig _configForReading(ObjectReader reader,
            Annotation[] annotations) {
        return CBOREndpointConfig.forReading(reader, annotations);
    }

    @Override
    protected CBOREndpointConfig _configForWriting(ObjectWriter writer,
            Annotation[] annotations) {
        return CBOREndpointConfig.forWriting(writer, annotations);
    }
}
