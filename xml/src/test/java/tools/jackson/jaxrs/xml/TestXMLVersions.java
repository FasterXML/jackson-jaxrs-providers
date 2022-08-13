package tools.jackson.jaxrs.xml;

import tools.jackson.core.Version;
import tools.jackson.core.Versioned;
import tools.jackson.jaxrs.xml.JacksonXMLProvider;

public class TestXMLVersions extends JaxrsTestBase
{
    public void testMapperVersions()
    {
        assertVersion(new JacksonXMLProvider());
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

