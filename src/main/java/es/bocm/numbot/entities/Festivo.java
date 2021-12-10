package es.bocm.numbot.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "festivos")
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
        if (fecha == null) {
            throw new IllegalArgumentException("Se debe especificar una fecha");
        }
        this.fecha = fecha;
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("Se debe especificar una descripción");
        }
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Long getId() {
        return id;
    }
}
