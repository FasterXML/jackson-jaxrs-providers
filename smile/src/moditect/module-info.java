module tools.jackson.jaxrs.smile {
    exports tools.jackson.jaxrs.smile;
    // [jaxrs-providers#119]: CXF, RESTEasy, OpenAPI require reflective access
    opens tools.jackson.jaxrs.smile;

    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires tools.jackson.dataformat.smile;
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
        tools.jackson.jaxrs.smile.JacksonSmileProvider;
    provides javax.ws.rs.ext.MessageBodyWriter with
        tools.jackson.jaxrs.smile.JacksonSmileProvider;
}
