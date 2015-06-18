package com.fasterxml.jackson.jaxrs.yaml;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

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
        JacksonYAMLProvider prov = new JacksonYAMLProvider();
        TypeReference<?> ref = new TypeReference<List<Bean>>(){};
        ArrayList<Bean> list = new ArrayList<Bean>();
        list.add(new Bean());
        list.add(new Bean());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MediaType mt = MediaType.APPLICATION_JSON_TYPE;
        prov.writeTo(list, List.class, ref.getType(), new Annotation[0], mt, null, out);

        String yaml = out.toString("UTF-8");
        // This produces !<bean> class names, is this valid?

        assertEquals("---\n" +
                "- !<bean>\n" +
                "  a: 3\n" +
                "- !<bean>\n" +
                "  a: 3\n", yaml);
    }
}
