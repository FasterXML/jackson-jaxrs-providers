package com.fasterxml.jackson.jaxrs.base;

import tools.jackson.core.exc.StreamReadException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Implementation of {@link ExceptionMapper} to send down a "400 Bad Request"
 * in the event unparsable JSON is received.
 */
public class StreamReadExceptionMapper implements ExceptionMapper<StreamReadException> {
    @Override
    public Response toResponse(StreamReadException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).type("text/plain").build();
    }
}
