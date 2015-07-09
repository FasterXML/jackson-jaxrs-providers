package com.fasterxml.jackson.jaxrs.cbor;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;

/**
 * Container class for figuring out annotation-based configuration
 * for JAX-RS end points.
 */
public class CBOREndpointConfig
    extends EndpointConfigBase<CBOREndpointConfig>
{
    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */

    protected CBOREndpointConfig(MapperConfig<?> config) {
        super(config);
    }

    public static CBOREndpointConfig forReading(ObjectReader reader,
            Annotation[] annotations)
    {
        return new CBOREndpointConfig(reader.getConfig())
            .add(annotations, false)
            .initReader(reader)
        ;
    }

    public static CBOREndpointConfig forWriting(ObjectWriter writer,
            Annotation[] annotations)
    {
        return new CBOREndpointConfig(writer.getConfig())
            .add(annotations, true)
            .initWriter(writer)
        ;
    }

    // No need to override, fine as-is:
//    protected void addAnnotation(Class<? extends Annotation> type, Annotation annotation, boolean forWriting)

    @Override
    public Object modifyBeforeWrite(Object value) {
        // nothing to add
        return value;
    }
}
