package com.fasterxml.jackson.jaxrs.yaml;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;

import java.lang.annotation.Annotation;

/**
 * Container class for figuring out annotation-based configuration
 * for JAX-RS end points.
 */
public class YAMLEndpointConfig
        extends EndpointConfigBase<YAMLEndpointConfig>
{
    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */

    protected YAMLEndpointConfig(MapperConfig<?> config) {
        super(config);
    }

    public static YAMLEndpointConfig forReading(ObjectReader reader,
                                                Annotation[] annotations) {
        return new YAMLEndpointConfig(reader.getConfig())
                .add(annotations, false)
                .initReader(reader);
    }

    public static YAMLEndpointConfig forWriting(ObjectWriter writer,
                                                Annotation[] annotations) {
        return new YAMLEndpointConfig(writer.getConfig())
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
