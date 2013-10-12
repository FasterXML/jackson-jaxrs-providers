package com.fasterxml.jackson.jaxrs.json.dw;

import java.io.IOException;
import java.net.*;

import javax.servlet.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.jetty.server.Server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;
import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterInjector;
import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterModifier;
import com.fasterxml.jackson.jaxrs.json.ResourceTestBase;

public class TestWriteModifications extends ResourceTestBase
{
    final static int TEST_PORT = 6021;
    
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
        @Produces(MediaType.APPLICATION_JSON)
        public Point getPoint() {
            return new Point(1, 2);
        }
    }

    public static class SimpleResourceApp extends JsonApplication {
        public SimpleResourceApp() { super(new SimpleResource()); }
    }

    public static class IndentingModifier extends ObjectWriterModifier
    {
        public static boolean doIndent = false;
        
        @Override
        public ObjectWriter modify(EndpointConfigBase<?> endpoint,
                MultivaluedMap<String, Object> httpHeaders,
                Object valueToWrite, ObjectWriter w, JsonGenerator g)
            throws IOException
        {
            if (doIndent) {
                g.useDefaultPrettyPrinter();
            }
            return w;
        }
    }

    public static class InjectingFilter implements javax.servlet.Filter
    {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException { }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response,
                FilterChain chain) throws IOException, ServletException
        {
            ObjectWriterInjector.set(new IndentingModifier());
            chain.doFilter(request, response);
        }

        @Override
        public void destroy() { }
    }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */
    
    public void testIndentation() throws Exception
    {
        // We need a filter to inject modifier that enables
        Server server = startServer(TEST_PORT, SimpleResourceApp.class,
                InjectingFilter.class);
        final URL url = new URL("http://localhost:"+TEST_PORT+"/point");

        try {
            // First, without indent:
            IndentingModifier.doIndent = false;
            String json = readUTF8(url.openStream());
            assertEquals(aposToQuotes("{'x':1,'y':2}"), json);
    
            // and then with indentation
            IndentingModifier.doIndent = true;
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            json = readUTF8(url.openStream());
            assertEquals(aposToQuotes("{\n  'x' : 1,\n  'y' : 2\n}"), json);
        } finally {
            server.stop();
        }
    }
}
