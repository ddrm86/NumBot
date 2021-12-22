package es.bocm.numbot.entities;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Collection;
import java.util.List;

/**
 * DAO para la entidad {@link Festivo}
 */
@Stateless
public class FestivoDao {
    @PersistenceContext(unitName = "pu-numbot")
    private EntityManager em;

    /**
     * Busca todos los festivos para un año determinado.
     *
     * @param anno año para el que buscar los boletines.
     * @return festivos para el año indicado.
     */
    public List<Festivo> buscarFestivosPorAnno(int anno) {
        return em.createNamedQuery("Festivo.buscarPorAnno", Festivo.class)
                .setParameter("anno", anno)
                .getResultList();
    }

    /**
     * Borra en la BBDD todos los festivos proporcionados.
     *
     * @param festivos festivos a borrar.
     */
    public void borrarFestivos(Collection<Festivo> festivos) {
        festivos.forEach(f -> em.remove(em.merge(f)));
    }

    /**
     * Crea en la BBDD todos los festivos proporcionados.
     *
     * @param festivos festivos a crear.
     */
    public void crearFestivos(Collection<Festivo> festivos) {
        festivos.forEach(f -> em.merge(f));
    }
}
