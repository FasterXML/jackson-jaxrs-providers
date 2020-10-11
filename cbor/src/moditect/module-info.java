// Generated 02-Apr-2019 using Moditect maven plugin
module com.fasterxml.jackson.jaxrs.cbor {
    requires com.fasterxml.jackson.dataformat.cbor;
    requires com.fasterxml.jackson.module.jaxb;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.jaxrs.base;

    requires static javax.ws.rs.api;
    requires static java.ws.rs;
    requires static jakarta.ws.rs.api;

    exports com.fasterxml.jackson.jaxrs.cbor;

    provides javax.ws.rs.ext.MessageBodyReader with
        com.fasterxml.jackson.jaxrs.cbor.JacksonCBORProvider;
    provides javax.ws.rs.ext.MessageBodyWriter with
        com.fasterxml.jackson.jaxrs.cbor.JacksonCBORProvider;
}
