package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioLineaPedido;
import co.edu.sena.farmacia.logica.ServicioPedido;
import co.edu.sena.farmacia.logica.ServicioProducto;
import co.edu.sena.farmacia.modelo.LineaPedido;
import co.edu.sena.farmacia.util.ConversorDatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del modulo de Detalle de Pedidos (tabla TblLineaPedido).
 *
 * Cada vez que se guarda o elimina una linea, la capa de logica valida el
 * stock, exige la formula medica si aplica y recalcula el total del pedido.
 */
@WebServlet(name = "ServletLineaPedido", urlPatterns = {"/lineas"})
public class ServletLineaPedido extends ServletBase {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "lineas.jsp";
    private static final String RUTA = "lineas";

    private final transient ServicioLineaPedido servicioLinea = new ServicioLineaPedido();
    private final transient ServicioPedido servicioPedido = new ServicioPedido();
    private final transient ServicioProducto servicioProducto = new ServicioProducto();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "listar");

        try {
            if ("editar".equals(accion)) {
                peticion.setAttribute("lineaEnEdicion",
                        servicioLinea.consultarPorId(leerEntero(peticion, "id")));

            } else if ("eliminar".equals(accion)) {
                servicioLinea.eliminar(leerEntero(peticion, "id"));
                publicarExito(peticion, "La linea fue eliminada y el total del pedido se actualizo.");
                redirigirAlModulo(peticion, respuesta, RUTA);
                return;
            }

            cargarListaDeLineas(peticion);
            peticion.setAttribute("listaPedidos", servicioPedido.consultarTodos());
            peticion.setAttribute("listaProductos", servicioProducto.consultarTodos());

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        reenviarAVista(peticion, respuesta, VISTA);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        try {
            LineaPedido linea = new LineaPedido();
            linea.setIdLinea(leerEnteroOpcional(peticion, "idLinea"));
            linea.setIdPedido(leerEnteroOpcional(peticion, "idPedido"));
            linea.setIdProducto(leerEnteroOpcional(peticion, "idProducto"));
            linea.setCantidad(ConversorDatos.aDouble(peticion.getParameter("cantidad"), "cantidad"));

            if (linea.getIdLinea() > 0) {
                servicioLinea.modificar(linea);
                publicarExito(peticion, "La linea del pedido fue actualizada.");
            } else {
                servicioLinea.registrar(linea);
                publicarExito(peticion, "El producto fue agregado al pedido.");
            }

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        redirigirAlModulo(peticion, respuesta, RUTA);
    }

    /**
     * Carga todas las lineas o solo las de un pedido, segun el filtro GET.
     *
     * @param peticion peticion HTTP en curso
     */
    private void cargarListaDeLineas(HttpServletRequest peticion) {
        int idPedidoFiltro = leerEnteroOpcional(peticion, "idPedidoFiltro");

        if (idPedidoFiltro > 0) {
            peticion.setAttribute("listaLineas", servicioLinea.consultarPorPedido(idPedidoFiltro));
            peticion.setAttribute("idPedidoFiltro", idPedidoFiltro);
        } else {
            peticion.setAttribute("listaLineas", servicioLinea.consultarTodos());
        }
    }
}
