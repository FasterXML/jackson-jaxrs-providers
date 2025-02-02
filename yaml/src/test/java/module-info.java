// JAX-RS YAML module-info for (unit) Tests
module tools.jackson.jaxrs.yaml
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires tools.jackson.dataformat.yaml;
    requires tools.jackson.module.jaxb;

    requires tools.jackson.jaxrs.base;
    
    requires java.ws.rs;

    // Additional test lib/framework dependencies
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    // Other test deps: we test format-negotiation so need JSON and Smile providers

    requires tools.jackson.jaxrs.json;
    requires tools.jackson.jaxrs.smile;
    requires tools.jackson.dataformat.smile;
    
    // Further, need to open up test packages for JUnit et al
    
    opens tools.jackson.jaxrs.yaml;
    opens tools.jackson.jaxrs.yaml.dw;
    opens tools.jackson.jaxrs.yaml.jersey;
}
