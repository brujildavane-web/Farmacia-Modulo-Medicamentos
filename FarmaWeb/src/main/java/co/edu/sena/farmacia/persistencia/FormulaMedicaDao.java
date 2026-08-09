package co.edu.sena.farmacia.persistencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import co.edu.sena.farmacia.modelo.FormulaMedica;
import co.edu.sena.farmacia.util.ConexionBaseDatos;

/**
 * Capa de Persistencia del modulo de Formulas Medicas.
 * Opera sobre la tabla TblFormulaMedica.
 */
public class FormulaMedicaDao implements DaoGenerico<FormulaMedica> {

    private static final String COLUMNAS =
            "fo.For_IdFormula, fo.For_FechaPrescripcion, fo.For_FechaVencimiento, fo.For_Archivo, "
            + "fo.TblPed_PedIdPedido, fo.TblPro_ProIdProducto, p.Pro_Nombre, "
            + "CONCAT(c.Cli_Nombre, ' ', c.Cli_Apellido) AS NombreCliente";

    private static final String SQL_UNIONES =
            " FROM TblFormulaMedica fo "
            + "INNER JOIN TblProducto p ON p.Pro_IdProducto = fo.TblPro_ProIdProducto "
            + "INNER JOIN TblPedido pe ON pe.Ped_IdPedido = fo.TblPed_PedIdPedido "
            + "INNER JOIN TblCliente c ON c.Clie_IdCliente = pe.TblCli_ClieIdCliente ";

    private static final String SQL_LISTAR =
            "SELECT " + COLUMNAS + SQL_UNIONES + "ORDER BY fo.For_FechaPrescripcion DESC";

    private static final String SQL_BUSCAR =
            "SELECT " + COLUMNAS + SQL_UNIONES + "WHERE fo.For_IdFormula = ?";

    private static final String SQL_LISTAR_POR_PEDIDO =
            "SELECT " + COLUMNAS + SQL_UNIONES + "WHERE fo.TblPed_PedIdPedido = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO TblFormulaMedica (For_FechaPrescripcion, For_FechaVencimiento, For_Archivo, "
            + "TblPed_PedIdPedido, TblPro_ProIdProducto) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE TblFormulaMedica SET For_FechaPrescripcion = ?, For_FechaVencimiento = ?, "
            + "For_Archivo = ?, TblPed_PedIdPedido = ?, TblPro_ProIdProducto = ? "
            + "WHERE For_IdFormula = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM TblFormulaMedica WHERE For_IdFormula = ?";

    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM TblFormulaMedica";

    private static final String SQL_EXISTE_VIGENTE_PARA_PRODUCTO =
            "SELECT COUNT(*) FROM TblFormulaMedica "
            + "WHERE TblPed_PedIdPedido = ? AND TblPro_ProIdProducto = ? "
            + "AND (For_FechaVencimiento IS NULL OR For_FechaVencimiento >= CURDATE())";

    @Override
    public List<FormulaMedica> listar() {
        return ejecutarConsultaLista(SQL_LISTAR, null);
    }

    /**
     * Consulta las formulas adjuntas a un pedido.
     *
     * @param idPedido identificador del pedido
     * @return formulas asociadas al pedido
     */
    public List<FormulaMedica> listarPorPedido(int idPedido) {
        return ejecutarConsultaLista(SQL_LISTAR_POR_PEDIDO, idPedido);
    }

    @Override
    public FormulaMedica buscarPorId(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {

            sentencia.setInt(1, identificador);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? convertirFila(resultado) : null;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar la formula solicitada.", error);
        }
    }

    /**
     * Verifica que un producto controlado tenga una prescripcion vigente
     * dentro del pedido.
     *
     * Implementa la regla de negocio que rechaza la venta de medicamentos
     * controlados sin receta valida.
     *
     * @param idPedido identificador del pedido
     * @param idProducto identificador del producto controlado
     * @return true si existe al menos una formula vigente
     */
    public boolean existeFormulaVigente(int idPedido, int idProducto) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_EXISTE_VIGENTE_PARA_PRODUCTO)) {

            sentencia.setInt(1, idPedido);
            sentencia.setInt(2, idProducto);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() && resultado.getInt(1) > 0;
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible validar la formula medica.", error);
        }
    }

    @Override
    public void insertar(FormulaMedica formula) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {

            asignarParametros(sentencia, formula);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible registrar la formula medica.", error);
        }
    }

    @Override
    public void actualizar(FormulaMedica formula) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(sentencia, formula);
            sentencia.setInt(6, formula.getIdFormula());
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible actualizar la formula medica.", error);
        }
    }

    @Override
    public void eliminar(int identificador) {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {

            sentencia.setInt(1, identificador);
            sentencia.executeUpdate();

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible eliminar la formula medica.", error);
        }
    }

    @Override
    public int contar() {
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            return resultado.next() ? resultado.getInt(1) : 0;

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible contar las formulas medicas.", error);
        }
    }

    private List<FormulaMedica> ejecutarConsultaLista(String sentenciaSql, Integer filtro) {
        List<FormulaMedica> formulas = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sentenciaSql)) {

            if (filtro != null) {
                sentencia.setInt(1, filtro);
            }

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    formulas.add(convertirFila(resultado));
                }
            }

        } catch (SQLException error) {
            throw new ExcepcionPersistencia("No fue posible consultar las formulas medicas.", error);
        }
        return formulas;
    }

    private void asignarParametros(PreparedStatement sentencia, FormulaMedica formula)
            throws SQLException {
        sentencia.setDate(1, Date.valueOf(formula.getFechaPrescripcion()));

        if (formula.getFechaVencimiento() != null) {
            sentencia.setDate(2, Date.valueOf(formula.getFechaVencimiento()));
        } else {
            sentencia.setNull(2, Types.DATE);
        }

        sentencia.setString(3, formula.getArchivo());
        sentencia.setInt(4, formula.getIdPedido());
        sentencia.setInt(5, formula.getIdProducto());
    }

    private FormulaMedica convertirFila(ResultSet resultado) throws SQLException {
        FormulaMedica formula = new FormulaMedica();
        formula.setIdFormula(resultado.getInt("For_IdFormula"));

        Date fechaPrescripcion = resultado.getDate("For_FechaPrescripcion");
        if (fechaPrescripcion != null) {
            formula.setFechaPrescripcion(fechaPrescripcion.toLocalDate());
        }

        Date fechaVencimiento = resultado.getDate("For_FechaVencimiento");
        if (fechaVencimiento != null) {
            formula.setFechaVencimiento(fechaVencimiento.toLocalDate());
        }

        formula.setArchivo(resultado.getString("For_Archivo"));
        formula.setIdPedido(resultado.getInt("TblPed_PedIdPedido"));
        formula.setIdProducto(resultado.getInt("TblPro_ProIdProducto"));
        formula.setNombreProducto(resultado.getString("Pro_Nombre"));
        formula.setNombreCliente(resultado.getString("NombreCliente"));
        return formula;
    }
}
