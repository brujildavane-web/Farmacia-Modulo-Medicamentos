package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioRol;
import co.edu.sena.farmacia.logica.ServicioUsuario;
import co.edu.sena.farmacia.modelo.Usuario;
import co.edu.sena.farmacia.util.ConversorDatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del modulo de Usuarios (tabla TblUsuario).
 *
 * Ademas del CRUD permite cambiar la contrasena de un usuario, que se guarda
 * siempre cifrada por la capa de logica.
 */
@WebServlet(name = "ServletUsuario", urlPatterns = {"/usuarios"})
public class ServletUsuario extends ServletBase {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "usuarios.jsp";
    private static final String RUTA = "usuarios";

    private final transient ServicioUsuario servicioUsuario = new ServicioUsuario();
    private final transient ServicioRol servicioRol = new ServicioRol();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "listar");

        try {
            if ("editar".equals(accion)) {
                peticion.setAttribute("usuarioEnEdicion",
                        servicioUsuario.consultarPorId(leerEntero(peticion, "id")));

            } else if ("eliminar".equals(accion)) {
                servicioUsuario.eliminar(leerEntero(peticion, "id"));
                publicarExito(peticion, "El usuario fue eliminado correctamente.");
                redirigirAlModulo(peticion, respuesta, RUTA);
                return;
            }

            peticion.setAttribute("listaUsuarios", servicioUsuario.consultarTodos());
            peticion.setAttribute("listaRoles", servicioRol.consultarTodos());

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        reenviarAVista(peticion, respuesta, VISTA);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "guardar");

        try {
            if ("cambiarpassword".equals(accion)) {
                servicioUsuario.cambiarPassword(
                        leerEntero(peticion, "idUsuario"), peticion.getParameter("password"));
                publicarExito(peticion, "La contrasena fue actualizada correctamente.");

            } else {
                guardarUsuario(peticion);
            }

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        redirigirAlModulo(peticion, respuesta, RUTA);
    }

    /**
     * Arma el usuario con los parametros del formulario y lo guarda.
     *
     * @param peticion peticion HTTP con los datos enviados por POST
     */
    private void guardarUsuario(HttpServletRequest peticion) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(leerEnteroOpcional(peticion, "idUsuario"));
        usuario.setEmail(peticion.getParameter("email"));
        usuario.setIdRol(leerEnteroOpcional(peticion, "idRol"));
        usuario.setFechaRegistro(
                ConversorDatos.aFechaOpcional(peticion.getParameter("fechaRegistro"), "fecha de registro"));

        if (usuario.getIdUsuario() > 0) {
            servicioUsuario.modificar(usuario);
            publicarExito(peticion, "El usuario fue actualizado correctamente.");
        } else {
            servicioUsuario.registrar(usuario, peticion.getParameter("password"));
            publicarExito(peticion, "El usuario fue registrado correctamente.");
        }
    }
}
