package co.edu.sena.farmacia.persistencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.Historial;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Historial de Pedidos.
 * Opera sobre la tabla TblHistorial, que funciona como bitacora de estados.
 */
public class HistorialDao implements DaoGenerico<Historial> {

    private static final String COLUMNAS =
            "h.His_IdPedidoHistorico, h.TblPed_PedIdPedido, h.His_Fecha, "
            + "h.TblPro_ProIdProducto, h.His_Estado, p.Pro_Nombre, "
            + "CONCAT(c.Cli_Nombre, ' ', c.Cli_Apellido) AS NombreCliente";

    private static final String SQL_UNIONES =
            " FROM TblHistorial h "
            + "INNER JOIN TblProducto p ON p.Pro_IdProducto = h.TblPro_ProIdProducto "
            + "INNER JOIN TblPedido pe ON pe.Ped_IdPedido = h.TblPed_PedIdPedido "
            + "INNER JOIN TblCliente c ON c.Clie_IdCliente = pe.TblCli_ClieIdCliente ";

    private static final String SQL_LISTAR =
            "SELECT " + COLUMNAS + SQL_UNIONES + "ORDER BY h.His_Fecha DESC, h.His_IdPedidoHistorico DESC";

    private static final String SQL_BUSCAR =
            "SELECT " + COLUMNAS + SQL_UNIONES + "WHERE h.His_IdPedidoHistorico = ?";

    private static final String SQL_LISTAR_POR_PEDIDO =
            "SELECT " + COLUMNAS + SQL_UNIONES + "WHERE h.TblPed_PedIdPedido = ? ORDER BY h.His_Fecha";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblHistorial (TblPed_PedIdPedido, His_Fecha, TblPro_ProIdProducto, His_Estado) "
            + "VALUES (?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblHistorial SET TblPed_PedIdPedido = ?, His_Fecha = ?, "
            + "TblPro_ProIdProducto = ?, His_Estado = ? WHERE His_IdPedidoHistorico = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM TblHistorial WHERE His_IdPedidoHistorico = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblHistorial";

    @Override
    public List<Historial> listar() {
        return ejecutarConsultaLista(SQL_LISTAR, null);
    }

    /**
     * Consulta la traza completa de un pedido para su rastreo.
     *
     * @param idPedido identificador del pedido
     * @return registros de historial en orden cronologico
     */
    public List<Historial> listarPorPedido(int idPedido) {
        return ejecutarConsultaLista(SQL_LISTAR_POR_PEDIDO, idPedido);
    }

    @Override
    public Historial buscarPorId(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, identificador);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el registro de historial.", error);
        }
    }

    @Override
    public void insertar(Historial historial) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            asignarParametros(sentencia, historial);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar el historial.", error);
        }
    }

    @Override
    public void actualizar(Historial historial) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(sentencia, historial);
            sentencia.setInt(5, historial.getIdPedidoHistorico());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar el historial.", error);
        }
    }

    @Override
    public void eliminar(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {

            sentencia.setInt(1, identificador);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible eliminar el registro de historial.", error);
        }
    }

    @Override
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar los registros de historial.", error);
        }
    }

    private List<Historial> ejecutarConsultaLista(String sentenciaSql, Integer filtro) {
        List<Historial> registros = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sentenciaSql)) {

            if (filtro != null) {
                sentencia.setInt(1, filtro);
            }

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    registros.add(convertirFila(resultado));
                }
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el historial de pedidos.", error);
        }
        return registros;
    }

    private void asignarParametros(PreparedStatement sentencia, Historial historial) throws SQLException {
        sentencia.setInt(1, historial.getIdPedido());
        sentencia.setDate(2, Date.valueOf(historial.getFecha()));
        sentencia.setInt(3, historial.getIdProducto());
        sentencia.setString(4, historial.getEstado());
    }

    private Historial convertirFila(ResultSet resultado) throws SQLException {
        Historial historial = new Historial();
        historial.setIdPedidoHistorico(resultado.getInt("His_IdPedidoHistorico"));
        historial.setIdPedido(resultado.getInt("TblPed_PedIdPedido"));

        Date fecha = resultado.getDate("His_Fecha");
        if (fecha != null) {
            historial.setFecha(fecha.toLocalDate());
        }

        historial.setIdProducto(resultado.getInt("TblPro_ProIdProducto"));
        historial.setEstado(resultado.getString("His_Estado"));
        historial.setNombreProducto(resultado.getString("Pro_Nombre"));
        historial.setNombreCliente(resultado.getString("NombreCliente"));
        return historial;
    }
}
