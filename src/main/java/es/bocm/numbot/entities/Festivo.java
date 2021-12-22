package es.bocm.numbot.entities;

import es.bocm.numbot.calculations.CalcUtils;
import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Representa un día festivo.
 *
 * Para no confundir los cálculos, no debe ser sábado, ni domingo, ni fecha en la que no se publica el Boletín.
 */
@Entity
@Table(name = "festivos")
@NamedQuery(name="Festivo.buscarPorAnno", query="select f from Festivo f where YEAR(f.fecha) = :anno")
public class Festivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * La fecha del día festivo.
     */
    @Column(name = "fecha", nullable = false, unique = true)
    private LocalDate fecha;

    /**
     * La descripción del día festivo.
     */
    @Column(name = "descripcion", nullable = false, length = 50)
    private String descripcion;

    public Festivo() {
    }

    /**
     * Crea un objeto Festivo.
     *
     * @param id el id en la BBDD. Se puede dejar nulo y que se autoincremente.
     * @param fecha la fecha del día festivo.
     * @param descripcion la descripción del día festivo.
     */
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

    /**
     * Obtiene la información del objeto en formato adecuado para las respuestas del recurso REST.
     *
     * @return la información del objeto.
     */
    public Map<String, String> toMap() {
        DateTimeFormatter formmater = DateTimeFormatter.ofPattern("MM-dd");
        return Map.of("descripcion", this.descripcion, "fecha", this.fecha.format(formmater));
    }

    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Comprueba si una fecha para día festivo es válida .
     *
     * @param fecha la fecha a comprobar.
     * @return true si es válida, false en caso contrario.
     */
    private boolean esFechaValida(LocalDate fecha) {
        if (fecha == null) {return false;}
        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY) {return false;}
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {return false;}
        if (CalcUtils.esFechaSinBoletin(fecha)) {return false;}
        return true;
    }
}
