package tools.jackson.jaxrs.json;

import java.lang.annotation.Annotation;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

import tools.jackson.core.*;

import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.jaxrs.base.ProviderBase;

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
 *  <li>By defining JAX-RS <code>Provider</code> that returns {@link ObjectMapper}s.
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
 * Note: version 1.3 added a sub-class ({@link JacksonJaxbJsonProvider}) which
 * is configured by default to use both Jackson and JAXB annotations for configuration
 * (base class when used as-is defaults to using just Jackson annotations)
 *
 * @author Tatu Saloranta
 */
@Provider
@Consumes(MediaType.WILDCARD) // NOTE: required to support "non-standard" JSON variants
@Produces(MediaType.WILDCARD)
public class JacksonJsonProvider
    extends ProviderBase<JacksonJsonProvider,
        JsonMapper,
        JsonEndpointConfig,
        JsonMapperConfigurator>
{
    public final static String MIME_JAVASCRIPT = "application/javascript";

    public final static String MIME_JAVASCRIPT_MS = "application/x-javascript";

    /*
    /**********************************************************************
    /* General configuration
    /**********************************************************************
     */

    /**
     * JSONP function name to use for automatic JSONP wrapping, if any;
     * if null, no JSONP wrapping is done.
     * Note that this is the default value that can be overridden on
     * per-endpoint basis.
     */
    protected String _jsonpFunctionName;

    /*
    /**********************************************************************
    /* Context configuration
    /**********************************************************************
     */

    /**
     * Injectable context object used to locate configured
     * instance of {@link JsonMapper} to use for actual
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
    public JacksonJsonProvider() {
        this(null, null);
    }

    public JacksonJsonProvider(JsonMapper mapper) {
        this(mapper, null);
    }

    /**
     * Constructor to use when a custom mapper (usually components
     * like serializer/deserializer factories that have been configured)
     * is to be used.
     *
     * @param aiOverride AnnotationIntrospector to override default with, if any
     */
    public JacksonJsonProvider(JsonMapper mapper,
            AnnotationIntrospector aiOverride) {
        super(new JsonMapperConfigurator(mapper, aiOverride));
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
    /* JSON-specific configuration
    /**********************************************************************
     */

    public void setJSONPFunctionName(String fname) {
        _jsonpFunctionName = fname;
    }

    /*
    /**********************************************************************
    /* Abstract method impls
    /**********************************************************************
     */

    /**
     * Helper method used to check whether given media type
     * is supported by this provider.
     * Current implementation essentially checks to see whether
     * {@link MediaType#getSubtype} returns "json" or something
     * ending with "+json".
     * Or "text/x-json" (since 2.3)
     */
    @Override
    protected boolean hasMatchingMediaType(MediaType mediaType)
    {
        /* As suggested by Stephen D, there are 2 ways to check: either
         * being as inclusive as possible (if subtype is "json"), or
         * exclusive (major type "application", minor type "json").
         * Let's start with inclusive one, hard to know which major
         * types we should cover aside from "application".
         */
        if (mediaType != null) {
            // Ok: there are also "xxx+json" subtypes, which count as well
            String subtype = mediaType.getSubtype();

            // [Issue#14]: also allow 'application/javascript'
            return "json".equalsIgnoreCase(subtype) || subtype.endsWith("+json")
                   || "javascript".equals(subtype)
                   // apparently Microsoft once again has interesting alternative types?
                   || "x-javascript".equals(subtype)
                   || "x-json".equals(subtype) // [Issue#40]
                   ;
        }
        // Not sure if this can happen; but it seems reasonable
        // that we can at least produce JSON without media type?
        return true;
    }

    @Override
    protected JsonMapper _locateMapperViaProvider(Class<?> type, MediaType mediaType)
    {
        JsonMapper m = _mapperConfig.getConfiguredMapper();
        if (m == null) {
            if (_providers != null) {
                ContextResolver<JsonMapper> resolver = _providers.getContextResolver(JsonMapper.class, mediaType);
                /* Above should work as is, but due to this bug
                 *   [https://jersey.dev.java.net/issues/show_bug.cgi?id=288]
                 * in Jersey, it doesn't. But this works until resolution of
                 * the issue:
                 */
                if (resolver == null) {
                    resolver = _providers.getContextResolver(JsonMapper.class, null);
                }
                if (resolver != null) {
                    return resolver.getContext(type);
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
    protected JsonEndpointConfig _configForReading(ObjectReader reader,
        Annotation[] annotations) {
        return JsonEndpointConfig.forReading(reader, annotations);
    }

    @Override
    protected JsonEndpointConfig _configForWriting(ObjectWriter writer,
        Annotation[] annotations) {
        return JsonEndpointConfig.forWriting(writer, annotations,
                _jsonpFunctionName);
    }
}
