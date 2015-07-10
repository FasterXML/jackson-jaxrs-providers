package com.fasterxml.jackson.jaxrs.xml;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;

/**
 * Container class for figuring out annotation-based configuration
 * for JAX-RS end points.
 */
public class XMLEndpointConfig
    extends EndpointConfigBase<XMLEndpointConfig>
{
    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */

    protected XMLEndpointConfig(MapperConfig<?> config) { super(config); }

    public static XMLEndpointConfig forReading(ObjectReader reader,
            Annotation[] annotations)
    {
        return new XMLEndpointConfig(reader.getConfig())
            .add(annotations, false)
            .initReader(reader);
    }

    public static XMLEndpointConfig forWriting(ObjectWriter writer,
            Annotation[] annotations)
    {
        return new XMLEndpointConfig(writer.getConfig())
            .add(annotations, true)
            .initWriter(writer)
        ;
    }

    /*// since 2.6, nothing to add
    @Override
    protected void addAnnotation(Class<? extends Annotation> type,
            Annotation annotation, boolean forWriting)
    {
        super.addAnnotation(type, annotation, forWriting);
    }
    */
    
    @Override
    public Object modifyBeforeWrite(Object value) {
        // nothing to add
        return value;
    }
}
