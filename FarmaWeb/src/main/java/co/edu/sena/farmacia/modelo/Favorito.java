package co.edu.sena.farmacia.modelo;

import java.time.LocalDate;

/**
 * Representa la tabla TblFavorito: la marcacion que hace un cliente sobre
 * un producto para volver a comprarlo con rapidez.
 *
 * Su llave primaria es compuesta (cliente + producto), por lo que no maneja
 * un identificador autonumerico propio.
 */
public class Favorito {

    private int idCliente;
    private int idProducto;
    private LocalDate fechaMarcacion;

    /** Datos obtenidos por union para mostrarlos en la interfaz. */
    private String nombreCliente;
    private String nombreProducto;

    public Favorito() {
    }

    public Favorito(int idCliente, int idProducto, LocalDate fechaMarcacion) {
        this.idCliente = idCliente;
        this.idProducto = idProducto;
        this.fechaMarcacion = fechaMarcacion;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public LocalDate getFechaMarcacion() {
        return fechaMarcacion;
    }

    public void setFechaMarcacion(LocalDate fechaMarcacion) {
        this.fechaMarcacion = fechaMarcacion;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
}
