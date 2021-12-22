package es.bocm.numbot.rest;
import java.util.Map;

/**
 * Respuesta de error común a todos los recursos.
 */
public record ErrorResponse(boolean exito, Map<String, String> data) implements NumbotApiResponse {
    /**
     * Construye una respuesta de error a partir del mensaje de error.
     *
     * @param mensaje el mensaje de error.
     */
    public ErrorResponse(String mensaje) {
        this(false, Map.of("error", mensaje));
    }
}
