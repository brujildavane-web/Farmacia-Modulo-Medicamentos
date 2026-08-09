package co.edu.sena.farmacia.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa la tabla TblTransaccionPago: el registro del pago de un pedido
 * a traves de una pasarela certificada.
 */
public class TransaccionPago {

    private int idTransaccion;
    private LocalDateTime fechaPago;
    private BigDecimal valor;
    private String estadoTransaccion;
    private int idPedido;
    private int idPasarela;

    /** Datos obtenidos por union para mostrarlos en la interfaz. */
    private String nombreProveedor;
    private String nombreCliente;

    public TransaccionPago() {
    }

    public TransaccionPago(int idTransaccion, LocalDateTime fechaPago, BigDecimal valor, String estadoTransaccion) {
        this.idTransaccion = idTransaccion;
        this.fechaPago = fechaPago;
        this.valor = valor;
        this.estadoTransaccion = estadoTransaccion;
    }

    /**
     * Indica si el pago quedo confirmado por la pasarela.
     *
     * @return true cuando el estado de la transaccion es APROBADA
     */
    public boolean estaAprobada() {
        return estadoTransaccion != null && estadoTransaccion.equalsIgnoreCase("APROBADA");
    }

    public int getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(int idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getEstadoTransaccion() {
        return estadoTransaccion;
    }

    public void setEstadoTransaccion(String estadoTransaccion) {
        this.estadoTransaccion = estadoTransaccion;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdPasarela() {
        return idPasarela;
    }

    public void setIdPasarela(int idPasarela) {
        this.idPasarela = idPasarela;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public boolean getAprobada() {
        return estaAprobada();
    }
}
