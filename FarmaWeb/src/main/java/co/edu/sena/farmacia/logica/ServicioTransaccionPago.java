package co.edu.sena.farmacia.logica;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import co.edu.sena.farmacia.modelo.TransaccionPago;
import co.edu.sena.farmacia.persistencia.TransaccionPagoDao;
import co.edu.sena.farmacia.util.ConversorDatos;

/**
 * Capa de Logica del modulo de Transacciones de Pago.
 *
 * Valida el pago de los pedidos e impide registrar dos cobros aprobados
 * sobre el mismo pedido, como control basico de fraude.
 */
public class ServicioTransaccionPago {

    /** Estados con que la pasarela puede responder un intento de pago. */
    public static final List<String> ESTADOS_VALIDOS = Arrays.asList(
            "APROBADA", "PENDIENTE", "RECHAZADA", "REVERSADA");

    private final TransaccionPagoDao transaccionDao = new TransaccionPagoDao();

    /**
     * Consulta las transacciones registradas.
     *
     * @return lista de transacciones de la mas reciente a la mas antigua
     */
    public List<TransaccionPago> consultarTodos() {
        return transaccionDao.listar();
    }

    /**
     * Consulta los pagos de un pedido.
     *
     * @param idPedido identificador del pedido
     * @return transacciones del pedido
     */
    public List<TransaccionPago> consultarPorPedido(int idPedido) {
        return transaccionDao.listarPorPedido(idPedido);
    }

    /**
     * Busca una transaccion por su identificador.
     *
     * @param idTransaccion identificador de la transaccion
     * @return la transaccion encontrada
     */
    public TransaccionPago consultarPorId(int idTransaccion) {
        TransaccionPago transaccion = transaccionDao.buscarPorId(idTransaccion);
        if (transaccion == null) {
            throw new ExcepcionNegocio("La transaccion solicitada no existe.");
        }
        return transaccion;
    }

    /**
     * Registra el pago de un pedido.
     *
     * @param transaccion datos del pago
     */
    public void registrar(TransaccionPago transaccion) {
        validar(transaccion);

        if (transaccion.estaAprobada()
                && transaccionDao.existePagoAprobado(transaccion.getIdPedido())) {
            throw new ExcepcionNegocio(
                    "El pedido ya tiene un pago aprobado registrado. Verifique antes de continuar.");
        }

        transaccionDao.insertar(transaccion);
    }

    /**
     * Modifica los datos de una transaccion existente.
     *
     * @param transaccion datos nuevos del pago
     */
    public void modificar(TransaccionPago transaccion) {
        validar(transaccion);
        transaccionDao.actualizar(transaccion);
    }

    /**
     * Elimina una transaccion de pago.
     *
     * @param idTransaccion identificador de la transaccion
     */
    public void eliminar(int idTransaccion) {
        transaccionDao.eliminar(idTransaccion);
    }

    /**
     * Cuenta las transacciones registradas.
     *
     * @return cantidad de transacciones
     */
    public int contar() {
        return transaccionDao.contar();
    }

    private void validar(TransaccionPago transaccion) {
        if (transaccion.getIdPedido() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el pedido que se esta pagando.");
        }
        if (transaccion.getIdPasarela() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar la pasarela de pago utilizada.");
        }
        if (transaccion.getValor() == null
                || transaccion.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ExcepcionNegocio("El valor del pago debe ser mayor que cero.");
        }
        if (ConversorDatos.estaVacio(transaccion.getEstadoTransaccion())) {
            throw new ExcepcionNegocio("Debe indicar el estado de la transaccion.");
        }

        String estado = transaccion.getEstadoTransaccion().trim().toUpperCase();
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new ExcepcionNegocio("El estado " + transaccion.getEstadoTransaccion()
                    + " no es valido para una transaccion.");
        }

        transaccion.setEstadoTransaccion(estado);

        // La columna Tran_FechaPago no admite nulos.
        if (transaccion.getFechaPago() == null) {
            transaccion.setFechaPago(LocalDateTime.now());
        }
    }
}
