package com.fasterxml.jackson.datatype.jaxrs;

import java.io.IOException;

import javax.ws.rs.core.Link;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

/**
 * @since 2.8
 */
public class LinkDeserializer extends StdScalarDeserializer<Link>
{
    private static final long serialVersionUID = 1L;

    public LinkDeserializer() {
        super(Link.class);
    }

    @Override
    public Link deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException
    {
        String text = p.getValueAsString();
        if (text != null) { // has String representation
            // should we check for empty, and if so, should it become null-value/empty-value?
            return Link.valueOf(text);
        }
        JsonToken t = p.getCurrentToken();
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
        return (Link) ctxt.handleUnexpectedToken(_valueClass, p);
    }

}
