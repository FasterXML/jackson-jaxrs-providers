package com.fasterxml.jackson.datatype.jaxrs;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.deser.std.StdScalarDeserializer;

import javax.ws.rs.core.Link;

public class LinkDeserializer extends StdScalarDeserializer<Link>
{
    public LinkDeserializer() {
        super(Link.class);
    }

    @Override
    public Link deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        String text = p.getValueAsString();
        if (text != null) { // has String representation
            // should we check for empty, and if so, should it become null-value/empty-value?
            return Link.valueOf(text);
        }
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            p.nextToken();
            final Link value = deserialize(p, ctxt);
            if (p.nextToken() != JsonToken.END_ARRAY) {
                handleMissingEndArrayForSingle(p, ctxt);
            }
            return value;
        }
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object ob = p.getEmbeddedObject();
            if (ob == null) {
                return null;
            }
            if (ob instanceof Link) {
                return (Link) ob;
            }
        }
        return (Link) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
    }
}
