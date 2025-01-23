package tools.jackson.jaxrs.smile;

import tools.jackson.databind.*;
import tools.jackson.databind.cfg.MapperBuilder;

import tools.jackson.dataformat.smile.SmileFactory;
import tools.jackson.dataformat.smile.SmileMapper;

import tools.jackson.jaxrs.cfg.MapperConfiguratorBase;

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
