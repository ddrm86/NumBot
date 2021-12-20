package es.bocm.numbot.rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.bocm.numbot.entities.Festivo;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FestivoResourceTest {
    private static UndertowJaxrsServer server;
    private static Client client;

    @ApplicationPath("/test")
    public static class MyApp extends Application
    {
        @Override
        public Set<Class<?>> getClasses()
        {
            HashSet<Class<?>> classes = new HashSet<>();
            classes.add(FestivoResource.class);
            return classes;
        }
    }

    @BeforeAll
    static void beforeAll() {
        server = new UndertowJaxrsServer().start();
        server.deployOldStyle(MyApp.class);
        client = ClientBuilder.newClient();

    }

    @AfterAll
    static void afterAll() {
        client.close();
        server.stop();
    }

    @ParameterizedTest
    @ValueSource(strings = {"asdf", "1", "12", "123", "123a"})
    void producesCorrectInvalidYearResponse(String anno) {
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Año con formato incorrecto. " +
                "El formato debe ser YYYY\"}}";
        Response response = client.target(TestPortProvider
                .generateURL("/test/festivos/" + anno)).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectUnknownErrorGetResponse() {
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Error desconocido. con el administrador de sistemas" +
                " para que revise la conexión con la BBDD y otras posibles causas.\"}}";
        // Will not be able to connect to the database
        Response response = client.target(TestPortProvider
                .generateURL("/test/festivos/1000")).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectResponseWithData() {
        List<Festivo> fests = List.of(
                new Festivo(null, LocalDate.of(2021, 3, 1), "desc1"),
                new Festivo(null, LocalDate.of(2021, 5, 14), "desc2")
        );
        String expected_str = "{\"exito\":true,\"data\":{\"festivos\":[{\"fecha\":\"03-01\"," +
                "\"descripcion\":\"desc1\"},{\"fecha\":\"05-14\",\"descripcion\":\"desc2\"}]}}";
        JsonObject expected = JsonParser.parseString(expected_str).getAsJsonObject();
        Response response = FestivoResource.crearRespuestaExitosa(fests);
        assertEquals(Response.Status.OK, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        JsonObject res = JsonParser.parseString(json_res).getAsJsonObject();
        assertEquals(expected, res);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asdf", "1", "12", "123", "123a"})
    void producesCorrectInvalidYearPutResponse(String anno) {
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Año con formato incorrecto. " +
                "El formato debe ser YYYY\"}}";
        Response response = client.target(TestPortProvider
                .generateURL("/test/festivos/" + anno)).request(MediaType.APPLICATION_JSON)
                .put(Entity.json(""));
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectInvalidDataResponse() {
        String json_input = "[{\"descripcion\": \"Festivo en sábado\",  \"fecha\": \"01-08\"}]";
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Festivos con formato o fecha no válida. " +
                "El formato debe ser [{\\\"descripcion\\\": \\\"descripcion festivo 1\\\", \\\"fecha\\\":" +
                " \\\"MM-DD\\\"}, {\\\"descripcion\\\": \\\"descripcion festivo 2\\\", \\\"fecha\\\": \\\"MM-DD\\\"}]" +
                ". No se deben incluir festivos que caen en sábado ni en domingo, o en los días que no hay boletín " +
                "(1 de enero, 25 de diciembre y Viernes Santo)\"}}";
        Response response = client.target(TestPortProvider
                        .generateURL("/test/festivos/1921")).request(MediaType.APPLICATION_JSON)
                .put(Entity.json(json_input));
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }
}
