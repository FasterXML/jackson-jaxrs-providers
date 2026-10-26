package tools.jackson.jaxrs.yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.jupiter.api.Test;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.jaxrs.cfg.JaxRSFeature;

import static org.junit.jupiter.api.Assertions.*;

public class TestCanDeserialize extends JaxrsTestBase
{

    static class Bean {
        public int x;
    }
    
    @Test
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

    @Test
    public void testCanDeserializeEmpty() throws IOException
    {
        JacksonYAMLProvider prov = new JacksonYAMLProvider();
        Bean b = (Bean) prov.readFrom(Object.class, Bean.class, new Annotation[0],
                JaxrsTestBase.YAML_MEDIA_TYPE, null, new ByteArrayInputStream(new byte[0]));
        assertNull(b);
    }

    // [jaxrs-providers#234]: multi-document stream of mappings is not affected
    // by root-level Array unwrapping
    @Test
    public void testMappingIteratorMultiDocBeans() throws IOException
    {
        MappingIterator<Bean> it = _readIterator(new JacksonYAMLProvider(),
                new TypeReference<MappingIterator<Bean>>() { }, "x: 1\n---\nx: 2\n");
        assertTrue(it.hasNextValue());
        assertEquals(1, it.nextValue().x);
        assertTrue(it.hasNextValue());
        assertEquals(2, it.nextValue().x);
        assertFalse(it.hasNextValue());
    }

    // [jaxrs-providers#234]: root-level sequence to be unwrapped for `MappingIterator`
    @Test
    public void testMappingIteratorSequence() throws IOException
    {
        MappingIterator<String> it = _readIterator(new JacksonYAMLProvider(),
                new TypeReference<MappingIterator<String>>() { }, "- a\n- b\n");
        assertEquals(List.of("a", "b"), it.readAll());
    }

    // [jaxrs-providers#234]: but this means that multi-document stream of sequences
    // requires disabling of unwrapping (same as with `YAMLMapper.readValues()`)
    @Test
    public void testMappingIteratorMultiDocSequences() throws IOException
    {
        final String YAML = "- a\n- b\n---\n- c\n";
        final TypeReference<MappingIterator<List<String>>> type =
                new TypeReference<MappingIterator<List<String>>>() { };

        MappingIterator<List<String>> it = _readIterator(new JacksonYAMLProvider(), type, YAML);
        assertThrows(MismatchedInputException.class, () -> it.readAll());

        JacksonYAMLProvider prov = new JacksonYAMLProvider()
                .disable(JaxRSFeature.UNWRAP_ROOT_ARRAY_FOR_ITERATOR);
        assertEquals(List.of(List.of("a", "b"), List.of("c")),
                _readIterator(prov, type, YAML).readAll());
    }

    @SuppressWarnings("unchecked")
    private <T> MappingIterator<T> _readIterator(JacksonYAMLProvider prov,
            TypeReference<MappingIterator<T>> type, String yaml)
        throws IOException
    {
        return (MappingIterator<T>) prov.readFrom(
                (Class<Object>) (Class<?>) MappingIterator.class, type.getType(), new Annotation[0],
                JaxrsTestBase.YAML_MEDIA_TYPE, null, new ByteArrayInputStream(yaml.getBytes()));
    }
}
