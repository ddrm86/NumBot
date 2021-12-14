package es.bocm.numbot.rest;

import es.bocm.numbot.entities.Festivo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

import static es.bocm.numbot.rest.RestUtils.*;

@Path("/festivos")
public class FestivoResource {
    @PersistenceContext(unitName = "pu-numbot")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{anno}")
    public Response getFestivos(@PathParam("anno") String anno) {
        if (esAnnoNoValido(anno)) {
            return crearRespuestaAnnoNoValido();
        } else {
            List<Festivo> festivos;
            try {
                festivos = buscarPorAnno(anno);
            } catch (Exception e) {
                return crearRespuestaErrorDesconocido();
            }
            if (festivos.isEmpty()) {
                ErrorResponse response = new ErrorResponse(
                        "Faltan datos en la BBDD para procesar la petición: no están establecidos los " +
                                "festivos de este año.");
                return crearRespuestaJson(Response.Status.NOT_FOUND, response);
            } else {
                List<Map<String,String>> data = festivos.stream().map(Festivo::toMap).toList();
                FestivoResponse response = new FestivoResponse(true, data);
                return crearRespuestaJson(Response.Status.OK, response);
            }
        }
    }

    private List<Festivo> buscarPorAnno(String anno) {
        return em.createQuery("select e from Festivo e where YEAR(e.fecha) = :anno", Festivo.class)
                .setParameter("anno", Integer.parseInt(anno))
                .getResultList();
    }

}
