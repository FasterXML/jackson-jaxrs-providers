package com.fasterxml.jackson.jaxrs.smile;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;

import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.databind.SmileMapper;
import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;

/**
 * Helper class used to encapsulate details of configuring an
 * {@link ObjectMapper} instance to be used for data binding, as
 * well as accessing it.
 */
public class SmileMapperConfigurator
    extends MapperConfiguratorBase<SmileMapperConfigurator, SmileMapper>
{
    public SmileMapperConfigurator(SmileMapper mapper,
            AnnotationIntrospector aiOverride)
    {
        super(mapper, aiOverride);
    }

    /*
    /**********************************************************************
    /* Abstract method impls
    /**********************************************************************
     */

    @Override
    protected MapperBuilder<?,?> mapperBuilder() {
        return SmileMapper.builder(new SmileFactory());
    }
}
