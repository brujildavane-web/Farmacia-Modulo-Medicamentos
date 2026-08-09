package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ExcepcionNegocio;
import co.edu.sena.farmacia.persistencia.ExcepcionPersistencia;
import co.edu.sena.farmacia.util.ConversorDatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Clase padre de todos los servlets del sistema.
 *
 * Reune el comportamiento que comparten los trece modulos: reenviar la
 * peticion a una pagina JSP, publicar mensajes de exito o error y traducir
 * las excepciones a un texto entendible para el usuario.
 *
 * Aplica el principio de herencia para no repetir este codigo en cada
 * controlador del proyecto.
 */
public abstract class ServletBase extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Carpeta donde viven las paginas JSP de los modulos. */
    protected static final String RUTA_VISTAS = "/WEB-INF/jsp/modulos/";

    /** Nombre con que se guarda el usuario autenticado en la sesion. */
    public static final String ATRIBUTO_USUARIO = "usuarioSesion";

    /** Nombres de los mensajes que la cabecera JSP muestra y luego borra. */
    public static final String ATRIBUTO_MENSAJE_EXITO = "mensajeExito";
    public static final String ATRIBUTO_MENSAJE_ERROR = "mensajeError";

    /**
     * Entrega el control a una pagina JSP para que dibuje la interfaz.
     *
     * @param peticion peticion HTTP en curso
     * @param respuesta respuesta HTTP en curso
     * @param nombreVista nombre del archivo JSP dentro de la carpeta de modulos
     */
    protected void reenviarAVista(HttpServletRequest peticion, HttpServletResponse respuesta,
            String nombreVista) throws ServletException, IOException {
        peticion.getRequestDispatcher(RUTA_VISTAS + nombreVista).forward(peticion, respuesta);
    }

    /**
     * Guarda un mensaje de exito que se mostrara en la siguiente pantalla.
     *
     * @param peticion peticion HTTP en curso
     * @param mensaje texto a mostrar al usuario
     */
    protected void publicarExito(HttpServletRequest peticion, String mensaje) {
        peticion.getSession().setAttribute(ATRIBUTO_MENSAJE_EXITO, mensaje);
    }

    /**
     * Guarda un mensaje de error que se mostrara en la siguiente pantalla.
     *
     * @param peticion peticion HTTP en curso
     * @param mensaje texto a mostrar al usuario
     */
    protected void publicarError(HttpServletRequest peticion, String mensaje) {
        peticion.getSession().setAttribute(ATRIBUTO_MENSAJE_ERROR, mensaje);
    }

    /**
     * Traduce una excepcion al mensaje que se muestra en la interfaz.
     *
     * Los errores de negocio se muestran tal cual porque estan redactados
     * para el usuario; los errores tecnicos se resumen sin exponer detalles
     * internos del motor de base de datos.
     *
     * @param peticion peticion HTTP en curso
     * @param error excepcion capturada
     */
    protected void publicarErrorDeExcepcion(HttpServletRequest peticion, RuntimeException error) {
        if (error instanceof ExcepcionNegocio) {
            publicarError(peticion, error.getMessage());
        } else if (error instanceof ExcepcionPersistencia) {
            publicarError(peticion, error.getMessage());
            log("Error de persistencia en " + getClass().getSimpleName(), error);
        } else {
            publicarError(peticion, "Ocurrio un error inesperado al procesar la solicitud.");
            log("Error inesperado en " + getClass().getSimpleName(), error);
        }
    }

    /**
     * Aplica el patron peticion-redireccion para que al recargar el navegador
     * no se repita el envio del formulario.
     *
     * @param peticion peticion HTTP en curso
     * @param respuesta respuesta HTTP en curso
     * @param rutaModulo ruta del modulo, por ejemplo "productos"
     */
    protected void redirigirAlModulo(HttpServletRequest peticion, HttpServletResponse respuesta,
            String rutaModulo) throws IOException {
        respuesta.sendRedirect(peticion.getContextPath() + "/" + rutaModulo);
    }

    /**
     * Lee un parametro obligatorio de la peticion y lo convierte a entero.
     *
     * @param peticion peticion HTTP en curso
     * @param nombreParametro nombre del parametro enviado por GET o POST
     * @return valor numerico del parametro
     */
    protected int leerEntero(HttpServletRequest peticion, String nombreParametro) {
        return ConversorDatos.aEntero(peticion.getParameter(nombreParametro), nombreParametro);
    }

    /**
     * Lee un parametro numerico opcional de la peticion.
     *
     * @param peticion peticion HTTP en curso
     * @param nombreParametro nombre del parametro
     * @return valor numerico, o 0 si el parametro no fue enviado
     */
    protected int leerEnteroOpcional(HttpServletRequest peticion, String nombreParametro) {
        String valor = peticion.getParameter(nombreParametro);
        return ConversorDatos.estaVacio(valor) ? 0 : ConversorDatos.aEntero(valor, nombreParametro);
    }

    /**
     * Lee la accion solicitada por el usuario.
     *
     * @param peticion peticion HTTP en curso
     * @param accionPorDefecto accion a usar si el parametro no viene
     * @return nombre de la accion en minusculas
     */
    protected String leerAccion(HttpServletRequest peticion, String accionPorDefecto) {
        String accion = peticion.getParameter("accion");
        return ConversorDatos.estaVacio(accion) ? accionPorDefecto : accion.trim().toLowerCase();
    }

    /**
     * Consulta si hay un usuario autenticado en la sesion.
     *
     * @param peticion peticion HTTP en curso
     * @return true cuando existe sesion activa
     */
    protected boolean existeSesionActiva(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession(false);
        return sesion != null && sesion.getAttribute(ATRIBUTO_USUARIO) != null;
    }
}
