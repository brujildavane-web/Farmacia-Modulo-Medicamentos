package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioPasarelaPago;
import co.edu.sena.farmacia.modelo.PasarelaPago;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del modulo de Pasarelas de Pago (tabla TblPasarelaPago).
 */
@WebServlet(name = "ServletPasarelaPago", urlPatterns = {"/pasarelas"})
public class ServletPasarelaPago extends ServletBase {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "pasarelas.jsp";
    private static final String RUTA = "pasarelas";

    private final transient ServicioPasarelaPago servicioPasarela = new ServicioPasarelaPago();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "listar");

        try {
            if ("editar".equals(accion)) {
                peticion.setAttribute("pasarelaEnEdicion",
                        servicioPasarela.consultarPorId(leerEntero(peticion, "id")));

            } else if ("eliminar".equals(accion)) {
                servicioPasarela.eliminar(leerEntero(peticion, "id"));
                publicarExito(peticion, "La pasarela de pago fue eliminada.");
                redirigirAlModulo(peticion, respuesta, RUTA);
                return;
            }

            peticion.setAttribute("listaPasarelas", servicioPasarela.consultarTodos());

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        reenviarAVista(peticion, respuesta, VISTA);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        try {
            PasarelaPago pasarela = new PasarelaPago();
            pasarela.setIdPasarela(leerEnteroOpcional(peticion, "idPasarela"));
            pasarela.setNombreProveedor(peticion.getParameter("nombreProveedor"));
            pasarela.setApiKeyPublica(peticion.getParameter("apiKeyPublica"));

            if (pasarela.getIdPasarela() > 0) {
                servicioPasarela.modificar(pasarela);
                publicarExito(peticion, "La pasarela de pago fue actualizada.");
            } else {
                servicioPasarela.registrar(pasarela);
                publicarExito(peticion, "La pasarela de pago fue habilitada.");
            }

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        redirigirAlModulo(peticion, respuesta, RUTA);
    }
}
