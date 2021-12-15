package es.bocm.numbot.rest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import es.bocm.numbot.entities.Festivo;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static es.bocm.numbot.rest.RestUtils.*;

@Path("/festivos")
public class FestivoResource {
    @PersistenceContext(unitName = "pu-numbot")
    private EntityManager em;
    @Resource
    private UserTransaction userTransaction;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{anno}")
    public Response getFestivos(@PathParam("anno") String anno) {
        if (esAnnoNoValido(anno)) {
            return crearRespuestaAnnoNoValido();
        } else {
            List<Festivo> festivos;
            try {
                festivos = buscarFestivosPorAnno(em, anno);
            } catch (Exception e) {
                return crearRespuestaErrorDesconocido();
            }
            if (festivos.isEmpty()) {
                ErrorResponse response = new ErrorResponse(
                        "Faltan datos en la BBDD para procesar la petición: no están establecidos los " +
                                "festivos de este año.");
                return crearRespuestaJson(Response.Status.NOT_FOUND, response);
            } else {
                List<Map<String,String>> data = festivos.stream().map(Festivo::toMap).toList();
                FestivoResponse response = new FestivoResponse(true, data);
                return crearRespuestaJson(Response.Status.OK, response);
            }
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{anno}")
    public Response createOrUpdateFestivos(@PathParam("anno") String anno, String festivos_json) {
        if (esAnnoNoValido(anno)) {
            return crearRespuestaAnnoNoValido();
        } else {
            List<Festivo> festivos_nuevos;
            try {
                festivos_nuevos = crearFestivos(anno, festivos_json);
            } catch (IllegalArgumentException | JsonSyntaxException | NullPointerException | DateTimeParseException e) {
                ErrorResponse response = new ErrorResponse("Festivos con formato o fecha no válida. " +
                        "El formato debe ser [{\"descripcion\": \"descripcion festivo 1\", \"fecha\": \"MM-DD\"}, " +
                        "{\"descripcion\": \"descripcion festivo 2\", \"fecha\": \"MM-DD\"}]. No se deben incluir " +
                        "festivos que caen en sábado ni en domingo, o en los días que no hay boletín " +
                        "(1 de enero, 25 de diciembre y Viernes Santo)");
                return crearRespuestaJson(Response.Status.BAD_REQUEST, response);
            }
            try {
                userTransaction.begin();
                List<Festivo> festivos_antiguos = buscarFestivosPorAnno(em, anno);
                festivos_antiguos.forEach(f -> em.remove(f));
                userTransaction.commit();
                userTransaction.begin();
                festivos_nuevos.forEach(f -> em.merge(f));
                userTransaction.commit();
            } catch (Exception e) {
                return crearRespuestaErrorDesconocido();
            }
            List<Map<String,String>> data = festivos_nuevos.stream().map(Festivo::toMap).toList();
            FestivoResponse response = new FestivoResponse(true, data);
            return crearRespuestaJson(Response.Status.OK, response);
        }
    }

    private List<Festivo> crearFestivos(String anno, String festivos_json) {
        List<Festivo> festivos = new ArrayList<>();
        Type type = new TypeToken<List<Map<String, String>>>(){}.getType();
        List<Map<String, String>> festivos_map = new Gson().fromJson(festivos_json, type);
        for (Map<String, String> festivo_map : festivos_map) {
            LocalDate fecha = LocalDate.parse(anno + '-' + festivo_map.get("fecha"));
            festivos.add(new Festivo(null, fecha, festivo_map.get("descripcion")));
        }
        return festivos;
    }
}
