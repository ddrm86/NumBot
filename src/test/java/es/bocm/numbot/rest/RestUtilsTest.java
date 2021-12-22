package es.bocm.numbot.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestUtilsTest {
    @Test
    void acceptsCorrectYears() {
        assertFalse(RestUtils.esAnnoNoValido("1923"));
    }

    @Test
    void rejectsIncorrectYears() {
        assertTrue(RestUtils.esAnnoNoValido("invalidYear"));
    }
}
