package tools.jackson.jaxrs.yaml.jersey;

import org.glassfish.jersey.servlet.ServletContainer;

import tools.jackson.jaxrs.yaml.dw.SimpleEndpointTestBase;

import javax.servlet.Servlet;

public class SimpleEndpointTest extends SimpleEndpointTestBase {
    @Override
    protected Class<? extends Servlet> servletContainerClass() { return ServletContainer.class; }
}
