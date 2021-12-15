package es.bocm.numbot.calculations;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

public final class CalcularNumBot {
    private CalcularNumBot() {
        throw new AssertionError("Clase de utilidades. No instanciar.");
    }

    public static int getNumBot(LocalDate fecha, int numExtraordinarios) {
        final LocalDate unoEnero = LocalDate.of(fecha.getYear(), Month.JANUARY, 1);
        int numeroDias = (int) (ChronoUnit.DAYS.between(unoEnero, fecha) + 1);
        int numNoBot = (int) CalcUtils.fechasSinBoletin(fecha.getYear())
                .stream()
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .filter(fecha::isAfter)
                .count();
        return numeroDias + numExtraordinarios - calcNumDomingos(fecha) - numNoBot;
    }

    private static int calcNumDias(LocalDate fecha) {
        final LocalDate unoEnero = LocalDate.of(fecha.getYear(), Month.JANUARY, 1);
        return (int) (ChronoUnit.DAYS.between(unoEnero, fecha) + 1);
    }

    private static int calcNumDomingos(LocalDate fecha) {
        int numDias = calcNumDias(fecha);
        int diaPrimerDomingo = calcDiaPrimerDomingo(fecha);
        return (numDias + (7 - diaPrimerDomingo)) / 7;
    }

    private static int calcDiaPrimerDomingo(LocalDate fecha) {
        LocalDate fechaAux = LocalDate.of(fecha.getYear(), Month.JANUARY, 1);
        while (fechaAux.getDayOfWeek() != DayOfWeek.SUNDAY) {
            fechaAux = fechaAux.plusDays(1);
        }
        return fechaAux.getDayOfMonth();
    }

    public static void main(String[] args) {
        System.out.println(getNumBot(LocalDate.of(2021,4,5), 0));
    }
}
