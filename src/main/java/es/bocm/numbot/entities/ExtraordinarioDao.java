package es.bocm.numbot.entities;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ExtraordinarioDao {
    @PersistenceContext(unitName = "pu-numbot")
    private EntityManager em;
    @Resource
    private UserTransaction userTransaction;

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

    public void crearOActualizar(Extraordinario extraordinario)
            throws SystemException, NotSupportedException, HeuristicRollbackException,
            HeuristicMixedException, RollbackException {
        userTransaction.begin();
        em.merge(extraordinario);
        userTransaction.commit();
    }
}
