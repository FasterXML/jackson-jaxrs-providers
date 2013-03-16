package com.fasterxml.jackson.jaxrs.json.annotation;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;

import com.fasterxml.jackson.jaxrs.base.cfg.EndpointConfigBase;

/**
 * Container class for figuring out annotation-based configuration
 * for JAX-RS end points.
 */
public class JsonEndpointConfig
    extends EndpointConfigBase<JsonEndpointConfig>
{
    // // Serialization-only config

    protected JSONP.Def _jsonp;
    
    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */

    protected JsonEndpointConfig() { }
    
    public static JsonEndpointConfig forReading(ObjectMapper mapper, Annotation[] annotations)
    {
        return new JsonEndpointConfig()
            .add(annotations, false)
            .initReader(mapper);
    }

    public static JsonEndpointConfig forWriting(ObjectMapper mapper, Annotation[] annotations,
            String defaultJsonpMethod)
    {
        JsonEndpointConfig config =  new JsonEndpointConfig();
        if (defaultJsonpMethod != null) {
            config._jsonp = new JSONP.Def(defaultJsonpMethod);
        }
        return config
            .add(annotations, true)
            .initWriter(mapper)
        ;
    }

    /*
    /**********************************************************
    /* Abstract method impls, overrides
    /**********************************************************
     */
    
    @Override
    protected void addAnnotation(Class<? extends Annotation> type,
            Annotation annotation, boolean forWriting)
    {
        if (type == JSONP.class) {
            if (forWriting) {
                _jsonp = new JSONP.Def((JSONP) annotation);
            }
        } else if (type == JacksonFeatures.class) {
            JacksonFeatures feats = (JacksonFeatures) annotation;
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
        return applyJSONP(value);
    }
    
    /*
    /**********************************************************
    /* Accessors
    /**********************************************************
     */

    /**
     * Method that will add JSONP wrapper object, if and as
     * configured by collected annotations.
     */
    public Object applyJSONP(Object value)
    {
        if (_jsonp != null) {
            // full prefix+suffix?
            if (_jsonp.prefix != null || _jsonp.suffix != null) {
                return new JSONWrappedObject(_jsonp.prefix, _jsonp.suffix, value);
            }
            if (_jsonp.method != null) {
                return new JSONPObject(_jsonp.method, value);
            }
        }
        return value;
    }
    
    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    private static <T> T[] nullIfEmpty(T[] arg) {
        if (arg == null || arg.length == 0) {
            return null;
        }
        return arg;
    }
}
