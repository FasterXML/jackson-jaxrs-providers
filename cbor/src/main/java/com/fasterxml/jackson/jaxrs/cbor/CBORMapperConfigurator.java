package com.fasterxml.jackson.jaxrs.cbor;

import tools.jackson.databind.*;
import tools.jackson.databind.cfg.MapperBuilder;

import tools.jackson.dataformat.cbor.CBORFactory;
import tools.jackson.dataformat.cbor.databind.CBORMapper;

import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;

/**
 * Helper class used to encapsulate details of configuring an
 * {@link ObjectMapper} instance to be used for data binding, as
 * well as accessing it.
 */
public class CBORMapperConfigurator
    extends MapperConfiguratorBase<CBORMapperConfigurator, CBORMapper>
{
    public CBORMapperConfigurator(CBORMapper mapper,
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
        return CBORMapper.builder(new CBORFactory());
    }
}
