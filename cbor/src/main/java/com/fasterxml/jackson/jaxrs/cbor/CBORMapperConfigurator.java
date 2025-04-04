package com.fasterxml.jackson.jaxrs.cbor;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * Helper class used to encapsulate details of configuring an
 * {@link ObjectMapper} instance to be used for data binding, as
 * well as accessing it.
 */
public class CBORMapperConfigurator
    extends MapperConfiguratorBase<CBORMapperConfigurator, ObjectMapper>
{
    // @since 2.17.1
    private final ReentrantLock _lock = new ReentrantLock();

    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */
    
    public CBORMapperConfigurator(ObjectMapper mapper, Annotations[] defAnnotations)
    {
        super(mapper, defAnnotations);
    }

    /**
     * Method that locates, configures and returns {@link ObjectMapper} to use
     */
    @Override
    public ObjectMapper getConfiguredMapper() {
        // important: should NOT call mapper(); needs to return null
        // if no instance has been passed or constructed
        return _mapper;
    }

    @Override
    public ObjectMapper getDefaultMapper() {
        if (_defaultMapper == null) {
            _lock.lock();
            try {
                if (_defaultMapper == null) {
                    _defaultMapper = new CBORMapper();
                    _setAnnotations(_defaultMapper, _defaultAnnotationsToUse);
                }
            } finally {
                _lock.unlock();
            }
        }
        return _defaultMapper;
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
            _lock.lock();
            try {
                if (_mapper == null) {
                    _mapper = new CBORMapper();
                    _setAnnotations(_mapper, _defaultAnnotationsToUse);
                }
            } finally {
                _lock.unlock();
            }
        }
        return _mapper;
    }

    @Override
    protected AnnotationIntrospector _resolveIntrospectors(Annotations[] annotationsToUse)
    {
        // Let's ensure there are no dups there first, filter out nulls
        ArrayList<AnnotationIntrospector> intr = new ArrayList<>();
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
            // For this, need to use indirection just so that error occurs
            // when we get here, and not when this class is being loaded
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
