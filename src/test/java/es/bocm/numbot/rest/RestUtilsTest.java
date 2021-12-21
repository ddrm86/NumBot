package es.bocm.numbot.rest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;


import static org.junit.jupiter.api.Assertions.*;

class RestUtilsTest {
    @ParameterizedTest
    @ValueSource(strings = {"1000", "9999", "2020", "0000", "0123"})
    void acceptsCorrectYears(String year) {
        assertFalse(RestUtils.esAnnoNoValido(year));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"\n", "word", "20", "1", "12345", "932", "123a", "a123", "12a3"})
    void rejectsIncorrectYears(String year) {
        assertTrue(RestUtils.esAnnoNoValido(year));
    }
}
