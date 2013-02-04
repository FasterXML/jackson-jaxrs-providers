package com.fasterxml.jackson.jaxrs.xml.util;

import java.lang.annotation.Annotation;

/**
 * Helper class used to allow efficient caching of information,
 * given a sequence of Annotations.
 * This is mostly used for reusing introspected information on
 * JAX-RS end points.
 */
public final class AnnotationBundleKey
{
    private final static Annotation[] NO_ANNOTATIONS = new Annotation[0];
    
    private final Annotation[] _annotations;
    
    private final boolean _annotationsCopied;

    private final int _hashCode;
    
    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */
    
    public AnnotationBundleKey(Annotation[] annotations)
    {
        if (annotations == null || annotations.length == 0) {
            annotations = NO_ANNOTATIONS;
            _annotationsCopied = true;
            _hashCode = -1;
        } else {
            _annotationsCopied = false;
            _hashCode = calcHash(annotations);
        }
        _annotations = annotations;  
    }

    private AnnotationBundleKey(Annotation[] annotations, int hashCode)
    {
        _annotations = annotations;            
        _annotationsCopied = true;
        _hashCode = hashCode;
    }

    private final static int calcHash(Annotation[] annotations)
    {
        /* hmmh. Can't just base on Annotation type; chances are that Annotation
         * instances use identity hash, which has to do.
         */
        final int len = annotations.length;
        int hash = len;
        for (int i = 0; i < len; ++i) {
            hash = (hash * 31) + annotations[i].hashCode();
        }
        return hash;
    }
    
    /**
     * Method called to create a safe immutable copy of the key; used when
     * adding entry with this key -- lookups are ok without calling the method.
     */
    public AnnotationBundleKey immutableKey() {
        if (_annotationsCopied) {
            return this;
        }
        int len = _annotations.length;
        Annotation[] newAnnotations = new Annotation[len];
        System.arraycopy(_annotations, 0, newAnnotations, 0, len);
        return new AnnotationBundleKey(newAnnotations, _hashCode);
    }
    
    /*
    /**********************************************************
    /* Overridden methods
    /**********************************************************
     */

    @Override
    public String toString() {
        return "[Annotations: "+_annotations.length+", hash 0x"+Integer.toHexString(_hashCode)
                +", copied: "+_annotationsCopied+"]";
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) return true;
        if (o == null) return false;
        if (o.getClass() != getClass()) return false;
        AnnotationBundleKey other = (AnnotationBundleKey) o;
        if (other._hashCode != _hashCode) return false;
        return _equals(other._annotations);
    }
    
    private final boolean _equals(Annotation[] otherAnn)
    {
        final int len = _annotations.length;
        if (otherAnn.length != len) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (_annotations[i] != otherAnn[i]) {
                return false;
            }
        }
        return true;
    }
}
