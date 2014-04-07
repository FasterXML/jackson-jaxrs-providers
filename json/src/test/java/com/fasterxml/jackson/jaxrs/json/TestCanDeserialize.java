package com.fasterxml.jackson.jaxrs.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.jaxrs.cfg.JaxRSFeature;

/**
 * Unit test to check [JACKSON-540]
 */
public class TestCanDeserialize extends JaxrsTestBase {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testCanDeserialize() throws IOException {
		Map<String, Object> object = new LinkedHashMap<String, Object>();
		JacksonJsonProvider prov = new JacksonJsonProvider();

		String json = "{\"foo\":\"bar\"}";
		InputStream stream = new ByteArrayInputStream(json.getBytes());

		object = (Map) prov.readFrom(Object.class, object.getClass(), new Annotation[0],
				MediaType.APPLICATION_JSON_TYPE, null, stream);

		assertEquals("bar", object.get("foo"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testCanDeserializeEmpty() throws IOException {
		JacksonJsonProvider prov = new JacksonJsonProvider();

		InputStream stream = new ByteArrayInputStream(new byte[0]);
		Class<Object> type = _type(Map.class);

          Map<String, Object> result = (Map) prov.readFrom(type, type, new Annotation[0],
				MediaType.APPLICATION_JSON_TYPE, null, stream);
		
		assertNull(result);
	}

	/**
	 * Unit test for verifying functioning of {@link JaxRSFeature#ALLOW_EMPTY_INPUT}.
	 */
     public void testFailingDeserializeEmpty() throws IOException {
         JacksonJsonProvider prov = new JacksonJsonProvider();
         prov.disable(JaxRSFeature.ALLOW_EMPTY_INPUT);

         InputStream stream = new ByteArrayInputStream(new byte[0]);
         Class<Object> type = _type(Map.class);
         try {
             prov.readFrom(type, type, new Annotation[0],
                   MediaType.APPLICATION_JSON_TYPE, null, stream);
             fail("Should not succeed with passing of empty input");
         } catch (IOException e) {
             verifyException(e, "no content");
             
             final String clsName = e.getClass().getName();
             if ("javax.ws.rs.core.NoContentException".equals(clsName)) {
                 // Ideally, we'd get this
             } else if (e.getClass() == IOException.class) {
                 // but for JAX-RS 1.x this'll do
             } else {
                 fail("Unexpected exception type: "+clsName);
             }
         }
    }

    @SuppressWarnings("unchecked")
    private Class<Object> _type(Class<?> cls) {
        return (Class<Object>) cls;
    }
}
