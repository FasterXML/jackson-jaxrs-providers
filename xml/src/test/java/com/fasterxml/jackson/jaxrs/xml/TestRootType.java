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
        ArrayList<Bean> list = new ArrayList<Bean>();
        list.add(new Bean());
        list.add(new Bean());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MediaType mt = MediaType.APPLICATION_JSON_TYPE;
        prov.writeTo(list, List.class, ref.getType(), new Annotation[0], mt, null, out);

        String xml = out.toString("UTF-8");
        /* 09-Oct-2013, tatu: With 2.2, this produced "unwrapped" output; but
         *   with 2.3 it should use same defaults as XML module. So 'wrappers'
         *   are used for Collections, unless explicitly disabled.
         */
        assertEquals("<List><item><bean><a>3</a></bean></item>"
                +"<item><bean><a>3</a></bean></item></List>", xml);
    }
}
