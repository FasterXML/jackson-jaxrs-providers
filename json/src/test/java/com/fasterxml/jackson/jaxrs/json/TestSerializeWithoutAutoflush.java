package com.fasterxml.jackson.jaxrs.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Unit test to check that ProviderBase always writes its content, even if flush-after-write is off.
 */
public class TestSerializeWithoutAutoflush extends JaxrsTestBase
{
    static class Simple {
        protected List<String> list;

        public List<String> getList( ) { return list; }
        public void setList(List<String> l) { list = l; }
    }

    public void testCanSerialize() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        mapper.disable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);

        JacksonJsonProvider provider = new JacksonJsonProvider(mapper);

        // construct test object
        List<String> l = new ArrayList<String>();
        l.add("foo");
        l.add("bar");

        Simple s = new Simple();
        s.setList(l);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        provider.writeTo(s, Simple.class, Simple.class, new Annotation[0],
                MediaType.APPLICATION_JSON_TYPE, null, stream);

        Simple result = mapper.readValue(stream.toByteArray(), Simple.class);
        assertNotNull(result.list);
        assertEquals(2, result.list.size());
    }
}
