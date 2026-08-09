package co.edu.sena.farmacia.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.PasarelaPago;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Pasarelas de Pago.
 * Opera sobre la tabla TblPasarelaPago.
 */
public class PasarelaPagoDao implements DaoGenerico<PasarelaPago> {

    private static final String SQL_LISTAR =
            "SELECT Pas_IdPasarela, Pas_NombreProveedor, Pas_Apikeypublic FROM TblPasarelaPago "
            + "ORDER BY Pas_NombreProveedor";

    private static final String SQL_BUSCAR =
            "SELECT Pas_IdPasarela, Pas_NombreProveedor, Pas_Apikeypublic FROM TblPasarelaPago "
            + "WHERE Pas_IdPasarela = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblPasarelaPago (Pas_NombreProveedor, Pas_Apikeypublic) VALUES (?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblPasarelaPago SET Pas_NombreProveedor = ?, Pas_Apikeypublic = ? "
            + "WHERE Pas_IdPasarela = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM TblPasarelaPago WHERE Pas_IdPasarela = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblPasarelaPago";

    @Override
    public List<PasarelaPago> listar() {
        List<PasarelaPago> pasarelas = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                pasarelas.add(convertirFila(resultado));
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar las pasarelas de pago.", error);
        }
        return pasarelas;
    }

    @Override
    public PasarelaPago buscarPorId(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, identificador);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar la pasarela solicitada.", error);
        }
    }

    @Override
    public void insertar(PasarelaPago pasarela) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            sentencia.setString(1, pasarela.getNombreProveedor());
            sentencia.setString(2, pasarela.getApiKeyPublica());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar la pasarela de pago.", error);
        }
    }

    @Override
    public void actualizar(PasarelaPago pasarela) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            sentencia.setString(1, pasarela.getNombreProveedor());
            sentencia.setString(2, pasarela.getApiKeyPublica());
            sentencia.setInt(3, pasarela.getIdPasarela());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar la pasarela de pago.", error);
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
                    "No fue posible eliminar la pasarela: tiene transacciones asociadas.", error);
        }
    }

    @Override
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar las pasarelas de pago.", error);
        }
    }

    private PasarelaPago convertirFila(ResultSet resultado) throws SQLException {
        PasarelaPago pasarela = new PasarelaPago();
        pasarela.setIdPasarela(resultado.getInt("Pas_IdPasarela"));
        pasarela.setNombreProveedor(resultado.getString("Pas_NombreProveedor"));
        pasarela.setApiKeyPublica(resultado.getString("Pas_Apikeypublic"));
        return pasarela;
    }
}
