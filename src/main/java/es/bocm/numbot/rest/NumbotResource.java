package es.bocm.numbot.rest;

import es.bocm.numbot.calculations.CalcUtils;
import es.bocm.numbot.calculations.CalcularNumBot;
import es.bocm.numbot.entities.Extraordinario;
import es.bocm.numbot.entities.Festivo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static es.bocm.numbot.rest.RestUtils.*;

@Path("/numero-boletin")
public class NumbotResource {
    @PersistenceContext(unitName = "pu-numbot")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{fecha}")
    public Response getNumbot(@PathParam("fecha") String fecha_str) {
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fecha_str);
        } catch (DateTimeParseException e) {
            return crearRespuestaFechaNoValida();
        }
        int anno = fecha.getYear();
        List<Extraordinario> extAnno;
        List<Festivo> festivosAnno;
        try {
            extAnno = RestUtils.buscarExtraordinariosPorAnno(em, anno);
            festivosAnno = RestUtils.buscarFestivosPorAnno(em, anno);
        } catch (Exception e) {
            return crearRespuestaErrorDesconocido();
        }
        if (festivosAnno.isEmpty()) {
            return crearRespuestaFaltanFestivos();
        }
        int numBot = CalcularNumBot.getNumBot(fecha, extAnno);
        int numBotsEnFestivoSeguidos = CalcUtils.numBotsEnFestivoSeguidos(fecha, festivosAnno);
        NumbotResponse response = new NumbotResponse(Integer.toString(numBot),
                Integer.toString(numBotsEnFestivoSeguidos));
        return crearRespuestaJson(Response.Status.OK, response);
    }
}
