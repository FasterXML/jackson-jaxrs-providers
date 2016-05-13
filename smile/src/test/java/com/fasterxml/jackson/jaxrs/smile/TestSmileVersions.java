package com.fasterxml.jackson.jaxrs.smile;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;

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

