// Originally generated using Moditect maven plugin, last mod 14-Oct-2020
module com.fasterxml.jackson.jaxrs.yaml {
    exports com.fasterxml.jackson.jaxrs.yaml;
    // [jaxrs-providers#119]: CXF, RESTEasy, OpenAPI require reflective access
    opens com.fasterxml.jackson.jaxrs.yaml;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.module.jaxb;

    requires com.fasterxml.jackson.jaxrs.base;

    requires static javax.ws.rs.api;
    requires static java.ws.rs;
    requires static jakarta.ws.rs.api;

    provides javax.ws.rs.ext.MessageBodyReader with
        com.fasterxml.jackson.jaxrs.yaml.JacksonYAMLProvider;
    provides javax.ws.rs.ext.MessageBodyWriter with
        com.fasterxml.jackson.jaxrs.yaml.JacksonYAMLProvider;
}
