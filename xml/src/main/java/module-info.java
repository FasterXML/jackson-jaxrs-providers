// JAX-RS XML module-info for Main artifact
module tools.jackson.jaxrs.xml
{
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;
    requires tools.jackson.dataformat.xml;
    requires tools.jackson.module.jaxb;

    requires tools.jackson.jaxrs.base;

    requires java.ws.rs;

    exports tools.jackson.jaxrs.xml;
    // [jaxrs-providers#119]: CXF, RESTEasy, OpenAPI require reflective access
    opens tools.jackson.jaxrs.xml;

    provides javax.ws.rs.ext.MessageBodyReader with
        tools.jackson.jaxrs.xml.JacksonXMLProvider;
    provides javax.ws.rs.ext.MessageBodyWriter with
        tools.jackson.jaxrs.xml.JacksonXMLProvider;
}
