package es.bocm.numbot.rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.bocm.numbot.entities.Extraordinario;
import es.bocm.numbot.entities.ExtraordinarioDao;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NumbotResourceTest {
    @Mock
    private FestivoDao mockFestDao;
    @Mock
    private ExtraordinarioDao mockExtDao;

    @InjectMocks
    private NumbotResource numRest;

    @Test
    void producesCorrectInvalidDateResponse() {
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Fecha errónea o con formato incorrecto. " +
                "El formato debe ser YYYY-MM-DD\"}}";
        Response response = numRest.getNumbot("badYear");
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        assertEquals(expected, json_res);
    }

    @Test
    void producesCorrectUnknownErrorResponse() {
        when(mockExtDao.buscarExtraordinariosPorAnno(anyInt())).thenThrow(PersistenceException.class);
        String expected = "{\"exito\":false,\"data\":{\"error\":\"Error desconocido. con el administrador de sistemas" +
                " para que revise la conexión con la BBDD y otras posibles causas.\"}}";
        Response response = numRest.getNumbot("2021-03-03");
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
        Response response = numRest.getNumbot("2021-03-03");
        assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
        String json_res = response.readEntity(String.class);
        JsonObject res = JsonParser.parseString(json_res).getAsJsonObject();
        assertEquals(expected, res);
    }

    @Test
    void producesCorrectResponseWithData() {
        int anno = 2021;
        List<Festivo> fests = List.of(
                new Festivo(null, LocalDate.of(anno, 1, 8), "viernes")
        );
        List<Extraordinario> exts = List.of(
                new Extraordinario(null, LocalDate.of(anno, 1, 4), 1)
        );
        when(mockFestDao.buscarFestivosPorAnno(anno)).thenReturn(fests);
        when(mockExtDao.buscarExtraordinariosPorAnno(anno)).thenReturn(exts);

        String expected_str = "{\"exito\":true,\"data\":{\"numero_boletin\":\"6\",\"" +
                "numero_boletines_en_no_laboral_seguidos\":\"2\"}}";
        JsonObject expected = JsonParser.parseString(expected_str).getAsJsonObject();

        Response response = numRest.getNumbot("2021-01-07");
        assertEquals(Response.Status.OK, response.getStatusInfo());

        String json_res = response.readEntity(String.class);
        JsonObject res = JsonParser.parseString(json_res).getAsJsonObject();

        assertEquals(expected, res);
    }
}
