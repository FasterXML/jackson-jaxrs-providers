package com.fasterxml.jackson.jaxrs.json.jersey;

import javax.servlet.Servlet;

import com.fasterxml.jackson.jaxrs.json.dw.SimpleEndpointTestBase;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class SimpleEndpointTest extends SimpleEndpointTestBase {
    @Override
    protected Class<? extends Servlet> servletContainerClass() { return ServletContainer.class; }
}
