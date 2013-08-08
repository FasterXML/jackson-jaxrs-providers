package com.fasterxml.jackson.jaxrs.cfg;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;

/**
 * Container class for figuring out annotation-based configuration
 * for JAX-RS end points.
 */
public abstract class EndpointConfigBase<THIS extends EndpointConfigBase<THIS>>
{
    // // General configuration

	protected Class<?> _activeView;

    protected String _rootName;
    
    // // Deserialization-only config
    
    protected DeserializationFeature[] _deserEnable;
    protected DeserializationFeature[] _deserDisable;

    protected ObjectReader _reader;
    
    // // Serialization-only config
    
    protected SerializationFeature[] _serEnable;
    protected SerializationFeature[] _serDisable;

    protected ObjectWriter _writer;
    
    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */

    protected EndpointConfigBase() { }
    
    @SuppressWarnings("unchecked")
    protected THIS add(Annotation[] annotations, boolean forWriting)
    {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                addAnnotation(annotation.annotationType(), annotation, forWriting);
            }
        }
        return (THIS) this;
    }

    protected void addAnnotation(Class<? extends Annotation> type,
            Annotation annotation, boolean forWriting)
    {
        if (type == JsonView.class) {
            // Can only use one view; but if multiple defined, use first (no exception)
            Class<?>[] views = ((JsonView) annotation).value();
            _activeView = (views.length > 0) ? views[0] : null;
        } else if (type == JacksonFeatures.class) {
            JacksonFeatures feats = (JacksonFeatures) annotation;
            if (forWriting) {
                _serEnable = nullIfEmpty(feats.serializationEnable());
                _serDisable = nullIfEmpty(feats.serializationDisable());
            } else {
                _deserEnable = nullIfEmpty(feats.deserializationEnable());
                _deserDisable = nullIfEmpty(feats.deserializationDisable());
            }
        } else if (type == JsonRootName.class) {
            _rootName = ((JsonRootName) annotation).value();
        } else if (type == JacksonAnnotationsInside.class) {
            // skip; processed below (in parent), so encountering here is of no use
        } else {
            // For all unrecognized types, check meta-annotation(s) to see if they are bundles
            JacksonAnnotationsInside inside = type.getAnnotation(JacksonAnnotationsInside.class);
            if (inside != null) {
                add(type.getAnnotations(), forWriting);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    protected THIS initReader(ObjectMapper mapper)
    {
        // first common config
        if (_activeView != null) {
            _reader = mapper.readerWithView(_activeView);
        } else {
            _reader = mapper.reader();
        }

        if (_rootName != null) {
            _reader = _reader.withRootName(_rootName);
        }
        // Then deser features
        if (_deserEnable != null) {
            _reader = _reader.withFeatures(_deserEnable);
        }
        if (_deserDisable != null) {
            _reader = _reader.withoutFeatures(_deserDisable);
        }
        /* Important: we are NOT to close the underlying stream after
         * mapping, so we need to instruct parser:
         */
        _reader.getFactory().disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        
        return (THIS) this;
    }
    
    @SuppressWarnings("unchecked")
    protected THIS initWriter(ObjectMapper mapper)
    {
        // first common config
        if (_activeView != null) {
            _writer = mapper.writerWithView(_activeView);
        } else {
            _writer = mapper.writer();
        }
        if (_rootName != null) {
            _writer = _writer.withRootName(_rootName);
        }
        // Then features
        if (_serEnable != null) {
            _writer = _writer.withFeatures(_serEnable);
        }
        if (_serDisable != null) {
            _writer = _writer.withoutFeatures(_serDisable);
        }
        /* Important: we are NOT to close the underlying stream after
         * mapping, so we need to instruct parser:
         */
        _writer.getFactory().disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        
        return (THIS) this;
    }
    
    /*
    /**********************************************************
    /* Accessors
    /**********************************************************
     */
    
    public final ObjectReader getReader() {
        if (_reader == null) { // sanity check, should never happen
            throw new IllegalStateException();
        }
        return _reader;
    }

    public final ObjectWriter getWriter() {
        if (_writer == null) { // sanity check, should never happen
            throw new IllegalStateException();
        }
        return _writer;
    }

    /*
    /**********************************************************
    /* Value modifications
    /**********************************************************
     */

    public abstract Object modifyBeforeWrite(Object value);

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    protected static <T> T[] nullIfEmpty(T[] arg) {
        if (arg == null || arg.length == 0) {
            return null;
        }
        return arg;
    }
}
