package es.bocm.numbot.rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.bocm.numbot.entities.Extraordinario;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Entity;
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
    void producesCorrectUnknownErrorGetResponse() {
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
        String expected_str = "{\"exito\":true,\"data\":{\"extraordinarios\":[{\"fecha\":\"03-01\",\"numero\":\"2\"}," +
                "{\"fecha\":\"05-15\",\"numero\":\"1\"}]}}";
        JsonObject expected = JsonParser.parseString(expected_str).getAsJsonObject();
        Response response = ExtraordinarioResource.crearRespuestaExitosa(exts);
        assertEquals(Response.Status.OK, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        JsonObject res = JsonParser.parseString(json_res).getAsJsonObject();
        assertEquals(expected, res);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asdf", "1234", "2021-13-12", "2021-3-04", "2021-03-4", "2021-03-32", "2021-0a-12"})
    void producesCorrectInvalidDateResponse(String date) {
        String json_input = "";
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Fecha errónea o con formato incorrecto. " +
                "El formato debe ser YYYY-MM-DD\"}}";
        Client client = ClientBuilder.newClient();
        Response response = client.target(TestPortProvider
                .generateURL("/test/extraordinarios/" + date)).request(MediaType.APPLICATION_JSON)
                .put(Entity.json(json_input));
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asdf", "0", "-12", ""})
    void producesCorrectInvalidNumExtsResponse(String numExts) {
        String json_input = "{\"numero_extraordinarios\": \"" + numExts + "\"}";
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Formato de número de boletines extraordinarios " +
                "incorrecto. Debe ser un entero mayor o igual que cero. " +
                "Ejemplo: {\\\"numero_extraordinarios\\\": \\\"1\\\"}\"}}";
        Client client = ClientBuilder.newClient();
        Response response = client.target(TestPortProvider
                        .generateURL("/test/extraordinarios/1932-03-03")).request(MediaType.APPLICATION_JSON)
                .put(Entity.json(json_input));
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asdf", "extraordinarios", "numro_extraordinarios"})
    void producesCorrectInvalidJsonDataResponse(String key) {
        String json_input = "{\"" + key + "\": \"3\"}";
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Formato de número de boletines extraordinarios " +
                "incorrecto. Debe ser un entero mayor o igual que cero. " +
                "Ejemplo: {\\\"numero_extraordinarios\\\": \\\"1\\\"}\"}}";
        Client client = ClientBuilder.newClient();
        Response response = client.target(TestPortProvider
                        .generateURL("/test/extraordinarios/1932-03-03")).request(MediaType.APPLICATION_JSON)
                .put(Entity.json(json_input));
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }
}
