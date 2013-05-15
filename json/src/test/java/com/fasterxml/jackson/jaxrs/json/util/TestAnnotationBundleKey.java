package com.fasterxml.jackson.jaxrs.json.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.fasterxml.jackson.jaxrs.cfg.AnnotationBundleKey;

import com.fasterxml.jackson.jaxrs.json.JaxrsTestBase;
import com.fasterxml.jackson.jaxrs.json.annotation.JSONP;

public class TestAnnotationBundleKey extends JaxrsTestBase
{
    @JSONP("foo")
    public void annotated1() { }

    @JSONP("foo")
    public void annotated2() { }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testKeys() throws Exception
    {
       Method m1 = getClass().getDeclaredMethod("annotated1");
       Method m2 = getClass().getDeclaredMethod("annotated2");

       assertNotSame(m1, m2);

       Annotation[] ann1 = m1.getAnnotations();
       assertEquals(1, ann1.length);
       Annotation[] ann2 = m2.getAnnotations();
       assertEquals(1, ann2.length);

       AnnotationBundleKey key1 = new AnnotationBundleKey(ann1, Object.class);
       AnnotationBundleKey key2 = new AnnotationBundleKey(ann2, Object.class);
       AnnotationBundleKey key1dup = new AnnotationBundleKey(ann1, Object.class);
       AnnotationBundleKey key1immutable = key1.immutableKey();

       // identity checks first
       assertEquals(key1, key1);
       assertEquals(key2, key2);
       assertEquals(key1dup, key1dup);
       assertEquals(key1immutable, key1immutable);

       assertEquals(key1.hashCode(), key1dup.hashCode());
       
       // then inequality by content (even though both have 1 JSONP annotation)
       assertFalse(key1.equals(key2));
       assertFalse(key2.equals(key1));

       // but safe copy ought to be equal
       assertTrue(key1.equals(key1dup)); // from same method
       assertTrue(key1dup.equals(key1));
       assertTrue(key1.equals(key1immutable)); // and immutable variant
       assertTrue(key1immutable.equals(key1));
    }
}
