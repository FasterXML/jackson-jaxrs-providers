package com.fasterxml.jackson.jaxrs.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

/**
 * Unit test to check [JACKSON-540]
 */
public class TestCanDeserialize extends JaxrsTestBase {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testCanSerialize() throws IOException {
		Map<String, Object> object = new LinkedHashMap<String, Object>();
		JacksonJsonProvider prov = new JacksonJsonProvider();

		String json = "{\"foo\":\"bar\"}";
		InputStream stream = new ByteArrayInputStream(json.getBytes());

		object = (Map) prov.readFrom(Object.class, object.getClass(), new Annotation[0],
				MediaType.APPLICATION_JSON_TYPE, null, stream);

		assertEquals("bar", object.get("foo"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testCanSerializeEmpty() throws IOException {
		Map<String, Object> object = new LinkedHashMap<String, Object>();
		JacksonJsonProvider prov = new JacksonJsonProvider();

		InputStream stream = new ByteArrayInputStream(new byte[0]);

		object = (Map) prov.readFrom(Object.class, object.getClass(), new Annotation[0],
				MediaType.APPLICATION_JSON_TYPE, null, stream);
		
		assertNull(object);
	}
}
