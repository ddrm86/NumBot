package es.bocm.numbot.rest;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

class ExtraordinarioResourceTest {
    private static UndertowJaxrsServer server;

    @Path("/test")
    public static class Resource
    {
        @GET
        @Produces("text/plain")
        public String get()
        {
            return "hello world";
        }
    }

    @ApplicationPath("/base")
    public static class MyApp extends Application
    {
        @Override
        public Set<Class<?>> getClasses()
        {
            HashSet<Class<?>> classes = new HashSet<>();
            classes.add(Resource.class);
            return classes;
        }
    }

    @BeforeAll
    static void beforeAll() {
        server = new UndertowJaxrsServer().start();
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @Test
    void testApplicationPath()
    {
        server.deployOldStyle(MyApp.class);
        Client client = ClientBuilder.newClient();
        String val = client.target(TestPortProvider.generateURL("/base/test"))
                .request().get(String.class);
        assertEquals("hello world", val);
        client.close();
    }
}
