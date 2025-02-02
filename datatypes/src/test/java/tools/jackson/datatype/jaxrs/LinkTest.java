package tools.jackson.datatype.jaxrs;

import javax.ws.rs.core.Link;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class LinkTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
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
