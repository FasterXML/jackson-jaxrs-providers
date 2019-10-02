package com.fasterxml.jackson.jaxrs.base.cfg;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.jaxrs.base.BaseTestBase;
import com.fasterxml.jackson.jaxrs.cfg.AnnotationBundleKey;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;

// for [jaxrs-providers#111]
public class AnnotationBundleKeyTest
    extends BaseTestBase
{
    // let's also test multiple annotation case
    @JsonIgnoreProperties
    @JsonPropertyOrder({ "a", "b" })
    @JsonSerialize
    @JsonDeserialize
    static class Helper {
        @JsonCreator
        public Helper(@JsonProperty("x") int x) { }

        @JsonValue
        @JsonView(Object.class)
        public int getX() { return 3; }

        // A "copy" of `getX`
        @JsonValue
        @JsonView(Object.class)
        public int altX() { return 3; }

        @JsonPropertyOrder
        @JsonView(Object.class)
        public int notX() { return 4; }
        
        public void setX(@JsonProperty("x") int x) { }
    }

    public void testWithClassAnnotations() throws Exception
    {
        _checkWith(Helper.class.getAnnotations(), Helper.class.getAnnotations());
    }

    public void testWithMethodAnnotationEquals() throws Exception
    {
        // First, same method parameters definitely should match
        _checkWith(Helper.class.getDeclaredMethod("getX").getAnnotations(),
                Helper.class.getDeclaredMethod("getX").getAnnotations());
        // but so should annotations from different method as long as
        // same parameters are in same order
        _checkWith(Helper.class.getDeclaredMethod("getX").getAnnotations(),
                Helper.class.getDeclaredMethod("altX").getAnnotations());
    }

    public void testWithMethodAnnotationDifferent() throws Exception
    {
        // However: not so with actually differing annotations
        _checkNotEqual(Helper.class.getDeclaredMethod("getX").getAnnotations(),
                Helper.class.getDeclaredMethod("notX").getAnnotations());
    }

    public void testWithMethodParameterAnnotation() throws Exception
    {
        _checkWith(Helper.class.getDeclaredMethod("setX", Integer.TYPE).getParameterAnnotations()[0],
                Helper.class.getDeclaredMethod("setX", Integer.TYPE).getParameterAnnotations()[0]);
    }

    public void testWithConstructorAnnotation() throws Exception
    {
        _checkWith(Helper.class.getConstructor(Integer.TYPE).getAnnotations(),
                Helper.class.getConstructor(Integer.TYPE).getAnnotations());
    }
    
    public void testWithConstructorParameterAnnotation() throws Exception
    {
        _checkWith(Helper.class.getConstructor(Integer.TYPE).getParameterAnnotations()[0],
                Helper.class.getConstructor(Integer.TYPE).getParameterAnnotations()[0]);
    }

    protected void _checkWith(Annotation[] anns1, Annotation[] anns2) {
        // First, sanity check2 to know we passed non-empty annotations, same by equality
        if (anns1.length == 0) {
            fail("Internal error: empty annotation array");
        }
        assertArrayEquals("Internal error: should never differ", anns1, anns2);

        AnnotationBundleKey b1 = new AnnotationBundleKey(anns1, Object.class);
        AnnotationBundleKey b2 = new AnnotationBundleKey(anns2, Object.class);

        if (!b1.equals(b2) || !b2.equals(b1)) {
            assertEquals(String.format("Implementations over %s backed annotations differ", anns1[0].getClass()),
                    b1, b2);
        }
    }

    protected void _checkNotEqual(Annotation[] anns1, Annotation[] anns2) {
        AnnotationBundleKey b1 = new AnnotationBundleKey(anns1, Object.class);
        AnnotationBundleKey b2 = new AnnotationBundleKey(anns2, Object.class);

        if (b1.equals(b2) || b2.equals(b1)) {
            assertNotEquals(String.format("Implementations over %s backed annotations SHOULD differ but won't", anns1[0].getClass()),
                    b1, b2);
        }
    }
}
