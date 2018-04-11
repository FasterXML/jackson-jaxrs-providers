package com.fasterxml.jackson.jaxrs.xml;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.jaxb.XmlJaxbAnnotationIntrospector;

import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;

/**
 * Helper class used to encapsulate details of configuring an
 * {@link XmlMapper} instance to be used for data binding, as
 * well as accessing it.
 */
public class XMLMapperConfigurator
    extends MapperConfiguratorBase<XMLMapperConfigurator, XmlMapper>
{
    /*
    /**********************************************************************
    /* Construction
    /**********************************************************************
     */

    public XMLMapperConfigurator(XmlMapper mapper, Annotations[] defAnnotations)
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
        return XmlMapper.builder();
    }

    @Override
    protected AnnotationIntrospector _jacksonIntrospector() {
        return new JacksonXmlAnnotationIntrospector();
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
            return new XmlJaxbAnnotationIntrospector();
        }
    }
}
