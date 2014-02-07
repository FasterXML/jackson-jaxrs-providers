package com.fasterxml.jackson.jaxrs.cbor;

import javax.ws.rs.core.MediaType;

public class CBORMediaTypes {
    // Should be the official one, from CBOR spec
    public static final String    APPLICATION_JACKSON_CBOR      = "application/cbor";
    public static final MediaType APPLICATION_JACKSON_CBOR_TYPE = MediaType.valueOf(APPLICATION_JACKSON_CBOR);
}
