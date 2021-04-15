package com.fasterxml.jackson.jaxrs.base.cfg;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.jaxrs.base.BaseTestBase;
import com.fasterxml.jackson.jaxrs.cfg.AnnotationBundleKey;
import com.fasterxml.jackson.jaxrs.annotation.JacksonLocks;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;

// for [jaxrs-providers#111]
public class AnnotationBundleKeyTest
        extends BaseTestBase
{
    // let's also test multiple annotation case
//    @JsonIgnoreProperties  // victoryang00: I think there's data race in Serialization Properties.
//    @JsonPropertyOrder({ "a", "b" })
    @JsonSerialize
//    @JsonDeserialize
//    @JacksonLocks(rollbackFor = Exception.class)
    @Transactional
    static class Helper {
        @JsonCreator
        public Helper(@JsonProperty("x") int x) { }

        @JsonValue
        @JsonView(Object.class)
        public int getX() { return 3; }

//         A "copy" of `getX`
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
        // victoryang00: ordering that may be deferent

        /** Case1: AnnotationBundleKeyTest.testWithClassAnnotations:46->_checkWith:90 Internal error: should never differ: arrays first differed at element [0]; expected:<@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=false, value=[], allowGetters=false, allowSetters=false)> but was:<@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder=class java.lang.Void, contentConverter=class com.fasterxml.jackson.databind.util.Converter$None, converter=
         class com.fasterxml.jackson.databind.util.Converter$None, keyUsing=class com.fasterxml.jackson.databind.KeyDeserializer$None, as=class java.lang.Void, keyAs=class java.lang.Void, contentUsing=class com.fasterxml.jackson.databind.ValueDeserializer$None, using=class com.fasterxml.jackson.databind.ValueDeserializer$None, contentAs=class java.lang.Void)>*/

        /** Case2: AnnotationBundleKeyTest.testWithClassAnnotations:46->_checkWith:90 Internal error: should never differ: arrays first differed at element [0]; expected:<@com.fasterxml.jackson.databind.annotation.JsonSerialize(as=class java.lang.Void, using=class com.fasterxml.jackson.databind.ValueSerializer$None, converter=class com.fasterxml.jackson.databind.util.Converter$None, contentAs=class java.lang.Void, contentUsing=class com.fasterxml.jackson.databind.ValueSerializ
        er$None, typing=DEFAULT_TYPING, contentConverter=class com.fasterxml.jackson.databind.util.Converter$None, keyUsing=class com.fasterxml.jackson.databind.ValueSerializer$None, nullsUsing=class com.fasterxml.jackson.databind.ValueSerializer$None, keyAs=class java.lang.Void)> but was:<@com.fasterxml.jackson.databind.annotation.JsonDeserialize(keyUsing=class com.fasterxml.jackson.databind.KeyDeserializer$None, contentConverter=class com.fasterxml.jackson.databind.util.Con
        verter$None, contentAs=class java.lang.Void, converter=class com.fasterxml.jackson.databind.util.Converter$None, keyAs=class java.lang.Void, using=class com.fasterxml.jackson.databind.ValueDeserializer$None, as=class java.lang.Void, contentUsing=class com.fasterxml.jackson.databind.ValueDeserializer$None, builder=class java.lang.Void)> */

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