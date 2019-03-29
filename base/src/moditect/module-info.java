// Generated 28-Mar-2019 using Moditect maven plugin
module com.fasterxml.jackson.jaxrs.base {
    requires com.fasterxml.jackson.annotation;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires javax.ws.rs.api;

    exports com.fasterxml.jackson.jaxrs.annotation;
    exports com.fasterxml.jackson.jaxrs.base;
    exports com.fasterxml.jackson.jaxrs.base.nocontent;
    exports com.fasterxml.jackson.jaxrs.cfg;
    exports com.fasterxml.jackson.jaxrs.util;
}
