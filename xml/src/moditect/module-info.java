// Originally generated using Moditect maven plugin, last mod 14-Oct-2020
module com.fasterxml.jackson.jaxrs.xml {
    exports com.fasterxml.jackson.jaxrs.xml;
    // [jaxrs-providers#119]: CXF, RESTEasy, OpenAPI require reflective access
    opens com.fasterxml.jackson.jaxrs.xml;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.module.jaxb;

    requires com.fasterxml.jackson.jaxrs.base;

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
        com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;
    provides javax.ws.rs.ext.MessageBodyWriter with
        com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;
}
