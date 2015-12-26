package com.fasterxml.jackson.datatype.jaxrs;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class JaxRsModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    public JaxRsModule() {
        super(PackageVersion.VERSION);

        // 26-Dec-2015, tatu: TODO: add custom serializers/deserializers for Link,
        //    other new JAX-RS 2.0 datatypes.
    }

    // yes, will try to avoid duplicate registrations (if MapperFeature enabled)
    @Override
    public String getModuleName() {
        return getClass().getSimpleName();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
