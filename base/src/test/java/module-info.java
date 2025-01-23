// JAX-RS Base module-info for (unit) Tests
module tools.jackson.jaxrs.base
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    // Additional test lib/framework dependencies
    requires junit; // JUnit 4

    // Further, need to open up test packages for JUnit et al
    
    opens tools.jackson.jaxrs.base;
    opens tools.jackson.jaxrs.base.cfg;
}
