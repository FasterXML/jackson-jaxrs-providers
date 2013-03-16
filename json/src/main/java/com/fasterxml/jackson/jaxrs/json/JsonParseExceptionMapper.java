package com.fasterxml.jackson.jaxrs.json;

import com.fasterxml.jackson.core.JsonParseException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @deprecated Use {@link com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper} instead.
 */
@Deprecated
@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {
    @Override
    public Response toResponse(JsonParseException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).type("text/plain").build();
    }
}
