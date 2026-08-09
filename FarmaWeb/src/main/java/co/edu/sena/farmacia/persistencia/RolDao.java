package co.edu.sena.farmacia.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.Rol;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Roles. Opera sobre la tabla TblRol.
 */
public class RolDao implements DaoGenerico<Rol> {

    private static final String SQL_LISTAR =
            "SELECT Rol_IdRol, Rol_NombreRol, Rol_Permisos FROM TblRol ORDER BY Rol_NombreRol";

    private static final String SQL_BUSCAR =
            "SELECT Rol_IdRol, Rol_NombreRol, Rol_Permisos FROM TblRol WHERE Rol_IdRol = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblRol (Rol_NombreRol, Rol_Permisos) VALUES (?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblRol SET Rol_NombreRol = ?, Rol_Permisos = ? WHERE Rol_IdRol = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM TblRol WHERE Rol_IdRol = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblRol";

    @Override
    public List<Rol> listar() {
        List<Rol> roles = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                roles.add(convertirFila(resultado));
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar los roles.", error);
        }
        return roles;
    }

    @Override
    public Rol buscarPorId(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, identificador);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el rol solicitado.", error);
        }
    }

    @Override
    public void insertar(Rol rol) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            sentencia.setString(1, rol.getNombreRol());
            sentencia.setString(2, rol.getPermisos());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar el rol.", error);
        }
    }

    @Override
    public void actualizar(Rol rol) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            sentencia.setString(1, rol.getNombreRol());
            sentencia.setString(2, rol.getPermisos());
            sentencia.setInt(3, rol.getIdRol());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar el rol.", error);
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
                    "No fue posible eliminar el rol: puede tener usuarios asociados.", error);
        }
    }

    @Override
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar los roles.", error);
        }
    }

    /**
     * Traduce una fila del ResultSet a un objeto del modelo.
     */
    private Rol convertirFila(ResultSet resultado) throws SQLException {
        Rol rol = new Rol();
        rol.setIdRol(resultado.getInt("Rol_IdRol"));
        rol.setNombreRol(resultado.getString("Rol_NombreRol"));
        rol.setPermisos(resultado.getString("Rol_Permisos"));
        return rol;
    }
}
