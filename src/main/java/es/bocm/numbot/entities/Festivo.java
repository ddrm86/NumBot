package es.bocm.numbot.entities;

import es.bocm.numbot.calculations.CalcUtils;
import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
            throw new IllegalArgumentException("No es una fecha válida");
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

    public LocalDate getFecha() {
        return fecha;
    }

    private boolean esFechaValida(LocalDate fecha) {
        if (fecha == null) {return false;}
        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY) {return false;}
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {return false;}
        if (CalcUtils.esFechaSinBoletin(fecha)) {return false;}
        return true;
    }
}
