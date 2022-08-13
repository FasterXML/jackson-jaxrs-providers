package com.fasterxml.jackson.jaxrs.base.cfg;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.*;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.jaxrs.base.BaseTestBase;
import com.fasterxml.jackson.jaxrs.cfg.AnnotationBundleKey;

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
        Annotation[] annotation1 = Helper.class.getAnnotations();
        Annotation[] annotation2 = Helper.class.getAnnotations();
        Arrays.sort(annotation1, (i, j) -> i.toString().compareTo(j.toString()));
        Arrays.sort(annotation2, (i, j) -> i.toString().compareTo(j.toString()));
        _checkWith(annotation1, annotation2);
    }

    public void testWithMethodAnnotationEquals() throws Exception
    {
        // First, same method parameters definitely should match
        Annotation[] annotation1 = Helper.class.getDeclaredMethod("getX").getAnnotations();
        Annotation[] annotation2 = Helper.class.getDeclaredMethod("getX").getAnnotations();
        Arrays.sort(annotation1, (i, j) -> i.toString().compareTo(j.toString()));
        Arrays.sort(annotation2, (i, j) -> i.toString().compareTo(j.toString()));
        _checkWith(annotation1, annotation2);
        // but so should annotations from different method as long as
        // same parameters are in same order
        Annotation[] annotation3 = Helper.class.getDeclaredMethod("getX").getAnnotations();
        Annotation[] annotation4 = Helper.class.getDeclaredMethod("altX").getAnnotations();
        Arrays.sort(annotation3, (i, j) -> i.toString().compareTo(j.toString()));
        Arrays.sort(annotation4, (i, j) -> i.toString().compareTo(j.toString()));
        _checkWith(annotation3, annotation4);
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
        HashSet<Annotation> annsSet1 = new HashSet<Annotation>(Arrays.asList(anns1));
        HashSet<Annotation> annsSet2 = new HashSet<Annotation>(Arrays.asList(anns2));
        assertTrue("Internal error: should never differ", annsSet1.equals(annsSet2));

        AnnotationBundleKey b1 = new AnnotationBundleKey(anns1, Object.class);
        AnnotationBundleKey b2 = new AnnotationBundleKey(anns2, Object.class);

        assertTrue(String.format("Implementations over %s backed annotations differ", anns1[0].getClass()), (b1.equals(b2) && b2.equals(b1)));
    }

    protected void _checkNotEqual(Annotation[] anns1, Annotation[] anns2) {
        AnnotationBundleKey b1 = new AnnotationBundleKey(anns1, Object.class);
        AnnotationBundleKey b2 = new AnnotationBundleKey(anns2, Object.class);

        assertFalse(String.format("Implementations over %s backed annotations SHOULD differ but won't", anns1[0].getClass()), (b1.equals(b2) || b2.equals(b1)));
    }
}
