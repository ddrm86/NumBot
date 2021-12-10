package es.bocm.numbot.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "extraordinarios")
public class Extraordinario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "anno", nullable = false, unique = true)
    private int anno;

    @Column(name = "numero", nullable = false)
    private int numero;

    public Extraordinario() {
    }

    public Extraordinario(Long id, int anno, int numero) {
        this.id = id;
        if (anno <= 0) {
            throw new IllegalArgumentException("El año debe ser mayor que cero");
        }
        this.anno = anno;
        if (numero <= 0) {
            throw new IllegalArgumentException("El número de boletines extraordinarios" +
                    " debe ser mayor que cero");
        }
        this.numero = numero;
    }

    public int getAnno() {
        return anno;
    }

    public Long getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }
}
