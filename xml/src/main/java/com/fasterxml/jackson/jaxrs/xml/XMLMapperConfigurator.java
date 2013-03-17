package com.fasterxml.jackson.jaxrs.xml;

import java.util.*;

import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.jaxb.XmlJaxbAnnotationIntrospector;

import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;

/**
 * Helper class used to encapsulate details of configuring an
 * {@link XmlMapper} instance to be used for data binding, as
 * well as accessing it.
 */
public class XMLMapperConfigurator
    extends MapperConfiguratorBase<XMLMapperConfigurator, XmlMapper>
{
    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */
    
    public XMLMapperConfigurator(XmlMapper mapper, Annotations[] defAnnotations)
    {
        super(mapper, defAnnotations);
    }

    /**
     * Method that locates, configures and returns {@link XmlMapper} to use
     */
    @Override
    public synchronized XmlMapper getConfiguredMapper() {
        /* important: should NOT call mapper(); needs to return null
         * if no instance has been passed or constructed
         */
        return _mapper;
    }

    @Override
    public synchronized XmlMapper getDefaultMapper()
    {
        if (_defaultMapper == null) {
            // 10-Oct-2012, tatu: Better do things explicitly...
            JacksonXmlModule module = getConfiguredModule();
            _defaultMapper = (module == null) ? new XmlMapper() : new XmlMapper(module);
            _setAnnotations(_defaultMapper, _defaultAnnotationsToUse);
        }
        return _defaultMapper;
    }

    protected JacksonXmlModule getConfiguredModule()
    {
        return new JacksonXmlModule();
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
    protected XmlMapper mapper()
    {
        if (_mapper == null) {
            _mapper = new XmlMapper();
            _setAnnotations(_mapper, _defaultAnnotationsToUse);
        }
        return _mapper;
    }

    @Override
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
        /* 11-Oct-2012, tatu: IMPORTANT: we MUST override choices here,
         *   since XML module provides extended versions of BOTH standard
         *   AnnotationIntrospectors.
         */
        
        switch (ann) {
        case JACKSON:
            return new JacksonXmlAnnotationIntrospector();
        case JAXB:
            /* For this, need to use indirection just so that error occurs
             * when we get here, and not when this class is being loaded
             */
            try {
                if (_jaxbIntrospectorClass == null) {
                    _jaxbIntrospectorClass = XmlJaxbAnnotationIntrospector.class;
                }
                return _jaxbIntrospectorClass.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to instantiate XmlJaxbAnnotationIntrospector: "+e.getMessage(), e);
            }
        default:
            throw new IllegalStateException(); 
        }
    }
}
