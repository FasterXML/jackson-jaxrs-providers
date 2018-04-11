package com.fasterxml.jackson.jaxrs.smile;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;

import com.fasterxml.jackson.dataformat.smile.SmileFactory;

import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * Helper class used to encapsulate details of configuring an
 * {@link ObjectMapper} instance to be used for data binding, as
 * well as accessing it.
 */
public class SmileMapperConfigurator
    extends MapperConfiguratorBase<SmileMapperConfigurator, ObjectMapper>
{
    /*
    /**********************************************************************
    /* Construction
    /**********************************************************************
     */
    
    public SmileMapperConfigurator(ObjectMapper mapper, Annotations[] defAnnotations)
    {
        super(mapper, defAnnotations);
    }

    /*
    /**********************************************************************
    /* Abstract method impls
    /**********************************************************************
     */

    @Override
    protected MapperBuilder<?,?> mapperBuilder() {
        return ObjectMapper.builder(new SmileFactory());
    }

    @Override
    protected AnnotationIntrospector _jaxbIntrospector() {
        return JaxbHolder.get();
    }

    // Silly class to encapsulate reference to JAXB introspector class so that
    // loading of parent class does not require it; only happens if and when
    // introspector needed
    private static class JaxbHolder {
        public static AnnotationIntrospector get() {
            return new JaxbAnnotationIntrospector();
        }
    }
}
