package com.fasterxml.jackson.jaxrs.xml;

import java.util.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;

/**
 * Unit tests for verifying that certain JDK base types will be
 * ignored by default Jackson JAX-RS conversion provider.
 */
public class TestUntouchables
    extends JaxrsTestBase
{
    /**
     * Test type added for [JACKSON-460]... just to ensure that "isXMLType"
     * remains overridable.
     */
    public static class MyJacksonProvider extends JacksonXMLProvider {
         // ensure isJsonType remains "protected" � this is a compile-time check.
         // Some users of JacksonJsonProvider override this method;
         // changing to "private" would regress them.
         @Override
         protected boolean isXMLType(MediaType mediaType) { return super.isXMLType(mediaType); }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */
    
    public void testDefaultUntouchables() throws Exception
    {
        JacksonXMLProvider prov = new JacksonXMLProvider();
        // By default, no reason to exclude, say, this test class...
        assertTrue(prov.isReadable(getClass(), getClass(), null, null));
        assertTrue(prov.isWriteable(getClass(), getClass(), null, null));

        // but some types should be ignored (set of ignorable may change over time tho!)
        assertFalse(prov.isWriteable(StreamingOutput.class, StreamingOutput.class, null, null));

        // and then on-the-fence things (see [Issue-1])
        assertFalse(prov.isReadable(String.class, getClass(), null, null));
        assertFalse(prov.isReadable(byte[].class, getClass(), null, null));
    }

    public void testCustomUntouchables() throws Exception
    {
        JacksonXMLProvider prov = new JacksonXMLProvider();        
        // can mark this as ignorable...
        prov.addUntouchable(getClass());
        // and then it shouldn't be processable
        assertFalse(prov.isReadable(getClass(), getClass(), null, null));
        assertFalse(prov.isWriteable(getClass(), getClass(), null, null));

        // Same for interfaces, like:
        prov.addUntouchable(Collection.class);
        assertFalse(prov.isReadable(ArrayList.class, ArrayList.class, null, null));
        assertFalse(prov.isWriteable(HashSet.class, HashSet.class, null, null));
    }
}
    