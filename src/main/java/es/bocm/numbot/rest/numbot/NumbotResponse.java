package es.bocm.numbot.rest.numbot;

import es.bocm.numbot.rest.NumbotApiResponse;

import java.util.Map;

/**
 * Respuesta exitosa para el recurso de numero-boletin.
 */
public record NumbotResponse(boolean exito, Map<String, String> data) implements NumbotApiResponse {
    /**
     * Crea una respuesta a partir de los datos.
     *
     * @param numBot el número de boletín.
     * @param numBotsEnFestivoSeguidos el número de boletines en día no laboral seguidos.
     */
    public NumbotResponse(String numBot, String numBotsEnFestivoSeguidos) {
        this(true, Map.of("numero_boletin", numBot,
                "numero_boletines_en_no_laboral_seguidos", numBotsEnFestivoSeguidos));
    }
}
