package es.bocm.numbot.rest;

import es.bocm.numbot.entities.Extraordinario;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ExtraordinarioResourceTest {
    private static UndertowJaxrsServer server;

    @ApplicationPath("/test")
    public static class MyApp extends Application
    {
        @Override
        public Set<Class<?>> getClasses()
        {
            HashSet<Class<?>> classes = new HashSet<>();
            classes.add(ExtraordinarioResource.class);
            return classes;
        }
    }

    @BeforeAll
    static void beforeAll() {
        server = new UndertowJaxrsServer().start();
        server.deployOldStyle(MyApp.class);
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @ParameterizedTest
    @ValueSource(strings = {"asdf", "1", "12", "123", "123a"})
    void producesCorrectInvalidYearResponse(String anno) {
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Año con formato incorrecto. " +
                "El formato debe ser YYYY\"}}";
        Client client = ClientBuilder.newClient();
        Response response = client.target(TestPortProvider
                .generateURL("/test/extraordinarios/" + anno)).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectUnknownErrorResponse() {
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Error desconocido. con el administrador de sistemas" +
                " para que revise la conexión con la BBDD y otras posibles causas.\"}}";
        Client client = ClientBuilder.newClient();
        // Will not be able to connect to the database
        Response response = client.target(TestPortProvider
                .generateURL("/test/extraordinarios/1000")).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectNoExtsResponse() {
        String expected = "{\"exito\":true,\"data\":{\"extraordinarios\":[]}}";
        Response response = ExtraordinarioResource.crearRespuestaExitosa(Collections.emptyList());
        assertEquals(Response.Status.OK, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectResponseWithData() {
        List<Extraordinario> exts = List.of(
                new Extraordinario(null, LocalDate.of(2021, 3, 1), 2),
                new Extraordinario(null, LocalDate.of(2021, 5, 15), 1)
        );
        String expected = "{\"exito\":true,\"data\":{\"extraordinarios\":[{\"fecha\":\"03-01\",\"numero\":\"2\"}," +
                "{\"fecha\":\"05-15\",\"numero\":\"1\"}]}}";
        Response response = ExtraordinarioResource.crearRespuestaExitosa(exts);
        assertEquals(Response.Status.OK, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        System.out.println(json_res);
        assertEquals(expected, json_res);
    }
}
