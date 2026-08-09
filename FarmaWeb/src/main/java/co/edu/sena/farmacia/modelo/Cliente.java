package co.edu.sena.farmacia.modelo;

import java.time.LocalDate;
import java.time.Period;

/**
 * Representa la tabla TblCliente: la persona que compra medicamentos.
 *
 * Hereda de Persona los atributos de identificacion y agrega los datos
 * de contacto y domicilio necesarios para la entrega de los pedidos.
 */
public class Cliente extends Persona {

    private int idCliente;
    private String direccion;
    private String email;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private int idUsuario;

    /** Correo del usuario asociado, obtenido por union para la interfaz. */
    private String emailUsuario;

    public Cliente() {
        super();
    }

    public Cliente(int idCliente, String nombre, String apellido, String telefono) {
        super(nombre, apellido, telefono);
        this.idCliente = idCliente;
    }

    /**
     * Calcula la edad del cliente a partir de su fecha de nacimiento.
     *
     * Evita que el dato quede desactualizado en la base de datos y sirve
     * para validar la venta de medicamentos restringidos por edad.
     *
     * @return anios cumplidos, o null si no se registro la fecha
     */
    public Integer calcularEdadActual() {
        if (fechaNacimiento == null) {
            return null;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    @Override
    public String describirRol() {
        return "Cliente";
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }
}
