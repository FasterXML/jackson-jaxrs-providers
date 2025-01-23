// JAX-RS Datatypes module-info for Main artifact
module tools.jackson.datatype.jaxrs
{
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;
    requires java.ws.rs;
    
    exports tools.jackson.datatype.jaxrs;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.jaxrs.Jaxrs2TypesModule;
}
