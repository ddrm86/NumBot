package es.bocm.numbot.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Entity
@Table(name = "extraordinarios")
@NamedQuery(name="Extraordinario.buscarPorAnno", query="select e from Extraordinario e where YEAR(e.fecha) = :anno")
public class Extraordinario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "FECHA", nullable = false, unique = true)
    private LocalDate fecha;

    @Column(name = "numero", nullable = false)
    private int numero;

    public Extraordinario() {
    }

    public Extraordinario(Long id, LocalDate fecha, int numero) {
        this.id = id;
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        this.fecha = fecha;
        if (numero <= 0) {
            throw new IllegalArgumentException("El número de boletines extraordinarios" +
                    " debe ser mayor que cero");
        }
        this.numero = numero;
    }

    public Map<String, String> toMap() {
        DateTimeFormatter formmater = DateTimeFormatter.ofPattern("MM-dd");
        return Map.of("numero", Integer.toString(this.numero), "fecha", this.fecha.format(formmater));
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
