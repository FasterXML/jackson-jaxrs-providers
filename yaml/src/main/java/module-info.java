// JAX-RS YAML module-info for Main artifact
module tools.jackson.jaxrs.yaml
{
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;
    requires tools.jackson.dataformat.yaml;
    requires tools.jackson.module.jaxb;

    requires tools.jackson.jaxrs.base;

    requires java.ws.rs;

    exports tools.jackson.jaxrs.yaml;
    // [jaxrs-providers#119]: CXF, RESTEasy, OpenAPI require reflective access
    opens tools.jackson.jaxrs.yaml;

    provides javax.ws.rs.ext.MessageBodyReader with
        tools.jackson.jaxrs.yaml.JacksonYAMLProvider;
    provides javax.ws.rs.ext.MessageBodyWriter with
        tools.jackson.jaxrs.yaml.JacksonYAMLProvider;
}
