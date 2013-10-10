package com.fasterxml.jackson.jaxrs.base;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A thread local context to store ObjectWriters for use in various JAX-RS contexts.
 * For example, you may use a ContainerRequestFilter to configure an ObjectWriter per request.
 * 
 * @author Andy Pemberton
 */
public class ObjectWriterThreadLocal {

    private static final ThreadLocal<ObjectWriter> objectWriterThreadLocal = new ThreadLocal<ObjectWriter>();

    public static void set(ObjectWriter objectWriter) {
        objectWriterThreadLocal.set(objectWriter);
    }

    public static void unset() {
        objectWriterThreadLocal.remove();
    }

    public static ObjectWriter get() {
        return objectWriterThreadLocal.get();
    }

    public static ObjectWriter mergeAndUnset(ObjectWriter writer) {
        if (get() != null) {
            if (get().isEnabled(SerializationFeature.INDENT_OUTPUT)) {
                writer = writer.with(SerializationFeature.INDENT_OUTPUT);
            }
            if (get().getConfig().getFilterProvider() != null) {
                writer = writer.with(get().getConfig().getFilterProvider());
            }
            ObjectWriterThreadLocal.unset();
        }
        return writer;
    }

}

/*
 * Copyright 2013 Capital One Financial Corporation All Rights Reserved.
 * 
 * This software contains valuable trade secrets and proprietary information of Capital One and is protected by law. It
 * may not be copied or distributed in any form or medium, disclosed to third parties, reverse engineered or used in any
 * manner without prior written authorization from Capital One.
 */
