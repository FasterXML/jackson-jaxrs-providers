package com.fasterxml.jackson.jaxrs.smile;

import java.io.*;
import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test to check [JACKSON-540]
 */
public class TestCanDeserialize extends JaxrsTestBase
{
    static class Bean {
        public int x;
    }
    
    /*
    public void testCanSerialize() throws IOException
    {
        JacksonSmileProvider prov = new JacksonSmileProvider();
        byte[] smile = ...;
        Bean b = (Bean) prov.readFrom(Object.class, Bean.class, new Annotation[0],
                MediaType.APPLICATION_XML_TYPE, null,
                new ByteArrayInputStream(smile);
        assertNotNull(b);
        assertEquals(3, b.x);
    }
    */

    // [Issue#1]: exception for no content
    @Test
    public void testCanSerializeEmpty() throws IOException
    {
        JacksonSmileProvider prov = new JacksonSmileProvider();
        Bean b = (Bean) prov.readFrom(Object.class, Bean.class, new Annotation[0],
                MediaType.APPLICATION_XML_TYPE, null, new ByteArrayInputStream(new byte[0]));
        assertNull(b);
    }
}
