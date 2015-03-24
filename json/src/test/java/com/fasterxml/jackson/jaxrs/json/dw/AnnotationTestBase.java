package com.fasterxml.jackson.jaxrs.json.dw;

import java.io.*;
import java.net.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Server;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

public abstract class AnnotationTestBase extends ResourceTestBase
{
    final static int TEST_PORT = 6030;

    @JsonPropertyOrder({ "x", "y", "text" })
    public static class Point {
        // Include 'x' for incoming parameter
        @JsonView(ParamView.class)
        public int x;

        // But only serialize 'y'
        @JsonView(ResultView.class)
        public int y;

        // and include 'text' for both
        public String text;
        
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
            Point result = new Point(3, 4);
            result.text = "("+input.x+","+input.y+")";
            return result;
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
    public void testInputOutputFiltering() throws Exception
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
        w.write("{\"x\":1,\"y\":2}");
        w.close();

        try {
            String json = readUTF8(conn.getInputStream());
            // Although (1,2) passed, 2 is filtered by view
            assertEquals(aposToQuotes("{'y':4,'text':'(1,0)'}"), json);
        } finally {
            server.stop();
        }
    }
}
