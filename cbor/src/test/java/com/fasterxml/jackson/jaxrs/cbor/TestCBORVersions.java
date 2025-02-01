package com.fasterxml.jackson.jaxrs.cbor;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;

import static org.junit.jupiter.api.Assertions.*;

public class TestCBORVersions extends JaxrsTestBase
{
    @Test
    public void testMapperVersions()
    {
        assertVersion(new JacksonCBORProvider());
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    private void assertVersion(Versioned vers)
    {
        final Version v = vers.version();
        assertFalse(v.isUnknownVersion(), "Should find version information (got "+v+")");
        Version exp = PackageVersion.VERSION;
        assertEquals(exp.toFullString(), v.toFullString());
        assertEquals(exp, v);
    }
}

