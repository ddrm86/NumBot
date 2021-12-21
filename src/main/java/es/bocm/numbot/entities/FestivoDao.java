package es.bocm.numbot.entities;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Collection;
import java.util.List;

@Stateless
public class FestivoDao {
    @PersistenceContext(unitName = "pu-numbot")
    private EntityManager em;

    public List<Festivo> buscarFestivosPorAnno(int anno) {
        return em.createNamedQuery("Festivo.buscarPorAnno", Festivo.class)
                .setParameter("anno", anno)
                .getResultList();
    }

    public void borrarFestivos(Collection<Festivo> festivos) {
        festivos.forEach(f -> em.remove(em.merge(f)));
    }

    public void crearFestivos(Collection<Festivo> festivos) {
        festivos.forEach(f -> em.merge(f));
    }
}
