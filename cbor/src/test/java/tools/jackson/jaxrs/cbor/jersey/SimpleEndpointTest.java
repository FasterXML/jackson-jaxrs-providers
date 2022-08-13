package tools.jackson.jaxrs.cbor.jersey;

import javax.servlet.Servlet;

import org.glassfish.jersey.servlet.ServletContainer;

import tools.jackson.jaxrs.cbor.dw.SimpleEndpointTestBase;

public class SimpleEndpointTest extends SimpleEndpointTestBase {
    @Override
    protected Class<? extends Servlet> servletContainerClass() { return ServletContainer.class; }
}
