package es.bocm.numbot.calculations;

import es.bocm.numbot.entities.Extraordinario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

/**
 * Contiene los métodos necesarios para calcular el número de boletín que corresponde a una fecha.
 */
public final class CalcularNumBot {
    private static final Logger log = LoggerFactory.getLogger(CalcularNumBot.class);

    private CalcularNumBot() {
        throw new AssertionError("Clase de utilidades. No instanciar.");
    }

    /**
     * Calcula el número de Boletín que corresponde a una fecha determinada.
     *
     * Se tiene en cuenta el número de días del año que han pasado salvo los que no se publica el Boletín y el número
     * de boletines extraordinarios que ha habido.
     *
     * @param fecha la fecha para la que se quiere obtener el número de Boletín.
     * @param numExtMismoAnnoAntesOIgualFecha el número total de boletines extraordinarios que ha habido antes o en el
     *                                        mismo día durante ese año.
     * @return el número de Boletín.
     */
    public static int getNumBot(LocalDate fecha, int numExtMismoAnnoAntesOIgualFecha) {
        log.debug("Inicia cálculo del número de boletín con fecha {} y número de extraordinarios en el mismo año de" +
                " igual o anterior fecha {}", fecha, numExtMismoAnnoAntesOIgualFecha);
        final LocalDate unoEnero = LocalDate.of(fecha.getYear(), Month.JANUARY, 1);
        int numeroDias = (int) (ChronoUnit.DAYS.between(unoEnero, fecha) + 1);
        int numNoBot = (int) CalcUtils.fechasSinBoletin(fecha.getYear())
                .stream()
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .filter(fecha::isAfter)
                .count();
        int numDomingos = calcNumDomingos(fecha);
        int numBot = numeroDias + numExtMismoAnnoAntesOIgualFecha - numDomingos - numNoBot;
        log.debug("Finaliza cálculo del número de boletín con resultado: {} + {} - {} - {} = {}",
                numeroDias, numExtMismoAnnoAntesOIgualFecha, numDomingos, numNoBot, numBot);
        return numBot;
    }

    /**
     * Calcula el número de Boletín que corresponde a una fecha determinada.
     *
     * @param fecha la fecha para la que se quiere obtener el número de Boletín.
     * @param extraordinariosAnno los boletines extraordinarios habidos durante el año. Se tendrán en cuenta solo los
     *                            anteriores o iguales a la fecha.
     * @return el número de Boletín.
     */
    public static int getNumBot(LocalDate fecha, Collection<Extraordinario> extraordinariosAnno) {
        log.debug("Calculando el número de boletín con fecha {} y extraordinarios del año:\n{}",
                fecha, extraordinariosAnno);
        int numExtMismoAnnoAntesOIgualFecha = (int) extraordinariosAnno
                .stream()
                .filter(e -> e.getFecha().isBefore(fecha.plusDays(1)))
                .count();
        return getNumBot(fecha, numExtMismoAnnoAntesOIgualFecha);
    }

    /**
     * Calcula el número de días que han pasado del año hasta una fecha determinada.
     *
     * @param fecha la fecha para la que se quieren calcular los días.
     * @return el número de días que han pasado desde el 1 de enero hasta la fecha, inclusive.
     */
    private static int calcNumDias(LocalDate fecha) {
        final LocalDate unoEnero = LocalDate.of(fecha.getYear(), Month.JANUARY, 1);
        return (int) (ChronoUnit.DAYS.between(unoEnero, fecha) + 1);
    }

    /**
     * Calcula el número de domingos que han pasado del año hasta una fecha determinada.
     *
     * @param fecha la fecha para la que se quieren calcular los domingos.
     * @return el número de domingos que han pasado desde el 1 de enero hasta la fecha.
     */
    private static int calcNumDomingos(LocalDate fecha) {
        int numDias = calcNumDias(fecha);
        int diaPrimerDomingo = calcDiaPrimerDomingo(fecha);
        return (numDias + (7 - diaPrimerDomingo)) / 7;
    }

    /**
     * Calcula el número de días que han pasado hasta el primer domingo del año para una fecha determinada.
     *
     * @param fecha la fecha para la que se quiere calcular el primer domingo del año.
     * @return el número de días que han pasado hasta el primer domingo del año, inclusive.
     */
    private static int calcDiaPrimerDomingo(LocalDate fecha) {
        log.debug("Inicia cálculo del primer domingo del año para la fecha {}", fecha);
        LocalDate fechaAux = LocalDate.of(fecha.getYear(), Month.JANUARY, 1);
        while (fechaAux.getDayOfWeek() != DayOfWeek.SUNDAY) {
            fechaAux = fechaAux.plusDays(1);
        }
        int primerDomingo = fechaAux.getDayOfMonth();
        log.debug("Finaliza cálculo del primer domingo del año para la fecha {} con resultado {}",
                fecha, primerDomingo);
        return primerDomingo;
    }
}
