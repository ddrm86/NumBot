package es.bocm.numbot.rest;

import java.util.List;
import java.util.Map;

public record FestivoResponse(boolean exito, Map<String, List<Map<String, String>>> data) implements NumbotResponse {
    public FestivoResponse(boolean exito, List<Map<String, String>> data) {
        this(exito, Map.of("festivos", data));
    }
}
