package com.fasterxml.jackson.jaxrs.xml.dw;

import java.io.*;
import java.net.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Server;
import org.junit.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sun.jersey.core.header.MediaTypes;

public abstract class SimpleEndpointTestBase extends ResourceTestBase
{
    final static int TEST_PORT = 6011;
    
    static class Point {
        public int x, y;

        protected Point() { }
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    @Path("/point")
    public static class SimpleResource
    {
        @GET
        @Produces({ MediaType.APPLICATION_XML })
        public Point getPoint() {
            return new Point(1, 2);
        }
    }

    public static class SimpleResourceApp extends XMLApplicationWithJackson {
        public SimpleResourceApp() { super(new SimpleResource()); }
    }

    private final static byte[] UNTOUCHABLE_RESPONSE = new byte[] { 1, 2, 3, 4 };

    @Path("/raw")
    public static class RawResource
    {
        @GET
        @Path("bytes")
        @Produces({ MediaType.APPLICATION_XML })
        public byte[] getBytes() throws IOException {
            return UNTOUCHABLE_RESPONSE;
        }
    }

    public static class SimpleRawApp extends XMLApplicationWithJackson {
        public SimpleRawApp() { super(new RawResource()); }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testStandardSmile() throws Exception
    {
        final ObjectMapper mapper = new XmlMapper();
        Server server = startServer(TEST_PORT, SimpleResourceApp.class);
        Point p;

        try {
            InputStream in = new URL("http://localhost:"+TEST_PORT+"/point").openStream();
            p = mapper.readValue(in, Point.class);
            in.close();
        } finally {
            server.stop();
        }
        // ensure we got a valid Point
        assertNotNull(p);
        assertEquals(1, p.x);
        assertEquals(2, p.y);
    }

    // [Issue#34] Verify that Untouchables act the way as they should
    @SuppressWarnings("resource")
    public void testUntouchables() throws Exception
    {
        Server server = startServer(TEST_PORT, SimpleRawApp.class);
        try {
            InputStream in = new URL("http://localhost:"+TEST_PORT+"/raw/bytes").openStream();
            Assert.assertArrayEquals(UNTOUCHABLE_RESPONSE, readAll(in));
        } finally {
            server.stop();
        }
    }
}
