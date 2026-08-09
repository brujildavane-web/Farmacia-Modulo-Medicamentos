package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioFormulaMedica;
import co.edu.sena.farmacia.logica.ServicioPedido;
import co.edu.sena.farmacia.logica.ServicioProducto;
import co.edu.sena.farmacia.modelo.FormulaMedica;
import co.edu.sena.farmacia.util.ConversorDatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del modulo de Formulas Medicas (tabla TblFormulaMedica).
 *
 * Registra las prescripciones que autorizan la entrega de medicamentos
 * controlados. La capa de logica rechaza las formulas vencidas.
 */
@WebServlet(name = "ServletFormulaMedica", urlPatterns = {"/formulas"})
public class ServletFormulaMedica extends ServletBase {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "formulas.jsp";
    private static final String RUTA = "formulas";

    private final transient ServicioFormulaMedica servicioFormula = new ServicioFormulaMedica();
    private final transient ServicioPedido servicioPedido = new ServicioPedido();
    private final transient ServicioProducto servicioProducto = new ServicioProducto();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "listar");

        try {
            if ("editar".equals(accion)) {
                peticion.setAttribute("formulaEnEdicion",
                        servicioFormula.consultarPorId(leerEntero(peticion, "id")));

            } else if ("eliminar".equals(accion)) {
                servicioFormula.eliminar(leerEntero(peticion, "id"));
                publicarExito(peticion, "La formula medica fue eliminada.");
                redirigirAlModulo(peticion, respuesta, RUTA);
                return;
            }

            cargarListaDeFormulas(peticion);
            peticion.setAttribute("listaPedidos", servicioPedido.consultarTodos());
            peticion.setAttribute("listaProductos", servicioProducto.consultarTodos());
            peticion.setAttribute("formatosPermitidos", ServicioFormulaMedica.FORMATOS_PERMITIDOS);

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        reenviarAVista(peticion, respuesta, VISTA);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        try {
            FormulaMedica formula = new FormulaMedica();
            formula.setIdFormula(leerEnteroOpcional(peticion, "idFormula"));
            formula.setIdPedido(leerEnteroOpcional(peticion, "idPedido"));
            formula.setIdProducto(leerEnteroOpcional(peticion, "idProducto"));
            formula.setArchivo(ConversorDatos.limpiar(peticion.getParameter("archivo")));
            formula.setFechaPrescripcion(ConversorDatos.aFecha(
                    peticion.getParameter("fechaPrescripcion"), "fecha de prescripcion"));
            formula.setFechaVencimiento(ConversorDatos.aFechaOpcional(
                    peticion.getParameter("fechaVencimiento"), "fecha de vencimiento"));

            if (formula.getIdFormula() > 0) {
                servicioFormula.modificar(formula);
                publicarExito(peticion, "La formula medica fue actualizada.");
            } else {
                servicioFormula.registrar(formula);
                publicarExito(peticion, "La formula medica fue registrada correctamente.");
            }

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        redirigirAlModulo(peticion, respuesta, RUTA);
    }

    /**
     * Carga todas las formulas o solo las de un pedido, segun el filtro GET.
     *
     * @param peticion peticion HTTP en curso
     */
    private void cargarListaDeFormulas(HttpServletRequest peticion) {
        int idPedidoFiltro = leerEnteroOpcional(peticion, "idPedidoFiltro");

        if (idPedidoFiltro > 0) {
            peticion.setAttribute("listaFormulas", servicioFormula.consultarPorPedido(idPedidoFiltro));
            peticion.setAttribute("idPedidoFiltro", idPedidoFiltro);
        } else {
            peticion.setAttribute("listaFormulas", servicioFormula.consultarTodos());
        }
    }
}
