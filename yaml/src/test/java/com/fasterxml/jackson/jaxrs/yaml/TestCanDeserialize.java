package com.fasterxml.jackson.jaxrs.yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;

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

    @Test
    public void testCanSerialize() throws IOException
    {
        JacksonYAMLProvider prov = new JacksonYAMLProvider();
        String YAML = "x: 3";
        InputStream stream = new ByteArrayInputStream(YAML.getBytes());
        Bean b = (Bean) prov.readFrom(Object.class, Bean.class, new Annotation[0],
                JaxrsTestBase.YAML_MEDIA_TYPE, null, stream);
        assertNotNull(b);
        assertEquals(3, b.x);
    }

    // [Issue#4]: exception for no content
    @Test
    public void testCanSerializeEmpty() throws IOException
    {
        JacksonYAMLProvider prov = new JacksonYAMLProvider();
        Bean b = (Bean) prov.readFrom(Object.class, Bean.class, new Annotation[0],
                JaxrsTestBase.YAML_MEDIA_TYPE, null, new ByteArrayInputStream(new byte[0]));
        assertNull(b);
    }
}
