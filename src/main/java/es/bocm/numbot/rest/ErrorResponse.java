package es.bocm.numbot.rest;
import java.util.Map;

public record ErrorResponse(boolean exito, Map<String, String> data) implements NumbotApiResponse {
    public ErrorResponse(String mensaje) {
        this(false, Map.of("error", mensaje));
    }
}
