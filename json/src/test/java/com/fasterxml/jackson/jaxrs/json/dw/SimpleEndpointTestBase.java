package com.fasterxml.jackson.jaxrs.json.dw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eclipse.jetty.server.Server;
import org.junit.Assert;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public abstract class SimpleEndpointTestBase extends ResourceTestBase
{
    final protected static int TEST_PORT = 6011;
    
    static protected class Point {
        public int x, y;

        protected Point() { }
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static protected class ExtendedPoint extends Point {
        public int z;

        protected ExtendedPoint() { }
        public ExtendedPoint(int x, int y, int z) {
            super(x, y);
            this.z = z;
        }
    }

	protected static abstract class Page<E> {

		public static final String PREV_PAGE_REL = "prev";
		public static final String NEXT_PAGE_REL = "next";

		public final Link getPreviousPageLink() {
			return getLink(PREV_PAGE_REL);
		}

		public final Link getNextPageLink() {
			return getLink(NEXT_PAGE_REL);
		}

		public abstract List<E> getEntities();

		public abstract Link getLink(String rel);

	}

	@JsonPropertyOrder({ "entities", "links" })
	@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
	protected static class PageImpl<E> extends Page<E> {

		protected static class JsonLinkSerializer extends JsonSerializer<javax.ws.rs.core.Link> {

			static final String HREF_PROPERTY = "href";

			@Override
			public void serialize(Link link, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
					throws IOException {
				jsonGenerator.writeStartObject();
				jsonGenerator.writeStringField(HREF_PROPERTY, link.getUri().toString());
				for (Entry<String, String> entry : link.getParams().entrySet()) {
					jsonGenerator.writeStringField(entry.getKey(), entry.getValue());
				}
				jsonGenerator.writeEndObject();
			}

		}

		protected static class JsonLinkDeserializer extends JsonDeserializer<javax.ws.rs.core.Link> {

			@Override
			public Link deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
					throws IOException {
				Link link = null;
				JsonNode jsonNode = jsonParser.getCodec().<JsonNode> readTree(jsonParser);
				JsonNode hrefJsonNode = jsonNode.get(JsonLinkSerializer.HREF_PROPERTY);
				if (hrefJsonNode != null) {
					Link.Builder linkBuilder = Link.fromUri(hrefJsonNode.asText());
					Iterator<String> fieldNamesIterator = jsonNode.fieldNames();
					while (fieldNamesIterator.hasNext()) {
						String fieldName = fieldNamesIterator.next();
						if (!JsonLinkSerializer.HREF_PROPERTY.equals(fieldName)) {
							linkBuilder.param(fieldName, jsonNode.get(fieldName).asText());
						}
					}
					link = linkBuilder.build();
				}
				return link;
			}

		}

		private final List<E> entities;
		@JsonSerialize(contentUsing = JsonLinkSerializer.class)
		@JsonDeserialize(contentUsing = JsonLinkDeserializer.class)
		private final List<Link> links;

		protected PageImpl() {
			this.entities = new ArrayList<>();
			this.links = new ArrayList<>();
		}

		public void addEntities(E... entitities) {
			Collections.addAll(this.entities, entitities);
		}

		public void addLinks(Link... links) {
			Collections.addAll(this.links, links);
		}

		@Override
		public List<E> getEntities() {
			return this.entities;
		}

		@Override
		public Link getLink(String rel) {
			for (Link link : this.links) {
				if (link.getRel().equals(rel)) {
					return link;
				}
			}
			return null;
		}

	}

    @Path("/point")
    public static class SimpleResource
    {
        @Path("/javascript")
        @GET
        @Produces({ MediaType.APPLICATION_JSON, "application/javascript" })
        public Point getPointJS() {
            return new Point(1, 2);
        }

        @Path("/jsonx")
        @GET
        @Produces({ MediaType.APPLICATION_JSON, "text/x-json" })
        public Point getPointJSONX() {
            return new Point(1, 2);
        }

        @Path("/custom")
        @GET
        @Produces({ MediaType.APPLICATION_JSON, "application/vnd.com.example.v1+json" })
        public Point getPointCustomMediaType() {
            return new Point(1, 2);
        }

        @Path("/max")
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Point maxPoint(MappingIterator<Point> points) throws IOException
        {
            Point max = null;
            int maxDist = 0;
            while (points.hasNextValue()) {
                Point p = points.nextValue();
                int dist = _distance(p);
                if (max == null || (dist > maxDist)) {
                    maxDist = dist;
                    max = p;
                }
            }
            return max;
        }

        private int _distance(Point p) {
            return (p.x * p.x) + (p.y * p.y);
        }
    }

    public static class SimpleResourceApp extends JsonApplicationWithJackson {
        public SimpleResourceApp() { super(new SimpleResource()); }
    }

    private final static String UNTOUCHABLE_RESPONSE = "[1]";

    @Path("/raw")
    public static class RawResource
    {
        @GET
        @Produces({ MediaType.APPLICATION_JSON, "application/javascript" })
        @Path("string")
        public String getString() {
            return UNTOUCHABLE_RESPONSE;
        }

        @GET
        @Path("bytes")
        @Produces({ MediaType.APPLICATION_JSON, "application/javascript" })
        public byte[] getBytes() throws IOException {
            return UNTOUCHABLE_RESPONSE.getBytes("UTF-8");
        }
    }

    @Path("/fluff")
    public static class FluffyResource
    {
        @GET
        @Produces({ MediaType.APPLICATION_OCTET_STREAM })
        @Path("bytes")
        public StreamingOutput getFluff(@QueryParam("size") final long size) {
            if (size <= 0L) {
                throw new IllegalArgumentException("Missing 'size'");
            }
            return new StreamingOutput() {
                @Override
                public void write(OutputStream output) throws IOException {
                    byte[] buf = new byte[1024];
                    for (int i = 0, end = buf.length; i < end; ++i) {
                        buf[i] = (byte) i;
                    }
                    long left = size;

                    while (left > 0) {
                        int len;
                        if (left >= buf.length) {
                            len = buf.length;
                        } else {
                            len = (int) left;
                        }
                        output.write(buf, 0, len);
                        left -= len;
                    }
                }
            };
        }
    }

    @Path("/dynamic")
    public static class DynamicTypingResource
    {
		@Context
		private UriInfo uriInfo;

        @GET
        @Path("single")
        @Produces(MediaType.APPLICATION_JSON)
        public Point getPoint() {
            return new ExtendedPoint(1, 2, 3);
        }

        @GET
        @Path("list")
        @Produces(MediaType.APPLICATION_JSON)
        public List<Point> getPoints() {
            return Arrays.asList(getPoint());
        }

		@GET
		@Path("genericPageEntity")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getPointsAsGenericPageEntity() {
			PageImpl<Point> page = new PageImpl<>();
			page.addEntities(getPoint());
			URI selfUri = UriBuilder.fromUri(this.uriInfo.getBaseUri()).path(DynamicTypingResource.class)
					.path(DynamicTypingResource.class, "getPointsAsGenericPageEntity").build();
			page.addLinks(Link.fromUri(selfUri).rel("self").build());
			return Response.ok(new GenericEntity<Page<Point>>(page) {
			}).build();
		}

		@GET
		@Path("genericPageImplEntity")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getPointsAsGenericPageImplEntity() {
			PageImpl<Point> page = new PageImpl<>();
			page.addEntities(getPoint());
			URI selfUri = UriBuilder.fromUri(this.uriInfo.getBaseUri()).path(DynamicTypingResource.class)
					.path(DynamicTypingResource.class, "getPointsAsGenericPageImplEntity").build();
			page.addLinks(Link.fromUri(selfUri).rel("self").build());
			return Response.ok(new GenericEntity<PageImpl<Point>>(page) {
			}).build();
		}

		@GET
		@Path("genericCollectionEntity")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getPointsAsGenericCollectionEntity() {
			Collection<Point> list = new ArrayList<>();
			list.add(getPoint());
			return Response.ok(new GenericEntity<Collection<Point>>(list) {
			}).build();
		}

		@GET
		@Path("genericCollectionImplEntity")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getPointsAsGenericCollectionImplEntity() {
			ArrayList<Point> list = new ArrayList<>();
			list.add(getPoint());
			return Response.ok(new GenericEntity<ArrayList<Point>>(list) {
			}).build();
		}
		
    }

    public static class SimpleRawApp extends JsonApplicationWithJackson {
        public SimpleRawApp() { super(new RawResource()); }
    }

    public static class SimpleFluffyApp extends JsonApplicationWithJackson {
        public SimpleFluffyApp() { super(new FluffyResource()); }
    }

    public static class SimpleDynamicTypingApp extends JsonApplicationWithJackson {
        public SimpleDynamicTypingApp() { super(new DynamicTypingResource()); }
    }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testStandardJson() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        Server server = startServer(TEST_PORT, SimpleResourceApp.class);
        Point p;

        try {
            InputStream in = new URL("http://localhost:"+TEST_PORT+"/point/javascript").openStream();
            p = mapper.readValue(in, Point.class);
            in.close();
        } finally {
            server.stop();
        }
        // ensure we got a valid Point
        assertNotNull(p);
        assertEquals(1, p.x);
        assertEquals(2, p.y);
    }

    public void testAcceptJavascriptType() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        Server server = startServer(TEST_PORT, SimpleResourceApp.class);
        URL urlJS = new URL("http://localhost:"+TEST_PORT+"/point/javascript");
        URL urlJsonX = new URL("http://localhost:"+TEST_PORT+"/point/jsonx");
        URL urlCustom = new URL("http://localhost:"+TEST_PORT+"/point/custom");

        try {
            HttpURLConnection conn = (HttpURLConnection) urlJS.openConnection();

            // First: verify that weird types are not supported...
            conn.setRequestProperty("Accept", "foo/bar");
            conn.connect();
            assertEquals(HttpURLConnection.HTTP_NOT_ACCEPTABLE, conn.getResponseCode());
            conn.disconnect();

            // try again with somewhat non-standard, but supported JSON-like type (application/javascript)
            conn = (HttpURLConnection) urlJS.openConnection();
            conn.setRequestProperty("Accept", "application/javascript");
            assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            InputStream in = conn.getInputStream();
            Point p;
            try {
                p = mapper.readValue(in, Point.class);
            } finally {
                in.close();
            }
            assertNotNull(p);
            assertEquals(1, p.x);
            assertEquals(2, p.y);
            conn.disconnect();

            // [Issue#40]: another oddball type to consider
            conn = (HttpURLConnection) urlJsonX.openConnection();
            conn.setRequestProperty("Accept", "text/x-json");
            assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            in = conn.getInputStream();
            p = null;
            try {
                p = mapper.readValue(in, Point.class);
            } finally {
                in.close();
            }
            assertNotNull(p);
            assertEquals(1, p.x);
            assertEquals(2, p.y);
            conn.disconnect();

            conn = (HttpURLConnection) urlCustom.openConnection();
            conn.setRequestProperty("Accept", "application/vnd.com.example.v1+json");
            assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            in = conn.getInputStream();
            p = null;
            try {
                p = mapper.readValue(in, Point.class);
            } finally {
                in.close();
            }
            assertNotNull(p);
            assertEquals(1, p.x);
            assertEquals(2, p.y);
            conn.disconnect();
        } finally {
            server.stop();
        }
    }

    /*
        @Path("/max")
        @POST
        @Produces(MediaType.APPLICATION_JSON)
        public Point maxPoint(MappingIterator<Point> points) throws IOException
        {
     */
    
    // [jaxrs-providers#69]
    public void testMappingIterator() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        Server server = startServer(TEST_PORT, SimpleResourceApp.class);
        Point p;

        try {
            URL url = new URL("http://localhost:"+TEST_PORT+"/point/max");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
            conn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            OutputStream out = conn.getOutputStream();
            out.write(aposToQuotes("{'x':1,'y':1}\n{'y':4,'x':-4}{'x':2,'y':5}"
                    ).getBytes("UTF-8"));
            out.close();
            InputStream in = conn.getInputStream();
            p = mapper.readValue(in, Point.class);
            in.close();
        } finally {
            server.stop();
        }
        // ensure we got a valid Point
        assertNotNull(p);
        assertEquals(-4, p.x);
        assertEquals(4, p.y);
    }

    // [Issue#34] Verify that Untouchables act the way as they should
    @SuppressWarnings("resource")
    public void testUntouchables() throws Exception
    {
        Server server = startServer(TEST_PORT, SimpleRawApp.class);
        try {
            InputStream in = new URL("http://localhost:"+TEST_PORT+"/raw/string").openStream();
            assertEquals(UNTOUCHABLE_RESPONSE, readUTF8(in));

            in = new URL("http://localhost:"+TEST_PORT+"/raw/bytes").openStream();
            Assert.assertArrayEquals(UNTOUCHABLE_RESPONSE.getBytes("UTF-8"), readAll(in));
        } finally {
            server.stop();
        }
    }

    /**
     * Test that exercises underlying JAX-RS container by reading/writing 500 megs of fluff;
     * but does not do actual data format content. Goal being to ensure that
     * <code>StreamingOutput</code> works as expected even if provider is registered.
     */
    public void testHugeFluffyContent() throws Exception
    {
        Server server = startServer(TEST_PORT, SimpleFluffyApp.class);
        try {
            // Let's try with 4.5 gigs, just to be sure (should run OOME if buffering; or be
            // super slow if disk-backed buffering)
            final long size = 4500 * 1024 * 1024;
            InputStream in = new URL("http://localhost:"+TEST_PORT+"/fluff/bytes?size="+size).openStream();
            byte[] stuff = new byte[64000];
            long total = 0L;
            int count;

            while ((count = in.read(stuff)) > 0) {
                // verify contents, too
                for (int i = 0; i < count; ++i) {
                    int exp = ((int) total) & 0xFF;
                    int act = stuff[i] & 0xFF;
                    if (exp != act) {
                        fail("Content differs at #"+Long.toHexString(total)+"; got 0x"+Integer.toHexString(act)
                                +", expected 0x"+Integer.toHexString(exp));
                    }
                    ++total;
                }
                
            }
            in.close();

            assertEquals(size, total);
            
        } finally {
            server.stop();
        }
    }

    public void testDynamicTypingSingle() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        Server server = startServer(TEST_PORT, SimpleDynamicTypingApp.class);
        ExtendedPoint p;

        try {
            InputStream in = new URL("http://localhost:"+TEST_PORT+"/dynamic/single").openStream();
            p = mapper.readValue(in, ExtendedPoint.class);
            in.close();
        } finally {
            server.stop();
        }
        // ensure we got a valid Point
        assertNotNull(p);
        assertEquals(1, p.x);
        assertEquals(2, p.y);
        assertEquals(3, p.z);
    }

    // for [#60], problems with non-polymorphic Lists
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

	// for [#87], problems with GenericEntity where type != rawType
	public void testDynamicTypingGenericPageEntity() throws Exception {
		testDynamicTypingPage(URI.create("http://localhost:" + TEST_PORT + "/dynamic/genericPageEntity"));
	}

	// for [#87], problems with GenericEntity where type != rawType
	public void testDynamicTypingGenericPageImplEntity() throws Exception {
		testDynamicTypingPage(URI.create("http://localhost:" + TEST_PORT + "/dynamic/genericPageImplEntity"));
	}

	// for [#87], problems with GenericEntity where type != rawType
	private void testDynamicTypingPage(URI uri) throws Exception {
		Server server = startServer(TEST_PORT, SimpleDynamicTypingApp.class);
		Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
		try {
			Builder invocationBuilder = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE);

			// Test that JSON serialization on server side correctly take
			// @JsonAutoDetect into account
			String response = invocationBuilder.get(String.class);
			assertFalse(response.contains("previousPageLink"));

			PageImpl<ExtendedPoint> page = invocationBuilder.get(new GenericType<PageImpl<ExtendedPoint>>() {
			});
			Link expectedLink = Link.fromUri(uri).rel("self").build();
			Link currentLink = page.getLink("self");
			assertEquals(expectedLink, currentLink);
		} finally {
			server.stop();
			client.close();
		}
	}

	// for [#87], problems with GenericEntity where type != rawType
	public void testDynamicTypingGenericCollectionEntity() throws Exception {
		testDynamicTypingCollection(URI.create("http://localhost:" + TEST_PORT + "/dynamic/genericCollectionEntity"));
	}

	// for [#87], problems with GenericEntity where type != rawType
	public void testDynamicTypingGenericCollectionImplEntity() throws Exception {
		testDynamicTypingCollection(URI
				.create("http://localhost:" + TEST_PORT + "/dynamic/genericCollectionImplEntity"));
	}

	// for [#87], problems with GenericEntity where type != rawType
	private void testDynamicTypingCollection(URI uri) throws Exception {
		Server server = startServer(TEST_PORT, SimpleDynamicTypingApp.class);
		Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
		try {
			ArrayList<ExtendedPoint> collection = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE)
					.get(new GenericType<ArrayList<ExtendedPoint>>() {
					});
			assertNotNull(collection);
			assertEquals(1, collection.size());
			// ensure we got a valid Point
			ExtendedPoint p = collection.iterator().next();
			assertEquals(1, p.x);
			assertEquals(2, p.y);
			if (p.z != 3) {
				fail("Expected p.z == 3, was " + p.z
						+ "; most likely due to incorrect serialization using base type (issue #60)");
			}
		} finally {
			server.stop();
			client.close();
		}
	}

}
