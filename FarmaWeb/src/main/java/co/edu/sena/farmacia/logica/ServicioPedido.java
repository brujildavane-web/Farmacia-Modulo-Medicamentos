package co.edu.sena.farmacia.logica;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import co.edu.sena.farmacia.modelo.Pedido;
import co.edu.sena.farmacia.persistencia.LineaPedidoDao;
import co.edu.sena.farmacia.persistencia.PedidoDao;
import co.edu.sena.farmacia.util.ConversorDatos;

/**
 * Capa de Logica del modulo de Pedidos.
 *
 * Controla los estados por los que puede pasar un pedido y recalcula su
 * total a partir de las lineas de detalle registradas.
 */
public class ServicioPedido {

    /** Estados validos del ciclo de vida de un pedido. */
    public static final List<String> ESTADOS_VALIDOS = Arrays.asList(
            "PENDIENTE", "CONFIRMADO", "EN CAMINO", "ENTREGADO", "ANULADO");

    private final PedidoDao pedidoDao = new PedidoDao();
    private final LineaPedidoDao lineaPedidoDao = new LineaPedidoDao();

    /**
     * Consulta los pedidos registrados.
     *
     * @return lista de pedidos del mas reciente al mas antiguo
     */
    public List<Pedido> consultarTodos() {
        return pedidoDao.listar();
    }

    /**
     * Busca un pedido por su identificador.
     *
     * @param idPedido identificador del pedido
     * @return el pedido encontrado
     */
    public Pedido consultarPorId(int idPedido) {
        Pedido pedido = pedidoDao.buscarPorId(idPedido);
        if (pedido == null) {
            throw new ExcepcionNegocio("El pedido solicitado no existe.");
        }
        return pedido;
    }

    /**
     * Registra un pedido nuevo.
     *
     * @param pedido datos del pedido
     */
    public void registrar(Pedido pedido) {
        validar(pedido);
        pedidoDao.insertar(pedido);
    }

    /**
     * Modifica un pedido existente.
     *
     * @param pedido datos nuevos del pedido
     */
    public void modificar(Pedido pedido) {
        validar(pedido);
        pedidoDao.actualizar(pedido);
    }

    /**
     * Cambia el estado de un pedido durante su seguimiento.
     *
     * @param idPedido identificador del pedido
     * @param estadoNuevo estado al que pasa el pedido
     */
    public void cambiarEstado(int idPedido, String estadoNuevo) {
        if (ConversorDatos.estaVacio(estadoNuevo)) {
            throw new ExcepcionNegocio("Debe indicar el estado del pedido.");
        }

        String estado = estadoNuevo.trim().toUpperCase();
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new ExcepcionNegocio("El estado " + estadoNuevo + " no es valido para un pedido.");
        }

        pedidoDao.actualizarEstado(idPedido, estado);
    }

    /**
     * Recalcula el total del pedido sumando los subtotales de sus lineas.
     *
     * Se invoca cada vez que se agrega, modifica o elimina una linea para
     * que el encabezado y el detalle siempre coincidan.
     *
     * @param idPedido identificador del pedido
     * @return total recalculado
     */
    public BigDecimal recalcularTotal(int idPedido) {
        BigDecimal total = lineaPedidoDao.sumarSubtotalesPorPedido(idPedido);
        pedidoDao.actualizarTotal(idPedido, total);
        return total;
    }

    /**
     * Elimina un pedido del sistema.
     *
     * @param idPedido identificador del pedido
     */
    public void eliminar(int idPedido) {
        pedidoDao.eliminar(idPedido);
    }

    /**
     * Cuenta los pedidos registrados.
     *
     * @return cantidad de pedidos
     */
    public int contar() {
        return pedidoDao.contar();
    }

    /**
     * Calcula el total vendido por la farmacia.
     *
     * @return suma de los pedidos no anulados
     */
    public BigDecimal calcularTotalVentas() {
        return pedidoDao.sumarVentas();
    }

    private void validar(Pedido pedido) {
        if (pedido.getIdCliente() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el cliente del pedido.");
        }
        if (pedido.getIdFarmaceutico() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el farmaceutico que atiende el pedido.");
        }
        if (ConversorDatos.estaVacio(pedido.getEstado())) {
            throw new ExcepcionNegocio("Debe indicar el estado del pedido.");
        }
        if (!ESTADOS_VALIDOS.contains(pedido.getEstado().trim().toUpperCase())) {
            throw new ExcepcionNegocio("El estado " + pedido.getEstado() + " no es valido.");
        }
        if (pedido.getTotal() != null && pedido.getTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new ExcepcionNegocio("El total del pedido no puede ser negativo.");
        }

        pedido.setEstado(pedido.getEstado().trim().toUpperCase());

        // Las columnas Ped_Fecha y Ped_Total no admiten nulos: se completan
        // aqui para que tanto el registro como la edicion queden consistentes.
        if (pedido.getFecha() == null) {
            pedido.setFecha(LocalDateTime.now());
        }
        if (pedido.getTotal() == null) {
            pedido.setTotal(BigDecimal.ZERO);
        }
    }
}
