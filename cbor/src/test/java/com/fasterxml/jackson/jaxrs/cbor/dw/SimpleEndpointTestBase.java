package com.fasterxml.jackson.jaxrs.cbor.dw;

import java.io.*;
import java.net.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.jetty.server.Server;
import org.junit.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.jaxrs.cbor.CBORMediaTypes;

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
        @Produces({ CBORMediaTypes.APPLICATION_JACKSON_CBOR })
        public Point getPoint() {
            return new Point(1, 2);
        }

        @GET
        @Path("/custom")
        @Produces({ "application/vnd.com.example.v1+cbor" })
        public Point getPointCustom() {
            return new Point(1, 2);
        }
    }

    public static class SimpleResourceApp extends CBORApplicationWithJackson {
        public SimpleResourceApp() { super(new SimpleResource()); }
    }

    private final static byte[] UNTOUCHABLE_RESPONSE = new byte[] { 1, 2, 3, 4 };

    @Path("/raw")
    public static class RawResource
    {
        @GET
        @Path("bytes")
        @Produces({ CBORMediaTypes.APPLICATION_JACKSON_CBOR })
        public byte[] getBytes() throws IOException {
            return UNTOUCHABLE_RESPONSE;
        }
    }

    public static class SimpleRawApp extends CBORApplicationWithJackson {
        public SimpleRawApp() { super(new RawResource()); }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testSimpleObject() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper(new CBORFactory());
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

    public void testCustomMediaTypeWithCborExtension() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper(new CBORFactory());
        Server server = startServer(TEST_PORT, SimpleResourceApp.class);
        Point p;

        try {
            final URL url = new URL("http://localhost:" + TEST_PORT + "/point/custom");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/vnd.com.example.v1+cbor");
            InputStream in = conn.getInputStream();
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
