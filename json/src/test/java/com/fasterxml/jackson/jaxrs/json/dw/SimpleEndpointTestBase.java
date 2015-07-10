package com.fasterxml.jackson.jaxrs.json.dw;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.eclipse.jetty.server.Server;
import org.junit.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class SimpleEndpointTestBase extends ResourceTestBase
{
    final protected static int TEST_PORT = 6011;
    
    static protected class Point {
        public int x, y;

        protected Point() { }
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static protected class ExtendedPoint extends Point {
        public int z;

        protected ExtendedPoint() { }
        public ExtendedPoint(int x, int y, int z) {
            super(x, y);
            this.z = z;
        }
    }

    @Path("/point")
    public static class SimpleResource
    {
        @Path("/javascript")
        @GET
        @Produces({ MediaType.APPLICATION_JSON, "application/javascript" })
        public Point getPointJS() {
            return new Point(1, 2);
        }

        @Path("/jsonx")
        @GET
        @Produces({ MediaType.APPLICATION_JSON, "text/x-json" })
        public Point getPointJSONX() {
            return new Point(1, 2);
        }

        @Path("/max")
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Point maxPoint(MappingIterator<Point> points) throws IOException
        {
            Point max = null;
            int maxDist = 0;
            while (points.hasNextValue()) {
                Point p = points.nextValue();
                int dist = _distance(p);
                if (max == null || (dist > maxDist)) {
                    maxDist = dist;
                    max = p;
                }
            }
            return max;
        }

        private int _distance(Point p) {
            return (p.x * p.x) + (p.y * p.y);
        }
    }

    public static class SimpleResourceApp extends JsonApplicationWithJackson {
        public SimpleResourceApp() { super(new SimpleResource()); }
    }

    private final static String UNTOUCHABLE_RESPONSE = "[1]";

    @Path("/raw")
    public static class RawResource
    {
        @GET
        @Produces({ MediaType.APPLICATION_JSON, "application/javascript" })
        @Path("string")
        public String getString() {
            return UNTOUCHABLE_RESPONSE;
        }

        @GET
        @Path("bytes")
        @Produces({ MediaType.APPLICATION_JSON, "application/javascript" })
        public byte[] getBytes() throws IOException {
            return UNTOUCHABLE_RESPONSE.getBytes("UTF-8");
        }
    }

    @Path("/fluff")
    public static class FluffyResource
    {
        @GET
        @Produces({ MediaType.APPLICATION_OCTET_STREAM })
        @Path("bytes")
        public StreamingOutput getFluff(@QueryParam("size") final long size) {
            if (size <= 0L) {
                throw new IllegalArgumentException("Missing 'size'");
            }
            return new StreamingOutput() {
                @Override
                public void write(OutputStream output) throws IOException {
                    byte[] buf = new byte[1024];
                    for (int i = 0, end = buf.length; i < end; ++i) {
                        buf[i] = (byte) i;
                    }
                    long left = size;

                    while (left > 0) {
                        int len;
                        if (left >= buf.length) {
                            len = buf.length;
                        } else {
                            len = (int) left;
                        }
                        output.write(buf, 0, len);
                        left -= len;
                    }
                }
            };
        }
    }

    @Path("/dynamic")
    public static class DynamicTypingResource
    {
        @GET
        @Path("single")
        @Produces(MediaType.APPLICATION_JSON)
        public Point getPoint() {
            return new ExtendedPoint(1, 2, 3);
        }

        @GET
        @Path("list")
        @Produces(MediaType.APPLICATION_JSON)
        public List<Point> getPoints() {
            return Arrays.asList(getPoint());
        }
    }

    public static class SimpleRawApp extends JsonApplicationWithJackson {
        public SimpleRawApp() { super(new RawResource()); }
    }

    public static class SimpleFluffyApp extends JsonApplicationWithJackson {
        public SimpleFluffyApp() { super(new FluffyResource()); }
    }

    public static class SimpleDynamicTypingApp extends JsonApplicationWithJackson {
        public SimpleDynamicTypingApp() { super(new DynamicTypingResource()); }
    }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testStandardJson() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        Server server = startServer(TEST_PORT, SimpleResourceApp.class);
        Point p;

        try {
            InputStream in = new URL("http://localhost:"+TEST_PORT+"/point/javascript").openStream();
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

    public void testAcceptJavascriptType() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        Server server = startServer(TEST_PORT, SimpleResourceApp.class);
        URL urlJS = new URL("http://localhost:"+TEST_PORT+"/point/javascript");
        URL urlJsonX = new URL("http://localhost:"+TEST_PORT+"/point/jsonx");

        try {
            HttpURLConnection conn = (HttpURLConnection) urlJS.openConnection();

            // First: verify that weird types are not supported...
            conn.setRequestProperty("Accept", "foo/bar");
            conn.connect();
            assertEquals(HttpURLConnection.HTTP_NOT_ACCEPTABLE, conn.getResponseCode());
            conn.disconnect();

            // try again with somewhat non-standard, but supported JSON-like type (application/javascript)
            conn = (HttpURLConnection) urlJS.openConnection();
            conn.setRequestProperty("Accept", "application/javascript");
            assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            InputStream in = conn.getInputStream();
            Point p;
            try {
                p = mapper.readValue(in, Point.class);
            } finally {
                in.close();
            }
            assertNotNull(p);
            assertEquals(1, p.x);
            assertEquals(2, p.y);
            conn.disconnect();

            // [Issue#40]: another oddball type to consider
            conn = (HttpURLConnection) urlJsonX.openConnection();
            conn.setRequestProperty("Accept", "text/x-json");
            assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            in = conn.getInputStream();
            p = null;
            try {
                p = mapper.readValue(in, Point.class);
            } finally {
                in.close();
            }
            assertNotNull(p);
            assertEquals(1, p.x);
            assertEquals(2, p.y);
            conn.disconnect();
        } finally {
            server.stop();
        }
    }

    /*
        @Path("/max")
        @POST
        @Produces(MediaType.APPLICATION_JSON)
        public Point maxPoint(MappingIterator<Point> points) throws IOException
        {
     */
    
    // [jaxrs-providers#69]
    public void testMappingIterator() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        Server server = startServer(TEST_PORT, SimpleResourceApp.class);
        Point p;

        try {
            URL url = new URL("http://localhost:"+TEST_PORT+"/point/max");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
            conn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            OutputStream out = conn.getOutputStream();
            out.write(aposToQuotes("{'x':1,'y':1}\n{'y':4,'x':-4}{'x':2,'y':5}"
                    ).getBytes("UTF-8"));
            out.close();
            InputStream in = conn.getInputStream();
            p = mapper.readValue(in, Point.class);
            in.close();
        } finally {
            server.stop();
        }
        // ensure we got a valid Point
        assertNotNull(p);
        assertEquals(-4, p.x);
        assertEquals(4, p.y);
    }

    // [Issue#34] Verify that Untouchables act the way as they should
    @SuppressWarnings("resource")
    public void testUntouchables() throws Exception
    {
        Server server = startServer(TEST_PORT, SimpleRawApp.class);
        try {
            InputStream in = new URL("http://localhost:"+TEST_PORT+"/raw/string").openStream();
            assertEquals(UNTOUCHABLE_RESPONSE, readUTF8(in));

            in = new URL("http://localhost:"+TEST_PORT+"/raw/bytes").openStream();
            Assert.assertArrayEquals(UNTOUCHABLE_RESPONSE.getBytes("UTF-8"), readAll(in));
        } finally {
            server.stop();
        }
    }

    /**
     * Test that exercises underlying JAX-RS container by reading/writing 500 megs of fluff;
     * but does not do actual data format content. Goal being to ensure that
     * <code>StreamingOutput</code> works as expected even if provider is registered.
     */
    public void testHugeFluffyContent() throws Exception
    {
        Server server = startServer(TEST_PORT, SimpleFluffyApp.class);
        try {
            // Let's try with 4.5 gigs, just to be sure (should run OOME if buffering; or be
            // super slow if disk-backed buffering)
            final long size = 4500 * 1024 * 1024;
            InputStream in = new URL("http://localhost:"+TEST_PORT+"/fluff/bytes?size="+size).openStream();
            byte[] stuff = new byte[64000];
            long total = 0L;
            int count;

            while ((count = in.read(stuff)) > 0) {
                // verify contents, too
                for (int i = 0; i < count; ++i) {
                    int exp = ((int) total) & 0xFF;
                    int act = stuff[i] & 0xFF;
                    if (exp != act) {
                        fail("Content differs at #"+Long.toHexString(total)+"; got 0x"+Integer.toHexString(act)
                                +", expected 0x"+Integer.toHexString(exp));
                    }
                    ++total;
                }
                
            }
            in.close();

            assertEquals(size, total);
            
        } finally {
            server.stop();
        }
    }

    public void testDynamicTypingSingle() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        Server server = startServer(TEST_PORT, SimpleDynamicTypingApp.class);
        ExtendedPoint p;

        try {
            InputStream in = new URL("http://localhost:"+TEST_PORT+"/dynamic/single").openStream();
            p = mapper.readValue(in, ExtendedPoint.class);
            in.close();
        } finally {
            server.stop();
        }
        // ensure we got a valid Point
        assertNotNull(p);
        assertEquals(1, p.x);
        assertEquals(2, p.y);
        assertEquals(3, p.z);
    }

    // for [#60], problems with non-polymorphic Lists
    public void testDynamicTypingList() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        Server server = startServer(TEST_PORT, SimpleDynamicTypingApp.class);
        List<ExtendedPoint> l;

        try {
            InputStream in = new URL("http://localhost:"+TEST_PORT+"/dynamic/list").openStream();
            l = mapper.readValue(in, new TypeReference<List<ExtendedPoint>>() { });
            in.close();
        } finally {
            server.stop();
        }
        assertNotNull(l);
        assertEquals(1, l.size());

        // ensure we got a valid Point
        ExtendedPoint p = l.get(0);
        assertEquals(1, p.x);
        assertEquals(2, p.y);
        if (p.z != 3) {
            fail("Expected p.z == 3, was "+p.z+"; most likely due to incorrect serialization using base type (issue #60)");
        }
    }
}
