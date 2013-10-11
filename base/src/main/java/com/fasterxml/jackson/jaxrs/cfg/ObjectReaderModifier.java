package com.fasterxml.jackson.jaxrs.cfg;

import java.io.IOException;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;

/**
 * @since 2.3
 */
public abstract class ObjectReaderModifier
{
    /**
     * Method called to let modifier make any changes it wants to to objects
     * used for reading request objects for specified endpoint.
     * 
     * @param endpoint End point for which reader is used
     * @param resultType Type that input is to be bound to
     * @param r ObjectReader as constructed for endpoint, type to handle
     * @param p Parser to use for reading content
     */
    public abstract ObjectReader modify(EndpointConfigBase<?> endpoint,
            JavaType resultType, ObjectReader r, JsonParser p)
        throws IOException;
}
