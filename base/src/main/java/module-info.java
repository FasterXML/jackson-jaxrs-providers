// JAX-RS Base module-info for Main artifact
module tools.jackson.jaxrs.base
{
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    requires java.ws.rs;

    exports tools.jackson.jaxrs.annotation;
    exports tools.jackson.jaxrs.base;
    exports tools.jackson.jaxrs.cfg;
    exports tools.jackson.jaxrs.util;
}
