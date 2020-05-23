// Generated 02-Apr-2019 using Moditect maven plugin
module com.fasterxml.jackson.jaxrs.cbor {
    requires com.fasterxml.jackson.dataformat.cbor;
    requires com.fasterxml.jackson.module.jaxb;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.jaxrs.base;
    requires jakarta.ws.rs.api;

    exports com.fasterxml.jackson.jaxrs.cbor;

    provides jakarta.ws.rs.ext.MessageBodyReader with
        com.fasterxml.jackson.jaxrs.cbor.JacksonCBORProvider;
    provides jakarta.ws.rs.ext.MessageBodyWriter with
        com.fasterxml.jackson.jaxrs.cbor.JacksonCBORProvider;
}
