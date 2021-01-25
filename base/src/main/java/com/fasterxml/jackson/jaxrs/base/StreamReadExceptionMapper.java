package com.fasterxml.jackson.jaxrs.base;

import com.fasterxml.jackson.core.exc.StreamReadException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Implementation of {@link ExceptionMapper} to send down a "400 Bad Request"
 * in the event unparsable JSON is received.
 *<p>
 * Note that {@link javax.ws.rs.ext.Provider} annotation was include up to
 * Jackson 2.7, but removed from 2.8 (as per [jaxrs-providers#22]
 *
 * @since 2.2
 */
public class StreamReadExceptionMapper implements ExceptionMapper<StreamReadException> {
    @Override
    public Response toResponse(StreamReadException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).type("text/plain").build();
    }
}
