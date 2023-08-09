package es.bocm.numbot.entities;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DAO para la entidad {@link Extraordinario}
 */
@Stateless
public class ExtraordinarioDao {
    private static final Logger log = LoggerFactory.getLogger(ExtraordinarioDao.class);

    @PersistenceContext(unitName = "pu-numbot")
    private EntityManager em;

    /**
     * Busca un Extraordinario en una fecha determinada.
     *
     * @param fecha la fecha en la que buscar.
     * @return Optional con objeto Extraordinario para la fecha indicada, o vacío si no se encuentra.
     */
    public Optional<Extraordinario> buscarPorFecha(LocalDate fecha) {
        log.debug("Inicia búsqueda de extraordinarios para la fecha {}", fecha);
        try {
            Extraordinario ext =
                    em.createQuery("select e from Extraordinario e where e.fecha = :fecha", Extraordinario.class)
                            .setParameter("fecha", fecha)
                            .getSingleResult();
            log.debug("Finaliza búsqueda de extraordinarios para la fecha {} con resultado {}", fecha, ext);
            return Optional.of(ext);
        } catch (NoResultException e) {
            log.debug("Finaliza búsqueda de extraordinarios para la fecha {} sin éxito", fecha);
            return Optional.empty();
        }
    }

    /**
     * Busca todos los boletines extraordinarios para un año determinado.
     *
     * @param anno año para el que buscar los boletines.
     * @return boletines extraordinarios para el año indicado.
     */
    public List<Extraordinario> buscarExtraordinariosPorAnno(int anno) {
        log.debug("Inicia búsqueda de extraordinarios para el año {}", anno);
        List<Extraordinario> extraordinarios =
                em.createNamedQuery("Extraordinario.buscarPorAnno", Extraordinario.class)
                        .setParameter("anno", anno)
                        .getResultList();
        log.debug("Finaliza búsqueda de extraordinarios para el año {} con resultado:\n{}", anno, extraordinarios);
        return extraordinarios;
    }

    /**
     * Crea o actualiza en la BBDD un Extraordinario.
     *
     * @param extraordinario Extraordinario a crear o actualizar.
     */
    public void crearOActualizar(Extraordinario extraordinario) {
        log.debug("Creando o actualizando Extraordinario {}", extraordinario);
        em.merge(extraordinario);
    }
}
