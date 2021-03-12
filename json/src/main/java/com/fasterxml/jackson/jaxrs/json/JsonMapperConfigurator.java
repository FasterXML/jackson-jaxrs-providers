package com.fasterxml.jackson.jaxrs.json;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;

/**
 * Helper class used to encapsulate details of configuring an
 * {@link ObjectMapper} instance to be used for data binding, as
 * well as accessing it.
 */
public class JsonMapperConfigurator
    extends MapperConfiguratorBase<JsonMapperConfigurator, JsonMapper>
{
    /*
    /**********************************************************************
    /* Construction
    /**********************************************************************
     */
    
    public JsonMapperConfigurator(JsonMapper mapper,
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
        return JsonMapper.builder();
    }
}
