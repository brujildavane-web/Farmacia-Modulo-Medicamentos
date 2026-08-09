package co.edu.sena.farmacia.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.Producto;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Productos. Opera sobre la tabla
 * TblProducto y agrega el stock disponible sumando sus lotes vigentes.
 */
public class ProductoDao implements DaoGenerico<Producto> {

    /**
     * Suma el stock de los lotes que aun no estan vencidos. Se usa LEFT JOIN
     * para que los productos sin lotes tambien aparezcan en el catalogo.
     */
    private static final String SQL_LISTAR =
            "SELECT p.Pro_IdProducto, p.Pro_Nombre, p.Pro_SkuCode, p.Pro_Descripcion, "
            + "p.Pro_Precio, p.Pro_RequiereReceta, "
            + "COALESCE(SUM(CASE WHEN l.LotP_FechaVencimiento >= CURDATE() "
            + "THEN l.LotP_StockActual ELSE 0 END), 0) AS StockTotal "
            + "FROM TblProducto p "
            + "LEFT JOIN TblLoteProducto l ON l.TblPro_ProIdProducto = p.Pro_IdProducto "
            + "GROUP BY p.Pro_IdProducto, p.Pro_Nombre, p.Pro_SkuCode, p.Pro_Descripcion, "
            + "p.Pro_Precio, p.Pro_RequiereReceta "
            + "ORDER BY p.Pro_Nombre";

    /** Subconsulta que calcula el stock vigente de un solo producto. */
    private static final String SUBCONSULTA_STOCK =
            "(SELECT COALESCE(SUM(l.LotP_StockActual), 0) FROM TblLoteProducto l "
            + "WHERE l.TblPro_ProIdProducto = p.Pro_IdProducto "
            + "AND l.LotP_FechaVencimiento >= CURDATE()) AS StockTotal";

    private static final String SQL_BUSCAR =
            "SELECT p.Pro_IdProducto, p.Pro_Nombre, p.Pro_SkuCode, p.Pro_Descripcion, p.Pro_Precio, "
            + "p.Pro_RequiereReceta, " + SUBCONSULTA_STOCK + " FROM TblProducto p "
            + "WHERE p.Pro_IdProducto = ?";

    private static final String SQL_BUSCAR_POR_SKU =
            "SELECT p.Pro_IdProducto, p.Pro_Nombre, p.Pro_SkuCode, p.Pro_Descripcion, p.Pro_Precio, "
            + "p.Pro_RequiereReceta, " + SUBCONSULTA_STOCK + " FROM TblProducto p "
            + "WHERE p.Pro_SkuCode = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblProducto (Pro_Nombre, Pro_SkuCode, Pro_Descripcion, Pro_Precio, "
            + "Pro_RequiereReceta) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblProducto SET Pro_Nombre = ?, Pro_SkuCode = ?, Pro_Descripcion = ?, "
            + "Pro_Precio = ?, Pro_RequiereReceta = ? WHERE Pro_IdProducto = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM TblProducto WHERE Pro_IdProducto = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblProducto";

    private static final String SQL_STOCK_DISPONIBLE =
            "SELECT COALESCE(SUM(LotP_StockActual), 0) FROM TblLoteProducto "
            + "WHERE TblPro_ProIdProducto = ? AND LotP_FechaVencimiento >= CURDATE()";

    @Override
    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                productos.add(convertirFila(resultado));
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el catalogo de productos.", error);
        }
        return productos;
    }

    @Override
    public Producto buscarPorId(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, identificador);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el producto solicitado.", error);
        }
    }

    /**
     * Busca un producto por su codigo SKU o de barras.
     *
     * Permite validar que el codigo no se repita y da soporte al escaneo
     * de codigos de barras del modulo de inventario.
     *
     * @param skuCode codigo unico del producto
     * @return el producto encontrado, o null si el codigo no existe
     */
    public Producto buscarPorSku(String skuCode) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_POR_SKU)) {

            sentencia.setString(1, skuCode);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible verificar el codigo SKU.", error);
        }
    }

    /**
     * Consulta el stock vigente de un producto sumando sus lotes no vencidos.
     *
     * @param idProducto identificador del producto
     * @return unidades disponibles para la venta
     */
    public double consultarStockDisponible(int idProducto) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_STOCK_DISPONIBLE)) {

            sentencia.setInt(1, idProducto);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? resultado.getDouble(1) : 0;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el stock del producto.", error);
        }
    }

    @Override
    public void insertar(Producto producto) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            asignarParametros(sentencia, producto);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar el producto.", error);
        }
    }

    @Override
    public void actualizar(Producto producto) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(sentencia, producto);
            sentencia.setInt(6, producto.getIdProducto());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar el producto.", error);
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
                    "No fue posible eliminar el producto: tiene lotes o pedidos asociados.", error);
        }
    }

    @Override
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar los productos.", error);
        }
    }

    private void asignarParametros(PreparedStatement sentencia, Producto producto) throws SQLException {
        sentencia.setString(1, producto.getNombre());
        sentencia.setString(2, producto.getSkuCode());
        sentencia.setString(3, producto.getDescripcion());
        sentencia.setBigDecimal(4, producto.getPrecio());
        sentencia.setBoolean(5, producto.isRequiereReceta());
    }

    private Producto convertirFila(ResultSet resultado) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(resultado.getInt("Pro_IdProducto"));
        producto.setNombre(resultado.getString("Pro_Nombre"));
        producto.setSkuCode(resultado.getString("Pro_SkuCode"));
        producto.setDescripcion(resultado.getString("Pro_Descripcion"));
        producto.setPrecio(resultado.getBigDecimal("Pro_Precio"));
        producto.setRequiereReceta(resultado.getBoolean("Pro_RequiereReceta"));
        producto.setStockTotal(resultado.getDouble("StockTotal"));
        return producto;
    }
}
