package com.fasterxml.jackson.jaxrs.smile.jersey;

import javax.servlet.Servlet;

import com.fasterxml.jackson.jaxrs.smile.dw.SimpleEndpointTestBase;

import org.glassfish.jersey.servlet.ServletContainer;

public class SimpleEndpointTest extends SimpleEndpointTestBase {
    @Override
    protected Class<? extends Servlet> servletContainerClass() { return ServletContainer.class; }
}
