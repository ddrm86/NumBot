package es.bocm.numbot.entities;

import es.bocm.numbot.calculations.CalcUtils;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(Festivo.class);

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
        log.debug("Inicia creación de entidad Festivo con id {}, fecha {} y descripción {}", id, fecha, descripcion);
        this.id = id;
        if (!esFechaValida(fecha)) {
            log.warn("Se intentó crear entidad Festivo con fecha no válida");
            throw new IllegalArgumentException("No es una fecha válida");
        }
        this.fecha = fecha;
        if (descripcion == null || descripcion.isBlank()) {
            log.warn("Se intentó crear entidad Festivo sin descripción");
            throw new IllegalArgumentException("Se debe especificar una descripción");
        }
        this.descripcion = descripcion;
        log.debug("Finaliza creación de entidad Festivo");
    }

    /**
     * Obtiene la información del objeto en formato adecuado para las respuestas del recurso REST.
     *
     * @return la información del objeto.
     */
    public Map<String, String> toMap() {
        log.debug("Inicia obtención de información de entidad Festivo con formato para respuesta REST.");
        DateTimeFormatter formmater = DateTimeFormatter.ofPattern("MM-dd");
        Map<String, String> mapa = Map.of("descripcion", this.descripcion, "fecha",
                this.fecha.format(formmater));
        log.debug("Finaliza obtención de información de entidad Festivo con formato para respuesta REST " +
                "con resultado\n{}", mapa);
        return mapa;
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
        log.debug("Inicia validación de la fecha {}", fecha);
        if (fecha == null) {
            log.debug("La fecha es nula");
            return false;
        }
        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY) {
            log.debug("La fecha es en sábado");
            return false;
        }
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            log.debug("La fecha es en domingo");
            return false;
        }
        if (CalcUtils.esFechaSinBoletin(fecha)) {
            log.debug("La fecha es en día sin boletín");
            return false;
        }
        log.debug("Finaliza con éxito validación de la fecha {}", fecha);
        return true;
    }
}
