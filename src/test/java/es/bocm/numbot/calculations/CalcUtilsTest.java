package es.bocm.numbot.calculations;

import es.bocm.numbot.entities.Festivo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalcUtilsTest {
    @ParameterizedTest
    @CsvSource({
            "2021, 2021-04-04",
            "2022, 2022-04-17",
            "2023, 2023-04-09",
            "2050, 2050-04-10",
    })
    void getCorrectEasterSundayDate(int year, LocalDate expectedDate) {
        assertEquals(expectedDate, CalcUtils.getEasterSundayDate(year));
    }

    @ParameterizedTest
    @ValueSource(ints = {2020, 2021, 2030, 2050})
    void getCorrectNumberOfDaysWithoutBulletin(int year) {
        assertEquals(3, CalcUtils.fechasSinBoletin(year).size());
    }

    @Test
    void getCorrectDatesWithoutBulletin() {
        Set<LocalDate> expected_dates = Set.of(
                LocalDate.of(2021, 1, 1),
                LocalDate.of(2021, 12, 25),
                LocalDate.of(2021, 4, 2)  // Good Friday
        );
        Set<LocalDate> result_dates = CalcUtils.fechasSinBoletin(2021);
        assertTrue(result_dates.containsAll(expected_dates));
    }

    final static Set<Festivo> nonWorkingDates = Set.of(
            new Festivo(null, LocalDate.of(2021, 8, 6), "dummy desc"),
            new Festivo(null, LocalDate.of(2021, 12, 24), "dummy desc"),
            new Festivo(null, LocalDate.of(2021, 5, 3), "dummy desc"),
            new Festivo(null, LocalDate.of(2021, 6, 18), "dummy desc"),
            new Festivo(null, LocalDate.of(2021, 6, 21), "dummy desc")
    );

    @ParameterizedTest
    @CsvSource({
            "2021-08-04, 0",
            "2021-08-17, 0",
            "2021-12-23, 1",
            "2021-04-30, 2",
            "2021-06-17, 3",
            "2022-12-23, 1",
    })
    void numBotsEnFestivoSeguidos(LocalDate date, int expected) {
        assertEquals(expected, CalcUtils.numBotsEnFestivoSeguidos(date, nonWorkingDates));
    }

    @ParameterizedTest
    @CsvSource({
            "2021-04-04, false", // No bulletin on Sundays, but Sundays are taken care of separately
            "2021-04-02, true",  // Good Friday
            "2000-01-01, true",
            "2050-12-25, true",
            "2021-10-20, false",
    })
    void guessCorrectlyIfDateWithoutBulletin(LocalDate date, boolean expected) {
        assertEquals(expected, CalcUtils.esFechaSinBoletin(date));
    }
}
