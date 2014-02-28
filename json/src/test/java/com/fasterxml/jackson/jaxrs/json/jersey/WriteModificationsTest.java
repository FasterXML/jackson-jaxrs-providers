package com.fasterxml.jackson.jaxrs.json.jersey;

import javax.servlet.Servlet;

import com.fasterxml.jackson.jaxrs.json.dw.WriteModificationsTestBase;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class WriteModificationsTest extends WriteModificationsTestBase {
    @Override
    protected Class<? extends Servlet> servletContainerClass() { return ServletContainer.class; }
}
