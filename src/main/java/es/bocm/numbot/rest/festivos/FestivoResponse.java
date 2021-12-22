package es.bocm.numbot.rest.festivos;

import es.bocm.numbot.rest.NumbotApiResponse;

import java.util.List;
import java.util.Map;

public record FestivoResponse(boolean exito, Map<String, List<Map<String, String>>> data) implements NumbotApiResponse {
    public FestivoResponse(List<Map<String, String>> data) {
        this(true, Map.of("festivos", data));
    }
}
