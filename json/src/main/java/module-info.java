// JAX-RS JSON module-info for Main artifact
module tools.jackson.jaxrs.json {
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;
    requires tools.jackson.module.jaxb;

    requires tools.jackson.jaxrs.base;

    requires java.ws.rs;

    exports tools.jackson.jaxrs.json;
    // [jaxrs-providers#119]: CXF, RESTEasy, OpenAPI require reflective access
    opens tools.jackson.jaxrs.json;

    provides javax.ws.rs.ext.MessageBodyReader with
        tools.jackson.jaxrs.json.JacksonJsonProvider;
    provides javax.ws.rs.ext.MessageBodyWriter with
        tools.jackson.jaxrs.json.JacksonJsonProvider;
}
