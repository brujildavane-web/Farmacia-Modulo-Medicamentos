package co.edu.sena.farmacia.modelo;

import java.time.LocalDate;

/**
 * Representa la tabla TblHistorial: la traza de los estados por los que pasa
 * un producto dentro de un pedido.
 *
 * Sirve de bitacora para la auditoria y el rastreo de pedidos.
 */
public class Historial {

    private int idPedidoHistorico;
    private int idPedido;
    private LocalDate fecha;
    private int idProducto;
    private String estado;

    /** Datos obtenidos por union para mostrarlos en la interfaz. */
    private String nombreProducto;
    private String nombreCliente;

    public Historial() {
    }

    public Historial(int idPedido, LocalDate fecha, int idProducto, String estado) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.idProducto = idProducto;
        this.estado = estado;
    }

    public int getIdPedidoHistorico() {
        return idPedidoHistorico;
    }

    public void setIdPedidoHistorico(int idPedidoHistorico) {
        this.idPedidoHistorico = idPedidoHistorico;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
}
