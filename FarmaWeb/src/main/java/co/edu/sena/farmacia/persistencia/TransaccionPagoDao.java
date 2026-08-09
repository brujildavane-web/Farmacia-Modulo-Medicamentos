package co.edu.sena.farmacia.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.TransaccionPago;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Transacciones de Pago.
 * Opera sobre la tabla TblTransaccionPago.
 */
public class TransaccionPagoDao implements DaoGenerico<TransaccionPago> {

    private static final String COLUMNAS =
            "t.Tran_IdTransaccion, t.Tran_FechaPago, t.Tran_Valor, t.Tran_EstadoTransaccion, "
            + "t.TblPed_PedIdPedido, t.TblPas_PasIdPasarela, pa.Pas_NombreProveedor, "
            + "CONCAT(c.Cli_Nombre, ' ', c.Cli_Apellido) AS NombreCliente";

    private static final String SQL_UNIONES =
            " FROM TblTransaccionPago t "
            + "INNER JOIN TblPasarelaPago pa ON pa.Pas_IdPasarela = t.TblPas_PasIdPasarela "
            + "INNER JOIN TblPedido pe ON pe.Ped_IdPedido = t.TblPed_PedIdPedido "
            + "INNER JOIN TblCliente c ON c.Clie_IdCliente = pe.TblCli_ClieIdCliente ";

    private static final String SQL_LISTAR =
            "SELECT " + COLUMNAS + SQL_UNIONES + "ORDER BY t.Tran_FechaPago DESC";

    private static final String SQL_BUSCAR =
            "SELECT " + COLUMNAS + SQL_UNIONES + "WHERE t.Tran_IdTransaccion = ?";

    private static final String SQL_LISTAR_POR_PEDIDO =
            "SELECT " + COLUMNAS + SQL_UNIONES + "WHERE t.TblPed_PedIdPedido = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblTransaccionPago (Tran_FechaPago, Tran_Valor, Tran_EstadoTransaccion, "
            + "TblPed_PedIdPedido, TblPas_PasIdPasarela) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblTransaccionPago SET Tran_FechaPago = ?, Tran_Valor = ?, "
            + "Tran_EstadoTransaccion = ?, TblPed_PedIdPedido = ?, TblPas_PasIdPasarela = ? "
            + "WHERE Tran_IdTransaccion = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM TblTransaccionPago WHERE Tran_IdTransaccion = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblTransaccionPago";

    private static final String SQL_EXISTE_PAGO_APROBADO =
            "SELECT COUNT(*) FROM TblTransaccionPago "
            + "WHERE TblPed_PedIdPedido = ? AND UPPER(Tran_EstadoTransaccion) = 'APROBADA'";

    @Override
    public List<TransaccionPago> listar() {
        return ejecutarConsultaLista(SQL_LISTAR, null);
    }

    /**
     * Consulta los pagos registrados para un pedido.
     *
     * @param idPedido identificador del pedido
     * @return transacciones asociadas al pedido
     */
    public List<TransaccionPago> listarPorPedido(int idPedido) {
        return ejecutarConsultaLista(SQL_LISTAR_POR_PEDIDO, idPedido);
    }

    @Override
    public TransaccionPago buscarPorId(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, identificador);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar la transaccion solicitada.", error);
        }
    }

    /**
     * Verifica si un pedido ya cuenta con un pago aprobado.
     *
     * Evita registrar dos veces el cobro del mismo pedido.
     *
     * @param idPedido identificador del pedido
     * @return true cuando existe una transaccion en estado APROBADA
     */
    public boolean existePagoAprobado(int idPedido) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_EXISTE_PAGO_APROBADO)) {

            sentencia.setInt(1, idPedido);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() && resultado.getInt(1) > 0;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible verificar el pago del pedido.", error);
        }
    }

    @Override
    public void insertar(TransaccionPago transaccion) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            asignarParametros(sentencia, transaccion);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar la transaccion de pago.", error);
        }
    }

    @Override
    public void actualizar(TransaccionPago transaccion) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(sentencia, transaccion);
            sentencia.setInt(6, transaccion.getIdTransaccion());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar la transaccion de pago.", error);
        }
    }

    @Override
    public void eliminar(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {

            sentencia.setInt(1, identificador);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible eliminar la transaccion de pago.", error);
        }
    }

    @Override
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar las transacciones de pago.", error);
        }
    }

    private List<TransaccionPago> ejecutarConsultaLista(String sentenciaSql, Integer filtro) {
        List<TransaccionPago> transacciones = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sentenciaSql)) {

            if (filtro != null) {
                sentencia.setInt(1, filtro);
            }

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    transacciones.add(convertirFila(resultado));
                }
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar las transacciones de pago.", error);
        }
        return transacciones;
    }

    private void asignarParametros(PreparedStatement sentencia, TransaccionPago transaccion)
            throws SQLException {
        sentencia.setTimestamp(1, Timestamp.valueOf(transaccion.getFechaPago()));
        sentencia.setBigDecimal(2, transaccion.getValor());
        sentencia.setString(3, transaccion.getEstadoTransaccion());
        sentencia.setInt(4, transaccion.getIdPedido());
        sentencia.setInt(5, transaccion.getIdPasarela());
    }

    private TransaccionPago convertirFila(ResultSet resultado) throws SQLException {
        TransaccionPago transaccion = new TransaccionPago();
        transaccion.setIdTransaccion(resultado.getInt("Tran_IdTransaccion"));

        Timestamp fechaPago = resultado.getTimestamp("Tran_FechaPago");
        if (fechaPago != null) {
            transaccion.setFechaPago(fechaPago.toLocalDateTime());
        }

        transaccion.setValor(resultado.getBigDecimal("Tran_Valor"));
        transaccion.setEstadoTransaccion(resultado.getString("Tran_EstadoTransaccion"));
        transaccion.setIdPedido(resultado.getInt("TblPed_PedIdPedido"));
        transaccion.setIdPasarela(resultado.getInt("TblPas_PasIdPasarela"));
        transaccion.setNombreProveedor(resultado.getString("Pas_NombreProveedor"));
        transaccion.setNombreCliente(resultado.getString("NombreCliente"));
        return transaccion;
    }
}
