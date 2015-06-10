package com.fasterxml.jackson.jaxrs.xml.jersey;

import javax.servlet.Servlet;

import com.fasterxml.jackson.jaxrs.xml.dw.SimpleEndpointTestBase;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class SimpleEndpointTest extends SimpleEndpointTestBase {
    @Override
    protected Class<? extends Servlet> servletContainerClass() { return ServletContainer.class; }
}
