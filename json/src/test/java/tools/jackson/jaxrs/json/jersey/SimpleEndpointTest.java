package tools.jackson.jaxrs.json.jersey;

import javax.servlet.Servlet;

import org.glassfish.jersey.servlet.ServletContainer;

import tools.jackson.jaxrs.json.dw.SimpleEndpointTestBase;

public class SimpleEndpointTest extends SimpleEndpointTestBase {
    @Override
    protected Class<? extends Servlet> servletContainerClass() { return ServletContainer.class; }
}
