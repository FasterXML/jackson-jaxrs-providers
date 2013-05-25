package com.fasterxml.jackson.jaxrs.json.dw;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JaxrsTestBase;

public class TestSimpleEndpoint extends JaxrsTestBase
{
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
        @Produces({ MediaType.APPLICATION_JSON, "application/javascript" })
        public Point getPoint() {
            return new Point(1, 2);
        }
    }

    public static class SimpleResourceApp extends JsonApplication {
        public SimpleResourceApp() { super(new SimpleResource()); }
    }

    static abstract class JsonApplication extends Application
    {
        protected final Object _resource;

        protected JsonApplication(Object r) { _resource = r; }
        
        @Override
        public Set<Object> getSingletons() {
            HashSet<Object> singletons = new HashSet<Object>();
            singletons.add(new JacksonJsonProvider());
            singletons.add(_resource);
            return singletons;
        }
    }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */
    
    public void testStandardJson() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        Server server = startServer(6061, SimpleResourceApp.class);
        InputStream in = new URL("http://localhost:6061/point").openStream();
        Point p;

        try {
            p = mapper.readValue(in, Point.class);
        } finally {
            in.close();
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
        Server server = startServer(6062, SimpleResourceApp.class);
        URL url = new URL("http://localhost:6062/point");

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // First: verify that weird types are not supported...
            conn.setRequestProperty("Accept", "foo/bar");
            conn.connect();
            assertEquals(HttpURLConnection.HTTP_NOT_ACCEPTABLE, conn.getResponseCode());
            conn.disconnect();

            // try again with somewhat non-standard, but supported JSON-like type (application/javascript)
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/javascript");
//            conn.setRequestProperty("Accept", "application/json");
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
        } finally {
            server.stop();
        }
        
    }
    
}
