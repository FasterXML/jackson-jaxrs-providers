package com.fasterxml.jackson.jaxrs.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test case for https://github.com/FasterXML/jackson-jaxrs-providers/issues/189
 * <p>
 * JacksonJsonProvider appears to ignore DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS
 * configuration set on the ObjectMapper.
 */
public class TestIssue189ConfigSettings {

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name", scope = Value.class)
    static class Value {
        public String name;
        public Integer value;
    }

    static class Owned {
        public String name;
        public Value optionalValue;

        Optional<Value> optionalValue() {
            return Optional.ofNullable(optionalValue);
        }
    }

    static class Owner {
        public List<Owned> owned = new ArrayList<>();
        public List<Value> values = new ArrayList<>();
    }

    private final static String PAYLOAD = "{\n" +
            "    \"owned\": [\n" +
            "        { \"name\": \"foo\", \"optionalValue\": \"vFoo\" },\n" +
            "        { \"name\": \"bar\", \"optionalValue\": \"this is not a valid ref to some value\" },\n" +
            "        { \"name\": \"baz\" },\n" +
            "        { \"name\": \"qux\", \"optionalValue\": { \"name\": \"vQux\", \"value\": 3 } }\n" +
            "    ],\n" +
            "    \"values\": [\n" +
            "        { \"name\": \"vFoo\", \"value\": 1 },\n" +
            "        { \"name\": \"vBar\", \"value\": 2 }\n" +
            "    ]\n" +
            "}";

    @Test
    public void should_deserialize_illegal_reference_when_configured_leniently() throws Exception {
        final ObjectMapper objectMapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false)
                .build();
        final Owner owner = objectMapper.readValue(PAYLOAD, Owner.class);

        // With lenient configuration, unresolved references should be null
        assertEquals(4, owner.owned.size());
        assertEquals(Integer.valueOf(1), owner.owned.get(0).optionalValue().map(v -> v.value).orElse(null));
        assertNull(owner.owned.get(1).optionalValue); // Invalid reference -> null
        assertNull(owner.owned.get(2).optionalValue); // No reference -> null
        assertEquals(Integer.valueOf(3), owner.owned.get(3).optionalValue().map(v -> v.value).orElse(null));
    }

    @Test
    public void should_reject_illegal_reference_by_default() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("Default FAIL_ON_UNRESOLVED_OBJECT_IDS: " +
            objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS));

        // By default, should throw exception on unresolved object IDs
        //assertThrows(JacksonException.class, () => objectMapper.readValue(payload, Owner.class));

        // Let's actually try to read it and see what happens
        try {
            Owner owner = objectMapper.readValue(PAYLOAD, Owner.class);
            System.out.println("Successfully read - this is unexpected!");
            System.out.println("owner.owned.size(): " + owner.owned.size());
            if (owner.owned.size() > 1) {
                System.out.println("owner.owned.get(1).optionalValue: " + owner.owned.get(1).optionalValue);
            }
            // If we get here without exception, fail the test
            fail("Expected JacksonException but parsing succeeded");
        } catch (JacksonException e) {
            // This is expected
        }
    }

    @Test
    public void debug_mapper_configuration() throws Exception {
        // Create and configure mapper
        final ObjectMapper objectMapper = new ObjectMapper();

        // Check the default value first
        System.out.println("Default FAIL_ON_UNRESOLVED_OBJECT_IDS: " +
            objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS));

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, true);

        // Verify mapper is configured
        assertTrue(objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS),
                "ObjectMapper should have FAIL_ON_UNRESOLVED_OBJECT_IDS enabled");

        // Check ObjectReader created from mapper
        ObjectReader reader = objectMapper.reader();
        System.out.println("Reader from mapper.reader() FAIL_ON_UNRESOLVED_OBJECT_IDS: " +
            reader.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS));

        assertTrue(reader.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS),
                "ObjectReader from mapper.reader() should have FAIL_ON_UNRESOLVED_OBJECT_IDS enabled");

        // Now test forType
        ObjectReader typedReader = reader.forType(Owner.class);
        System.out.println("Reader from reader.forType(Owner.class) FAIL_ON_UNRESOLVED_OBJECT_IDS: " +
            typedReader.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS));

        assertTrue(typedReader.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS),
                "ObjectReader from reader.forType() should preserve FAIL_ON_UNRESOLVED_OBJECT_IDS");

        // Test withFeatures - this might be the problem!
        ObjectReader readerWithFeature = reader.withFeatures(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        System.out.println("Reader from reader.withFeatures(FAIL_ON_TRAILING_TOKENS) FAIL_ON_UNRESOLVED_OBJECT_IDS: " +
            readerWithFeature.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS));

        assertTrue(readerWithFeature.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS),
                "ObjectReader from reader.withFeatures() should preserve FAIL_ON_UNRESOLVED_OBJECT_IDS");

        // Create provider
        final JacksonJsonProvider jsonProvider = new JacksonJsonProvider(objectMapper);

        // Get the mapper from the provider
        ObjectMapper retrievedMapper = jsonProvider.locateMapper(Owner.class, MediaType.APPLICATION_JSON_TYPE);
        assertNotNull(retrievedMapper, "Provider should return a mapper");

        System.out.println("Original mapper FAIL_ON_UNRESOLVED_OBJECT_IDS: " +
            objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS));
        System.out.println("Retrieved mapper FAIL_ON_UNRESOLVED_OBJECT_IDS: " +
            retrievedMapper.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS));
        System.out.println("Mappers are same instance: " + (objectMapper == retrievedMapper));
    }

    @Test
    public void should_honor_mapper_configuration_with_provider() throws Exception {
        // Setup for JAX-RS provider usage
        @SuppressWarnings("unchecked")
        final Class<Object> type = (Class<Object>) (Class<?>) Owner.class;
        final MultivaluedHashMap<String, String> httpHeaders = new MultivaluedHashMap<>();
        final Annotation[] annotations = new Annotation[] {};
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);
        outputStream.write(PAYLOAD.getBytes(StandardCharsets.UTF_8));

        // Configure ObjectMapper to FAIL on unresolved object IDs
        final ObjectMapper objectMapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, true)
                .build();

        // Verify the ObjectMapper is configured correctly
        assertTrue(objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS),
                "ObjectMapper should have FAIL_ON_UNRESOLVED_OBJECT_IDS enabled");

        final JacksonJsonProvider jsonProvider = new JacksonJsonProvider(objectMapper);

        // The provider should respect the ObjectMapper configuration and throw exception
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
            JacksonException exception = assertThrows(JacksonException.class,
                () -> jsonProvider.readFrom(type, type, annotations, MediaType.APPLICATION_JSON_TYPE, httpHeaders, inputStream));

            // Verify it's actually failing due to unresolved object ID
            assertTrue(exception.getMessage().contains("Unresolved") ||
                      exception.getMessage().contains("not a valid") ||
                      exception.getMessage().contains("reference"),
                      "Expected exception about unresolved reference but got: " + exception.getMessage());
        }
    }

    @Test
    public void should_honor_lenient_mapper_configuration_with_provider() throws Exception {
        // Setup for JAX-RS provider usage
        @SuppressWarnings("unchecked")
        final Class<Object> type = (Class<Object>) (Class<?>) Owner.class;
        final MultivaluedHashMap<String, String> httpHeaders = new MultivaluedHashMap<String, String>();
        final Annotation[] annotations = new Annotation[] {};
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);
        outputStream.write(PAYLOAD.getBytes(StandardCharsets.UTF_8));

        // Configure ObjectMapper to NOT FAIL on unresolved object IDs
        final ObjectMapper objectMapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false)
                .build();
        final JacksonJsonProvider jsonProvider = new JacksonJsonProvider(objectMapper);

        // The provider should respect the ObjectMapper configuration and allow nulls
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
            final Object object = jsonProvider.readFrom(type, type, annotations, MediaType.APPLICATION_JSON_TYPE, httpHeaders, inputStream);
            final Owner owner = (Owner) object;

            // Verify lenient behavior - invalid references should be null
            assertEquals(4, owner.owned.size());
            assertEquals(Integer.valueOf(1), owner.owned.get(0).optionalValue().map(v -> v.value).orElse(null));
            assertNull(owner.owned.get(1).optionalValue); // Invalid reference -> null
            assertNull(owner.owned.get(2).optionalValue); // No reference -> null
            assertEquals(Integer.valueOf(3), owner.owned.get(3).optionalValue().map(v -> v.value).orElse(null));
        }
    }
}
