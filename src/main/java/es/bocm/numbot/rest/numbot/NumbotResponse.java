package es.bocm.numbot.rest.numbot;

import es.bocm.numbot.rest.NumbotApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Respuesta exitosa para el recurso de numero-boletin.
 */
public record NumbotResponse(boolean exito, Map<String, String> data) implements NumbotApiResponse {
    private static final Logger log = LoggerFactory.getLogger(NumbotResponse.class);

    /**
     * Crea una respuesta a partir de los datos.
     *
     * @param numBot el número de boletín.
     * @param numBotsEnFestivoSeguidos el número de boletines en día no laboral seguidos.
     */
    public NumbotResponse(String numBot, String numBotsEnFestivoSeguidos) {
        this(true, Map.of("numero_boletin", numBot,
                "numero_boletines_en_no_laboral_seguidos", numBotsEnFestivoSeguidos));
        log.debug("Creado NumbotResponse con número de boletín {} y bots en no laborales seguidos {}",
                numBot, numBotsEnFestivoSeguidos);
    }
}
