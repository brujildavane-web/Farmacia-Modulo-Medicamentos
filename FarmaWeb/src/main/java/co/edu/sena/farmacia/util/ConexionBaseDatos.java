package co.edu.sena.farmacia.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Capa de Persistencia: administra el acceso fisico a la base de datos MySQL.
 *
 * Centraliza la apertura de conexiones para que ningun otro componente del
 * sistema necesite conocer la URL, el usuario ni la contrasena del motor.
 */
public final class ConexionBaseDatos {

    private static final String ARCHIVO_CONFIGURACION = "db.properties";

    private static final Properties CONFIGURACION = cargarConfiguracion();

    /** Clase de utilidad: no se permite crear instancias. */
    private ConexionBaseDatos() {
    }

    /**
     * Lee una sola vez el archivo db.properties y registra el driver JDBC.
     */
    private static Properties cargarConfiguracion() {
        Properties propiedades = new Properties();
        ClassLoader cargador = ConexionBaseDatos.class.getClassLoader();

        try (InputStream flujo = cargador.getResourceAsStream(ARCHIVO_CONFIGURACION)) {
            if (flujo == null) {
                throw new IllegalStateException("No se encontro el archivo " + ARCHIVO_CONFIGURACION);
            }
            propiedades.load(flujo);
        } catch (IOException error) {
            throw new IllegalStateException("Error al leer " + ARCHIVO_CONFIGURACION, error);
        }

        try {
            Class.forName(propiedades.getProperty("farmacia.db.driver"));
        } catch (ClassNotFoundException error) {
            throw new IllegalStateException("No se encontro el driver JDBC de MySQL", error);
        }

        return propiedades;
    }

    /**
     * Entrega una conexion nueva a la base de datos bd_farmacia.
     *
     * @return conexion abierta que el llamador debe cerrar
     * @throws SQLException si el motor de base de datos no responde
     */
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(
                CONFIGURACION.getProperty("farmacia.db.url"),
                CONFIGURACION.getProperty("farmacia.db.usuario"),
                CONFIGURACION.getProperty("farmacia.db.password"));
    }

    /**
     * Verifica que el servidor de base de datos este disponible.
     *
     * @return true si la conexion se pudo establecer correctamente
     */
    public static boolean probarConexion() {
        try (Connection conexion = obtenerConexion()) {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException error) {
            return false;
        }
    }
}
