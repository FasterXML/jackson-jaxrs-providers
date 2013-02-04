package com.fasterxml.jackson.jaxrs.xml;

import com.fasterxml.jackson.core.JsonParseException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Implementation of {@link ExceptionMapper} to send down a "400 Bad Request"
 * in the event unprocessable XML is received.
 */
@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {
    public Response toResponse(JsonParseException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).type("text/plain").build();
    }
}
