package es.bocm.numbot.calculations;

import es.bocm.numbot.entities.Festivo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Set;

/**
 * Contiene métodos utilizados para cálculos de fechas.
 */
public final class CalcUtils {
    private static final Logger log = LoggerFactory.getLogger(CalcUtils.class);

    private CalcUtils() {
        throw new AssertionError("Clase de utilidades. No instanciar.");
    }

    /**
     * Calcula la fecha del domingo de Pascua para un año determinado.
     *
     * Se utiliza el algoritmo de Gauss.
     *
     * @param year el año para el que se calcula el domingo de Pascua.
     * @return la fecha del domingo de Pascua para el año indicado.
     */
    public static LocalDate getEasterSundayDate(int year) {
        log.debug("Inicia cálculo del domingo de Pascua para el año {}", year);
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

        LocalDate easterSunday = LocalDate.of(year, month, day);
        log.debug("Finaliza cálculo del domingo de Pascua para el año {} con resultado {}", year, easterSunday);
        return easterSunday;
    }

    /**
     * Obtiene las tres fechas en que no se publica el boletín, aparte de los domingos, para un año determinado.
     *
     * Las fechas son el uno de enero, el veinticinco de diciembre y el Viernes Santo.
     *
     * @param anno año para el que se quieren obtener las fechas.
     * @return las fechas en que no se publica el boletín ese año.
     */
    public static Set<LocalDate> fechasSinBoletin(int anno) {
        log.debug("Inicia obtención de fechas en que no se publica el boletín para el año {}", anno);
        final LocalDate unoEnero = LocalDate.of(anno, Month.JANUARY, 1);
        final LocalDate veinticincoDiciembre = LocalDate.of(anno, Month.DECEMBER, 25);
        final LocalDate viernesSanto = getEasterSundayDate(anno).minusDays(2);
        Set<LocalDate> fechasSinBoletin = Set.of(unoEnero, veinticincoDiciembre, viernesSanto);
        log.debug("Finaliza obtención de fechas en que no se publica el boletín para el año {} con resultado {}",
                anno, fechasSinBoletin);
        return fechasSinBoletin;
    }

    /**
     * Calcula, a partir de una fecha determinada, el número de boletines seguidos que se publican en día no laboral.
     *
     * Estos boletines se suelen hacer antes en día laboral y se dejan aprobados para publicación.
     * Por ejemplo, si estamos en viernes laboral y el lunes es día festivo con publicación de Boletín, debe devolver 2,
     * ya que el sábado y el lunes no se trabaja, pero sí se publica el Boletín. El domingo no hay publicación y el
     * martes se trabaja.
     *
     * @param fecha la fecha a partir de la cual se quieren calcular los días no laborales con Boletín seguidos.
     * @param festivosAnno los festivos del año de la fecha indicada.
     * @return número de boletines seguidos que se publican en día no laboral, a partir del día siguiente de la fecha.
     */
    public static int numBotsEnFestivoSeguidos(LocalDate fecha, Collection<Festivo> festivosAnno) {
        log.debug("Inicia cálculo de número de boletines seguidos en día no laboral para la fecha {} y los festivos" +
                " del año:\n{}", fecha, festivosAnno);
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
        log.debug("Finaliza cálculo de número de boletines seguidos en día no laboral con resultado {}",
                numBotsEnFestivoSeguidos);
        return numBotsEnFestivoSeguidos;
    }

    /**
     * Comprueba si una fecha es un día sin publicación de Boletín, sin tener en cuenta los domingos.
     *
     * @param fecha la fecha que se quiere comprobar.
     * @return true si es fecha sin boletín, false en caso contrario.
     */
    public static boolean esFechaSinBoletin(LocalDate fecha) {
        return CalcUtils.fechasSinBoletin(fecha.getYear()).contains(fecha);
    }

    /**
     * Comprueba si una fecha es día festivo.
     *
     * @param fecha la fecha a comprobar.
     * @param festivos los festivos para los que se quiere comprobar la fecha.
     * @return true si la fecha corresponde a uno de los festivos proporcionados, false en caso contrario.
     */
    private static boolean esFestivo(LocalDate fecha, Collection<Festivo> festivos) {
        return festivos.stream().anyMatch(f -> f.getFecha().equals(fecha));
    }
}
