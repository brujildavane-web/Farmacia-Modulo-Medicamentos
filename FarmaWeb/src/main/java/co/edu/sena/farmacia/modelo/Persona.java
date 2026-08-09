package co.edu.sena.farmacia.modelo;

/**
 * Superclase que reune los datos comunes de las personas del sistema.
 *
 * Aplica el principio de herencia definido en los estandares de codificacion
 * del proyecto: Persona es la clase padre de Cliente y Farmaceutico, lo que
 * evita repetir los atributos de identificacion en cada subclase.
 */
public abstract class Persona {

    private String nombre;
    private String apellido;
    private String telefono;

    protected Persona() {
    }

    protected Persona(String nombre, String apellido, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
    }

    /**
     * Une nombre y apellido para mostrarlos en las tablas de la interfaz.
     *
     * @return nombre completo de la persona
     */
    public String obtenerNombreCompleto() {
        StringBuilder nombreCompleto = new StringBuilder();
        if (nombre != null) {
            nombreCompleto.append(nombre);
        }
        if (apellido != null) {
            nombreCompleto.append(" ").append(apellido);
        }
        return nombreCompleto.toString().trim();
    }

    /**
     * Define el comportamiento que cada subclase debe describir sobre si misma.
     *
     * @return rol que cumple la persona dentro de la farmacia
     */
    public abstract String describirRol();

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /** Alias sin prefijo get para poder usar persona.nombreCompleto en las JSP. */
    public String getNombreCompleto() {
        return obtenerNombreCompleto();
    }
}
