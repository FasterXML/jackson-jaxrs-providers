package com.fasterxml.jackson.jaxrs.json;

import java.io.*;
import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

public class TestStreamingOutput extends JaxrsTestBase
{
    static class StreamingSubtype implements StreamingOutput
    {
        // important: this can trick "canSerialize()" to include it:
        public int getFoo() { return 3; }

        @Override
        public void write(OutputStream out) throws IOException {
            out.write("OK".getBytes("UTF-8"));
        }
    }
    
    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    public void testSimpleSubtype() throws Exception
    {
        JacksonJsonProvider prov = new JacksonJsonProvider();
        assertFalse(prov.isWriteable(StreamingSubtype.class, StreamingSubtype.class,
                new Annotation[] { }, MediaType.APPLICATION_JSON_TYPE));
    }
}
