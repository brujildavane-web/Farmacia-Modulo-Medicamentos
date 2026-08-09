package co.edu.sena.farmacia.modelo;

/**
 * Representa la tabla TblRol: define los perfiles de acceso al sistema
 * (Administrador, Farmaceutico y Cliente) junto con sus permisos.
 */
public class Rol {

    private int idRol;
    private String nombreRol;
    private String permisos;

    public Rol() {
    }

    public Rol(int idRol, String nombreRol, String permisos) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.permisos = permisos;
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

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }

    @Override
    public String toString() {
        return nombreRol;
    }
}
