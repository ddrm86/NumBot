package es.bocm.numbot.entities;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Stateless
public class ExtraordinarioDao {
    @PersistenceContext(unitName = "pu-numbot")
    private EntityManager em;

    public Optional<Extraordinario> buscarPorFecha(LocalDate fecha) {
        try {
            Extraordinario ext =
                    em.createQuery("select e from Extraordinario e where e.fecha = :fecha", Extraordinario.class)
                            .setParameter("fecha", fecha)
                            .getSingleResult();
            return Optional.of(ext);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Extraordinario> buscarExtraordinariosPorAnno(int anno) {
        return em.createNamedQuery("Extraordinario.buscarPorAnno", Extraordinario.class)
                .setParameter("anno", anno)
                .getResultList();
    }

    public void crearOActualizar(Extraordinario extraordinario) {
        em.merge(extraordinario);
    }
}
