package com.fasterxml.jackson.jaxrs.xml;

import java.io.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.DefaultTyping;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Test for some aspects of default (polymorphic) type handling.
 *<p>
 * NOTE! For XML more limitations apply, so test differs from others a bit.
 */
public class TestCanSerialize extends JaxrsTestBase
{
    static class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
    
        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    public void testCanSerialize() throws IOException
    {
        ObjectMapper mapper = XmlMapper.builder()
                .activateDefaultTyping(new NoCheckSubTypeValidator(),
                        DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT)
                .build();
    
        // construct test object
        Simple s = new Simple();
        s.list = new String[] { "foo", "bar" };

        // but with problem of [JACKSON-540], we get nasty surprise here...
        String doc = mapper.writeValueAsString(s);
        Simple result = mapper.readValue(doc, Simple.class);
        assertNotNull(result.list);
        assertEquals(2, result.list.length);
        assertEquals("bar", result.list[1]);
    }
}

// Important: until a bug in XML handler fixed, can't use inner classes for type ids
class Simple {
    public String[] list;
}

