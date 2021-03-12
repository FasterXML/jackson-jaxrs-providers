package com.fasterxml.jackson.jaxrs.yaml;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;

/**
 * Helper class used to encapsulate details of configuring an
 * {@link YAMLMapper} instance to be used for data binding, as
 * well as accessing it.
 */
public class YAMLMapperConfigurator
        extends MapperConfiguratorBase<YAMLMapperConfigurator, YAMLMapper>
{
    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */

    public YAMLMapperConfigurator(YAMLMapper mapper,
            AnnotationIntrospector aiOverride) {
        super(mapper, aiOverride);
    }

    /*
    /**********************************************************************
    /* Abstract method impls
    /**********************************************************************
     */

    @Override
    protected MapperBuilder<?,?> mapperBuilder() {
        return YAMLMapper.builder();
    }
}
