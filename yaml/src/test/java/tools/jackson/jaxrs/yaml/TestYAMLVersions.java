package tools.jackson.jaxrs.yaml;

import tools.jackson.core.Version;
import tools.jackson.core.Versioned;
import tools.jackson.jaxrs.yaml.JacksonYAMLProvider;

public class TestYAMLVersions extends JaxrsTestBase
{
    public void testMapperVersions()
    {
        assertVersion(new JacksonYAMLProvider());
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
