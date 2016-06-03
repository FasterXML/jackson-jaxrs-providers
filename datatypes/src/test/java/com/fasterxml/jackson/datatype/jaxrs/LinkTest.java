package com.fasterxml.jackson.datatype.jaxrs;

import javax.ws.rs.core.Link;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LinkTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();
    
    public void testLink() throws Exception
    {
        Link input = Link.fromUri("http://dot.com?foo=bar")
                .type("someType")
                .build();
        String json = MAPPER.writeValueAsString(input);

        Link output = MAPPER.readValue(json, Link.class);

        assertNotNull(output);

        assertEquals(input, output);
    }
}
