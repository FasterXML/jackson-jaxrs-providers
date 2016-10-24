package com.fasterxml.jackson.jaxrs.base.nocontent;

import com.fasterxml.jackson.jaxrs.base.NoContentExceptionSupplier;

import java.io.IOException;

/**
 * Create plain IOException for JaxRS 1.x because {@link javax.ws.rs.core.NoContentException}
 * has been introduced in JaxRS 2.x
 */
public class JaxRS1NoContentExceptionSupplier implements NoContentExceptionSupplier
{
    @Override
    public IOException createNoContentException()
    {
        return new IOException(NO_CONTENT_MESSAGE);
    }
}
