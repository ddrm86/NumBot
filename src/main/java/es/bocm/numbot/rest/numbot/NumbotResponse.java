package es.bocm.numbot.rest.numbot;

import es.bocm.numbot.rest.NumbotApiResponse;

import java.util.Map;

public record NumbotResponse(boolean exito, Map<String, String> data) implements NumbotApiResponse {
    public NumbotResponse(String numBot, String numBotsEnFestivoSeguidos) {
        this(true, Map.of("numero_boletin", numBot,
                "numero_boletines_en_no_laboral_seguidos", numBotsEnFestivoSeguidos));
    }
}
