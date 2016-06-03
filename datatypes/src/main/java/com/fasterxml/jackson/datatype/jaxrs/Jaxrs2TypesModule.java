package com.fasterxml.jackson.datatype.jaxrs;

import javax.ws.rs.core.Link;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * Simple datatype module that adds serialization and deserialization
 * support for following JAX-RS 2.0 types:
 *<ul>
 * <li>{@link javax.ws.rs.core.Link}: serialized using "link header" representation
 *  </li>
 * </ul>
 *
 * @since 2.8
 */
public class Jaxrs2TypesModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    public Jaxrs2TypesModule() {
        super(PackageVersion.VERSION);

        // 26-Dec-2015, tatu: TODO: add custom serializers/deserializers for Link,
        //    other new JAX-RS 2.0 datatypes.

        addDeserializer(Link.class, new LinkDeserializer());

        addSerializer(Link.class, new ToStringSerializer(Link.class));
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
