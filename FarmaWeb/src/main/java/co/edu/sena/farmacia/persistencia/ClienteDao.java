package co.edu.sena.farmacia.persistencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.Cliente;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Clientes. Opera sobre la tabla TblCliente
 * y une con TblUsuario para mostrar el correo de acceso asociado.
 */
public class ClienteDao implements DaoGenerico<Cliente> {

    private static final String COLUMNAS =
            "c.Clie_IdCliente, c.Cli_Nombre, c.Cli_Apellido, c.Cli_Direccion, c.Cli_Telefono, "
            + "c.Cli_Email, c.Cli_FechaNacimiento, c.Cli_Edad, c.TblUsu_UsuIdUsuario, u.Usu_Email";

    private static final String SQL_LISTAR =
            "SELECT " + COLUMNAS + " FROM TblCliente c "
            + "INNER JOIN TblUsuario u ON u.Usu_IdUsuario = c.TblUsu_UsuIdUsuario "
            + "ORDER BY c.Cli_Apellido, c.Cli_Nombre";

    private static final String SQL_BUSCAR =
            "SELECT " + COLUMNAS + " FROM TblCliente c "
            + "INNER JOIN TblUsuario u ON u.Usu_IdUsuario = c.TblUsu_UsuIdUsuario "
            + "WHERE c.Clie_IdCliente = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblCliente (Cli_Nombre, Cli_Apellido, Cli_Direccion, Cli_Telefono, "
            + "Cli_Email, Cli_FechaNacimiento, Cli_Edad, TblUsu_UsuIdUsuario) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblCliente SET Cli_Nombre = ?, Cli_Apellido = ?, Cli_Direccion = ?, "
            + "Cli_Telefono = ?, Cli_Email = ?, Cli_FechaNacimiento = ?, Cli_Edad = ?, "
            + "TblUsu_UsuIdUsuario = ? WHERE Clie_IdCliente = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM TblCliente WHERE Clie_IdCliente = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblCliente";

    @Override
    public List<Cliente> listar() {
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                clientes.add(convertirFila(resultado));
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar los clientes.", error);
        }
        return clientes;
    }

    @Override
    public Cliente buscarPorId(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, identificador);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar el cliente solicitado.", error);
        }
    }

    @Override
    public void insertar(Cliente cliente) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            asignarParametros(sentencia, cliente);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar el cliente.", error);
        }
    }

    @Override
    public void actualizar(Cliente cliente) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(sentencia, cliente);
            sentencia.setInt(9, cliente.getIdCliente());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar el cliente.", error);
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
                    "No fue posible eliminar el cliente: puede tener pedidos asociados.", error);
        }
    }

    @Override
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar los clientes.", error);
        }
    }

    /**
     * Asigna los ocho campos comunes de insercion y actualizacion.
     */
    private void asignarParametros(PreparedStatement sentencia, Cliente cliente) throws SQLException {
        sentencia.setString(1, cliente.getNombre());
        sentencia.setString(2, cliente.getApellido());
        sentencia.setString(3, cliente.getDireccion());
        sentencia.setString(4, cliente.getTelefono());
        sentencia.setString(5, cliente.getEmail());

        if (cliente.getFechaNacimiento() != null) {
            sentencia.setDate(6, Date.valueOf(cliente.getFechaNacimiento()));
        } else {
            sentencia.setNull(6, Types.DATE);
        }

        if (cliente.getEdad() != null) {
            sentencia.setInt(7, cliente.getEdad());
        } else {
            sentencia.setNull(7, Types.INTEGER);
        }

        sentencia.setInt(8, cliente.getIdUsuario());
    }

    private Cliente convertirFila(ResultSet resultado) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(resultado.getInt("Clie_IdCliente"));
        cliente.setNombre(resultado.getString("Cli_Nombre"));
        cliente.setApellido(resultado.getString("Cli_Apellido"));
        cliente.setDireccion(resultado.getString("Cli_Direccion"));
        cliente.setTelefono(resultado.getString("Cli_Telefono"));
        cliente.setEmail(resultado.getString("Cli_Email"));

        Date fechaNacimiento = resultado.getDate("Cli_FechaNacimiento");
        if (fechaNacimiento != null) {
            cliente.setFechaNacimiento(fechaNacimiento.toLocalDate());
        }

        int edad = resultado.getInt("Cli_Edad");
        cliente.setEdad(resultado.wasNull() ? null : edad);

        cliente.setIdUsuario(resultado.getInt("TblUsu_UsuIdUsuario"));
        cliente.setEmailUsuario(resultado.getString("Usu_Email"));
        return cliente;
    }
}
