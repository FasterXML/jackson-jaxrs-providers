package com.fasterxml.jackson.jaxrs.smile.cfg;

import java.util.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;

import com.fasterxml.jackson.jaxrs.base.cfg.MapperConfiguratorBase;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import com.fasterxml.jackson.jaxrs.smile.Annotations;

/**
 * Helper class used to encapsulate details of configuring an
 * {@link ObjectMapper} instance to be used for data binding, as
 * well as accessing it.
 */
public class MapperConfigurator
    extends MapperConfiguratorBase<MapperConfigurator, ObjectMapper>
{
    /**
     * Annotations set to use by default; overridden by explicit call
     * to {@link #setAnnotationsToUse}
     */
    protected Annotations[] _defaultAnnotationsToUse;
    
    /**
     * To support optional dependency to Jackson JAXB annotations module
     * (needed iff JAXB annotations are used for configuration)
     */
    protected Class<? extends AnnotationIntrospector> _jaxbIntrospectorClass;
    
    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */
    
    public MapperConfigurator(ObjectMapper mapper, Annotations[] defAnnotations)
    {
        super(mapper);
        _defaultAnnotationsToUse = defAnnotations;
    }

    /**
     * Method that locates, configures and returns {@link ObjectMapper} to use
     */
    public synchronized ObjectMapper getConfiguredMapper() {
        /* important: should NOT call mapper(); needs to return null
         * if no instance has been passed or constructed
         */
        return _mapper;
    }

    public synchronized ObjectMapper getDefaultMapper() {
        if (_defaultMapper == null) {
            _defaultMapper = new ObjectMapper(new SmileFactory());
            _setAnnotations(_defaultMapper, _defaultAnnotationsToUse);
        }
        return _defaultMapper;
    }

    /*
    /***********************************************************
    /* Configuration methods
    /***********************************************************
     */

    public synchronized void setAnnotationsToUse(Annotations[] annotationsToUse) {
        _setAnnotations(mapper(), annotationsToUse);
    }

    /*
    /***********************************************************
    /* Internal methods
    /***********************************************************
     */

    /**
     * Helper method that will ensure that there is a configurable non-default
     * mapper (constructing an instance if one didn't yet exit), and return
     * that mapper.
     */
    @Override
    protected ObjectMapper mapper()
    {
        if (_mapper == null) {
            _mapper = new ObjectMapper(new SmileFactory());
            _setAnnotations(_mapper, _defaultAnnotationsToUse);
        }
        return _mapper;
    }

    protected void _setAnnotations(ObjectMapper mapper, Annotations[] annotationsToUse)
    {
        AnnotationIntrospector intr;
        if (annotationsToUse == null || annotationsToUse.length == 0) {
            intr = AnnotationIntrospector.nopInstance();
        } else {
            intr = _resolveIntrospectors(annotationsToUse);
        }
        mapper.setAnnotationIntrospector(intr);
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
            return new JacksonAnnotationIntrospector();
        case JAXB:
            /* For this, need to use indirection just so that error occurs
             * when we get here, and not when this class is being loaded
             */
            try {
                if (_jaxbIntrospectorClass == null) {
                    _jaxbIntrospectorClass = JaxbAnnotationIntrospector.class;
                }
                return _jaxbIntrospectorClass.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to instantiate JaxbAnnotationIntrospector: "+e.getMessage(), e);
            }
        default:
            throw new IllegalStateException(); 
        }
    }
}
