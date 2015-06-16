package com.fasterxml.jackson.jaxrs.yaml;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;

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
        assertFalse("Should find version information (got "+v+")", v.isUknownVersion());
        Version exp = PackageVersion.VERSION;
        assertEquals(exp.toFullString(), v.toFullString());
        assertEquals(exp, v);
    }
}

