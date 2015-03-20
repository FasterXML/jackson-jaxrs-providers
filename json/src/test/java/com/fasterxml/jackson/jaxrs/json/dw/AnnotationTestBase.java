package com.fasterxml.jackson.jaxrs.json.dw;

import java.io.*;
import java.net.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Server;

import com.fasterxml.jackson.annotation.JsonView;

public abstract class AnnotationTestBase extends ResourceTestBase
{
    final static int TEST_PORT = 6030;

    public static class Point {
        public int x, y;

        protected Point() { }
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class ResultView { }
    
    static class ParamView { }

    @Path("/annotated")
    public static class Resource
    {
        @Path("view")
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @JsonView(ResultView.class)
        public Point withPoint(@JsonView(ParamView.class) Point input) {
            return new Point(1, 2);
        }
    }

    public static class ResourceApp extends JsonApplicationWithJackson {
        public ResourceApp() { super(new Resource()); }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    /**
     * Test in which writer/generator modification is handled by
     * changing state from Servlet Filter.
     */
    public void testIndentationWithFilter() throws Exception
    {
        // We need a filter to inject modifier that enables
        Server server = startServer(TEST_PORT, ResourceApp.class, null);
        final URL url = new URL("http://localhost:"+TEST_PORT+"/annotated/view");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
        conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.connect();

        OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
        w.write("{\"x\":3,\"y\":4}");
        w.close();

        try {
            String json = readUTF8(conn.getInputStream());
            assertEquals(aposToQuotes("{'x':1,'y':2}"), json);
        } finally {
            server.stop();
        }
    }
}
