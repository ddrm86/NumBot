package es.bocm.numbot.rest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import es.bocm.numbot.entities.Extraordinario;
import jakarta.annotation.Resource;
import jakarta.persistence.*;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

import static es.bocm.numbot.rest.RestUtils.*;

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
        if (esAnnoNoValido(anno)) {
            return crearRespuestaAnnoNoValido();
        } else {
            Optional<Extraordinario> opt_ext;
            try {
                opt_ext = buscarPorAnno(anno);
            } catch (Exception e) {
                return crearRespuestaErrorDesconocido();
            }
            if (opt_ext.isPresent()) {
                ExtraordinarioResponse response = new ExtraordinarioResponse(true, "numero_extraordinarios",
                        opt_ext.get().getNumero());
                return crearRespuestaJson(Response.Status.OK, response);
            } else {
                ErrorResponse response = new ErrorResponse(
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
        if (esAnnoNoValido(anno)) {
            return crearRespuestaAnnoNoValido();
        } else {
            Extraordinario ext;
            Extraordinario ext_candidato;
            try {
                ext_candidato = crearExtraordinario(anno, ext_json);
            } catch (IllegalArgumentException | JsonSyntaxException | NullPointerException e) {
                ErrorResponse response = new ErrorResponse("Formato de número de boletines" +
                        " extraordinarios incorrecto. Debe ser un entero mayor o igual que cero. Ejemplo:" +
                        " {\"numero_extraordinarios\": \"1\"}");
                return crearRespuestaJson(Response.Status.BAD_REQUEST, response);
            }
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
            ExtraordinarioResponse response = new ExtraordinarioResponse(true, "numero_extraordinarios",
                    ext.getNumero());
            return crearRespuestaJson(Response.Status.OK, response);
        }
    }

    private Extraordinario crearExtraordinario(String anno, String ext_json) {
        Type type = new TypeToken<Map<String, Integer>>(){}.getType();
        Map<String, Integer> num_ext = new Gson().fromJson(ext_json, type);
        return new Extraordinario(null, Integer.parseInt(anno), num_ext.get("numero_extraordinarios"));
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
