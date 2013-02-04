package com.fasterxml.jackson.jaxrs.xml;

import java.io.*;
import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;

/**
 * Unit test to check [JACKSON-540]
 */
public class TestCanDeserialize extends JaxrsTestBase
{
    static class Bean {
        public int x;
    }
    
    public void testCanSerialize() throws IOException
    {
        JacksonXMLProvider prov = new JacksonXMLProvider();
        String XML = "<Bean><x>3</x></Bean>";
        InputStream stream = new ByteArrayInputStream(XML.getBytes());
        Bean b = (Bean) prov.readFrom(Object.class, Bean.class, new Annotation[0],
                MediaType.APPLICATION_XML_TYPE, null, stream);
        assertNotNull(b);
        assertEquals(3, b.x);
    }

    // [Issue#4]: exception for no content
    public void testCanSerializeEmpty() throws IOException
    {
        JacksonXMLProvider prov = new JacksonXMLProvider();
        Bean b = (Bean) prov.readFrom(Object.class, Bean.class, new Annotation[0],
                MediaType.APPLICATION_XML_TYPE, null, new ByteArrayInputStream(new byte[0]));
        assertNull(b);
    }
}
