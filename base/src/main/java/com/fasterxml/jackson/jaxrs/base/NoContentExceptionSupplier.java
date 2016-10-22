package com.fasterxml.jackson.jaxrs.base;

import java.io.IOException;

/**
 * Implementors of this class contains strategies for NoContentException creation
 */
public interface NoContentExceptionSupplier
{
    String NO_CONTENT_MESSAGE = "No content (empty input stream)";

    IOException createNoContentException();
}
