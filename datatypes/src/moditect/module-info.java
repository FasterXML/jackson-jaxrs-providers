// Generated 02-Apr-2019 using Moditect maven plugin
module com.fasterxml.jackson.datatype.jaxrs {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires javax.ws.rs.api;

    exports com.fasterxml.jackson.datatype.jaxrs;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.jaxrs.Jaxrs2TypesModule;
}
