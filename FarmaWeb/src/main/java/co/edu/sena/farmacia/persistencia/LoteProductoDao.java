package co.edu.sena.farmacia.persistencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.LoteProducto;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Inventario por lotes.
 * Opera sobre la tabla TblLoteProducto.
 */
public class LoteProductoDao implements DaoGenerico<LoteProducto> {

    private static final String COLUMNAS =
            "l.LotP_IdLote, l.LotP_FechaVencimiento, l.LotP_RegistroSanitario, l.LotP_StockActual, "
            + "l.LotP_Marca, l.TblPro_ProIdProducto, p.Pro_Nombre";

    private static final String SQL_LISTAR =
            "SELECT " + COLUMNAS + " FROM TblLoteProducto l "
            + "INNER JOIN TblProducto p ON p.Pro_IdProducto = l.TblPro_ProIdProducto "
            + "ORDER BY l.LotP_FechaVencimiento";

    private static final String SQL_BUSCAR =
            "SELECT " + COLUMNAS + " FROM TblLoteProducto l "
            + "INNER JOIN TblProducto p ON p.Pro_IdProducto = l.TblPro_ProIdProducto "
            + "WHERE l.LotP_IdLote = ?";

    private static final String SQL_LISTAR_POR_PRODUCTO =
            "SELECT " + COLUMNAS + " FROM TblLoteProducto l "
            + "INNER JOIN TblProducto p ON p.Pro_IdProducto = l.TblPro_ProIdProducto "
            + "WHERE l.TblPro_ProIdProducto = ? ORDER BY l.LotP_FechaVencimiento";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblLoteProducto (LotP_FechaVencimiento, LotP_RegistroSanitario, "
            + "LotP_StockActual, LotP_Marca, TblPro_ProIdProducto) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblLoteProducto SET LotP_FechaVencimiento = ?, LotP_RegistroSanitario = ?, "
            + "LotP_StockActual = ?, LotP_Marca = ?, TblPro_ProIdProducto = ? WHERE LotP_IdLote = ?";

    private static final String SQL_DESCONTAR_STOCK =
            "UPDATE TblLoteProducto SET LotP_StockActual = LotP_StockActual - ? WHERE LotP_IdLote = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM TblLoteProducto WHERE LotP_IdLote = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblLoteProducto";

    private static final String SQL_CONTAR_POR_VENCER =
            "SELECT COUNT(*) FROM TblLoteProducto "
            + "WHERE LotP_FechaVencimiento <= DATE_ADD(CURDATE(), INTERVAL ? DAY)";

    @Override
    public List<LoteProducto> listar() {
        return ejecutarConsultaLista(SQL_LISTAR, null);
    }

    /**
     * Consulta los lotes registrados para un producto especifico.
     *
     * @param idProducto identificador del producto
     * @return lotes ordenados del mas proximo a vencer al mas lejano
     */
    public List<LoteProducto> listarPorProducto(int idProducto) {
        return ejecutarConsultaLista(SQL_LISTAR_POR_PRODUCTO, idProducto);
    }

    @Override
    public LoteProducto buscarPorId(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, identificador);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el lote solicitado.", error);
        }
    }

    @Override
    public void insertar(LoteProducto lote) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            asignarParametros(sentencia, lote);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar el lote.", error);
        }
    }

    @Override
    public void actualizar(LoteProducto lote) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(sentencia, lote);
            sentencia.setInt(6, lote.getIdLote());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar el lote.", error);
        }
    }

    /**
     * Resta unidades del stock de un lote cuando se despacha un pedido.
     *
     * @param idLote identificador del lote afectado
     * @param cantidad unidades que salen del inventario
     */
    public void descontarStock(int idLote, double cantidad) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_DESCONTAR_STOCK)) {

            sentencia.setDouble(1, cantidad);
            sentencia.setInt(2, idLote);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible descontar el stock del lote.", error);
        }
    }

    @Override
    public void eliminar(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {

            sentencia.setInt(1, identificador);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible eliminar el lote.", error);
        }
    }

    @Override
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar los lotes.", error);
        }
    }

    /**
     * Cuenta los lotes que caducan dentro de un plazo determinado.
     *
     * Da soporte a la alerta automatica por caducidad del tablero.
     *
     * @param dias plazo de anticipacion en dias
     * @return cantidad de lotes proximos a vencer o ya vencidos
     */
    public int contarProximosAVencer(int dias) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR_POR_VENCER)) {

            sentencia.setInt(1, dias);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? resultado.getInt(1) : 0;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible calcular las alertas de caducidad.", error);
        }
    }

    /**
     * Ejecuta una consulta que devuelve varios lotes.
     *
     * @param sentenciaSql consulta a ejecutar
     * @param filtro valor del parametro, o null si la consulta no lleva filtro
     */
    private List<LoteProducto> ejecutarConsultaLista(String sentenciaSql, Integer filtro) {
        List<LoteProducto> lotes = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sentenciaSql)) {

            if (filtro != null) {
                sentencia.setInt(1, filtro);
            }

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    lotes.add(convertirFila(resultado));
                }
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar los lotes del inventario.", error);
        }
        return lotes;
    }

    private void asignarParametros(PreparedStatement sentencia, LoteProducto lote) throws SQLException {
        sentencia.setDate(1, Date.valueOf(lote.getFechaVencimiento()));
        sentencia.setString(2, lote.getRegistroSanitario());
        sentencia.setDouble(3, lote.getStockActual());
        sentencia.setString(4, lote.getMarca());
        sentencia.setInt(5, lote.getIdProducto());
    }

    private LoteProducto convertirFila(ResultSet resultado) throws SQLException {
        LoteProducto lote = new LoteProducto();
        lote.setIdLote(resultado.getInt("LotP_IdLote"));

        Date fechaVencimiento = resultado.getDate("LotP_FechaVencimiento");
        if (fechaVencimiento != null) {
            lote.setFechaVencimiento(fechaVencimiento.toLocalDate());
        }

        lote.setRegistroSanitario(resultado.getString("LotP_RegistroSanitario"));
        lote.setStockActual(resultado.getDouble("LotP_StockActual"));
        lote.setMarca(resultado.getString("LotP_Marca"));
        lote.setIdProducto(resultado.getInt("TblPro_ProIdProducto"));
        lote.setNombreProducto(resultado.getString("Pro_Nombre"));
        return lote;
    }
}
