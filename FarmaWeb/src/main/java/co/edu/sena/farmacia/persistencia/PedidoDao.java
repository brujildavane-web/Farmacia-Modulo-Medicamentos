package co.edu.sena.farmacia.persistencia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.Pedido;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Pedidos. Opera sobre la tabla TblPedido
 * y une con clientes y farmaceuticos para identificar a los responsables.
 */
public class PedidoDao implements DaoGenerico<Pedido> {

    private static final String COLUMNAS =
            "pe.Ped_IdPedido, pe.Ped_Fecha, pe.Ped_Total, pe.Ped_Estado, "
            + "pe.TblCli_ClieIdCliente, pe.TblFar_FarIdFarmaceutico, "
            + "CONCAT(c.Cli_Nombre, ' ', c.Cli_Apellido) AS NombreCliente, "
            + "CONCAT(f.Far_Nombre, ' ', f.Far_Apellido) AS NombreFarmaceutico";

    private static final String SQL_UNIONES =
            " FROM TblPedido pe "
            + "INNER JOIN TblCliente c ON c.Clie_IdCliente = pe.TblCli_ClieIdCliente "
            + "INNER JOIN TblFarmaceutico f ON f.Far_IdFarmaceutico = pe.TblFar_FarIdFarmaceutico ";

    private static final String SQL_LISTAR =
            "SELECT " + COLUMNAS + SQL_UNIONES + "ORDER BY pe.Ped_Fecha DESC";

    private static final String SQL_BUSCAR =
            "SELECT " + COLUMNAS + SQL_UNIONES + "WHERE pe.Ped_IdPedido = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblPedido (Ped_Fecha, Ped_Total, Ped_Estado, TblCli_ClieIdCliente, "
            + "TblFar_FarIdFarmaceutico) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblPedido SET Ped_Fecha = ?, Ped_Total = ?, Ped_Estado = ?, "
            + "TblCli_ClieIdCliente = ?, TblFar_FarIdFarmaceutico = ? WHERE Ped_IdPedido = ?";

    private static final String SQL_ACTUALIZAR_TOTAL =
            "UPDATE TblPedido SET Ped_Total = ? WHERE Ped_IdPedido = ?";

    private static final String SQL_ACTUALIZAR_ESTADO =
            "UPDATE TblPedido SET Ped_Estado = ? WHERE Ped_IdPedido = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM TblPedido WHERE Ped_IdPedido = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblPedido";

    private static final String SQL_SUMAR_VENTAS =
            "SELECT COALESCE(SUM(Ped_Total), 0) FROM TblPedido WHERE Ped_Estado <> 'ANULADO'";

    @Override
    public List<Pedido> listar() {
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                pedidos.add(convertirFila(resultado));
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar los pedidos.", error);
        }
        return pedidos;
    }

    @Override
    public Pedido buscarPorId(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, identificador);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el pedido solicitado.", error);
        }
    }

    @Override
    public void insertar(Pedido pedido) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            asignarParametros(sentencia, pedido);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar el pedido.", error);
        }
    }

    @Override
    public void actualizar(Pedido pedido) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(sentencia, pedido);
            sentencia.setInt(6, pedido.getIdPedido());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar el pedido.", error);
        }
    }

    /**
     * Guarda el total recalculado a partir de las lineas del pedido.
     *
     * @param idPedido identificador del pedido
     * @param total valor nuevo del pedido
     */
    public void actualizarTotal(int idPedido, BigDecimal total) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR_TOTAL)) {

            sentencia.setBigDecimal(1, total);
            sentencia.setInt(2, idPedido);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar el total del pedido.", error);
        }
    }

    /**
     * Cambia el estado del pedido durante su seguimiento.
     *
     * @param idPedido identificador del pedido
     * @param estado estado nuevo del pedido
     */
    public void actualizarEstado(int idPedido, String estado) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR_ESTADO)) {

            sentencia.setString(1, estado);
            sentencia.setInt(2, idPedido);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar el estado del pedido.", error);
        }
    }

    @Override
    public void eliminar(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {

            sentencia.setInt(1, identificador);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia(
                    "No fue posible eliminar el pedido: primero elimine sus lineas, formulas o pagos.",
                    error);
        }
    }

    @Override
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar los pedidos.", error);
        }
    }

    /**
     * Suma el valor de todos los pedidos no anulados.
     *
     * Alimenta el reporte de ventas del tablero administrativo.
     *
     * @return total vendido por la farmacia
     */
    public BigDecimal sumarVentas() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_SUMAR_VENTAS);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getBigDecimal(1) : BigDecimal.ZERO;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible calcular el total de ventas.", error);
        }
    }

    private void asignarParametros(PreparedStatement sentencia, Pedido pedido) throws SQLException {
        sentencia.setTimestamp(1, Timestamp.valueOf(pedido.getFecha()));
        sentencia.setBigDecimal(2, pedido.getTotal());
        sentencia.setString(3, pedido.getEstado());
        sentencia.setInt(4, pedido.getIdCliente());
        sentencia.setInt(5, pedido.getIdFarmaceutico());
    }

    private Pedido convertirFila(ResultSet resultado) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(resultado.getInt("Ped_IdPedido"));

        Timestamp fecha = resultado.getTimestamp("Ped_Fecha");
        if (fecha != null) {
            pedido.setFecha(fecha.toLocalDateTime());
        }

        pedido.setTotal(resultado.getBigDecimal("Ped_Total"));
        pedido.setEstado(resultado.getString("Ped_Estado"));
        pedido.setIdCliente(resultado.getInt("TblCli_ClieIdCliente"));
        pedido.setIdFarmaceutico(resultado.getInt("TblFar_FarIdFarmaceutico"));
        pedido.setNombreCliente(resultado.getString("NombreCliente"));
        pedido.setNombreFarmaceutico(resultado.getString("NombreFarmaceutico"));
        return pedido;
    }
}
