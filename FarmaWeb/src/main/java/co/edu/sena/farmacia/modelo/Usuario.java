package co.edu.sena.farmacia.modelo;

import java.time.LocalDate;

/**
 * Representa la tabla TblUsuario: la credencial con la que una persona
 * ingresa al sistema. Cada usuario esta asociado a un unico rol.
 */
public class Usuario {

    private int idUsuario;
    private String email;
    private String password;
    private LocalDate fechaRegistro;
    private int idRol;

    /** Nombre del rol obtenido por union, para mostrarlo en la interfaz. */
    private String nombreRol;

    public Usuario() {
    }

    public Usuario(int idUsuario, String email, LocalDate fechaRegistro, int idRol) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
        this.idRol = idRol;
    }

    /**
     * Verifica si el usuario tiene el perfil administrador.
     *
     * @return true cuando el nombre del rol corresponde a Administrador
     */
    public boolean esAdministrador() {
        return nombreRol != null && nombreRol.equalsIgnoreCase("Administrador");
    }

    /**
     * Verifica si el usuario tiene el perfil de farmaceutico.
     *
     * @return true cuando el nombre del rol corresponde a Farmaceutico
     */
    public boolean esFarmaceutico() {
        return nombreRol != null && nombreRol.equalsIgnoreCase("Farmaceutico");
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
}
