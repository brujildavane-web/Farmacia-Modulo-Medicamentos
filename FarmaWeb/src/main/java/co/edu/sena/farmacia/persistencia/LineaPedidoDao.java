package co.edu.sena.farmacia.persistencia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.LineaPedido;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Detalle de Pedidos.
 * Opera sobre la tabla TblLineaPedido.
 */
public class LineaPedidoDao implements DaoGenerico<LineaPedido> {

    private static final String COLUMNAS =
            "li.Lin_IdLinea, li.Lin_Cantidad, li.Lin_Precio, li.Lin_Subtotal, "
            + "li.TblPed_PedIdPedido, li.TblPro_ProIdProducto, p.Pro_Nombre";

    private static final String SQL_LISTAR =
            "SELECT " + COLUMNAS + " FROM TblLineaPedido li "
            + "INNER JOIN TblProducto p ON p.Pro_IdProducto = li.TblPro_ProIdProducto "
            + "ORDER BY li.TblPed_PedIdPedido DESC, li.Lin_IdLinea";

    private static final String SQL_BUSCAR =
            "SELECT " + COLUMNAS + " FROM TblLineaPedido li "
            + "INNER JOIN TblProducto p ON p.Pro_IdProducto = li.TblPro_ProIdProducto "
            + "WHERE li.Lin_IdLinea = ?";

    private static final String SQL_LISTAR_POR_PEDIDO =
            "SELECT " + COLUMNAS + " FROM TblLineaPedido li "
            + "INNER JOIN TblProducto p ON p.Pro_IdProducto = li.TblPro_ProIdProducto "
            + "WHERE li.TblPed_PedIdPedido = ? ORDER BY li.Lin_IdLinea";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblLineaPedido (Lin_Cantidad, Lin_Precio, Lin_Subtotal, "
            + "TblPed_PedIdPedido, TblPro_ProIdProducto) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblLineaPedido SET Lin_Cantidad = ?, Lin_Precio = ?, Lin_Subtotal = ?, "
            + "TblPed_PedIdPedido = ?, TblPro_ProIdProducto = ? WHERE Lin_IdLinea = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM TblLineaPedido WHERE Lin_IdLinea = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblLineaPedido";

    private static final String SQL_SUMAR_POR_PEDIDO =
            "SELECT COALESCE(SUM(Lin_Subtotal), 0) FROM TblLineaPedido WHERE TblPed_PedIdPedido = ?";

    @Override
    public List<LineaPedido> listar() {
        return ejecutarConsultaLista(SQL_LISTAR, null);
    }

    /**
     * Consulta el detalle de un pedido especifico.
     *
     * @param idPedido identificador del pedido
     * @return lineas que componen el pedido
     */
    public List<LineaPedido> listarPorPedido(int idPedido) {
        return ejecutarConsultaLista(SQL_LISTAR_POR_PEDIDO, idPedido);
    }

    @Override
    public LineaPedido buscarPorId(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, identificador);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar la linea solicitada.", error);
        }
    }

    @Override
    public void insertar(LineaPedido linea) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            asignarParametros(sentencia, linea);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar la linea del pedido.", error);
        }
    }

    @Override
    public void actualizar(LineaPedido linea) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(sentencia, linea);
            sentencia.setInt(6, linea.getIdLinea());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar la linea del pedido.", error);
        }
    }

    @Override
    public void eliminar(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {

            sentencia.setInt(1, identificador);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible eliminar la linea del pedido.", error);
        }
    }

    @Override
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar las lineas de pedido.", error);
        }
    }

    /**
     * Suma los subtotales de todas las lineas de un pedido.
     *
     * Es la base con la que la capa de logica recalcula el total del pedido.
     *
     * @param idPedido identificador del pedido
     * @return suma de los subtotales
     */
    public BigDecimal sumarSubtotalesPorPedido(int idPedido) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_SUMAR_POR_PEDIDO)) {

            sentencia.setInt(1, idPedido);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? resultado.getBigDecimal(1) : BigDecimal.ZERO;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible calcular el total del pedido.", error);
        }
    }

    private List<LineaPedido> ejecutarConsultaLista(String sentenciaSql, Integer filtro) {
        List<LineaPedido> lineas = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sentenciaSql)) {

            if (filtro != null) {
                sentencia.setInt(1, filtro);
            }

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    lineas.add(convertirFila(resultado));
                }
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el detalle de los pedidos.", error);
        }
        return lineas;
    }

    private void asignarParametros(PreparedStatement sentencia, LineaPedido linea) throws SQLException {
        sentencia.setDouble(1, linea.getCantidad());
        sentencia.setBigDecimal(2, linea.getPrecio());
        sentencia.setBigDecimal(3, linea.getSubtotal());
        sentencia.setInt(4, linea.getIdPedido());
        sentencia.setInt(5, linea.getIdProducto());
    }

    private LineaPedido convertirFila(ResultSet resultado) throws SQLException {
        LineaPedido linea = new LineaPedido();
        linea.setIdLinea(resultado.getInt("Lin_IdLinea"));
        linea.setCantidad(resultado.getDouble("Lin_Cantidad"));
        linea.setPrecio(resultado.getBigDecimal("Lin_Precio"));
        linea.setSubtotal(resultado.getBigDecimal("Lin_Subtotal"));
        linea.setIdPedido(resultado.getInt("TblPed_PedIdPedido"));
        linea.setIdProducto(resultado.getInt("TblPro_ProIdProducto"));
        linea.setNombreProducto(resultado.getString("Pro_Nombre"));
        return linea;
    }
}
