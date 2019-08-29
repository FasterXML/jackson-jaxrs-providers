package com.fasterxml.jackson.jaxrs.yaml;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.DefaultTyping;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestCanSerialize extends JaxrsTestBase
{
    static class Simple {
        protected List<String> list;

        public List<String> getList( ) { return list; }
        public void setList(List<String> l) { list = l; }
    }

    static class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
    
        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }
    
    public void testCanSerialize() throws IOException
    {
        ObjectMapper mapper = YAMLMapper.builder()
                .activateDefaultTyping(new NoCheckSubTypeValidator(),
                        DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY)
                .build();
    
        // construct test object
        List<String> l = new ArrayList<String>();
        l.add("foo");
        l.add("bar");
    
        Simple s = new Simple();
        s.setList(l);

        // but with problem of [JACKSON-540], we get nasty surprise here...
        String json = mapper.writeValueAsString(s);
        
        Simple result = mapper.readValue(json, Simple.class);
        assertNotNull(result.list);
        assertEquals(2, result.list.size());
    }
}
