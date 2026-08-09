package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioCliente;
import co.edu.sena.farmacia.logica.ServicioFarmaceutico;
import co.edu.sena.farmacia.logica.ServicioLineaPedido;
import co.edu.sena.farmacia.logica.ServicioPedido;
import co.edu.sena.farmacia.modelo.Pedido;
import co.edu.sena.farmacia.util.ConversorDatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del modulo de Pedidos (tabla TblPedido).
 *
 * Ademas del CRUD ofrece dos acciones propias del negocio: consultar el
 * detalle de un pedido y cambiar su estado de seguimiento.
 */
@WebServlet(name = "ServletPedido", urlPatterns = {"/pedidos"})
public class ServletPedido extends ServletBase {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "pedidos.jsp";
    private static final String RUTA = "pedidos";

    private final transient ServicioPedido servicioPedido = new ServicioPedido();
    private final transient ServicioCliente servicioCliente = new ServicioCliente();
    private final transient ServicioFarmaceutico servicioFarmaceutico = new ServicioFarmaceutico();
    private final transient ServicioLineaPedido servicioLinea = new ServicioLineaPedido();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "listar");

        try {
            if ("editar".equals(accion)) {
                peticion.setAttribute("pedidoEnEdicion",
                        servicioPedido.consultarPorId(leerEntero(peticion, "id")));

            } else if ("detalle".equals(accion)) {
                int idPedido = leerEntero(peticion, "id");
                peticion.setAttribute("pedidoConsultado", servicioPedido.consultarPorId(idPedido));
                peticion.setAttribute("detalleDelPedido", servicioLinea.consultarPorPedido(idPedido));

            } else if ("eliminar".equals(accion)) {
                servicioPedido.eliminar(leerEntero(peticion, "id"));
                publicarExito(peticion, "El pedido fue eliminado correctamente.");
                redirigirAlModulo(peticion, respuesta, RUTA);
                return;
            }

            peticion.setAttribute("listaPedidos", servicioPedido.consultarTodos());
            peticion.setAttribute("listaClientes", servicioCliente.consultarTodos());
            peticion.setAttribute("listaFarmaceuticos", servicioFarmaceutico.consultarTodos());
            peticion.setAttribute("estadosValidos", ServicioPedido.ESTADOS_VALIDOS);

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
            if ("cambiarestado".equals(accion)) {
                servicioPedido.cambiarEstado(
                        leerEntero(peticion, "idPedido"), peticion.getParameter("estado"));
                publicarExito(peticion, "El estado del pedido fue actualizado.");

            } else if ("recalcular".equals(accion)) {
                int idPedido = leerEntero(peticion, "idPedido");
                publicarExito(peticion, "Total recalculado: $"
                        + servicioPedido.recalcularTotal(idPedido));

            } else {
                guardarPedido(peticion);
            }

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        redirigirAlModulo(peticion, respuesta, RUTA);
    }

    /**
     * Arma el pedido con los parametros del formulario y lo guarda.
     *
     * @param peticion peticion HTTP con los datos enviados por POST
     */
    private void guardarPedido(HttpServletRequest peticion) {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(leerEnteroOpcional(peticion, "idPedido"));
        pedido.setEstado(peticion.getParameter("estado"));
        pedido.setIdCliente(leerEnteroOpcional(peticion, "idCliente"));
        pedido.setIdFarmaceutico(leerEnteroOpcional(peticion, "idFarmaceutico"));

        String fechaDigitada = peticion.getParameter("fecha");
        if (!ConversorDatos.estaVacio(fechaDigitada)) {
            pedido.setFecha(ConversorDatos.aFechaHora(fechaDigitada, "fecha del pedido"));
        }

        if (pedido.getIdPedido() > 0) {
            // El total lo calcula el sistema a partir de las lineas, no el formulario.
            pedido.setTotal(servicioPedido.consultarPorId(pedido.getIdPedido()).getTotal());
            servicioPedido.modificar(pedido);
            publicarExito(peticion, "El pedido fue actualizado correctamente.");
        } else {
            servicioPedido.registrar(pedido);
            publicarExito(peticion, "El pedido fue registrado. Ahora agregue sus productos.");
        }
    }
}
