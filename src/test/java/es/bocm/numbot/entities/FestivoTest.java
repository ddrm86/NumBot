package es.bocm.numbot.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FestivoTest {
    static Stream<LocalDate> invalidDatesProvider() {
        return Stream.of(null, // null date
                LocalDate.of(2021, 1, 9), // Saturday
                LocalDate.of(2021, 1, 10), // Sunday
                LocalDate.of(2021, 1, 1), // January 1
                LocalDate.of(2020, 12, 25), // December 25
                LocalDate.of(2021, 4, 2)); // Good Friday
    }

    @ParameterizedTest
    @MethodSource("invalidDatesProvider")
    void createFestivoWithInvalidDateThrowsException(LocalDate invalidDate) {
        assertThrows(IllegalArgumentException.class, () -> new Festivo(null, invalidDate, "desc1"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   ", "\t", "\n" })
    void createFestivoWithNullOrBlankDescriptionThrowsException(String desc) {
        assertThrows(IllegalArgumentException.class, () -> new Festivo(null,
                LocalDate.of(2021, 1, 12), desc));
    }

    @Test
    void generatesCorrectStringMap() {
        Map<String, String> ref_map = Map.of("descripcion", "desc1", "fecha", "01-12");
        Festivo fest = new Festivo(null, LocalDate.of(2021, 1, 12), "desc1");
        assertEquals(ref_map, fest.toMap());
    }
}
