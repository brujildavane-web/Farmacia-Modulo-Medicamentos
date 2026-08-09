package co.edu.sena.farmacia.logica;

import java.util.List;

import co.edu.sena.farmacia.modelo.LineaPedido;
import co.edu.sena.farmacia.modelo.Producto;
import co.edu.sena.farmacia.persistencia.FormulaMedicaDao;
import co.edu.sena.farmacia.persistencia.LineaPedidoDao;
import co.edu.sena.farmacia.persistencia.ProductoDao;

/**
 * Capa de Logica del modulo de Detalle de Pedidos.
 *
 * Concentra tres reglas de negocio del proyecto: el subtotal siempre se
 * recalcula en el servidor, no se puede vender mas de lo que hay en stock y
 * los medicamentos controlados exigen una formula medica vigente.
 */
public class ServicioLineaPedido {

    private final LineaPedidoDao lineaDao = new LineaPedidoDao();
    private final ProductoDao productoDao = new ProductoDao();
    private final FormulaMedicaDao formulaDao = new FormulaMedicaDao();
    private final ServicioPedido servicioPedido = new ServicioPedido();

    /**
     * Consulta todas las lineas registradas.
     *
     * @return lista de lineas de pedido
     */
    public List<LineaPedido> consultarTodos() {
        return lineaDao.listar();
    }

    /**
     * Consulta el detalle de un pedido.
     *
     * @param idPedido identificador del pedido
     * @return lineas que componen el pedido
     */
    public List<LineaPedido> consultarPorPedido(int idPedido) {
        return lineaDao.listarPorPedido(idPedido);
    }

    /**
     * Busca una linea por su identificador.
     *
     * @param idLinea identificador de la linea
     * @return la linea encontrada
     */
    public LineaPedido consultarPorId(int idLinea) {
        LineaPedido linea = lineaDao.buscarPorId(idLinea);
        if (linea == null) {
            throw new ExcepcionNegocio("La linea de pedido solicitada no existe.");
        }
        return linea;
    }

    /**
     * Agrega un producto al detalle de un pedido.
     *
     * @param linea datos de la linea
     */
    public void registrar(LineaPedido linea) {
        Producto producto = validarYObtenerProducto(linea);

        // El precio siempre se toma del catalogo, nunca del formulario.
        linea.setPrecio(producto.getPrecio());
        linea.setSubtotal(linea.calcularSubtotal());

        lineaDao.insertar(linea);
        servicioPedido.recalcularTotal(linea.getIdPedido());
    }

    /**
     * Modifica una linea del detalle de un pedido.
     *
     * @param linea datos nuevos de la linea
     */
    public void modificar(LineaPedido linea) {
        Producto producto = validarYObtenerProducto(linea);

        linea.setPrecio(producto.getPrecio());
        linea.setSubtotal(linea.calcularSubtotal());

        lineaDao.actualizar(linea);
        servicioPedido.recalcularTotal(linea.getIdPedido());
    }

    /**
     * Elimina una linea y actualiza el total del pedido.
     *
     * @param idLinea identificador de la linea
     */
    public void eliminar(int idLinea) {
        LineaPedido linea = consultarPorId(idLinea);
        lineaDao.eliminar(idLinea);
        servicioPedido.recalcularTotal(linea.getIdPedido());
    }

    /**
     * Cuenta las lineas de pedido registradas.
     *
     * @return cantidad de lineas
     */
    public int contar() {
        return lineaDao.contar();
    }

    /**
     * Valida la linea y devuelve el producto que se esta vendiendo.
     *
     * @param linea linea que se quiere guardar
     * @return producto del catalogo con su precio vigente
     */
    private Producto validarYObtenerProducto(LineaPedido linea) {
        if (linea.getIdPedido() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el pedido al que pertenece la linea.");
        }
        if (linea.getIdProducto() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el producto de la linea.");
        }
        if (linea.getCantidad() <= 0) {
            throw new ExcepcionNegocio("La cantidad debe ser mayor que cero.");
        }

        Producto producto = productoDao.buscarPorId(linea.getIdProducto());
        if (producto == null) {
            throw new ExcepcionNegocio("El producto seleccionado no existe en el catalogo.");
        }

        verificarStockSuficiente(linea, producto);
        verificarFormulaMedica(linea, producto);

        return producto;
    }

    /**
     * Impide vender mas unidades de las que hay disponibles en los lotes.
     */
    private void verificarStockSuficiente(LineaPedido linea, Producto producto) {
        double stockDisponible = productoDao.consultarStockDisponible(linea.getIdProducto());

        if (stockDisponible < linea.getCantidad()) {
            throw new ExcepcionNegocio("Stock insuficiente de " + producto.getNombre()
                    + ": hay " + stockDisponible + " unidades disponibles.");
        }
    }

    /**
     * Rechaza la venta de medicamentos controlados sin receta valida.
     */
    private void verificarFormulaMedica(LineaPedido linea, Producto producto) {
        if (!producto.isRequiereReceta()) {
            return;
        }

        boolean tieneFormula = formulaDao.existeFormulaVigente(
                linea.getIdPedido(), linea.getIdProducto());

        if (!tieneFormula) {
            throw new ExcepcionNegocio("El producto " + producto.getNombre()
                    + " requiere formula medica vigente. Registre la formula antes de agregarlo.");
        }
    }
}
