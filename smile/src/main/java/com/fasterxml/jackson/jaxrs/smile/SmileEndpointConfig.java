package com.fasterxml.jackson.jaxrs.smile;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;

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

    protected SmileEndpointConfig(MapperConfig<?> config) {
        super(config);
    }

    public static SmileEndpointConfig forReading(ObjectReader reader,
            Annotation[] annotations)
    {
        return new SmileEndpointConfig(reader.getConfig())
            .add(annotations, false)
            .initReader(reader)
        ;
    }

    public static SmileEndpointConfig forWriting(ObjectWriter writer,
            Annotation[] annotations)
    {
        return new SmileEndpointConfig(writer.getConfig())
            .add(annotations, true)
            .initWriter(writer)
        ;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void addAnnotation(Class<? extends Annotation> type,
            Annotation annotation, boolean forWriting)
    {
        if (type == com.fasterxml.jackson.jaxrs.smile.annotation.JacksonFeatures.class) {
            com.fasterxml.jackson.jaxrs.smile.annotation.JacksonFeatures feats = (com.fasterxml.jackson.jaxrs.smile.annotation.JacksonFeatures) annotation;
            if (forWriting) {
                _serEnable = nullIfEmpty(feats.serializationEnable());
                _serDisable = nullIfEmpty(feats.serializationDisable());
            } else {
                _deserEnable = nullIfEmpty(feats.deserializationEnable());
                _deserDisable = nullIfEmpty(feats.deserializationDisable());
            }
        } else {
            super.addAnnotation(type, annotation, forWriting);
        }
    }    
    
    @Override
    public Object modifyBeforeWrite(Object value) {
        // nothing to add
        return value;
    }
}
