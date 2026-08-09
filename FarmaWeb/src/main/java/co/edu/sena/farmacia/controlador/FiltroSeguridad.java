package co.edu.sena.farmacia.controlador;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Restringe el acceso a los modulos del sistema a los usuarios autenticados.
 *
 * Cumple el requisito no funcional de seguridad que exige controlar el
 * ingreso a las funcionalidades mediante credenciales validas.
 */
@WebFilter("/*")
public class FiltroSeguridad implements Filter {

    /** Rutas que se pueden visitar sin haber iniciado sesion. */
    private static final List<String> RUTAS_PUBLICAS = Arrays.asList(
            "/login", "/css/", "/img/", "/favicon.ico");

    @Override
    public void doFilter(ServletRequest peticionGenerica, ServletResponse respuestaGenerica,
            FilterChain cadena) throws IOException, ServletException {

        HttpServletRequest peticion = (HttpServletRequest) peticionGenerica;
        HttpServletResponse respuesta = (HttpServletResponse) respuestaGenerica;

        String rutaSolicitada = peticion.getRequestURI().substring(peticion.getContextPath().length());

        if (esRutaPublica(rutaSolicitada) || existeSesion(peticion)) {
            cadena.doFilter(peticion, respuesta);
            return;
        }

        respuesta.sendRedirect(peticion.getContextPath() + "/login");
    }

    /**
     * Determina si la ruta puede visitarse sin autenticacion.
     *
     * @param rutaSolicitada ruta pedida por el navegador
     * @return true cuando la ruta es publica
     */
    private boolean esRutaPublica(String rutaSolicitada) {
        if (rutaSolicitada.isEmpty() || rutaSolicitada.equals("/")) {
            return true;
        }

        for (String rutaPublica : RUTAS_PUBLICAS) {
            if (rutaSolicitada.startsWith(rutaPublica)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica que exista un usuario autenticado en la sesion.
     *
     * @param peticion peticion HTTP en curso
     * @return true cuando la sesion tiene un usuario
     */
    private boolean existeSesion(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession(false);
        return sesion != null && sesion.getAttribute(ServletBase.ATRIBUTO_USUARIO) != null;
    }
}
