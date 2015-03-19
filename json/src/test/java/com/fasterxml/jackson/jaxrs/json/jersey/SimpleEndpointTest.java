package com.fasterxml.jackson.jaxrs.json.jersey;

import javax.servlet.Servlet;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.jaxrs.json.dw.SimpleEndpointTestBase;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import org.eclipse.jetty.server.Server;

public class SimpleEndpointTest extends SimpleEndpointTestBase {
    @Override
    protected Class<? extends Servlet> servletContainerClass() { return ServletContainer.class; }

    // 18-Mar-2015, tatu: Should go back in base class once resolved; but for now there is no need
    //    to make it fail for multiple variants.

    public void testDynamicTypingList() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        Server server = startServer(TEST_PORT, SimpleDynamicTypingApp.class);
        List<ExtendedPoint> l;

        try {
            InputStream in = new URL("http://localhost:"+TEST_PORT+"/dynamic/list").openStream();
            l = mapper.readValue(in, new TypeReference<List<ExtendedPoint>>() { });
            in.close();
        } finally {
            server.stop();
        }
        assertNotNull(l);
        assertEquals(1, l.size());

        // ensure we got a valid Point
        ExtendedPoint p = l.get(0);
        assertEquals(1, p.x);
        assertEquals(2, p.y);
        if (p.z != 3) {
            fail("Expected p.z == 3, was "+p.z+"; most likely due to incorrect serialization using base type (issue #60)");
        }
    }
}
