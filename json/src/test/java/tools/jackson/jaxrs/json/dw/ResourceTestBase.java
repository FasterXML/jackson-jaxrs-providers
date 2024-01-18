package tools.jackson.jaxrs.json.dw;

import java.io.*;
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

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import tools.jackson.jaxrs.json.JacksonJsonProvider;
import tools.jackson.jaxrs.json.JaxrsTestBase;

/**
 * Intermediate base for tests that run actual full JAX-RS resource.
 */
public abstract class ResourceTestBase extends JaxrsTestBase
{
    protected static abstract class JsonApplication extends Application
    {
        protected final Object _jsonProvider;
        protected final Object _resource;

        protected JsonApplication(Object jsonProvider, Object resource) {
            _jsonProvider = jsonProvider;
            _resource = resource;
        }
        
        @Override
        public Set<Object> getSingletons() {
            HashSet<Object> singletons = new HashSet<Object>();
            singletons.add(_jsonProvider);
            singletons.add(_resource);
            return singletons;
        }
    }

    protected static abstract class JsonApplicationWithJackson extends JsonApplication
    {
        public JsonApplicationWithJackson(Object resource) {
            super(new JacksonJsonProvider(createMapper()), resource);
        }

        static JsonMapper createMapper() {
            // 17-Jan-2024, tatu: Need to configure slightly to change Jackson 3.0
            //   defaults wrt:
            //
            //   - View handling (not to fail on properties missing from view)

            return JsonMapper.builder()
                    .disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES)
                    .build();
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
    
    /*
    /**********************************************************
    /* Other helper methods
    /**********************************************************
     */
    
    protected String a2q(String json) {
        return json.replace("'", "\"");
    }
    
    protected String readUTF8(InputStream in) throws IOException
    {
        return new String(readAll(in), "UTF-8");
    }
    
    protected byte[] readAll(InputStream in) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(100);
        byte[] buffer = new byte[500];
        int count;

        while ((count = in.read(buffer)) > 0) {
            bytes.write(buffer, 0, count);
        }
        in.close();
        return bytes.toByteArray();
    }
}
