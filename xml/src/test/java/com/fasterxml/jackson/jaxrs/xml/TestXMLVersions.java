package com.fasterxml.jackson.jaxrs.xml;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;

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

