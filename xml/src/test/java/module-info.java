// JAX-RS XML module-info for (unit) Tests
module tools.jackson.jaxrs.xml
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires tools.jackson.dataformat.xml;
    requires tools.jackson.module.jaxb;

    requires tools.jackson.jaxrs.base;

    requires java.ws.rs;

    // Additional test lib/framework dependencies
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    // Further, need to open up test packages for JUnit et al
    
    opens tools.jackson.jaxrs.xml;
    opens tools.jackson.jaxrs.xml.dw;
    opens tools.jackson.jaxrs.xml.jersey;
}
