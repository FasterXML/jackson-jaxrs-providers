package com.fasterxml.jackson.jaxrs.smile.dw;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.ws.rs.core.Application;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.fasterxml.jackson.jaxrs.smile.JacksonSmileProvider;
import com.fasterxml.jackson.jaxrs.smile.JaxrsTestBase;

/**
 * Intermediate base for tests that run actual full JAX-RS resource.
 */
public abstract class ResourceTestBase extends JaxrsTestBase
{
    protected static abstract class SmileApplication extends Application
    {
        protected final Object _smileProvider;
        protected final Object _resource;

        protected SmileApplication(Object smileProvider, Object resource) {
            _smileProvider = smileProvider;
            _resource = resource;
        }
        
        @Override
        public Set<Object> getSingletons() {
            HashSet<Object> singletons = new HashSet<Object>();
            singletons.add(_smileProvider);
            singletons.add(_resource);
            return singletons;
        }
    }

    protected static abstract class SmileApplicationWithJackson extends SmileApplication
    {
        public SmileApplicationWithJackson(Object resource) {
            super(new JacksonSmileProvider(), resource);
        }
    }
    
    /*
    /**********************************************************
    /* Abstract and overridable config methods
    /**********************************************************
     */

    protected abstract Class<? extends Servlet> servletContainerClass();

    /*
    /**********************************************************
    /* Starting actual JAX-RS container
    /**********************************************************
     */
    
    protected Server startServer(int port, Class<? extends Application> appClass) {
        return startServer(port, appClass, null);
    }
    
    protected Server startServer(int port, Class<? extends Application> appClass,
            Class<? extends Filter> filterClass)
    {
        Server server = new Server(port);
        final ContextHandlerCollection contexts = new ContextHandlerCollection();
        server.setHandler(contexts);
        ServletHolder jaxrs = new ServletHolder(servletContainerClass());
        jaxrs.setInitParameter("javax.ws.rs.Application", appClass.getName());
        final ServletContextHandler mainHandler = new ServletContextHandler(contexts, "/", true, false);
        mainHandler.addServlet(jaxrs, "/*");

        if (filterClass != null) {
            mainHandler.addFilter(filterClass, "/*", java.util.EnumSet.allOf(DispatcherType.class));
        }
        
        server.setHandler(mainHandler);
        try {
            server.start();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return server;
    }
}
