package com.fasterxml.jackson.jaxrs.json;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Unit tests for verifying that certain JDK base types will be
 * ignored by default Jackson JAX-RS conversion provider.
 */
public class TestUntouchables
    extends JaxrsTestBase
{
    /**
     * Test type added for [JACKSON-460]... just to ensure that "isJsonType"
     * remains overridable.
     */
    public static class MyJacksonJsonProvider extends JacksonJsonProvider {
         // ensure isJsonType remains "protected" � this is a compile-time check.
         // Some users of JacksonJsonProvider override this method;
         // changing to "private" would regress them.
         @Override
         protected boolean hasMatchingMediaType(MediaType mediaType) { return super.hasMatchingMediaType(mediaType); }
    }

    static class StreamingSubType implements StreamingOutput {
        @Override
        public void write(OutputStream output) { }
    }
    
    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */
    
    public void testDefaultUntouchables() throws Exception
    {
        JacksonJsonProvider prov = new JacksonJsonProvider();
        // By default, no reason to exclude, say, this test class...
        assertTrue(prov.isReadable(getClass(), getClass(),
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));
        assertTrue(prov.isWriteable(getClass(), getClass(),
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));

        // but some types should be ignored (set of ignorable may change over time tho!)
        assertFalse(prov.isWriteable(StreamingOutput.class, StreamingOutput.class,
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));
        assertFalse(prov.isWriteable(StreamingSubType.class, StreamingSubType.class,
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));

        // and then on-the-fence things
        assertFalse(prov.isReadable(String.class, getClass(),
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));
        assertFalse(prov.isReadable(byte[].class, getClass(),
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));
    }

    public void testCustomUntouchables() throws Exception
    {
        JacksonJsonProvider prov = new JacksonJsonProvider();        
        // can mark this as ignorable...
        prov.addUntouchable(getClass());
        // and then it shouldn't be processable
        assertFalse(prov.isReadable(getClass(), getClass(),
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));
        assertFalse(prov.isWriteable(getClass(), getClass(),
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));

        // Same for interfaces, like:
        prov.addUntouchable(Collection.class);
        assertFalse(prov.isReadable(ArrayList.class, ArrayList.class,
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));
        assertFalse(prov.isWriteable(HashSet.class, HashSet.class,
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));

        // But also allow removals...
        prov.removeUntouchable(Collection.class);
        assertTrue(prov.isReadable(ArrayList.class, ArrayList.class,
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));
        assertTrue(prov.isWriteable(HashSet.class, HashSet.class,
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));
        // which should even override default ones

        assertFalse(prov.isReadable(String.class, getClass(),
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));
        assertFalse(prov.isWriteable(String.class, HashSet.class,
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));
        prov.removeUntouchable(String.class);
        assertTrue(prov.isReadable(String.class, getClass(),
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));
        assertTrue(prov.isWriteable(String.class, HashSet.class,
                new Annotation[0], MediaType.APPLICATION_JSON_TYPE));
    
    }
}
    