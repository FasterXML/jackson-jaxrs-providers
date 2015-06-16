package com.fasterxml.jackson.jaxrs.yaml.dw;

import com.fasterxml.jackson.jaxrs.yaml.JacksonYAMLProvider;
import com.fasterxml.jackson.jaxrs.yaml.JaxrsTestBase;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Intermediate base for tests that run actual full JAX-RS resource.
 */
public abstract class ResourceTestBase extends JaxrsTestBase
{
    protected static abstract class YAMLApplication extends Application
    {
        protected final Object _provider;
        protected final Object _resource;

        protected YAMLApplication(Object provider, Object resource) {
            _provider = provider;
            _resource = resource;
        }
        
        @Override
        public Set<Object> getSingletons() {
            HashSet<Object> singletons = new HashSet<Object>();
            singletons.add(_provider);
            singletons.add(_resource);
            return singletons;
        }
    }

    protected static abstract class YAMLApplicationWithJackson extends YAMLApplication
    {
        public YAMLApplicationWithJackson(Object resource) {
            super(new JacksonYAMLProvider(), resource);
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
