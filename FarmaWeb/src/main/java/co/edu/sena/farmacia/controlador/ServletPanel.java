package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioTablero;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del tablero de inicio.
 *
 * Muestra los indicadores de los trece modulos, el total facturado y las
 * alertas de caducidad del inventario.
 */
@WebServlet(name = "ServletPanel", urlPatterns = {"/panel"})
public class ServletPanel extends ServletBase {

    private static final long serialVersionUID = 1L;

    private final transient ServicioTablero servicioTablero = new ServicioTablero();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        try {
            peticion.setAttribute("conteosPorModulo", servicioTablero.contarRegistrosPorModulo());
            peticion.setAttribute("totalVentas", servicioTablero.calcularTotalVentas());
            peticion.setAttribute("alertasCaducidad", servicioTablero.contarAlertasCaducidad());

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        peticion.getRequestDispatcher("/WEB-INF/jsp/panel.jsp").forward(peticion, respuesta);
    }
}
