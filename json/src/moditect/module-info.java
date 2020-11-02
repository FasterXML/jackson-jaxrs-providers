// Originally generated using Moditect maven plugin, last mod 14-Oct-2020
module com.fasterxml.jackson.jaxrs.json {
    exports com.fasterxml.jackson.jaxrs.json;
    exports com.fasterxml.jackson.jaxrs.json.annotation;
    // 13-Nov-2019: [jaxrs-providers#119]: CXF, RESTEasy, OpenAPI require reflective access
    opens com.fasterxml.jackson.jaxrs.json;

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.module.jaxb;

    requires com.fasterxml.jackson.jaxrs.base;

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
        com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
    provides javax.ws.rs.ext.MessageBodyWriter with
        com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
}
