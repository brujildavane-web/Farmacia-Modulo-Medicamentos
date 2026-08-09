package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioHistorial;
import co.edu.sena.farmacia.logica.ServicioPedido;
import co.edu.sena.farmacia.logica.ServicioProducto;
import co.edu.sena.farmacia.modelo.Historial;
import co.edu.sena.farmacia.util.ConversorDatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del modulo de Historial de Pedidos (tabla TblHistorial).
 */
@WebServlet(name = "ServletHistorial", urlPatterns = {"/historial"})
public class ServletHistorial extends ServletBase {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "historial.jsp";
    private static final String RUTA = "historial";

    private final transient ServicioHistorial servicioHistorial = new ServicioHistorial();
    private final transient ServicioPedido servicioPedido = new ServicioPedido();
    private final transient ServicioProducto servicioProducto = new ServicioProducto();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "listar");

        try {
            if ("editar".equals(accion)) {
                peticion.setAttribute("historialEnEdicion",
                        servicioHistorial.consultarPorId(leerEntero(peticion, "id")));

            } else if ("eliminar".equals(accion)) {
                servicioHistorial.eliminar(leerEntero(peticion, "id"));
                publicarExito(peticion, "El registro de historial fue eliminado.");
                redirigirAlModulo(peticion, respuesta, RUTA);
                return;
            }

            cargarListaDeHistorial(peticion);
            peticion.setAttribute("listaPedidos", servicioPedido.consultarTodos());
            peticion.setAttribute("listaProductos", servicioProducto.consultarTodos());
            peticion.setAttribute("estadosValidos", ServicioPedido.ESTADOS_VALIDOS);

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        reenviarAVista(peticion, respuesta, VISTA);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        try {
            Historial historial = new Historial();
            historial.setIdPedidoHistorico(leerEnteroOpcional(peticion, "idPedidoHistorico"));
            historial.setIdPedido(leerEnteroOpcional(peticion, "idPedido"));
            historial.setIdProducto(leerEnteroOpcional(peticion, "idProducto"));
            historial.setEstado(peticion.getParameter("estado"));
            historial.setFecha(ConversorDatos.aFechaOpcional(
                    peticion.getParameter("fecha"), "fecha del historial"));

            if (historial.getIdPedidoHistorico() > 0) {
                servicioHistorial.modificar(historial);
                publicarExito(peticion, "El registro de historial fue actualizado.");
            } else {
                servicioHistorial.registrar(historial);
                publicarExito(peticion, "El registro fue agregado al historial del pedido.");
            }

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        redirigirAlModulo(peticion, respuesta, RUTA);
    }

    /**
     * Carga toda la bitacora o solo la de un pedido, segun el filtro GET.
     *
     * @param peticion peticion HTTP en curso
     */
    private void cargarListaDeHistorial(HttpServletRequest peticion) {
        int idPedidoFiltro = leerEnteroOpcional(peticion, "idPedidoFiltro");

        if (idPedidoFiltro > 0) {
            peticion.setAttribute("listaHistorial", servicioHistorial.consultarPorPedido(idPedidoFiltro));
            peticion.setAttribute("idPedidoFiltro", idPedidoFiltro);
        } else {
            peticion.setAttribute("listaHistorial", servicioHistorial.consultarTodos());
        }
    }
}
