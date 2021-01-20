package com.fasterxml.jackson.jaxrs.cfg;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Helper class used to encapsulate details of configuring an
 * {@link ObjectMapper} instance to be used for data binding, as
 * well as accessing it.
 */
public abstract class MapperConfiguratorBase<IMPL extends MapperConfiguratorBase<IMPL,MAPPER>,
    MAPPER extends ObjectMapper
>
{
    /*
    /**********************************************************************
    /* Configuration, simple features
    /**********************************************************************
     */

    /**
     * {@link DeserializationFeature}s to explicitly enable or disable
     */
    protected EnumMap<MapperFeature, Boolean> _mapperFeatures;
    
    /**
     * {@link DeserializationFeature}s to explicitly enable or disable
     */
    protected EnumMap<DeserializationFeature, Boolean> _deserFeatures;

    /**
     * {@link SerializationFeature}s to explicitly enable or disable
     */
    protected EnumMap<SerializationFeature, Boolean> _serFeatures;

    /*
    /**********************************************************************
    /* Configuration, other
    /**********************************************************************
     */
    
    /**
     * Annotations set to use by default; overridden by explicit call
     * to {@link #setAnnotationsToUse}
     */
    protected Annotations[] _annotationsToUse;
    
    /*
    /**********************************************************************
    /* Lazily constructed Mapper instance(s)
    /**********************************************************************
     */
    
    /**
     * Mapper provider was constructed with if any, or that was constructed
     * due to a call to explicitly configure mapper.
     * If defined (explicitly or implicitly) it will be used, instead
     * of using provider-based lookup.
     */
    protected MAPPER _mapper;

    /**
     * If no mapper was specified when constructed, and no configuration
     * calls are made, a default mapper is constructed. The difference
     * between default mapper and regular one is that default mapper
     * is only used if no mapper is found via provider lookup.
     */
    protected MAPPER _defaultMapper;

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */
    
    public MapperConfiguratorBase(MAPPER mapper, Annotations[] defaultAnnotations)
    {
        _mapper = mapper;
        _annotationsToUse = defaultAnnotations;
    }

    public synchronized MAPPER getDefaultMapper() {
        if (_defaultMapper == null) {
            _defaultMapper = _mapperWithConfiguration(mapperBuilder());
        }
        return _defaultMapper;
    }

    /**
     * Helper method that will ensure that there is a configurable non-default
     * mapper (constructing an instance if one didn't yet exit), and return
     * that mapper.
     */
    protected MAPPER mapper()
    {
        if (_mapper == null) {
            _mapper = _mapperWithConfiguration(mapperBuilder());
        }
        return _mapper;
    }

    /*
    /**********************************************************************
    /* Abstract methods to implement
    /**********************************************************************
     */

    /**
     * @since 3.0
     */
    protected abstract MapperBuilder<?,?> mapperBuilder();
    
    /*
    /**********************************************************************
    /* Configuration methods
    /**********************************************************************
     */

    /**
     * Method that locates, configures and returns {@link ObjectMapper} to use
     */
    public synchronized MAPPER getConfiguredMapper() {
        // important: should NOT call mapper(); needs to return null
        // if no instance has been passed or constructed
        return _mapper;
    }

    public synchronized final void setMapper(MAPPER m) {
        _mapper = m;
    }

    public synchronized final void setAnnotationsToUse(Annotations[] annotationsToUse) {
        _annotationsToUse = annotationsToUse;
    }

    public final void configure(DeserializationFeature f, boolean state) {
        if (_deserFeatures == null) {
            _deserFeatures = new EnumMap<>(DeserializationFeature.class);
        }
        _deserFeatures.put(f, state);
    }

    public final void configure(SerializationFeature f, boolean state) {
        if (_serFeatures == null) {
            _serFeatures = new EnumMap<>(SerializationFeature.class);
        }
        _serFeatures.put(f, state);
    }

    /*
    /**********************************************************************
    /* Helper methods for sub-classes
    /**********************************************************************
     */

    /**
     * Helper method that will configure given builder using configured overrides.
     */
    @SuppressWarnings("unchecked")
    protected MAPPER _mapperWithConfiguration(MapperBuilder<?,?> mapperBuilder)
    {
        return (MAPPER) _builderWithConfiguration(mapperBuilder)
                .build();
    }

    /**
     * Overridable helper method that applies all configuration on given builder.
     */
    protected MapperBuilder<?,?> _builderWithConfiguration(MapperBuilder<?,?> mapperBuilder)
    {
        // First, AnnotationIntrospector settings
        AnnotationIntrospector intr;
        if ((_annotationsToUse == null) || (_annotationsToUse.length == 0)) {
            intr = AnnotationIntrospector.nopInstance();
        } else {
            intr = _resolveIntrospectors(_annotationsToUse);
        }
        mapperBuilder = mapperBuilder.annotationIntrospector(intr);

        // Features?
        if (_mapperFeatures != null) {
            for (Map.Entry<MapperFeature,Boolean> entry : _mapperFeatures.entrySet()) {
                mapperBuilder = mapperBuilder.configure(entry.getKey(), entry.getValue());
            }
        }
        if (_serFeatures != null) {
            for (Map.Entry<SerializationFeature,Boolean> entry : _serFeatures.entrySet()) {
                mapperBuilder = mapperBuilder.configure(entry.getKey(), entry.getValue());
            }
        }
        if (_deserFeatures != null) {
            for (Map.Entry<DeserializationFeature,Boolean> entry : _deserFeatures.entrySet()) {
                mapperBuilder = mapperBuilder.configure(entry.getKey(), entry.getValue());
            }
        }

        // anything else?
        return mapperBuilder;
    }

    protected AnnotationIntrospector _resolveIntrospectors(Annotations[] annotationsToUse)
    {
        // Let's ensure there are no dups there first, filter out nulls
        ArrayList<AnnotationIntrospector> intr = new ArrayList<AnnotationIntrospector>();
        for (Annotations a : annotationsToUse) {
            if (a != null) {
                intr.add(_resolveIntrospector(a));
            }
        }
        int count = intr.size();
        if (count == 0) {
            return AnnotationIntrospector.nopInstance();
        }
        AnnotationIntrospector curr = intr.get(0);
        for (int i = 1, len = intr.size(); i < len; ++i) {
            curr = AnnotationIntrospector.pair(curr, intr.get(i));
        }
        return curr;
    }

    protected AnnotationIntrospector _resolveIntrospector(Annotations ann)
    {
        switch (ann) {
        case JACKSON:
            return _jacksonIntrospector();
        case JAXB:
            return _jaxbIntrospector();
        default:
            throw new IllegalStateException(); 
        }
    }

    // Separate method to allow overriding
    protected AnnotationIntrospector _jacksonIntrospector() {
        return new JacksonAnnotationIntrospector();
    }

    protected abstract AnnotationIntrospector _jaxbIntrospector();
}
