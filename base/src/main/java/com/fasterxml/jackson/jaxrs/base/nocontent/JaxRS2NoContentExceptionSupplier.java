package com.fasterxml.jackson.jaxrs.base.nocontent;

import com.fasterxml.jackson.jaxrs.base.NoContentExceptionSupplier;

import javax.ws.rs.core.NoContentException;
import java.io.IOException;

/**
 * This supplier creates fair NoContentException from JaxRS 2.x
 */
public class JaxRS2NoContentExceptionSupplier implements NoContentExceptionSupplier {
    @Override
    public IOException createNoContentException() {
        return new NoContentException(NoContentExceptionSupplier.NO_CONTENT_MESSAGE);
    }
}
