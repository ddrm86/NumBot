package es.bocm.numbot.rest.extraordinarios;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.bocm.numbot.entities.Extraordinario;
import es.bocm.numbot.entities.ExtraordinarioDao;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtraordinarioResourceTest {
    @Mock
    private ExtraordinarioDao mockExtDao;

    @InjectMocks
    private ExtraordinarioResource extRest;

    @Test
    void producesCorrectInvalidYearResponse() {
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Año con formato incorrecto. " +
                "El formato debe ser YYYY\"}}";
        Response response = extRest.getNumExtraordinarios("badYear");
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectUnknownErrorGetResponse() {
        when(mockExtDao.buscarExtraordinariosPorAnno(anyInt())).thenThrow(PersistenceException.class);
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Error desconocido. Contactar con el administrador " +
                "de sistemas para que revise la conexión con la BBDD y otras posibles causas.\"}}";
        Response response = extRest.getNumExtraordinarios("1000");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectNoExtsResponse() {
        when(mockExtDao.buscarExtraordinariosPorAnno(anyInt())).thenReturn(Collections.emptyList());
        String expected = "{\"exito\":true,\"data\":{\"extraordinarios\":[]}}";
        Response response = extRest.getNumExtraordinarios("1000");
        assertEquals(Response.Status.OK, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectResponseWithData() {
        int anno = 2021;
        List<Extraordinario> exts = List.of(
                new Extraordinario(null, LocalDate.of(anno, 3, 1), 2),
                new Extraordinario(null, LocalDate.of(anno, 5, 15), 1)
        );
        when(mockExtDao.buscarExtraordinariosPorAnno(anno)).thenReturn(exts);
        String expected_str = "{\"exito\":true,\"data\":{\"extraordinarios\":[{\"fecha\":\"03-01\",\"numero\":\"2\"}," +
                "{\"fecha\":\"05-15\",\"numero\":\"1\"}]}}";
        JsonObject expected = JsonParser.parseString(expected_str).getAsJsonObject();
        Response response = extRest.getNumExtraordinarios(String.valueOf(anno));
        assertEquals(Response.Status.OK, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        JsonObject res = JsonParser.parseString(json_res).getAsJsonObject();
        assertEquals(expected, res);
    }

    @Test
    void producesCorrectInvalidDateResponse() {
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Fecha errónea o con formato incorrecto. " +
                "El formato debe ser YYYY-MM-DD\"}}";
        Response response = extRest.createOrUpdateNumExtraordinarios("badDate", "");
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asdf", "-1", "-12", ""})
    void producesCorrectInvalidNumExtsResponse(String numExts) {
        String json_input = "{\"numero_extraordinarios\": \"" + numExts + "\"}";
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Formato de número de boletines extraordinarios " +
                "incorrecto. Debe ser un entero mayor o igual que cero. " +
                "Ejemplo: {\\\"numero_extraordinarios\\\": \\\"1\\\"}\"}}";
        Response response = extRest.createOrUpdateNumExtraordinarios("1932-03-03", json_input);
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
        Response response = extRest.createOrUpdateNumExtraordinarios("1932-03-03", json_input);
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectUnknownErrorPutResponse() {
        String json_input = "{\"numero_extraordinarios\": \"1\"}";
        when(mockExtDao.buscarPorFecha(any(LocalDate.class))).thenReturn(Optional.empty());
        doThrow(PersistenceException.class).when(mockExtDao).crearOActualizar(any(Extraordinario.class));
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Error desconocido. Contactar con el administrador" +
                " de sistemas para que revise la conexión con la BBDD y otras posibles causas.\"}}";
        Response response = extRest.createOrUpdateNumExtraordinarios("1932-03-03", json_input);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectPutCreateResponse() {
        String json_input = "{\"numero_extraordinarios\": \"1\"}";

        when(mockExtDao.buscarPorFecha(any(LocalDate.class))).thenReturn(Optional.empty());
        doNothing().when(mockExtDao).crearOActualizar(any(Extraordinario.class));

        String expected_str = "{\"exito\":true,\"data\":{\"extraordinarios\":[{\"numero\":\"1\"," +
                "\"fecha\":\"03-15\"}]}}";
        JsonObject expected = JsonParser.parseString(expected_str).getAsJsonObject();

        Response response = extRest.createOrUpdateNumExtraordinarios("1932-03-15", json_input);
        assertEquals(Response.Status.OK, response.getStatusInfo());

        String json_res = response.readEntity(String.class);
        JsonObject res = JsonParser.parseString(json_res).getAsJsonObject();
        assertEquals(expected, res);
    }

    @Test
    void producesCorrectPutUpdateResponse() {
        LocalDate fecha = LocalDate.of(1932, 3, 15);
        String json_input = "{\"numero_extraordinarios\": \"3\"}";

        Optional<Extraordinario> opt_ext = Optional.of(new Extraordinario(null, fecha, 1));
        when(mockExtDao.buscarPorFecha(any(LocalDate.class))).thenReturn(opt_ext);
        doNothing().when(mockExtDao).crearOActualizar(any(Extraordinario.class));

        String expected_str = "{\"exito\":true,\"data\":{\"extraordinarios\":[{\"numero\":\"3\"," +
                "\"fecha\":\"03-15\"}]}}";
        JsonObject expected = JsonParser.parseString(expected_str).getAsJsonObject();

        Response response = extRest.createOrUpdateNumExtraordinarios(fecha.toString(), json_input);
        assertEquals(3, opt_ext.get().getNumero());
        assertEquals(Response.Status.OK, response.getStatusInfo());

        String json_res = response.readEntity(String.class);
        JsonObject res = JsonParser.parseString(json_res).getAsJsonObject();
        assertEquals(expected, res);
    }
}
