package es.bocm.numbot.rest;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExtraordinarioResponseTest {
    @Test
    void constructorCreatesCorrectObject() {
        List<Map<String, String>> expectedDataList = List.of(Map.of("fecha", "03-20", "numero", "1"));
        Map<String, List<Map<String, String>>> expectedData = new HashMap<>();
        expectedData.put("extraordinarios", expectedDataList);
        ExtraordinarioResponse expected = new ExtraordinarioResponse(true, expectedData);
        ExtraordinarioResponse result = new ExtraordinarioResponse(expectedDataList);
        assertEquals(expected, result);
    }
}
