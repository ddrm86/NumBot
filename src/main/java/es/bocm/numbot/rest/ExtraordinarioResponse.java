package es.bocm.numbot.rest;

import java.util.Map;

public record ExtraordinarioResponse(boolean exito, Map<String, String> data) implements NumbotApiResponse {
    public ExtraordinarioResponse(boolean exito, String key, String value) {
        this(exito, Map.of(key, value));
    }

    public ExtraordinarioResponse(boolean exito, String key, int value) {
        this(exito, key, Integer.toString(value));
    }
}
