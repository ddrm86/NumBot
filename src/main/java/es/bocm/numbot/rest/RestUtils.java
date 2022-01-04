package es.bocm.numbot.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Agrupa métodos comunes a varios endpoints.
 */
public final class RestUtils {
    private static final Logger log = LoggerFactory.getLogger(RestUtils.class);

    private RestUtils() {
        throw new AssertionError("Clase de utilidades. No instanciar.");
    }

    /**
     * Comprueba si un año en formato cadena no es válido.
     *
     * @param anno el año a comprobar.
     * @return false si es un año válido en formato YYYY, true en cualquier otro caso.
     */
    public static boolean esAnnoNoValido(String anno) {
        log.debug("Inicia validación del año {}", anno);
        String dummyDate = anno + "-01-01";
        try {
            LocalDate.parse(dummyDate);
        } catch (DateTimeParseException e) {
            log.debug("Finaliza validación del año {}: no válido", anno);
            return true;
        }
        log.debug("Finaliza validación del año {}: válido", anno);
        return false;
    }

    /**
     * Crea la respuesta de error cuando el año indicado en la URI no tiene un formato correcto.
     *
     * @return la respuesta.
     */
    public static Response crearRespuestaAnnoNoValido() {
        log.debug("Creando respuesta de error de año no válido");
        ErrorResponse response = new ErrorResponse("Año con formato incorrecto. El formato debe ser YYYY");
        return crearRespuestaJson(Response.Status.BAD_REQUEST, response);
    }

    /**
     * Crea la respuesta de error cuando la fecha indicada en la URI no es válida.
     *
     * @return la respuesta.
     */
    public static Response crearRespuestaFechaNoValida() {
        log.debug("Creando respuesta de error de fecha no válida");
        ErrorResponse response = new ErrorResponse("Fecha errónea o con formato incorrecto. El formato" +
                " debe ser YYYY-MM-DD");
        return crearRespuestaJson(Response.Status.BAD_REQUEST, response);
    }

    /**
     * Crea una respuesta en formato JSON.
     *
     * @param estado estado HTTP de la respuesta.
     * @param objeto_respuesta objeto de respuesta a convertir en formato JSON. Consultar {@link NumbotApiResponse}.
     * @return la respuesta.
     */
    public static Response crearRespuestaJson(Response.Status estado, NumbotApiResponse objeto_respuesta) {
        log.debug("Creando respuesta JSON con estado {} y objeto:\n{}", estado, objeto_respuesta);
        return Response
                .status(estado)
                .entity(new Gson().toJson(objeto_respuesta))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Crea la respuesta de error cuando se ha producido un error desconocido (fallo en la BBDD, etc.).
     *
     * @return la respuesta.
     */
    public static Response crearRespuestaErrorDesconocido() {
        log.debug("Creando respuesta de error desconocido");
        ErrorResponse response = new ErrorResponse("Error desconocido. con el administrador de sistemas " +
                "para que revise la conexión con la BBDD y otras posibles causas.");
        return crearRespuestaJson(Response.Status.INTERNAL_SERVER_ERROR, response);
    }

    /**
     * Crea la respuesta de error cuando no existe información para los festivos de un año.
     *
     * @return la respuesta.
     */
    public static Response crearRespuestaFaltanFestivos() {
        log.debug("Creando respuesta de error por falta de festivos");
        ErrorResponse response = new ErrorResponse(
                "Faltan datos en la BBDD para procesar la petición: no están establecidos los " +
                        "festivos de este año.");
        return  crearRespuestaJson(Response.Status.NOT_FOUND, response);
    }
}
