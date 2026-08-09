package co.edu.sena.farmacia.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.Farmaceutico;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Farmaceuticos.
 * Opera sobre la tabla TblFarmaceutico.
 */
public class FarmaceuticoDao implements DaoGenerico<Farmaceutico> {

    private static final String COLUMNAS =
            "f.Far_IdFarmaceutico, f.Farm_RegistroProfesional, f.Far_Especialidad, f.Far_Nombre, "
            + "f.Far_Apellido, f.Far_Telefono, f.TblUsu_UsuIdUsuario, u.Usu_Email";

    private static final String SQL_LISTAR =
            "SELECT " + COLUMNAS + " FROM TblFarmaceutico f "
            + "INNER JOIN TblUsuario u ON u.Usu_IdUsuario = f.TblUsu_UsuIdUsuario "
            + "ORDER BY f.Far_Apellido, f.Far_Nombre";

    private static final String SQL_BUSCAR =
            "SELECT " + COLUMNAS + " FROM TblFarmaceutico f "
            + "INNER JOIN TblUsuario u ON u.Usu_IdUsuario = f.TblUsu_UsuIdUsuario "
            + "WHERE f.Far_IdFarmaceutico = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblFarmaceutico (Farm_RegistroProfesional, Far_Especialidad, Far_Nombre, "
            + "Far_Apellido, Far_Telefono, TblUsu_UsuIdUsuario) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblFarmaceutico SET Farm_RegistroProfesional = ?, Far_Especialidad = ?, "
            + "Far_Nombre = ?, Far_Apellido = ?, Far_Telefono = ?, TblUsu_UsuIdUsuario = ? "
            + "WHERE Far_IdFarmaceutico = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM TblFarmaceutico WHERE Far_IdFarmaceutico = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblFarmaceutico";

    @Override
    public List<Farmaceutico> listar() {
        List<Farmaceutico> farmaceuticos = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                farmaceuticos.add(convertirFila(resultado));
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar los farmaceuticos.", error);
        }
        return farmaceuticos;
    }

    @Override
    public Farmaceutico buscarPorId(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, identificador);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el farmaceutico solicitado.", error);
        }
    }

    @Override
    public void insertar(Farmaceutico farmaceutico) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            asignarParametros(sentencia, farmaceutico);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar el farmaceutico.", error);
        }
    }

    @Override
    public void actualizar(Farmaceutico farmaceutico) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(sentencia, farmaceutico);
            sentencia.setInt(7, farmaceutico.getIdFarmaceutico());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar el farmaceutico.", error);
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
                    "No fue posible eliminar el farmaceutico: puede tener pedidos asociados.", error);
        }
    }

    @Override
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar los farmaceuticos.", error);
        }
    }

    private void asignarParametros(PreparedStatement sentencia, Farmaceutico farmaceutico)
            throws SQLException {
        sentencia.setString(1, farmaceutico.getRegistroProfesional());
        sentencia.setString(2, farmaceutico.getEspecialidad());
        sentencia.setString(3, farmaceutico.getNombre());
        sentencia.setString(4, farmaceutico.getApellido());
        sentencia.setString(5, farmaceutico.getTelefono());
        sentencia.setInt(6, farmaceutico.getIdUsuario());
    }

    private Farmaceutico convertirFila(ResultSet resultado) throws SQLException {
        Farmaceutico farmaceutico = new Farmaceutico();
        farmaceutico.setIdFarmaceutico(resultado.getInt("Far_IdFarmaceutico"));
        farmaceutico.setRegistroProfesional(resultado.getString("Farm_RegistroProfesional"));
        farmaceutico.setEspecialidad(resultado.getString("Far_Especialidad"));
        farmaceutico.setNombre(resultado.getString("Far_Nombre"));
        farmaceutico.setApellido(resultado.getString("Far_Apellido"));
        farmaceutico.setTelefono(resultado.getString("Far_Telefono"));
        farmaceutico.setIdUsuario(resultado.getInt("TblUsu_UsuIdUsuario"));
        farmaceutico.setEmailUsuario(resultado.getString("Usu_Email"));
        return farmaceutico;
    }
}
