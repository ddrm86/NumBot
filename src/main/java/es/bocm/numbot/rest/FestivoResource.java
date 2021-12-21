package es.bocm.numbot.rest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import es.bocm.numbot.entities.Festivo;
import es.bocm.numbot.entities.FestivoDao;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static es.bocm.numbot.rest.RestUtils.*;

@Path("/festivos")
public class FestivoResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{anno}")
    public Response getFestivos(@PathParam("anno") String anno) {
        if (esAnnoNoValido(anno)) {
            return crearRespuestaAnnoNoValido();
        } else {
            List<Festivo> festivos;
            try {
                FestivoDao festDao = new FestivoDao();
                festivos = festDao.buscarFestivosPorAnno(Integer.parseInt(anno));
            } catch (Exception e) {
                return crearRespuestaErrorDesconocido();
            }
            if (festivos.isEmpty()) {
                return crearRespuestaFaltanFestivos();
            } else {
                return crearRespuestaExitosa(festivos);
            }
        }
    }

    public static Response crearRespuestaExitosa(Collection<Festivo> extraordinarios) {
        List<Map<String, String>> data = extraordinarios.stream().map(Festivo::toMap).toList();
        FestivoResponse response = new FestivoResponse(data);
        return crearRespuestaJson(Response.Status.OK, response);
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
                FestivoDao festDao = new FestivoDao();
                List<Festivo> festivos_antiguos = festDao.buscarFestivosPorAnno(Integer.parseInt(anno));
                festDao.borrarFestivos(festivos_antiguos);
                festDao.crearFestivos(festivos_nuevos);
            } catch (Exception e) {
                return crearRespuestaErrorDesconocido();
            }
            return crearRespuestaExitosa(festivos_nuevos);
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
