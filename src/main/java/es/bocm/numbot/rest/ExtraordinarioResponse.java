package es.bocm.numbot.rest;

import java.util.List;
import java.util.Map;

public record ExtraordinarioResponse(boolean exito, Map<String, List<Map<String, String>>> data) implements NumbotApiResponse {
    public ExtraordinarioResponse(List<Map<String, String>> data) {
        this(true, Map.of("extraordinarios", data));
    }
}
