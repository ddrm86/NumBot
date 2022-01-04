package es.bocm.numbot.rest.extraordinarios;

import es.bocm.numbot.rest.NumbotApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Respuesta exitosa para el recurso de boletines extraordinarios.
 */
public record ExtraordinarioResponse(boolean exito, Map<String, List<Map<String, String>>> data) implements NumbotApiResponse {
    private static final Logger log = LoggerFactory.getLogger(ExtraordinarioResponse.class);

    /**
     * Crea una respuesta a partir de los datos.
     *
     * @param data los datos de respuesta. Ver formato en {@link ExtraordinarioResource}.
     */
    public ExtraordinarioResponse(List<Map<String, String>> data) {
        this(true, Map.of("extraordinarios", data));
        log.debug("Creando ExtraordinarioResponse con los datos {}", data);
    }
}
