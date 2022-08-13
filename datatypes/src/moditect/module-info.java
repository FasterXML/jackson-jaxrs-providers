module tools.jackson.datatype.jaxrs {
    exports tools.jackson.datatype.jaxrs;

    requires tools.jackson.core;
    requires tools.jackson.databind;

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

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.jaxrs.Jaxrs2TypesModule;
}
