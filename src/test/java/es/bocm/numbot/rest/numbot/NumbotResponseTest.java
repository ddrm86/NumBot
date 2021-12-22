package es.bocm.numbot.rest.numbot;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumbotResponseTest {
    @Test
    void constructorCreatesCorrectObject() {
        NumbotResponse expected = new NumbotResponse(true,
                Map.of("numero_boletin", "42", "numero_boletines_en_no_laboral_seguidos", "2"));
        NumbotResponse result = new NumbotResponse("42", "2");
        assertEquals(expected, result);
    }
}
