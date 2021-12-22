package es.bocm.numbot.rest.festivos;

import es.bocm.numbot.rest.NumbotApiResponse;

import java.util.List;
import java.util.Map;

/**
 * Respuesta exitosa para el recurso de festivos.
 */
public record FestivoResponse(boolean exito, Map<String, List<Map<String, String>>> data) implements NumbotApiResponse {
    /**
     * Crea una respuesta a partir de los datos.
     *
     * @param data los datos de respuesta. Ver formato en {@link FestivoResource}.
     */
    public FestivoResponse(List<Map<String, String>> data) {
        this(true, Map.of("festivos", data));
    }
}
