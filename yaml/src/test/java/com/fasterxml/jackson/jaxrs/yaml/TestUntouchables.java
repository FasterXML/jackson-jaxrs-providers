package com.fasterxml.jackson.jaxrs.yaml;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;


/**
 * Unit tests for verifying that certain JDK base types will be
 * ignored by default Jackson JAX-RS conversion provider.
 */
public class TestUntouchables
    extends JaxrsTestBase
{
    /**
     * Test type added for [JACKSON-460]... just to ensure that "isYAMLType"
     * remains overridable.
     */
    public static class MyJacksonProvider extends JacksonYAMLProvider {
         // ensure isJsonType remains "protected" � this is a compile-time check.
         // Some users of JacksonJsonProvider override this method;
         // changing to "private" would regress them.
         @Override
         protected boolean hasMatchingMediaType(MediaType mediaType) { return super.hasMatchingMediaType(mediaType); }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    public void testDefaultUntouchables() throws Exception
    {
        JacksonYAMLProvider prov = new JacksonYAMLProvider();
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
        JacksonYAMLProvider prov = new JacksonYAMLProvider();
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
    