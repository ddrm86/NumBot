package es.bocm.numbot.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FestivoTest {
    @Test
    void createFestivoWithNullDateThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Festivo(null,null, "desc1"));
    }

    @Test
    void createFestivoWithDateOnSaturdayThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Festivo(null,
                LocalDate.of(2021, 1, 9), "desc1"));
    }

    @Test
    void createFestivoWithDateOnSundayThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Festivo(null,
                LocalDate.of(2021, 1, 10), "desc1"));
    }

    @Test
    void createFestivoWithDateOnJuanuary1ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Festivo(null,
                LocalDate.of(2021, 1, 1), "desc1"));
    }

    @Test
    void createFestivoWithDateOnDecember25ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Festivo(null,
                LocalDate.of(2020, 12, 25), "desc1"));
    }

    @Test
    void createFestivoWithDateOnGoodFridayThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Festivo(null,
                LocalDate.of(2021, 4, 2), "desc1"));
    }

    @Test
    void createFestivoWithNullDescriptionThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Festivo(null,
                LocalDate.of(2021, 1, 12), null));
    }

    @Test
    void createFestivoWithBlankDescriptionThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Festivo(null,
                LocalDate.of(2021, 1, 12), "   \t\n"));
    }

    @Test
    void generatesCorrectStringMap() {
        Map<String, String> ref_map = Map.of("descripcion", "desc1", "fecha", "01-12");
        Festivo fest = new Festivo(null, LocalDate.of(2021, 1, 12), "desc1");
        assertEquals(ref_map, fest.toMap());
    }
}
