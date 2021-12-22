package es.bocm.numbot.rest.festivos;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import es.bocm.numbot.entities.Festivo;
import es.bocm.numbot.entities.FestivoDao;
import es.bocm.numbot.rest.ErrorResponse;
import jakarta.inject.Inject;
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

/**
 * <p>Recurso REST para la consulta y actualización de los días festivos de un año.<p>
 *
 * Ejemplo de consulta:
 *
 * <pre>
 * GET: /festivos/2021
 *
 * HTTP/1.1 200 OK
 * Content-Type: application/json
 * {
 *  "exito": "true",
 *  "data": {
 *      "festivos":
 *          "[{"descripcion": "Reyes Magos",  "fecha": "01-06"},
 *          {"descripcion": "Constitucion", "fecha": "12-06"},
 *          {"descripcion": "Inmaculada",   "fecha": "12-08"}]"
 *  }
 * }
 * </pre>
 *
 * Ejemplo de actualización:
 *
 * <pre>
 * PUT: /festivos/2021
 * [{"descripcion": "Reyes Magos",  "fecha": "06-01"}, {"descripcion": "Constitucion", "fecha": "12-06"}]
 *
 * HTTP/1.1 200 OK
 * Content-Type: application/json
 * {
 * 	"exito": "true",
 * 	"data": {
 * 		"festivos":
 * 	        "[{"descripcion": "Reyes Magos",  "fecha": "06-01"},
 *          {"descripcion": "Constitucion", "fecha": "12-06"}]"
 * 	}
 * }
 * </pre>
 *
 * <b>Se borran los festivos anteriores para el año y se reemplazan con los nuevos.</b>
 *
 */
@Path("/festivos")
public class FestivoResource {
    @Inject
    FestivoDao festDao;

    /**
     * Obtiene información sobre los festivos de un año.
     *
     * @param anno el año en formato YYYY.
     * @return la información solicitada o mensaje de error.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{anno}")
    public Response getFestivos(@PathParam("anno") String anno) {
        if (esAnnoNoValido(anno)) {
            return crearRespuestaAnnoNoValido();
        } else {
            List<Festivo> festivos;
            try {
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

    /**
     * Crea una respuesta exitosa, es decir, se pudo obtener la información solicitada.
     *
     * @param festivos los festivos a incluir en la respuesta.
     * @return la información solicitada.
     */
    private static Response crearRespuestaExitosa(Collection<Festivo> festivos) {
        List<Map<String, String>> data = festivos.stream().map(Festivo::toMap).toList();
        FestivoResponse response = new FestivoResponse(data);
        return crearRespuestaJson(Response.Status.OK, response);
    }

    /**
     * Actualiza los festivos de un año determinado, borrando los anteriores si existen.
     *
     * @param anno el año en formato YYYY.
     * @param festivos_json los nuevos festivos para ese año.
     * @return la información actualizada o mensaje de error.
     */
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
                List<Festivo> festivos_antiguos = festDao.buscarFestivosPorAnno(Integer.parseInt(anno));
                festDao.borrarFestivos(festivos_antiguos);
                festDao.crearFestivos(festivos_nuevos);
            } catch (Exception e) {
                return crearRespuestaErrorDesconocido();
            }
            return crearRespuestaExitosa(festivos_nuevos);
        }
    }

    /**
     * Crea los objetos Festivo con la información obtenida desde el endpoint.
     *
     * @param anno el año en formato YYYY.
     * @param festivos_json los festivos para ese año en el formato de entrada del endpoint.
     * @return los objetos Festivo creados.
     */
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
