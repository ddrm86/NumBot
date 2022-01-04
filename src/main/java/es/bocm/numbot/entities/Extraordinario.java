package es.bocm.numbot.entities;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Representa el número de boletines extraordiarios publicados en una fecha determinada.
 */
@Entity
@Table(name = "extraordinarios")
@NamedQuery(name="Extraordinario.buscarPorAnno", query="select e from Extraordinario e where YEAR(e.fecha) = :anno")
public class Extraordinario {
    private static final Logger log = LoggerFactory.getLogger(Extraordinario.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Fecha de los boletines extraordinarios.
     */
    @Column(name = "FECHA", nullable = false, unique = true)
    private LocalDate fecha;

    /**
     * Número de boletines extraordinarios publicados en la fecha. Más de uno es extremadamente raro.
     */
    @Column(name = "numero", nullable = false)
    private int numero;

    public Extraordinario() {
    }

    /**
     * Crea un objeto Extraordinario.
     *
     * @param id el id en la BBDD. Se puede dejar nulo y que se autoincremente.
     * @param fecha la fecha de los boletines extraordinarios.
     * @param numero el número de boletines extraordinarios publicados en la fecha.
     */
    public Extraordinario(Long id, LocalDate fecha, int numero) {
        log.debug("Inicia creación de entidad Extraordinario con id {}, fecha {} y número {}", id, fecha, numero);
        this.id = id;
        if (fecha == null) {
            log.warn("Se intentó crear entidad Extraordinario con fecha nula");
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        this.fecha = fecha;
        if (numero <= 0) {
            log.warn("Se intentó crear entidad Extraordinario con número de boletines extraordinarios no válido");
            throw new IllegalArgumentException("El número de boletines extraordinarios" +
                    " debe ser mayor que cero");
        }
        this.numero = numero;
        log.debug("Finaliza creación de entidad Extraordinario");
    }

    /**
     * Obtiene la información del objeto en formato adecuado para las respuestas del recurso REST.
     *
     * @return la información del objeto.
     */
    public Map<String, String> toMap() {
        log.debug("Inicia obtención de información de entidad Extraordinario con formato para respuesta REST.");
        DateTimeFormatter formmater = DateTimeFormatter.ofPattern("MM-dd");
        Map<String, String> mapa = Map.of("numero", Integer.toString(this.numero), "fecha",
                this.fecha.format(formmater));
        log.debug("Finaliza obtención de información de entidad Extraordinario con formato para respuesta REST " +
                "con resultado\n{}", mapa);
        return mapa;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
}
