package com.fasterxml.jackson.jaxrs.smile;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.jaxrs.base.cfg.EndpointConfigBase;

/**
 * Container class for figuring out annotation-based configuration
 * for JAX-RS end points.
 */
public class SmileEndpointConfig
    extends EndpointConfigBase<SmileEndpointConfig>
{
    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */

    protected SmileEndpointConfig() { }

    public static SmileEndpointConfig forReading(ObjectMapper mapper, Annotation[] annotations)
    {
        return new SmileEndpointConfig()
            .add(annotations, false)
            .initReader(mapper);
    }

    public static SmileEndpointConfig forWriting(ObjectMapper mapper, Annotation[] annotations)
    {
        SmileEndpointConfig config =  new SmileEndpointConfig();
        return config
            .add(annotations, true)
            .initWriter(mapper)
        ;
    }

    @Override
    public Object modifyBeforeWrite(Object value) {
        // nothing to add
        return value;
    }
}
