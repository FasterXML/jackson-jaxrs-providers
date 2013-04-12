package com.fasterxml.jackson.jaxrs.json.dw;

import java.util.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Server;

import com.fasterxml.jackson.jaxrs.json.JaxrsTestBase;

public class TestSimpleEndpoint extends JaxrsTestBase
{
    static class Point {
        public int x, y;
    }

    static class SimpleResource {
        @Path("/point")
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Point getPoint() {
            return new Point();
        }
    }

    static class SimpleResourceApp extends Application
    {
        @Override
        public Set<Object> getSingletons() {
            return new HashSet<Object>(Arrays.<Object>asList(SimpleResource.class));
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
        server.stop();
    }

}
