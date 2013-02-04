package com.fasterxml.jackson.jaxrs.xml;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;

public class TestRootType
    extends JaxrsTestBase
{
    @JsonTypeInfo(use=Id.NAME, include=As.WRAPPER_OBJECT, property="type")
    @JsonTypeName("bean")
    static class Bean {
        public int a = 3;
    }
    
    public void testRootType() throws Exception
    {
        JacksonXMLProvider prov = new JacksonXMLProvider();
        TypeReference<?> ref = new TypeReference<List<Bean>>(){};

        Bean bean = new Bean();
        ArrayList<Bean> list = new ArrayList<Bean>();
        list.add(bean);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MediaType mt = MediaType.APPLICATION_JSON_TYPE;
        prov.writeTo(list, List.class, ref.getType(), new Annotation[0], mt, null, out);

        String xml = out.toString("UTF-8");
        assertEquals("<List><bean><a>3</a></bean></List>", xml);
    }
}
