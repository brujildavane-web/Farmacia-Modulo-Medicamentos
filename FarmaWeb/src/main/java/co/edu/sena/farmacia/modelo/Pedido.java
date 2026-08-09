package co.edu.sena.farmacia.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa la tabla TblPedido: el encabezado de una compra realizada por
 * un cliente y atendida por un farmaceutico.
 */
public class Pedido {

    /** Impuesto al valor agregado vigente en Colombia. */
    public static final BigDecimal IVA_COLOMBIA = new BigDecimal("0.19");

    private int idPedido;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String estado;
    private int idCliente;
    private int idFarmaceutico;

    /** Datos obtenidos por union para mostrarlos en la interfaz. */
    private String nombreCliente;
    private String nombreFarmaceutico;

    public Pedido() {
    }

    public Pedido(int idPedido, LocalDateTime fecha, BigDecimal total, String estado) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    /**
     * Calcula el valor del impuesto contenido en el total del pedido.
     *
     * @return valor del IVA sobre el total registrado
     */
    public BigDecimal calcularValorImpuesto() {
        if (total == null) {
            return BigDecimal.ZERO;
        }
        return total.multiply(IVA_COLOMBIA).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Verifica si el pedido admite cambios en sus lineas de detalle.
     *
     * @return true mientras el pedido no haya sido entregado ni anulado
     */
    public boolean permiteModificacion() {
        return estado == null
                || (!estado.equalsIgnoreCase("ENTREGADO") && !estado.equalsIgnoreCase("ANULADO"));
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdFarmaceutico() {
        return idFarmaceutico;
    }

    public void setIdFarmaceutico(int idFarmaceutico) {
        this.idFarmaceutico = idFarmaceutico;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreFarmaceutico() {
        return nombreFarmaceutico;
    }

    public void setNombreFarmaceutico(String nombreFarmaceutico) {
        this.nombreFarmaceutico = nombreFarmaceutico;
    }

    public BigDecimal getValorImpuesto() {
        return calcularValorImpuesto();
    }
}
