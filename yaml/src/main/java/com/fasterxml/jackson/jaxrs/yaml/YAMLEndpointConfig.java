package com.fasterxml.jackson.jaxrs.yaml;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;

import java.lang.annotation.Annotation;

/**
 * Container class for figuring out annotation-based configuration
 * for JAX-RS end points.
 */
public class YAMLEndpointConfig
        extends EndpointConfigBase<YAMLEndpointConfig> {
    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */

    protected YAMLEndpointConfig() {
    }

    public static YAMLEndpointConfig forReading(ObjectReader reader,
                                                Annotation[] annotations) {
        return new YAMLEndpointConfig()
                .add(annotations, false)
                .initReader(reader);
    }

    public static YAMLEndpointConfig forWriting(ObjectWriter writer,
                                                Annotation[] annotations) {
        YAMLEndpointConfig config = new YAMLEndpointConfig();
        return config
                .add(annotations, true)
                .initWriter(writer)
                ;
    }

    @Override
    public Object modifyBeforeWrite(Object value) {
        // nothing to add
        return value;
    }
}
