package co.edu.sena.farmacia.persistencia;

/**
 * Error tecnico ocurrido al comunicarse con la base de datos.
 *
 * Envuelve las SQLException para que las capas superiores no dependan
 * de la tecnologia JDBC.
 */
public class ExcepcionPersistencia extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExcepcionPersistencia(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public ExcepcionPersistencia(String mensaje) {
        super(mensaje);
    }
}
