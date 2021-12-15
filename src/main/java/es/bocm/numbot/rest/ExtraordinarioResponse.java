package es.bocm.numbot.rest;

import java.util.List;
import java.util.Map;

public record ExtraordinarioResponse(boolean exito, Map<String, List<Map<String, String>>> data) implements NumbotApiResponse {
    public ExtraordinarioResponse(boolean exito, List<Map<String, String>> data) {
        this(exito, Map.of("extraordinarios", data));
    }
}
