package tools.jackson.jaxrs.yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;

import tools.jackson.jaxrs.yaml.JacksonYAMLProvider;

public class TestCanDeserialize extends JaxrsTestBase
{

    static class Bean {
        public int x;
    }
    
    public void testCanDeserialize() throws IOException
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
    public void testCanDeserializeEmpty() throws IOException
    {
        JacksonYAMLProvider prov = new JacksonYAMLProvider();
        Bean b = (Bean) prov.readFrom(Object.class, Bean.class, new Annotation[0],
                JaxrsTestBase.YAML_MEDIA_TYPE, null, new ByteArrayInputStream(new byte[0]));
        assertNull(b);
    }
}
