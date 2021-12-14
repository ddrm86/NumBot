package es.bocm.numbot.rest;
import java.util.Map;

public record ErrorResponse(boolean exito, Map<String, String> data) implements NumbotResponse {
    public ErrorResponse(String mensaje) {
        this(false, Map.of("error", mensaje));
    }
}
