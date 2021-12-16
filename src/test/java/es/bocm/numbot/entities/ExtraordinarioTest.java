package es.bocm.numbot.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExtraordinarioTest {
    @Test
    void createExtraordinarioWithNegativeNumberThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Extraordinario(null, LocalDate.of(2021,
                1, 12), -2));
    }

    @Test
    void createExtraordinarioWithNumberZeroThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Extraordinario(null, LocalDate.of(2021,
                1, 12), 0));
    }

    @Test
    void createExtraordinarioWithNullDateThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Extraordinario(null, null, 2));
    }

    @Test
    void generatesCorrecStringMap() {
        Map<String, String> ref_map = Map.of("numero", "2", "fecha", "01-12");
        Extraordinario ext = new Extraordinario(null, LocalDate.of(2021, 1, 12), 2);
        Map<String, String> ext_map = ext.toMap();
        assertEquals(ref_map, ext_map);
    }
}
