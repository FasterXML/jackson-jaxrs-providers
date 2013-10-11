package com.fasterxml.jackson.jaxrs.cfg;

import java.io.IOException;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;

/**
 * @since 2.3
 */
public abstract class ObjectWriterModifier
{
    /**
     * Method called to let modifier make any changes it wants to to objects
     * used for writing response for specified endpoint.
     */
    public abstract ObjectWriter modify(EndpointConfigBase<?> endpoint,
            Object valueToWrite, ObjectWriter r, JsonGenerator g)
        throws IOException;
}
