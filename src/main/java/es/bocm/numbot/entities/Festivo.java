package es.bocm.numbot.entities;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Entity
@Table(name = "festivos")
@NamedQuery(name="Festivo.buscarPorAnno", query="select f from Festivo f where YEAR(f.fecha) = :anno")
public class Festivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "fecha", nullable = false, unique = true)
    private LocalDate fecha;

    @Column(name = "descripcion", nullable = false, length = 50)
    private String descripcion;

    public Festivo() {
    }

    public Festivo(Long id, LocalDate fecha, String descripcion) {
        this.id = id;
        if (!esFechaValida(fecha)) {
            throw new IllegalArgumentException("Se debe especificar una fecha");
        }
        this.fecha = fecha;
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("Se debe especificar una descripción");
        }
        this.descripcion = descripcion;
    }

    public Map<String, String> toMap() {
        DateTimeFormatter formmater = DateTimeFormatter.ofPattern("MM-dd");
        return Map.of("descripcion", this.descripcion, "fecha", this.fecha.format(formmater));
    }

    private boolean esFechaValida(LocalDate fecha) {
        if (fecha == null) {return false;}
        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY) {return false;}
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {return false;}
        final LocalDate unoEnero = LocalDate.of(fecha.getYear(), Month.JANUARY, 1);
        if (fecha.equals(unoEnero)) {return false;}
        final LocalDate veinticincoDiciembre = LocalDate.of(fecha.getYear(), Month.DECEMBER, 25);
        if (fecha.equals(veinticincoDiciembre)) {return false;}
        final LocalDate viernesSanto = getEasterSundayDate(fecha.getYear()).minusDays(2);
        if (fecha.equals(viernesSanto)) {return false;}
        return true;
    }

    private static LocalDate getEasterSundayDate(int year) {
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

}
