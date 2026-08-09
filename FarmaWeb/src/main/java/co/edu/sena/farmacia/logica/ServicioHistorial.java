package co.edu.sena.farmacia.logica;

import java.time.LocalDate;
import java.util.List;

import co.edu.sena.farmacia.modelo.Historial;
import co.edu.sena.farmacia.persistencia.HistorialDao;
import co.edu.sena.farmacia.util.ConversorDatos;

/**
 * Capa de Logica del modulo de Historial de Pedidos.
 *
 * Registra la bitacora de estados que permite rastrear un pedido y auditar
 * las acciones realizadas sobre el.
 */
public class ServicioHistorial {

    private final HistorialDao historialDao = new HistorialDao();

    /**
     * Consulta la bitacora completa del sistema.
     *
     * @return registros de historial del mas reciente al mas antiguo
     */
    public List<Historial> consultarTodos() {
        return historialDao.listar();
    }

    /**
     * Consulta la traza de un pedido para su rastreo.
     *
     * @param idPedido identificador del pedido
     * @return registros del pedido en orden cronologico
     */
    public List<Historial> consultarPorPedido(int idPedido) {
        return historialDao.listarPorPedido(idPedido);
    }

    /**
     * Busca un registro de historial por su identificador.
     *
     * @param idHistorial identificador del registro
     * @return el registro encontrado
     */
    public Historial consultarPorId(int idHistorial) {
        Historial historial = historialDao.buscarPorId(idHistorial);
        if (historial == null) {
            throw new ExcepcionNegocio("El registro de historial solicitado no existe.");
        }
        return historial;
    }

    /**
     * Agrega un registro a la bitacora del pedido.
     *
     * @param historial datos del registro
     */
    public void registrar(Historial historial) {
        validar(historial);
        historialDao.insertar(historial);
    }

    /**
     * Corrige un registro de la bitacora.
     *
     * @param historial datos nuevos del registro
     */
    public void modificar(Historial historial) {
        validar(historial);
        historialDao.actualizar(historial);
    }

    /**
     * Elimina un registro de la bitacora.
     *
     * @param idHistorial identificador del registro
     */
    public void eliminar(int idHistorial) {
        historialDao.eliminar(idHistorial);
    }

    /**
     * Cuenta los registros de la bitacora.
     *
     * @return cantidad de registros
     */
    public int contar() {
        return historialDao.contar();
    }

    private void validar(Historial historial) {
        if (historial.getIdPedido() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el pedido del registro.");
        }
        if (historial.getIdProducto() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el producto del registro.");
        }
        if (ConversorDatos.estaVacio(historial.getEstado())) {
            throw new ExcepcionNegocio("Debe indicar el estado registrado en el historial.");
        }
        if (historial.getFecha() != null && historial.getFecha().isAfter(LocalDate.now())) {
            throw new ExcepcionNegocio("La fecha del historial no puede ser posterior a hoy.");
        }

        historial.setEstado(historial.getEstado().trim().toUpperCase());

        // La columna His_Fecha no admite nulos.
        if (historial.getFecha() == null) {
            historial.setFecha(LocalDate.now());
        }
    }
}
