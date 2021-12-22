package es.bocm.numbot.calculations;

import es.bocm.numbot.entities.Extraordinario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalcularNumBotTest {
    @ParameterizedTest
    @CsvSource({
            "2021-01-07, 0, 5",
            "2021-01-07, 2, 7",
            "2021-12-16, 1, 299",
            "2023-01-02, 0, 1",
            "2021-01-01, 0, 1"
    })
    void getCorrectNumBot(LocalDate date, int extNumBots, int expectedResult) {
        assertEquals(expectedResult, CalcularNumBot.getNumBot(date, extNumBots));
    }

    @Test
    void getCorrectNumBotNoExts() {
        assertEquals(5, CalcularNumBot.getNumBot(LocalDate.of(2021, 1, 7),
                Collections.emptySet()));
    }

    @Test
    void getCorrectNumBotExtsAfterDate() {
        Set<Extraordinario> exts = Set.of(new Extraordinario(null,
                LocalDate.of(2021, 1, 8), 1));
        assertEquals(5, CalcularNumBot.getNumBot(LocalDate.of(2021, 1, 7),
                exts));
    }

    @Test
    void getCorrectNumBotExtsBeforeDate() {
        Set<Extraordinario> exts = Set.of(new Extraordinario(null,
                LocalDate.of(2021, 1, 6), 1));
        assertEquals(6, CalcularNumBot.getNumBot(LocalDate.of(2021, 1, 7),
                exts));
    }

    @Test
    void getCorrectNumBotExtsSameDate() {
        Set<Extraordinario> exts = Set.of(new Extraordinario(null,
                LocalDate.of(2021, 1, 7), 1));
        assertEquals(6, CalcularNumBot.getNumBot(LocalDate.of(2021, 1, 7),
                exts));
    }
}
