package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioPasarelaPago;
import co.edu.sena.farmacia.logica.ServicioPedido;
import co.edu.sena.farmacia.logica.ServicioTransaccionPago;
import co.edu.sena.farmacia.modelo.TransaccionPago;
import co.edu.sena.farmacia.util.ConversorDatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del modulo de Transacciones de Pago (tabla TblTransaccionPago).
 */
@WebServlet(name = "ServletTransaccionPago", urlPatterns = {"/transacciones"})
public class ServletTransaccionPago extends ServletBase {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "transacciones.jsp";
    private static final String RUTA = "transacciones";

    private final transient ServicioTransaccionPago servicioTransaccion = new ServicioTransaccionPago();
    private final transient ServicioPedido servicioPedido = new ServicioPedido();
    private final transient ServicioPasarelaPago servicioPasarela = new ServicioPasarelaPago();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "listar");

        try {
            if ("editar".equals(accion)) {
                peticion.setAttribute("transaccionEnEdicion",
                        servicioTransaccion.consultarPorId(leerEntero(peticion, "id")));

            } else if ("eliminar".equals(accion)) {
                servicioTransaccion.eliminar(leerEntero(peticion, "id"));
                publicarExito(peticion, "La transaccion de pago fue eliminada.");
                redirigirAlModulo(peticion, respuesta, RUTA);
                return;
            }

            cargarListaDeTransacciones(peticion);
            peticion.setAttribute("listaPedidos", servicioPedido.consultarTodos());
            peticion.setAttribute("listaPasarelas", servicioPasarela.consultarTodos());
            peticion.setAttribute("estadosValidos", ServicioTransaccionPago.ESTADOS_VALIDOS);

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        reenviarAVista(peticion, respuesta, VISTA);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        try {
            TransaccionPago transaccion = new TransaccionPago();
            transaccion.setIdTransaccion(leerEnteroOpcional(peticion, "idTransaccion"));
            transaccion.setIdPedido(leerEnteroOpcional(peticion, "idPedido"));
            transaccion.setIdPasarela(leerEnteroOpcional(peticion, "idPasarela"));
            transaccion.setEstadoTransaccion(peticion.getParameter("estadoTransaccion"));
            transaccion.setValor(ConversorDatos.aDecimal(peticion.getParameter("valor"), "valor"));

            String fechaDigitada = peticion.getParameter("fechaPago");
            if (!ConversorDatos.estaVacio(fechaDigitada)) {
                transaccion.setFechaPago(ConversorDatos.aFechaHora(fechaDigitada, "fecha de pago"));
            }

            if (transaccion.getIdTransaccion() > 0) {
                servicioTransaccion.modificar(transaccion);
                publicarExito(peticion, "La transaccion de pago fue actualizada.");
            } else {
                servicioTransaccion.registrar(transaccion);
                publicarExito(peticion, "El pago fue registrado correctamente.");
            }

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        redirigirAlModulo(peticion, respuesta, RUTA);
    }

    /**
     * Carga todas las transacciones o solo las de un pedido, segun el filtro GET.
     *
     * @param peticion peticion HTTP en curso
     */
    private void cargarListaDeTransacciones(HttpServletRequest peticion) {
        int idPedidoFiltro = leerEnteroOpcional(peticion, "idPedidoFiltro");

        if (idPedidoFiltro > 0) {
            peticion.setAttribute("listaTransacciones",
                    servicioTransaccion.consultarPorPedido(idPedidoFiltro));
            peticion.setAttribute("idPedidoFiltro", idPedidoFiltro);
        } else {
            peticion.setAttribute("listaTransacciones", servicioTransaccion.consultarTodos());
        }
    }
}
