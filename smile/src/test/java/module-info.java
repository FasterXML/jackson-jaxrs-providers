// JAX-RS Smile module-info for (unit) Tests
module tools.jackson.jaxrs.smile
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires tools.jackson.dataformat.smile;
    requires tools.jackson.module.jaxb;

    requires tools.jackson.jaxrs.base;

    requires java.ws.rs;

    // Additional test lib/framework dependencies
    requires org.junit.jupiter.api;

    // Further, need to open up test packages for JUnit et al
    
    opens tools.jackson.jaxrs.smile;
    opens tools.jackson.jaxrs.smile.dw;
    opens tools.jackson.jaxrs.smile.jersey;
}
