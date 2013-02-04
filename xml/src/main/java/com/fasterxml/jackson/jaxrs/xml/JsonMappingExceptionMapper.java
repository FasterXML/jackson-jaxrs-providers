package com.fasterxml.jackson.jaxrs.xml;

import com.fasterxml.jackson.databind.JsonMappingException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Implementation if {@link ExceptionMapper} to send down a "400 Bad Request"
 * response in the event that unprocessable XML is received.
 */
@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
    public Response toResponse(JsonMappingException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).type("text/plain").build();
    }
}
