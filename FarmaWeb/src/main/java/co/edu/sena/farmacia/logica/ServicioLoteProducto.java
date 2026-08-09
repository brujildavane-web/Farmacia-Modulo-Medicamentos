package co.edu.sena.farmacia.logica;

import java.time.LocalDate;
import java.util.List;

import co.edu.sena.farmacia.modelo.LoteProducto;
import co.edu.sena.farmacia.persistencia.LoteProductoDao;
import co.edu.sena.farmacia.util.ConversorDatos;

/**
 * Capa de Logica del modulo de Inventario por lotes.
 *
 * Aplica la regla de negocio que exige registrar numero de lote y fecha de
 * vencimiento al ingresar productos, e impide cargar lotes ya caducados.
 */
public class ServicioLoteProducto {

    private final LoteProductoDao loteDao = new LoteProductoDao();

    /**
     * Consulta todos los lotes del inventario.
     *
     * @return lista de lotes ordenada por fecha de vencimiento
     */
    public List<LoteProducto> consultarTodos() {
        return loteDao.listar();
    }

    /**
     * Consulta los lotes de un producto especifico.
     *
     * @param idProducto identificador del producto
     * @return lotes del producto
     */
    public List<LoteProducto> consultarPorProducto(int idProducto) {
        return loteDao.listarPorProducto(idProducto);
    }

    /**
     * Busca un lote por su identificador.
     *
     * @param idLote identificador del lote
     * @return el lote encontrado
     */
    public LoteProducto consultarPorId(int idLote) {
        LoteProducto lote = loteDao.buscarPorId(idLote);
        if (lote == null) {
            throw new ExcepcionNegocio("El lote solicitado no existe.");
        }
        return lote;
    }

    /**
     * Registra un lote nuevo en el inventario.
     *
     * @param lote datos del lote
     */
    public void registrar(LoteProducto lote) {
        validar(lote);

        if (lote.estaVencido()) {
            throw new ExcepcionNegocio(
                    "No se puede ingresar al inventario un lote con fecha de vencimiento pasada.");
        }

        loteDao.insertar(lote);
    }

    /**
     * Modifica los datos de un lote existente.
     *
     * @param lote datos nuevos del lote
     */
    public void modificar(LoteProducto lote) {
        validar(lote);
        loteDao.actualizar(lote);
    }

    /**
     * Descuenta unidades del lote cuando se despacha un pedido.
     *
     * @param idLote identificador del lote
     * @param cantidad unidades a descontar
     */
    public void descontarStock(int idLote, double cantidad) {
        if (cantidad <= 0) {
            throw new ExcepcionNegocio("La cantidad a descontar debe ser mayor que cero.");
        }

        LoteProducto lote = consultarPorId(idLote);
        if (lote.getStockActual() < cantidad) {
            throw new ExcepcionNegocio("El lote solo tiene " + lote.getStockActual() + " unidades.");
        }

        loteDao.descontarStock(idLote, cantidad);
    }

    /**
     * Elimina un lote del inventario.
     *
     * @param idLote identificador del lote
     */
    public void eliminar(int idLote) {
        loteDao.eliminar(idLote);
    }

    /**
     * Cuenta los lotes registrados.
     *
     * @return cantidad de lotes
     */
    public int contar() {
        return loteDao.contar();
    }

    /**
     * Cuenta los lotes que generan alerta de caducidad.
     *
     * @return lotes vencidos o proximos a vencer
     */
    public int contarAlertasCaducidad() {
        return loteDao.contarProximosAVencer(LoteProducto.DIAS_ALERTA_CADUCIDAD);
    }

    private void validar(LoteProducto lote) {
        if (lote.getIdProducto() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el producto al que pertenece el lote.");
        }
        if (lote.getFechaVencimiento() == null) {
            throw new ExcepcionNegocio("La fecha de vencimiento del lote es obligatoria.");
        }
        if (ConversorDatos.estaVacio(lote.getRegistroSanitario())) {
            throw new ExcepcionNegocio("El registro sanitario del lote es obligatorio.");
        }
        if (lote.getStockActual() < 0) {
            throw new ExcepcionNegocio("El stock del lote no puede ser negativo.");
        }
        if (lote.getFechaVencimiento().isAfter(LocalDate.now().plusYears(20))) {
            throw new ExcepcionNegocio("Verifique la fecha de vencimiento: supera los 20 anios.");
        }
    }
}
