package com.fasterxml.jackson.jaxrs.json.jersey;

import javax.servlet.Servlet;

import com.fasterxml.jackson.jaxrs.json.dw.AnnotationTestBase;

import org.glassfish.jersey.servlet.ServletContainer;

public class AnnotationTest extends AnnotationTestBase {
    @Override
    protected Class<? extends Servlet> servletContainerClass() { return ServletContainer.class; }
}
