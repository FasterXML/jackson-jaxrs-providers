// Generated 02-Apr-2019 using Moditect maven plugin
module com.fasterxml.jackson.jaxrs.yaml {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.module.jaxb;

    requires com.fasterxml.jackson.jaxrs.base;

    requires javax.ws.rs.api;

    exports com.fasterxml.jackson.jaxrs.xml;

    provides javax.ws.rs.ext.MessageBodyReader with
        com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;
    provides javax.ws.rs.ext.MessageBodyWriter with
        com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;

}
