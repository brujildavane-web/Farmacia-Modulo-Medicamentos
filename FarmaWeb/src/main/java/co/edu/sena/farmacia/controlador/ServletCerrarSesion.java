package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controlador que cierra la sesion del usuario y lo devuelve al formulario
 * de ingreso.
 */
@WebServlet(name = "ServletCerrarSesion", urlPatterns = {"/logout"})
public class ServletCerrarSesion extends ServletBase {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        HttpSession sesion = peticion.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }

        respuesta.sendRedirect(peticion.getContextPath() + "/login");
    }
}
