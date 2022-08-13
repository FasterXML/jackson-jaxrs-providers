package tools.jackson.jaxrs.smile;

import tools.jackson.core.Version;
import tools.jackson.core.Versioned;

public class TestSmileVersions extends JaxrsTestBase
{
    public void testMapperVersions()
    {
        assertVersion(new JacksonSmileProvider());
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    private void assertVersion(Versioned vers)
    {
        final Version v = vers.version();
        assertFalse("Should find version information (got "+v+")", v.isUnknownVersion());
        Version exp = PackageVersion.VERSION;
        assertEquals(exp.toFullString(), v.toFullString());
        assertEquals(exp, v);
    }
}

