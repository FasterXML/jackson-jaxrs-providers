package tools.jackson.jaxrs.yaml;

import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.cfg.MapperBuilder;

import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.jaxrs.cfg.MapperConfiguratorBase;

/**
 * Helper class used to encapsulate details of configuring an
 * {@link YAMLMapper} instance to be used for data binding, as
 * well as accessing it.
 */
public class YAMLMapperConfigurator
        extends MapperConfiguratorBase<YAMLMapperConfigurator, YAMLMapper>
{
    public YAMLMapperConfigurator(YAMLMapper mapper, AnnotationIntrospector aiOverride) {
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
