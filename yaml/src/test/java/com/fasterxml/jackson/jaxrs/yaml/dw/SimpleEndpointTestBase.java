package com.fasterxml.jackson.jaxrs.yaml.dw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
        @Produces({ "application/yaml" })
        public Point getPoint() {
            return new Point(1, 2);
        }
    }

    public static class SimpleResourceApp extends YAMLApplicationWithJackson {
        public SimpleResourceApp() { super(new SimpleResource()); }
    }

    private final static byte[] UNTOUCHABLE_RESPONSE = new byte[] { 1, 2, 3, 4 };

    @Path("/raw")
    public static class RawResource
    {
        @GET
        @Path("bytes")
        @Produces({ "application/yaml" })
        public byte[] getBytes() throws IOException {
            return UNTOUCHABLE_RESPONSE;
        }
    }

    public static class SimpleRawApp extends YAMLApplicationWithJackson {
        public SimpleRawApp() { super(new RawResource()); }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testSimplePoint() throws Exception
    {
        final ObjectMapper mapper = new YAMLMapper();
        Server server = startServer(TEST_PORT, SimpleResourceApp.class);
        Point p;
        String yaml = null;

        try {
            URL url = new URL("http://localhost:" + TEST_PORT + "/point");
            InputStream in = url.openStream();
            byte[] bytes = readAll(in);
            in.close();
            yaml = new String(bytes, "UTF-8");
            p = mapper.readValue(yaml, Point.class);
        } finally {
            server.stop();
        }
        // ensure we got a valid Point
        assertNotNull(p);
        assertEquals(1, p.x);
        assertEquals(2, p.y);

        if (!yaml.contains("x: 1") || !yaml.contains("y: 2")) {
            fail("Expected Point to be serialized as YAML, instead got: "+yaml);
        }
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
