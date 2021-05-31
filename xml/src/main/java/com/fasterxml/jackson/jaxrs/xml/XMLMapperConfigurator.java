package com.fasterxml.jackson.jaxrs.xml;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;

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
