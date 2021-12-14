package es.bocm.numbot.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class RestUtils {
    private RestUtils() {
        throw new AssertionError("Clase de utilidades. No instanciar.");
    }

    public static boolean esAnnoNoValido(String anno) {
        String dummyDate = anno + "-01-01";
        try {
            LocalDate.parse(dummyDate);
        } catch (DateTimeParseException e) {
            return true;
        }
        return false;
    }

    public static Response crearRespuestaAnnoNoValido() {
        ErrorResponse response = new ErrorResponse("Año con formato incorrecto. El formato debe ser YYYY");
        return crearRespuestaJson(Response.Status.BAD_REQUEST, response);
    }

    public static Response crearRespuestaJson(Response.Status estado, NumbotResponse objeto_respuesta) {
        return Response
                .status(estado)
                .entity(new Gson().toJson(objeto_respuesta))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public static Response crearRespuestaErrorDesconocido() {
        ErrorResponse response = new ErrorResponse("Error desconocido. con el administrador de sistemas " +
                "para que revise la conexión con la BBDD y otras posibles causas.");
        return crearRespuestaJson(Response.Status.INTERNAL_SERVER_ERROR, response);
    }

}