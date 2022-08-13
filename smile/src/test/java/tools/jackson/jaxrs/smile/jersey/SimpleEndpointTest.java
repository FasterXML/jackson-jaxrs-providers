package tools.jackson.jaxrs.smile.jersey;

import javax.servlet.Servlet;

import org.glassfish.jersey.servlet.ServletContainer;

import tools.jackson.jaxrs.smile.dw.SimpleEndpointTestBase;

public class SimpleEndpointTest extends SimpleEndpointTestBase {
    @Override
    protected Class<? extends Servlet> servletContainerClass() { return ServletContainer.class; }
}
