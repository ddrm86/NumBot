package es.bocm.numbot.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import es.bocm.numbot.entities.Extraordinario;
import jakarta.annotation.Resource;
import jakarta.persistence.*;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

@Path("/numero-extraordinarios")
public class ExtraordinarioResource {
    @PersistenceContext(unitName = "pu-numbot")
    private EntityManager em;
    @Resource
    private UserTransaction userTransaction;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{anno}")
    public Response getNumExtraordinarios(@PathParam("anno") String anno) {
        ExtraordinarioResponse response;
        if (!esAnnoValido(anno)) {
            return crearRespuestaAnnoNoValido();
        } else {
            Optional<Extraordinario> opt_ext;
            try {
                opt_ext = buscarPorAnno(anno);
            } catch (Exception e) {
                return crearRespuestaErrorDesconocido();
            }
            if (opt_ext.isPresent()) {
                response = new ExtraordinarioResponse(true, "numero_extraordinarios", opt_ext.get().getNumero());
                return crearRespuestaJson(Response.Status.OK, response);
            } else {
                response = new ExtraordinarioResponse(false, "error",
                        "Faltan datos en la BBDD para procesar la petición: no está establecida la cuenta " +
                                "actual de boletines extraordinarios de este año.");
                return crearRespuestaJson(Response.Status.NOT_FOUND, response);
            }
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{anno}")
    public Response createOrUpdateNumExtraordinarios(@PathParam("anno") String anno, String ext_json) {
        ExtraordinarioResponse response;
        if (!esAnnoValido(anno)) {
            return crearRespuestaAnnoNoValido();
        } else {
            Extraordinario ext;
            Extraordinario ext_candidato = crearExtraordinario(anno, ext_json); //TODO handle bad arguments
            Optional<Extraordinario> opt_ext = buscarPorAnno(anno);
            if (opt_ext.isPresent()) {
                ext = opt_ext.get();
                ext.setNumero(ext_candidato.getNumero());
            } else {
                ext = ext_candidato;
            }
            try {
                userTransaction.begin();
                em.merge(ext);
                userTransaction.commit();
            } catch (Exception e) {
                return crearRespuestaErrorDesconocido();
            }
            response = new ExtraordinarioResponse(true, "numero_extraordinarios", ext.getNumero());
            return crearRespuestaJson(Response.Status.OK, response);
        }
    }

    private Extraordinario crearExtraordinario(String anno, String ext_json) {
        Type type = new TypeToken<Map<String, Integer>>(){}.getType();
        Map<String, Integer> num_ext = new Gson().fromJson(ext_json, type);
        return new Extraordinario(null, Integer.parseInt(anno), num_ext.get("numero_extraordinarios"));
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

    private Response crearRespuestaAnnoNoValido() {
        ExtraordinarioResponse response = new ExtraordinarioResponse(false, "error", "Año con formato " +
                "incorrecto. El formato debe ser YYYY");
        return crearRespuestaJson(Response.Status.BAD_REQUEST, response);
    }

    private Response crearRespuestaErrorDesconocido() {
        ExtraordinarioResponse response = new ExtraordinarioResponse(false, "error",
                "Error desconocido. con el administrador de sistemas para que revise la conexión con la BBDD" +
                " y otras posibles causas.");
        return crearRespuestaJson(Response.Status.INTERNAL_SERVER_ERROR, response);
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
