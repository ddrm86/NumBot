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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static es.bocm.numbot.rest.RestUtils.*;

@Path("/extraordinarios")
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
            List<Extraordinario> extraordinarios;
            try {
                extraordinarios = buscarExtraordinariosPorAnno(em, Integer.parseInt(anno));
            } catch (Exception e) {
                return crearRespuestaErrorDesconocido();
            }
            List<Map<String, String>> data = extraordinarios.stream().map(Extraordinario::toMap).toList();
            ExtraordinarioResponse response = new ExtraordinarioResponse(true, data);
            return crearRespuestaJson(Response.Status.OK, response);
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{fecha}")
    public Response createOrUpdateNumExtraordinarios(@PathParam("fecha") String fecha_str, String ext_json) {
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fecha_str);
        } catch (DateTimeParseException e) {
            return crearRespuestaFechaNoValida();
        }
        Extraordinario ext;
        Extraordinario ext_candidato;
        try {
            ext_candidato = crearExtraordinario(fecha, ext_json);
        } catch (IllegalArgumentException | JsonSyntaxException | NullPointerException e) {
            ErrorResponse response = new ErrorResponse("Formato de número de boletines" +
                    " extraordinarios incorrecto. Debe ser un entero mayor o igual que cero. Ejemplo:" +
                    " {\"numero_extraordinarios\": \"1\"}");
            return crearRespuestaJson(Response.Status.BAD_REQUEST, response);
        }
        Optional<Extraordinario> opt_ext = buscarPorFecha(fecha);
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
        ExtraordinarioResponse response = new ExtraordinarioResponse(true, List.of(ext.toMap()));
        return crearRespuestaJson(Response.Status.OK, response);
    }


    private Extraordinario crearExtraordinario(LocalDate fecha, String ext_json) {
        Type type = new TypeToken<Map<String, Integer>>() {
        }.getType();
        Map<String, Integer> num_ext = new Gson().fromJson(ext_json, type);
        return new Extraordinario(null, fecha, num_ext.get("numero_extraordinarios"));
    }

    private Optional<Extraordinario> buscarPorFecha(LocalDate fecha) {
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
}
