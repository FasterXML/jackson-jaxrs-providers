package tools.jackson.jaxrs.cfg;

import javax.ws.rs.core.MultivaluedMap;

import tools.jackson.core.*;
import tools.jackson.databind.*;

public abstract class ObjectReaderModifier
{
    /**
     * Method called to let modifier make any changes it wants to to objects
     * used for reading request objects for specified endpoint.
     *<p>
     * NOTE: for {@link MappingIterator} endpoints, unwrapping of root-level Array
     * (see {@link JaxRSFeature#UNWRAP_ROOT_ARRAY_FOR_ITERATOR}) is done
     * <b>after</b> this method is called, based on the token parser points to at
     * that point. So modifiers should not advance the parser past the wrapper Array
     * themselves, unless the feature is disabled: otherwise the first value may be
     * unwrapped as well (for XML, where any element may be exposed as Array).
     * 
     * @param endpoint End point for which reader is used
     * @param httpHeaders HTTP headers sent with request (read-only)
     * @param resultType Type that input is to be bound to
     * @param r ObjectReader as constructed for endpoint, type to handle
     * @param p Parser to use for reading content
     */
    public abstract ObjectReader modify(EndpointConfigBase<?> endpoint,
            MultivaluedMap<String,String> httpHeaders,
            JavaType resultType, ObjectReader r, JsonParser p)
        throws JacksonException;
}
