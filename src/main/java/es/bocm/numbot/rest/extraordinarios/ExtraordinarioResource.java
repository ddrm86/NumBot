package es.bocm.numbot.rest.extraordinarios;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import es.bocm.numbot.entities.Extraordinario;
import es.bocm.numbot.entities.ExtraordinarioDao;
import es.bocm.numbot.rest.ErrorResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static es.bocm.numbot.rest.RestUtils.*;

/**
 * <p>Recurso REST para la consulta y actualización de los boletines extraordinarios.<p>
 *
 * Ejemplo de consulta:
 *
 * <pre>
 * GET: /extraordinarios/2021
 *
 * HTTP/1.1 200 OK
 * Content-Type: application/json
 * {
 * 	"exito": "true",
 * 	"data": {
 * 		"extraordinarios": "[{"fecha": "03-20", "numero": "1"}, {"fecha": "04-16", "numero": "1"}]"
 * 	}
 * }
 * </pre>
 *
 * Ejemplo de actualización:
 *
 * <pre>
 * PUT: /extraordinarios/2021-03-20
 * {"numero_extraordinarios": "3"}
 *
 * HTTP/1.1 200 OK
 * Content-Type: application/json
 * {
 * 	"exito": "true",
 * 	"data": {
 * 		"extraordinarios": "[{"fecha": "03-20", "numero": "3"}]"
 *  }
 * }
 * </pre>
 */
@Path("/extraordinarios")
public class ExtraordinarioResource {
    @Inject
    ExtraordinarioDao extDao;

    /**
     * Obtiene información sobre los boletines extraordinarios publicados en un año.
     *
     * @param anno el año en formato YYYY.
     * @return la información solicitada o mensaje de error.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{anno}")
    public Response getNumExtraordinarios(@PathParam("anno") String anno) {
        if (esAnnoNoValido(anno)) {
            return crearRespuestaAnnoNoValido();
        } else {
            List<Extraordinario> extraordinarios;
            try {
                extraordinarios = extDao.buscarExtraordinariosPorAnno(Integer.parseInt(anno));
            } catch (Exception e) {
                return crearRespuestaErrorDesconocido();
            }
            return crearRespuestaExitosa(extraordinarios);
        }
    }

    /**
     * Crea una respuesta exitosa, es decir, se pudo obtener la información solicitada.
     *
     * @param extraordinarios los boletines extraordinarios a incluir en la respuesta.
     * @return la información solicitada.
     */
    private static Response crearRespuestaExitosa(Collection<Extraordinario> extraordinarios) {
        List<Map<String, String>> data = extraordinarios.stream().map(Extraordinario::toMap).toList();
        ExtraordinarioResponse response = new ExtraordinarioResponse(data);
        return crearRespuestaJson(Response.Status.OK, response);
    }

    /**
     * Actualiza el número de boletines extraordinarios de una fecha determinada.
     *
     * @param fecha_str la fecha en formato YYYY-MM-DD.
     * @param ext_json el nuevo número de boletines extraordinarios para esa fecha.
     * @return la información actualizada o mensaje de error.
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{fecha}")
    public Response createOrUpdateNumExtraordinarios(@PathParam("fecha") String fecha_str, String ext_json) {
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fecha_str);
        } catch (DateTimeParseException e) {
            return crearRespuestaFechaNoValida();
        }
        Extraordinario ext;
        Extraordinario ext_candidato;
        try {
            ext_candidato = crearExtraordinario(fecha, ext_json);
        } catch (IllegalArgumentException | JsonSyntaxException | NullPointerException e) {
            ErrorResponse response = new ErrorResponse("Formato de número de boletines" +
                    " extraordinarios incorrecto. Debe ser un entero mayor o igual que cero. Ejemplo:" +
                    " {\"numero_extraordinarios\": \"1\"}");
            return crearRespuestaJson(Response.Status.BAD_REQUEST, response);
        }
        Optional<Extraordinario> opt_ext = extDao.buscarPorFecha(fecha);
        if (opt_ext.isPresent()) {
            ext = opt_ext.get();
            ext.setNumero(ext_candidato.getNumero());
        } else {
            ext = ext_candidato;
        }
        try {
            extDao.crearOActualizar(ext);
        } catch (Exception e) {
            return crearRespuestaErrorDesconocido();
        }
        return crearRespuestaExitosa(List.of(ext));
    }

    /**
     * Crea un objeto Extraordinario con la información obtenida desde el endpoint.
     *
     * @param fecha la fecha.
     * @param ext_json el número de boletines extraordinarios para esa fecha en el formato de entrada del endpoint.
     * @return el objeto Extraordinario creado.
     */
    private Extraordinario crearExtraordinario(LocalDate fecha, String ext_json) {
        Type type = new TypeToken<Map<String, Integer>>() {
        }.getType();
        Map<String, Integer> num_ext = new Gson().fromJson(ext_json, type);
        return new Extraordinario(null, fecha, num_ext.get("numero_extraordinarios"));
    }
}
