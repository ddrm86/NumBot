package es.bocm.numbot.rest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import es.bocm.numbot.entities.Extraordinario;
import es.bocm.numbot.entities.ExtraordinarioDao;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

import static es.bocm.numbot.rest.RestUtils.*;

@Path("/extraordinarios")
public class ExtraordinarioResource {
    @Inject
    ExtraordinarioDao extDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{anno}")
    public Response getNumExtraordinarios(@PathParam("anno") String anno) {
        if (esAnnoNoValido(anno)) {
            return crearRespuestaAnnoNoValido();
        } else {
            List<Extraordinario> extraordinarios;
            try {
                extraordinarios = extDao.buscarExtraordinariosPorAnno(Integer.parseInt(anno));
            } catch (Exception e) {
                return crearRespuestaErrorDesconocido();
            }
            return crearRespuestaExitosa(extraordinarios);
        }
    }

    private static Response crearRespuestaExitosa(Collection<Extraordinario> extraordinarios) {
        List<Map<String, String>> data = extraordinarios.stream().map(Extraordinario::toMap).toList();
        ExtraordinarioResponse response = new ExtraordinarioResponse(data);
        return crearRespuestaJson(Response.Status.OK, response);
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
        Optional<Extraordinario> opt_ext = extDao.buscarPorFecha(fecha);
        if (opt_ext.isPresent()) {
            ext = opt_ext.get();
            ext.setNumero(ext_candidato.getNumero());
        } else {
            ext = ext_candidato;
        }
        try {
            extDao.crearOActualizar(ext);
        } catch (Exception e) {
            return crearRespuestaErrorDesconocido();
        }
        return crearRespuestaExitosa(List.of(ext));
    }

    private Extraordinario crearExtraordinario(LocalDate fecha, String ext_json) {
        Type type = new TypeToken<Map<String, Integer>>() {
        }.getType();
        Map<String, Integer> num_ext = new Gson().fromJson(ext_json, type);
        return new Extraordinario(null, fecha, num_ext.get("numero_extraordinarios"));
    }
}
