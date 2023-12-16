package tools.jackson.jaxrs.xml;

import tools.jackson.databind.*;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.jaxrs.cfg.MapperConfiguratorBase;

/**
 * Helper class used to encapsulate details of configuring an
 * {@link XmlMapper} instance to be used for data binding, as
 * well as accessing it.
 */
public class XMLMapperConfigurator
    extends MapperConfiguratorBase<XMLMapperConfigurator, XmlMapper>
{
    public XMLMapperConfigurator(XmlMapper mapper, AnnotationIntrospector aiOverride) {
        super(mapper, aiOverride);
    }

    /*
    /**********************************************************************
    /* Abstract method impls
    /**********************************************************************
     */

    @Override
    protected MapperBuilder<?,?> mapperBuilder() {
        return XmlMapper.builder();
    }
}