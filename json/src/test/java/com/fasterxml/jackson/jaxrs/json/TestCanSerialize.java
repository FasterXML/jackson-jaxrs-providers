package com.fasterxml.jackson.jaxrs.json;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DefaultTyping;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit test to check [JACKSON-540]
 */
public class TestCanSerialize extends JaxrsTestBase
{
    static class Simple {
        protected List<String> list;

        public List<String> getList( ) { return list; }
        public void setList(List<String> l) { list = l; }
    }

    public void testCanSerialize() throws IOException
    {
        ObjectMapper mapper = ObjectMapper.builder()
                .enableDefaultTyping(DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY)
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
