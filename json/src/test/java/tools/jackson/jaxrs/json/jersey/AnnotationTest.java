package tools.jackson.jaxrs.json.jersey;

import javax.servlet.Servlet;

import org.glassfish.jersey.servlet.ServletContainer;

import tools.jackson.jaxrs.json.dw.AnnotationTestBase;

public class AnnotationTest extends AnnotationTestBase {
    @Override
    protected Class<? extends Servlet> servletContainerClass() { return ServletContainer.class; }
}
