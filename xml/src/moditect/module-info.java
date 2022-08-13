module tools.jackson.jaxrs.xml {
    exports tools.jackson.jaxrs.xml;
    // [jaxrs-providers#119]: CXF, RESTEasy, OpenAPI require reflective access
    opens tools.jackson.jaxrs.xml;

    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires tools.jackson.dataformat.xml;
    requires tools.jackson.module.jaxb;

    requires tools.jackson.jaxrs.base;

    //Allow multiple implementations of ws.rs
    //oracle location
    requires static javax.ws.rs.api;
    //oracle location
    requires static java.ws.rs;
    //jakarta initial location - 2.x
    requires static javax.ws.rs;
    //jakarta 3.x final location - https://github.com/jboss/jboss-jakarta-jaxrs-api_spec
    requires static jakarta.ws.rs;
    //jakarta 3.x final location - https://github.com/eclipse-ee4j/jaxrs-api
    requires static jakarta.ws.rs.api;

    provides javax.ws.rs.ext.MessageBodyReader with
        tools.jackson.jaxrs.xml.JacksonXMLProvider;
    provides javax.ws.rs.ext.MessageBodyWriter with
        tools.jackson.jaxrs.xml.JacksonXMLProvider;
}
