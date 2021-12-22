package es.bocm.numbot.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Servicio REST que proporciona información sobre la edición del Boletín de la Comunidad de Madrid (BOCM).
 * Se proporciona información sobre:
 * <p>
 *     <ul>
 *         <li> El número de Boletín que corresponde a una fecha determinada. </li>
 *         <li> El número de boletines seguidos que caen en días no laborales a partir de dicha fecha. </li>
 *     </ul>
 * <p>
 *
 * Los cálculos se realizan a partir de los siguientes factores:
 * <p>
 *     <ul>
 *         <li> La numeración de los boletines: el primero del año es el número 1, y se publica un Boletín cada día que
 *         corresponde, salvo en casos especiales.</li>
 *         <li> Los días en que sale un número del Boletín: todos los días excepto domingos, el 1 de enero, el 25 de
 *         diciembre y Viernes Santo.</li>
 *         <li> Los días laborales: de lunes a viernes excepto los festivos que correspondan cada año.</li>
 *         <li> Los boletines extraordinarios hechos hasta la fecha en ese año. En ocasiones especiales se saca más
 *         de un número del Boletín en un mismo día, e incluso se puede sacar un boletín extraordinario en domingo
 *         u otros días en que en principio no toca.</li>
 *     </ul>
 * <p>
 *
 * Se proporcionan endpoints para la consulta y actualización de los festivos y números extraordinarios.
 */
@ApplicationPath("/api")
public class NumbotApplication extends Application {

}
