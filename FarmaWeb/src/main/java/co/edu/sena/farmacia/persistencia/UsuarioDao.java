package co.edu.sena.farmacia.persistencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.Usuario;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Usuarios. Opera sobre la tabla TblUsuario
 * y une con TblRol para conocer el perfil de acceso de cada credencial.
 */
public class UsuarioDao implements DaoGenerico<Usuario> {

    private static final String COLUMNAS =
            "u.Usu_IdUsuario, u.Usu_Email, u.Usu_Password, u.Usu_FechaRegistro, "
            + "u.TblRol_RolIdRol, r.Rol_NombreRol";

    private static final String SQL_LISTAR =
            "SELECT " + COLUMNAS + " FROM TblUsuario u "
            + "INNER JOIN TblRol r ON r.Rol_IdRol = u.TblRol_RolIdRol "
            + "ORDER BY u.Usu_Email";

    private static final String SQL_BUSCAR =
            "SELECT " + COLUMNAS + " FROM TblUsuario u "
            + "INNER JOIN TblRol r ON r.Rol_IdRol = u.TblRol_RolIdRol "
            + "WHERE u.Usu_IdUsuario = ?";

    private static final String SQL_BUSCAR_POR_EMAIL =
            "SELECT " + COLUMNAS + " FROM TblUsuario u "
            + "INNER JOIN TblRol r ON r.Rol_IdRol = u.TblRol_RolIdRol "
            + "WHERE u.Usu_Email = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblUsuario (Usu_Email, Usu_Password, Usu_FechaRegistro, TblRol_RolIdRol) "
            + "VALUES (?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblUsuario SET Usu_Email = ?, Usu_FechaRegistro = ?, TblRol_RolIdRol = ? "
            + "WHERE Usu_IdUsuario = ?";

    private static final String SQL_ACTUALIZAR_PASSWORD =
            "UPDATE TblUsuario SET Usu_Password = ? WHERE Usu_IdUsuario = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM TblUsuario WHERE Usu_IdUsuario = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblUsuario";

    @Override
    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                usuarios.add(convertirFila(resultado));
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar los usuarios.", error);
        }
        return usuarios;
    }

    @Override
    public Usuario buscarPorId(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, identificador);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el usuario solicitado.", error);
        }
    }

    /**
     * Busca la credencial que corresponde a un correo electronico.
     *
     * Se usa tanto para autenticar el ingreso como para verificar que el
     * correo no este repetido antes de registrar un usuario nuevo.
     *
     * @param email correo electronico del usuario
     * @return el usuario encontrado, o null si el correo no esta registrado
     */
    public Usuario buscarPorEmail(String email) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_POR_EMAIL)) {

            sentencia.setString(1, email);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible verificar el correo del usuario.", error);
        }
    }

    @Override
    public void insertar(Usuario usuario) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            sentencia.setString(1, usuario.getEmail());
            sentencia.setString(2, usuario.getPassword());
            sentencia.setDate(3, Date.valueOf(usuario.getFechaRegistro()));
            sentencia.setInt(4, usuario.getIdRol());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar el usuario.", error);
        }
    }

    @Override
    public void actualizar(Usuario usuario) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            sentencia.setString(1, usuario.getEmail());
            sentencia.setDate(2, Date.valueOf(usuario.getFechaRegistro()));
            sentencia.setInt(3, usuario.getIdRol());
            sentencia.setInt(4, usuario.getIdUsuario());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar el usuario.", error);
        }
    }

    /**
     * Cambia unicamente la contrasena cifrada del usuario.
     *
     * @param idUsuario identificador del usuario
     * @param passwordCifrado hash de la contrasena nueva
     */
    public void actualizarPassword(int idUsuario, String passwordCifrado) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR_PASSWORD)) {

            sentencia.setString(1, passwordCifrado);
            sentencia.setInt(2, idUsuario);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar la contrasena.", error);
        }
    }

    @Override
    public void eliminar(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {

            sentencia.setInt(1, identificador);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible eliminar el usuario.", error);
        }
    }

    @Override
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar los usuarios.", error);
        }
    }

    private Usuario convertirFila(ResultSet resultado) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(resultado.getInt("Usu_IdUsuario"));
        usuario.setEmail(resultado.getString("Usu_Email"));
        usuario.setPassword(resultado.getString("Usu_Password"));

        Date fechaRegistro = resultado.getDate("Usu_FechaRegistro");
        if (fechaRegistro != null) {
            usuario.setFechaRegistro(fechaRegistro.toLocalDate());
        }

        usuario.setIdRol(resultado.getInt("TblRol_RolIdRol"));
        usuario.setNombreRol(resultado.getString("Rol_NombreRol"));
        return usuario;
    }
}
