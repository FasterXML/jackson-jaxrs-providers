// JAX-RS JSON module-info for (unit) Tests
module tools.jackson.jaxrs.json
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires tools.jackson.module.jaxb;

    requires tools.jackson.jaxrs.base;

    requires java.ws.rs;

    // Additional test lib/framework dependencies
    requires junit; // JUnit 4

    // Further, need to open up test packages for JUnit et al

    opens tools.jackson.jaxrs.json;
    opens tools.jackson.jaxrs.json.dw;
    opens tools.jackson.jaxrs.json.jersey;
    opens tools.jackson.jaxrs.json.testutil;
    opens tools.jackson.jaxrs.json.util;
}
