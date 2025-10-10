package tools.jackson.datatype.jaxrs;

import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.JacksonModule;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceLoaderIT {
    @Test
    public void testJacksonModule() {
        final List<JacksonModule> providers = ServiceLoader
            .load(JacksonModule.class)
            .stream()
            .map(Provider<JacksonModule>::get)
            .toList();

        assertEquals(providers
            .stream()
            .map(w -> w.getClass())
            .sorted(Comparator.comparing(Class::getSimpleName)).toList(), List.of(
                tools.jackson.datatype.jaxrs.Jaxrs2TypesModule.class
            ));
    }
}
