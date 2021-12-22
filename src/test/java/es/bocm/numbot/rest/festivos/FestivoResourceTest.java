package es.bocm.numbot.rest.festivos;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.bocm.numbot.entities.Festivo;
import es.bocm.numbot.entities.FestivoDao;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FestivoResourceTest {
    @Mock
    private FestivoDao mockFestDao;

    @InjectMocks
    private FestivoResource festRest;

    @Test
    void producesCorrectInvalidYearResponse() {
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Año con formato incorrecto. " +
                "El formato debe ser YYYY\"}}";
        Response response = festRest.getFestivos("badYear");
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectUnknownErrorGetResponse() {
        when(mockFestDao.buscarFestivosPorAnno(anyInt())).thenThrow(PersistenceException.class);
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Error desconocido. con el administrador de sistemas" +
                " para que revise la conexión con la BBDD y otras posibles causas.\"}}";
        Response response = festRest.getFestivos("2021");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectResponseWithNoData() {
        int anno = 2021;
        when(mockFestDao.buscarFestivosPorAnno(anno)).thenReturn(Collections.emptyList());
        String expected_str = "{\"exito\":false,\"data\":{\"error\":\"Faltan datos en la BBDD para procesar " +
                "la petición: no están establecidos los festivos de este año.\"}}";
        JsonObject expected = JsonParser.parseString(expected_str).getAsJsonObject();
        Response response = festRest.getFestivos(String.valueOf(anno));
        assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        JsonObject res = JsonParser.parseString(json_res).getAsJsonObject();
        assertEquals(expected, res);
    }

    @Test
    void producesCorrectResponseWithData() {
        int anno = 2021;
        List<Festivo> fests = List.of(
                new Festivo(null, LocalDate.of(anno, 3, 1), "desc1"),
                new Festivo(null, LocalDate.of(anno, 5, 14), "desc2")
        );
        when(mockFestDao.buscarFestivosPorAnno(anno)).thenReturn(fests);
        String expected_str = "{\"exito\":true,\"data\":{\"festivos\":[{\"fecha\":\"03-01\"," +
                "\"descripcion\":\"desc1\"},{\"fecha\":\"05-14\",\"descripcion\":\"desc2\"}]}}";
        JsonObject expected = JsonParser.parseString(expected_str).getAsJsonObject();
        Response response = festRest.getFestivos(String.valueOf(anno));
        assertEquals(Response.Status.OK, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        JsonObject res = JsonParser.parseString(json_res).getAsJsonObject();
        assertEquals(expected, res);
    }

    @Test
    void producesCorrectInvalidYearPutResponse() {
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Año con formato incorrecto. " +
                "El formato debe ser YYYY\"}}";
        Response response = festRest.createOrUpdateFestivos("badYear", "");
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
        Response response = festRest.createOrUpdateFestivos("1921", json_input);
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectUnknownErrorPutResponse() {
        String json_input = "[{\"descripcion\": \"Festivo en fecha correcta\",  \"fecha\": \"12-06\"}]";
        when(mockFestDao.buscarFestivosPorAnno(anyInt())).thenReturn(Collections.emptyList());
        doThrow(PersistenceException.class).when(mockFestDao).borrarFestivos(anyCollection());
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Error desconocido. con el administrador de sistemas" +
                " para que revise la conexión con la BBDD y otras posibles causas.\"}}";
        Response response = festRest.createOrUpdateFestivos("2021", json_input);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectPutResponse() {
        int anno = 2021;
        String json_input = "[{\"descripcion\": \"Festivo en fecha correcta\",  \"fecha\": \"12-06\"}]";
        Festivo fest = new Festivo(null, LocalDate.of(anno, 12, 6), "descripcion");
        List<Festivo> festivos = List.of(fest);

        when(mockFestDao.buscarFestivosPorAnno(anyInt())).thenReturn(festivos);
        doNothing().when(mockFestDao).borrarFestivos(anyCollection());
        doNothing().when(mockFestDao).crearFestivos(anyCollection());

        String expected_str = "{\"exito\":true,\"data\":{\"festivos\":[{\"descripcion\":" +
                "\"Festivo en fecha correcta\",\"fecha\":\"12-06\"}]}}";
        JsonObject expected = JsonParser.parseString(expected_str).getAsJsonObject();

        Response response = festRest.createOrUpdateFestivos(String.valueOf(anno), json_input);
        assertEquals(Response.Status.OK, response.getStatusInfo());
        verify(mockFestDao).buscarFestivosPorAnno(anno);
        verify(mockFestDao).borrarFestivos(festivos);
        verify(mockFestDao).crearFestivos(anyCollection());

        String json_res = response.readEntity(String.class);
        JsonObject res = JsonParser.parseString(json_res).getAsJsonObject();
        assertEquals(expected, res);
    }
}
