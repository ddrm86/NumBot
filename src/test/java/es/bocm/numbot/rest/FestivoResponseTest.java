package es.bocm.numbot.rest;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FestivoResponseTest {
    @Test
    void constructorCreatesCorrectObject() {
        List<Map<String, String>> expectedDataList = List.of(
                Map.of("fecha", "03-20", "descripcion", "desc1"));
        Map<String, List<Map<String, String>>> expectedData = new HashMap<>();
        expectedData.put("festivos", expectedDataList);
        FestivoResponse expected = new FestivoResponse(true, expectedData);
        FestivoResponse result = new FestivoResponse(expectedDataList);
        assertEquals(expected, result);
    }
}
