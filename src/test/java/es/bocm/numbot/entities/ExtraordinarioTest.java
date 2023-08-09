package es.bocm.numbot.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExtraordinarioTest {
    @ParameterizedTest
    @ValueSource(ints = { -1, -2 })
    void createExtraordinarioWithInvalidNumberThrowsException(int number) {
        assertThrows(IllegalArgumentException.class, () -> new Extraordinario(null, LocalDate.of(2021,
                1, 12), number));
    }

    @Test
    void createExtraordinarioWithNullDateThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Extraordinario(null, null, 2));
    }

    @Test
    void generatesCorrectStringMap() {
        Map<String, String> ref_map = Map.of("numero", "2", "fecha", "01-12");
        Extraordinario ext = new Extraordinario(null, LocalDate.of(2021, 1, 12), 2);
        assertEquals(ref_map, ext.toMap());
    }
}
