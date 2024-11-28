package com.fasterxml.jackson.jaxrs.yaml;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import java.lang.annotation.Annotation;
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
         // ensure isJsonType remains "protected" this is a compile-time check.
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
        assertTrue(prov.isReadable(getClass(), getClass(),
                new Annotation[0], YAMLMediaTypes.APPLICATION_JACKSON_YAML_TYPE));
        assertTrue(prov.isWriteable(getClass(), getClass(),
                new Annotation[0], YAMLMediaTypes.APPLICATION_JACKSON_YAML_TYPE));

        // but some types should be ignored (set of ignorable may change over time tho!)
        assertFalse(prov.isWriteable(StreamingOutput.class, StreamingOutput.class,
                new Annotation[0], YAMLMediaTypes.APPLICATION_JACKSON_YAML_TYPE));

        // and then on-the-fence things (see [Issue-1])
        assertFalse(prov.isReadable(String.class, getClass(),
                new Annotation[0], YAMLMediaTypes.APPLICATION_JACKSON_YAML_TYPE));
        assertFalse(prov.isReadable(byte[].class, getClass(),
                new Annotation[0], YAMLMediaTypes.APPLICATION_JACKSON_YAML_TYPE));
    }

    public void testCustomUntouchables() throws Exception
    {
        JacksonYAMLProvider prov = new JacksonYAMLProvider();
        // can mark this as ignorable...
        prov.addUntouchable(getClass());
        // and then it shouldn't be processable
        assertFalse(prov.isReadable(getClass(), getClass(),
                new Annotation[0], YAMLMediaTypes.APPLICATION_JACKSON_YAML_TYPE));
        assertFalse(prov.isWriteable(getClass(), getClass(),
                new Annotation[0], YAMLMediaTypes.APPLICATION_JACKSON_YAML_TYPE));

        // Same for interfaces, like:
        prov.addUntouchable(Collection.class);
        assertFalse(prov.isReadable(ArrayList.class, ArrayList.class,
                new Annotation[0], YAMLMediaTypes.APPLICATION_JACKSON_YAML_TYPE));
        assertFalse(prov.isWriteable(HashSet.class, HashSet.class,
                new Annotation[0], YAMLMediaTypes.APPLICATION_JACKSON_YAML_TYPE));
    }
}
