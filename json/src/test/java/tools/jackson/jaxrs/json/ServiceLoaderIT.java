package tools.jackson.jaxrs.json;

import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceLoaderIT {
    @Test
    public void testMessageBodyWriter() {
        @SuppressWarnings("rawtypes")
        final List<MessageBodyWriter> providers = ServiceLoader
            .load(MessageBodyWriter.class)
            .stream()
            .map(Provider<MessageBodyWriter>::get)
            .toList();

        assertEquals(providers
            .stream()
            .map(w -> w.getClass())
            .sorted(Comparator.comparing(Class::getSimpleName)).toList(), List.of(
                tools.jackson.jaxrs.json.JacksonJsonProvider.class
            ));
    }

    @Test
    public void testMessageBodyReader() {
        @SuppressWarnings("rawtypes")
        final List<MessageBodyReader> providers = ServiceLoader
            .load(MessageBodyReader.class)
            .stream()
            .map(Provider<MessageBodyReader>::get)
            .toList();

        assertEquals(providers
            .stream()
            .map(w -> w.getClass())
            .sorted(Comparator.comparing(Class::getSimpleName)).toList(), List.of(
                tools.jackson.jaxrs.json.JacksonJsonProvider.class
            ));
    }
}
