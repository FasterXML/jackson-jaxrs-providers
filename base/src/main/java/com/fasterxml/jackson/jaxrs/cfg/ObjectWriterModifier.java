package com.fasterxml.jackson.jaxrs.cfg;

import javax.ws.rs.core.MultivaluedMap;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.*;

/**
 * Handler that may be registered to apply per-call modifications to {@link ObjectWriter}
 * before writing it out.
 *<p>
 * Note one major difference from 2.x: {@code JsonGenerator} is not passed any more
 * as it must be constructed using {@link ObjectWriter} that is modified here.
 */
public abstract class ObjectWriterModifier
{
    /**
     * Method called to let modifier make any changes it wants to to objects
     * used for writing response for specified endpoint.
     * 
     * @param responseHeaders HTTP headers being returned with response (mutable)
     */
    public abstract ObjectWriter modify(EndpointConfigBase<?> endpoint,
            MultivaluedMap<String,Object> responseHeaders,
            Object valueToWrite, ObjectWriter w)
        throws JacksonException;
}
