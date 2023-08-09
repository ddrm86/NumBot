package es.bocm.numbot.rest.numbot;

import es.bocm.numbot.calculations.CalcUtils;
import es.bocm.numbot.calculations.CalcularNumBot;
import es.bocm.numbot.entities.Extraordinario;
import es.bocm.numbot.entities.ExtraordinarioDao;
import es.bocm.numbot.entities.Festivo;
import es.bocm.numbot.entities.FestivoDao;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static es.bocm.numbot.rest.RestUtils.*;

/**
 * <p>Recurso REST que proporciona información sobre la edición del Boletín.
 * Consultar {@link es.bocm.numbot.rest.NumbotApplication}.<p>
 *
 * Ejemplo de uso:
 *
 * <pre>
 * GET: /numero-boletin/2021-12-07
 *
 * HTTP/1.1 200 OK
 * Content-Type: application/json
 * {
 * 	"exito": "true",
 * 	"data": {
 * 		"numero_boletin": "301",
 * 		"numero_boletines_en_no_laboral_seguidos": "1"
 *  }
 * }
 * </pre>
 */
@Path("/numero-boletin")
public class NumbotResource {
    private static final Logger log = LoggerFactory.getLogger(NumbotResource.class);

    @Inject
    ExtraordinarioDao extDao;

    @Inject
    FestivoDao festDao;

    /**
     * Obtiene información sobre el número de boletín y el número de boletines en no laboral seguidos para una fecha
     * determinada.
     *
     * @param fecha_str la fecha en formato YYYY-MM-DD.
     * @return la información solicitada o mensaje de error.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{fecha}")
    public Response getNumbot(@PathParam("fecha") String fecha_str) {
        log.info("Inicia petición GET para la fecha {}", fecha_str);
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fecha_str);
        } catch (DateTimeParseException e) {
            log.warn("Finaliza petición GET con fecha no válida {}", fecha_str, e);
            return crearRespuestaFechaNoValida();
        }
        int anno = fecha.getYear();
        List<Extraordinario> extAnno;
        List<Festivo> festivosAnno;
        try {
            extAnno = extDao.buscarExtraordinariosPorAnno(anno);
            festivosAnno = festDao.buscarFestivosPorAnno(anno);
        } catch (Exception e) {
            log.error("Finaliza petición GET para la fecha {} con error desconocido", fecha_str, e);
            return crearRespuestaErrorDesconocido();
        }
        if (festivosAnno.isEmpty()) {
            log.warn("Finaliza petición GET para la fecha {} sin éxito por falta de festivos", fecha_str);
            return crearRespuestaFaltanFestivos();
        }
        int numBot = CalcularNumBot.getNumBot(fecha, extAnno);
        int numBotsEnFestivoSeguidos = CalcUtils.numBotsEnFestivoSeguidos(fecha, festivosAnno);
        NumbotResponse response = new NumbotResponse(Integer.toString(numBot),
                Integer.toString(numBotsEnFestivoSeguidos));
        log.info("Finaliza petición GET para la fecha {} con éxito", fecha_str);
        return crearRespuestaJson(Response.Status.OK, response);
    }
}
