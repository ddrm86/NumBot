package es.bocm.numbot.entities;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.*;

import java.util.Collection;
import java.util.List;

public class FestivoDao {
    @PersistenceContext(unitName = "pu-numbot")
    private EntityManager em;
    @Resource
    private UserTransaction userTransaction;

    public List<Festivo> buscarFestivosPorAnno(int anno) {
        return em.createNamedQuery("Festivo.buscarPorAnno", Festivo.class)
                .setParameter("anno", anno)
                .getResultList();
    }

    public void borrarFestivos(Collection<Festivo> festivos)
            throws SystemException, NotSupportedException, HeuristicRollbackException,
            HeuristicMixedException, RollbackException {
        userTransaction.begin();
        festivos.forEach(f -> em.remove(f));
        userTransaction.commit();
    }

    public void crearFestivos(Collection<Festivo> festivos)
            throws SystemException, NotSupportedException, HeuristicRollbackException,
            HeuristicMixedException, RollbackException {
        userTransaction.begin();
        festivos.forEach(f -> em.merge(f));
        userTransaction.commit();
    }
}
