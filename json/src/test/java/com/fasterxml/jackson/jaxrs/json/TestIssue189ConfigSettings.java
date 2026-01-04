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

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test case for https://github.com/FasterXML/jackson-jaxrs-providers/issues/189
 * <p>
 * JacksonJsonProvider appears to ignore DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS
 * configuration set on the ObjectMapper.
 */
public class TestIssue189ConfigSettings {

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "name", scope = Value.class)
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

        // By default, should throw exception on unresolved object IDs
        // Let's actually try to read it and see what happens
        try {
            /*Owner owner =*/ objectMapper.readValue(PAYLOAD, Owner.class);
            // If we get here without exception, fail the test
            fail("Expected JacksonException but parsing succeeded");
        } catch (UnresolvedForwardReference e) {
            // This is expected
        }
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
            UnresolvedForwardReference exception = assertThrows(UnresolvedForwardReference.class,
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
        final MultivaluedHashMap<String, String> httpHeaders = new MultivaluedHashMap<>();
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
