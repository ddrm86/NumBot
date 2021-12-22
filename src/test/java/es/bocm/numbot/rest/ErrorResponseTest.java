package es.bocm.numbot.rest;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseTest {
    @Test
    void constructorCreatesCorrectObject() {
        ErrorResponse expected = new ErrorResponse(false, Map.of("error", "dummy error message"));
        assertEquals(expected, new ErrorResponse("dummy error message"));
    }
}
