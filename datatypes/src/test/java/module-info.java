// JAX-RS Datatypes module-info for (unit) Tests
module tools.jackson.datatype.jaxrs
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires tools.jackson.core;
    requires transitive tools.jackson.databind;
    requires java.ws.rs;
    
    // Additional test lib/framework dependencies
    requires org.junit.jupiter.api;

    // Further, need to open up test packages for JUnit et al
    
    opens tools.jackson.datatype.jaxrs;
}
