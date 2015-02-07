package com.fasterxml.jackson.jaxrs.util;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;

/**
 * {@link BeanProperty} implementation used for passing annotations
 * from JAX-RS endpoint into Jackson. This tries to imitate behavior
 * one would get if actual resource method was used as POJO property;
 * ideally this would be how implementation works but due JAX-RS API
 * limitations, we are only given annotations associated, and that
 * has to do.
 */
public class EndpointAsBeanProperty
    extends BeanProperty.Std
{
    public final static PropertyName ENDPOINT_NAME = new PropertyName("JAX-RS/endpoint");

    private final static AnnotationMap NO_ANNOTATIONS = new AnnotationMap();

    public final AnnotationMap _annotations;

    public EndpointAsBeanProperty(JavaType type, Annotation[] annotations)
    {
        // TODO: find and pass wrapper; isRequired marker?
        super(ENDPOINT_NAME, type, /*PropertyName wrapperName*/ null,
                null, null, PropertyMetadata.STD_OPTIONAL);
        boolean hasAnn = (annotations != null && annotations.length > 0);
        if (hasAnn) {
            _annotations = new AnnotationMap();
            for (Annotation a : annotations) {
                _annotations.add(a);
            }
        } else {
            _annotations = NO_ANNOTATIONS;
        }
    }
}
