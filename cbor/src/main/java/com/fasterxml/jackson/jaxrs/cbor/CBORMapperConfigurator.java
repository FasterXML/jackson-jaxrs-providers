package com.fasterxml.jackson.jaxrs.cbor;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;

import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;

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
