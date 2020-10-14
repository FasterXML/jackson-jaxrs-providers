// Originally generated using Moditect maven plugin, last mod 14-Oct-2020
module com.fasterxml.jackson.datatype.jaxrs {
    exports com.fasterxml.jackson.datatype.jaxrs;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    requires static javax.ws.rs.api;
    requires static java.ws.rs;
    requires static jakarta.ws.rs.api;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.jaxrs.Jaxrs2TypesModule;
}
