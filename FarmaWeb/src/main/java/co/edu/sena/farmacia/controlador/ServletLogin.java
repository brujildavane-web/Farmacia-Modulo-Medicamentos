package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioUsuario;
import co.edu.sena.farmacia.modelo.Usuario;
import co.edu.sena.farmacia.util.ConexionBaseDatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controlador del ingreso al sistema.
 *
 * El metodo GET muestra el formulario de autenticacion y el metodo POST
 * recibe las credenciales digitadas por el usuario.
 */
@WebServlet(name = "ServletLogin", urlPatterns = {"/login"})
public class ServletLogin extends ServletBase {

    private static final long serialVersionUID = 1L;

    private final transient ServicioUsuario servicioUsuario = new ServicioUsuario();

    /**
     * Muestra el formulario de ingreso.
     *
     * Antes de dibujarlo comprueba que la base de datos responda, para que el
     * usuario sepa de inmediato si el motor MySQL esta apagado.
     */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        if (existeSesionActiva(peticion)) {
            redirigirAlModulo(peticion, respuesta, "panel");
            return;
        }

        peticion.setAttribute("baseDatosDisponible", ConexionBaseDatos.probarConexion());
        peticion.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(peticion, respuesta);
    }

    /**
     * Recibe el correo y la contrasena enviados por el formulario y abre la
     * sesion cuando las credenciales son correctas.
     */
    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String email = peticion.getParameter("email");
        String password = peticion.getParameter("password");

        try {
            Usuario usuarioAutenticado = servicioUsuario.autenticar(email, password);

            HttpSession sesion = peticion.getSession(true);
            sesion.setAttribute(ATRIBUTO_USUARIO, usuarioAutenticado);

            publicarExito(peticion, "Bienvenido " + usuarioAutenticado.getEmail() + ".");
            redirigirAlModulo(peticion, respuesta, "panel");

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
            peticion.setAttribute("emailDigitado", email);
            peticion.setAttribute("baseDatosDisponible", ConexionBaseDatos.probarConexion());
            peticion.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(peticion, respuesta);
        }
    }
}
