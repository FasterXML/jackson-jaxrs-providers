// JAX-RS CBOR module-info for Main artifact
module tools.jackson.jaxrs.cbor
{
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;
    requires tools.jackson.dataformat.cbor;
    requires tools.jackson.module.jaxb;

    requires tools.jackson.jaxrs.base;

    requires java.ws.rs;

    exports tools.jackson.jaxrs.cbor;
    // [jaxrs-providers#119]: CXF, RESTEasy, OpenAPI require reflective access
    opens tools.jackson.jaxrs.cbor;
    
    provides javax.ws.rs.ext.MessageBodyReader with
        tools.jackson.jaxrs.cbor.JacksonCBORProvider;
    provides javax.ws.rs.ext.MessageBodyWriter with
        tools.jackson.jaxrs.cbor.JacksonCBORProvider;
}
