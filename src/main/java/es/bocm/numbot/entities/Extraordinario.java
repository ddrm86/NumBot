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

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
}
