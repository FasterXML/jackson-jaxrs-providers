package com.fasterxml.jackson.jaxrs.xml;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;

public class TestVersions extends JaxrsTestBase
{
    /**
     * Not a good to do this, but has to do, for now...
     */
    private final static int MAJOR_VERSION = 2;
    private final static int MINOR_VERSION = 1;

    // could inject using Maven filters as well...
    private final static String GROUP_ID = "com.fasterxml.jackson.jaxrs";
    private final static String ARTIFACT_ID = "jackson-jaxrs-xml-provider";
    
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
        assertFalse("Should find version information (got "+v+")", v.isUknownVersion());
        assertEquals(MAJOR_VERSION, v.getMajorVersion());
        assertEquals(MINOR_VERSION, v.getMinorVersion());
        // Check patch level initially, comment out for maint versions
//        assertEquals(0, v.getPatchLevel());
        assertEquals(GROUP_ID, v.getGroupId());
        assertEquals(ARTIFACT_ID, v.getArtifactId());
    }
}

