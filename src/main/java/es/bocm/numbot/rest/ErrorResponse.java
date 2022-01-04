package es.bocm.numbot.rest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Respuesta de error común a todos los recursos.
 */
public record ErrorResponse(boolean exito, Map<String, String> data) implements NumbotApiResponse {
    private static final Logger log = LoggerFactory.getLogger(ErrorResponse.class);

    /**
     * Construye una respuesta de error a partir del mensaje de error.
     *
     * @param mensaje el mensaje de error.
     */
    public ErrorResponse(String mensaje) {
        this(false, Map.of("error", mensaje));
        log.debug("Creado ErrorResponse con el mensaje {}", mensaje);
    }
}
