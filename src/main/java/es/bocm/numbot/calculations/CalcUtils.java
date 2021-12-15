package es.bocm.numbot.calculations;

import es.bocm.numbot.entities.Festivo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Set;

public final class CalcUtils {
    private CalcUtils() {
        throw new AssertionError("Clase de utilidades. No instanciar.");
    }

    public static LocalDate getEasterSundayDate(int year) {
        int a = year % 19,
                b = year / 100,
                c = year % 100,
                d = b / 4,
                e = b % 4,
                g = (8 * b + 13) / 25,
                h = (19 * a + b - d - g + 15) % 30,
                j = c / 4,
                k = c % 4,
                m = (a + 11 * h) / 319,
                r = (2 * e + 2 * j - k - h + m + 32) % 7,
                month = (h - m + r + 90) / 25,
                day = (h - m + r + month + 19) % 32;

        return LocalDate.of(year, month, day);
    }

    public static Set<LocalDate> fechasSinBoletin(int anno) {
        final LocalDate unoEnero = LocalDate.of(anno, Month.JANUARY, 1);
        final LocalDate veinticincoDiciembre = LocalDate.of(anno, Month.DECEMBER, 25);
        final LocalDate viernesSanto = getEasterSundayDate(anno).minusDays(2);
        return Set.of(unoEnero, veinticincoDiciembre, viernesSanto);
    }

    public static int numBotsEnFestivoSeguidos(LocalDate fecha, Collection<Festivo> festivosAnno) {
        int numBotsEnFestivoSeguidos = 0;
        boolean laboral = false;
        fecha = fecha.plusDays(1);
        while (!laboral) {
            if (!esFechaSinBoletin(fecha)) {
                if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    numBotsEnFestivoSeguidos++;
                } else if (esFestivo(fecha, festivosAnno)) {
                    numBotsEnFestivoSeguidos++;
                } else if (fecha.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    laboral = true;
                }
            }
            fecha = fecha.plusDays(1);
        }
        return numBotsEnFestivoSeguidos;
    }

    public static boolean esFechaSinBoletin(LocalDate fecha) {
        return CalcUtils.fechasSinBoletin(fecha.getYear()).contains(fecha);
    }

    private static boolean esFestivo(LocalDate fecha, Collection<Festivo> festivos) {
        return festivos.stream().anyMatch(f -> f.getFecha().equals(fecha));
    }
}
