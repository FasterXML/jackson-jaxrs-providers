// JAX-RS Smile module-info for Main artifact
module tools.jackson.jaxrs.smile
{
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;
    requires tools.jackson.dataformat.smile;
    requires tools.jackson.module.jaxb;

    requires tools.jackson.jaxrs.base;

    requires java.ws.rs;

    exports tools.jackson.jaxrs.smile;
    // [jaxrs-providers#119]: CXF, RESTEasy, OpenAPI require reflective access
    opens tools.jackson.jaxrs.smile;

    provides javax.ws.rs.ext.MessageBodyReader with
        tools.jackson.jaxrs.smile.JacksonSmileProvider;
    provides javax.ws.rs.ext.MessageBodyWriter with
        tools.jackson.jaxrs.smile.JacksonSmileProvider;
}
