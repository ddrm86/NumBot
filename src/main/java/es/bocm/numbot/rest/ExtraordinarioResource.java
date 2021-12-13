package es.bocm.numbot.rest;

import com.google.gson.Gson;
import es.bocm.numbot.entities.Extraordinario;
import jakarta.persistence.*;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Path("/numero-extraordinarios")
public class ExtraordinarioResource {
    @PersistenceContext(unitName = "pu-numbot")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{anno}")
    public Response getNumExtraordinarios(@PathParam("anno") String anno) {
        ExtraordinarioResponse response;
        if (!esAnnoValido(anno)) {
            response = new ExtraordinarioResponse(false, "error", "Año con formato incorrecto. " +
                    "El formato debe ser YYYY");
            return crearRespuestaJson(Response.Status.BAD_REQUEST, response);
        } else {
            Optional<Extraordinario> ext;
            try {
                ext = buscarPorAnno(anno);
            } catch (Exception e) {
                response = new ExtraordinarioResponse(false, "error", "Error desconocido. Contactar" +
                        " con el administrador de sistemas para que revise la conexión con la BBDD" +
                        " y otras posibles causas.");
                return crearRespuestaJson(Response.Status.INTERNAL_SERVER_ERROR, response);
            }
            if (ext.isPresent()) {
                response = new ExtraordinarioResponse(true, "numero_extraordinarios", ext.get().getNumero());
                return crearRespuestaJson(Response.Status.OK, response);
            } else {
                response = new ExtraordinarioResponse(false, "error",
                        "Faltan datos en la BBDD para procesar la petición: no está establecida la cuenta " +
                                "actual de boletines extraordinarios de este año.");
                return crearRespuestaJson(Response.Status.NOT_FOUND, response);
            }
        }
    }

    private Response crearRespuestaJson(Response.Status estado, ExtraordinarioResponse objeto_respuesta) {
        return Response
                .status(estado)
                .entity(new Gson().toJson(objeto_respuesta))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private boolean esAnnoValido(String anno) {
        String dummyDate = anno + "-01-01";
        try {
            LocalDate.parse(dummyDate);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    private Optional<Extraordinario> buscarPorAnno(String anno) {
        try {
            Extraordinario ext =
                    em.createQuery("select e from Extraordinario e where e.anno = :anno", Extraordinario.class)
                            .setParameter("anno", Integer.parseInt(anno))
                            .getSingleResult();
            return Optional.of(ext);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
