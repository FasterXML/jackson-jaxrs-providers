package com.fasterxml.jackson.jaxrs.json.dw;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */
    
    public void testStandardJson() throws Exception
    {
        /*
        TestServiceConfig config = new TestServiceConfig(9090);
        TestService svc = TestService.create(config, new SimpleResource());
        svc.start();
        svc.stop();
        */
    }

}
