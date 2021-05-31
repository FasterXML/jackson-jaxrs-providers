package com.fasterxml.jackson.jaxrs.yaml;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

/**
 * JSON content type provider automatically configured to use both Jackson
 * and JAXB annotations (in that order of priority). Otherwise functionally
 * same as {@link JacksonYAMLProvider}.
 *<p>
 * Typical usage pattern is to just instantiate instance of this
 * provider for JAX-RS and use as is: this will use both Jackson and
 * JAXB annotations (with Jackson annotations having priority).
 *<p>
 * Note: class annotations are duplicated from super class, since it
 * is not clear whether JAX-RS implementations are required to
 * check settings of super-classes. It is important to keep annotations
 * in sync if changed.
 */
@Provider
@Consumes(MediaType.WILDCARD) // NOTE: required to support "non-standard" JSON variants
@Produces(MediaType.WILDCARD)
public class JacksonJaxbYAMLProvider extends JacksonYAMLProvider
{
    /**
     * Default constructor, usually used when provider is automatically
     * configured to be used with JAX-RS implementation.
     */
    public JacksonJaxbYAMLProvider() {
        this(null, JaxbHolder.get());
    }

    /**
     * Constructor to use when a custom mapper (usually components
     * like serializer/deserializer factories that have been configured)
     * is to be used.
     */
    public JacksonJaxbYAMLProvider(YAMLMapper mapper, AnnotationIntrospector aiOverride)
    {
        super(mapper, aiOverride);
    }

    // Silly class to encapsulate reference to JAXB introspector class so that
    // loading of parent class does not require it; only happens if and when
    // introspector needed
    private static class JaxbHolder {
        public static AnnotationIntrospector get() {
            return new JaxbAnnotationIntrospector();
        }
    }
}
