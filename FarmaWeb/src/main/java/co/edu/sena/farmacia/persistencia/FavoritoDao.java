package co.edu.sena.farmacia.persistencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.Favorito;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Favoritos. Opera sobre la tabla
 * TblFavorito.
 *
 * No implementa DaoGenerico porque su llave primaria es compuesta
 * (cliente + producto) y no un identificador autonumerico simple.
 */
public class FavoritoDao {

    private static final String COLUMNAS =
            "f.TblCli_ClieIdCliente, f.TblPro_ProIdProducto, f.Fav_FechaMarcacion, "
            + "CONCAT(c.Cli_Nombre, ' ', c.Cli_Apellido) AS NombreCliente, p.Pro_Nombre";

    private static final String SQL_LISTAR =
            "SELECT " + COLUMNAS + " FROM TblFavorito f "
            + "INNER JOIN TblCliente c ON c.Clie_IdCliente = f.TblCli_ClieIdCliente "
            + "INNER JOIN TblProducto p ON p.Pro_IdProducto = f.TblPro_ProIdProducto "
            + "ORDER BY NombreCliente, p.Pro_Nombre";

    private static final String SQL_BUSCAR =
            "SELECT " + COLUMNAS + " FROM TblFavorito f "
            + "INNER JOIN TblCliente c ON c.Clie_IdCliente = f.TblCli_ClieIdCliente "
            + "INNER JOIN TblProducto p ON p.Pro_IdProducto = f.TblPro_ProIdProducto "
            + "WHERE f.TblCli_ClieIdCliente = ? AND f.TblPro_ProIdProducto = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblFavorito (TblCli_ClieIdCliente, TblPro_ProIdProducto, Fav_FechaMarcacion) "
            + "VALUES (?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblFavorito SET Fav_FechaMarcacion = ? "
            + "WHERE TblCli_ClieIdCliente = ? AND TblPro_ProIdProducto = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM TblFavorito WHERE TblCli_ClieIdCliente = ? AND TblPro_ProIdProducto = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblFavorito";

    /**
     * Consulta todos los favoritos registrados en el sistema.
     *
     * @return lista de marcaciones cliente-producto
     */
    public List<Favorito> listar() {
        List<Favorito> favoritos = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                favoritos.add(convertirFila(resultado));
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar los favoritos.", error);
        }
        return favoritos;
    }

    /**
     * Busca una marcacion por su llave primaria compuesta.
     *
     * @param idCliente identificador del cliente
     * @param idProducto identificador del producto
     * @return la marcacion encontrada, o null si no existe
     */
    public Favorito buscarPorLlave(int idCliente, int idProducto) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, idCliente);
            sentencia.setInt(2, idProducto);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el favorito solicitado.", error);
        }
    }

    /**
     * Registra un producto como favorito de un cliente.
     *
     * @param favorito marcacion a guardar
     */
    public void insertar(Favorito favorito) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            sentencia.setInt(1, favorito.getIdCliente());
            sentencia.setInt(2, favorito.getIdProducto());
            sentencia.setDate(3, Date.valueOf(favorito.getFechaMarcacion()));
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar el favorito.", error);
        }
    }

    /**
     * Cambia la fecha de marcacion de un favorito existente.
     *
     * @param favorito marcacion con la fecha nueva
     */
    public void actualizar(Favorito favorito) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            sentencia.setDate(1, Date.valueOf(favorito.getFechaMarcacion()));
            sentencia.setInt(2, favorito.getIdCliente());
            sentencia.setInt(3, favorito.getIdProducto());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar el favorito.", error);
        }
    }

    /**
     * Quita un producto de la lista de favoritos de un cliente.
     *
     * @param idCliente identificador del cliente
     * @param idProducto identificador del producto
     */
    public void eliminar(int idCliente, int idProducto) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {

            sentencia.setInt(1, idCliente);
            sentencia.setInt(2, idProducto);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible eliminar el favorito.", error);
        }
    }

    /**
     * Cuenta las marcaciones de favoritos registradas.
     *
     * @return cantidad total de favoritos
     */
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar los favoritos.", error);
        }
    }

    private Favorito convertirFila(ResultSet resultado) throws SQLException {
        Favorito favorito = new Favorito();
        favorito.setIdCliente(resultado.getInt("TblCli_ClieIdCliente"));
        favorito.setIdProducto(resultado.getInt("TblPro_ProIdProducto"));

        Date fechaMarcacion = resultado.getDate("Fav_FechaMarcacion");
        if (fechaMarcacion != null) {
            favorito.setFechaMarcacion(fechaMarcacion.toLocalDate());
        }

        favorito.setNombreCliente(resultado.getString("NombreCliente"));
        favorito.setNombreProducto(resultado.getString("Pro_Nombre"));
        return favorito;
    }
}
