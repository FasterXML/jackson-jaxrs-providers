package com.fasterxml.jackson.jaxrs.json.dw;

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Server;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JaxrsTestBase;

public class TestSimpleEndpoint extends JaxrsTestBase
{
    static class Point {
        public int x, y;
    }

    @Path("/point")
    public static class SimpleResource {
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Point getPoint() {
            return new Point();
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
        Server server = startServer(6061, SimpleResourceApp.class);
        InputStream in = new URL("http://localhost:6061/point").openStream();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int i;
        
        while ((i = in.read()) >= 0) {
            bytes.write((byte) i);
        }
//        System.out.println("Bytes: "+bytes.size()+" -> "+bytes.toString("UTF-8"));
        server.stop();
    }

}
