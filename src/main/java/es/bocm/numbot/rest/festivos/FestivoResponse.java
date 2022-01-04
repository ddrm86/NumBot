package es.bocm.numbot.rest.festivos;

import es.bocm.numbot.rest.NumbotApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Respuesta exitosa para el recurso de festivos.
 */
public record FestivoResponse(boolean exito, Map<String, List<Map<String, String>>> data) implements NumbotApiResponse {
    private static final Logger log = LoggerFactory.getLogger(FestivoResponse.class);

    /**
     * Crea una respuesta a partir de los datos.
     *
     * @param data los datos de respuesta. Ver formato en {@link FestivoResource}.
     */
    public FestivoResponse(List<Map<String, String>> data) {
        this(true, Map.of("festivos", data));
        log.debug("Creado FestivoResponse con los datos {}", data);
    }
}
