package tools.jackson.jaxrs.smile;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SequenceWriter;
import tools.jackson.dataformat.smile.SmileMapper;

import static org.junit.jupiter.api.Assertions.*;

public class TestCanDeserialize extends JaxrsTestBase
{
    static class Bean {
        public int x;

        protected Bean() { }
        Bean(int x) { this.x = x; }
    }

    private final static ObjectMapper MAPPER = new SmileMapper();
    
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

    // [jaxrs-providers#234]: root-level Array to be unwrapped for `MappingIterator`
    @Test
    public void testMappingIteratorArray() throws IOException
    {
        byte[] doc = MAPPER.writeValueAsBytes(List.of(new Bean(1), new Bean(2)));
        assertEquals(List.of(1, 2), _xValues(_readIterator(doc)));
    }

    // [jaxrs-providers#234]
    @Test
    public void testMappingIteratorEmptyArray() throws IOException
    {
        byte[] doc = MAPPER.writeValueAsBytes(List.of());
        assertEquals(List.of(), _xValues(_readIterator(doc)));
    }

    // [jaxrs-providers#234]: non-wrapped root-level sequence still read until end
    @Test
    public void testMappingIteratorRootSequence() throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (SequenceWriter w = MAPPER.writer().writeValues(bytes)) {
            w.write(new Bean(1));
            w.write(new Bean(2));
        }
        assertEquals(List.of(1, 2), _xValues(_readIterator(bytes.toByteArray())));
    }

    @SuppressWarnings("unchecked")
    private MappingIterator<Bean> _readIterator(byte[] doc) throws IOException
    {
        Type type = new TypeReference<MappingIterator<Bean>>() { }.getType();
        return (MappingIterator<Bean>) new JacksonSmileProvider().readFrom(
                (Class<Object>) (Class<?>) MappingIterator.class, type, new Annotation[0],
                MediaType.valueOf(SmileMediaTypes.APPLICATION_JACKSON_SMILE), null, new ByteArrayInputStream(doc));
    }

    private List<Integer> _xValues(MappingIterator<Bean> it) {
        return it.readAll().stream().map(b -> b.x).toList();
    }
}
