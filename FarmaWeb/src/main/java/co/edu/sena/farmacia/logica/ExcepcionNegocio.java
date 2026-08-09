package co.edu.sena.farmacia.logica;

/**
 * Error provocado por el incumplimiento de una regla de negocio.
 *
 * A diferencia de los errores tecnicos, su mensaje esta escrito para que el
 * usuario final lo entienda y se muestra directamente en la interfaz.
 */
public class ExcepcionNegocio extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExcepcionNegocio(String mensaje) {
        super(mensaje);
    }

    public ExcepcionNegocio(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
