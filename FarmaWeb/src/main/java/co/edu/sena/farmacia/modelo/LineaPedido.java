package co.edu.sena.farmacia.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Representa la tabla TblLineaPedido: el detalle de cada producto incluido
 * en un pedido, con su cantidad, precio unitario y subtotal.
 *
 * Es la relacion de composicion con Pedido descrita en el diagrama de clases.
 */
public class LineaPedido {

    private int idLinea;
    private double cantidad;
    private BigDecimal precio;
    private BigDecimal subtotal;
    private int idPedido;
    private int idProducto;

    /** Nombre del producto obtenido por union, para mostrarlo en la interfaz. */
    private String nombreProducto;

    public LineaPedido() {
    }

    public LineaPedido(int idLinea, double cantidad, BigDecimal precio, int idProducto) {
        this.idLinea = idLinea;
        this.cantidad = cantidad;
        this.precio = precio;
        this.idProducto = idProducto;
    }

    /**
     * Multiplica cantidad por precio unitario para obtener el subtotal.
     *
     * Nunca se confia en el subtotal enviado desde el formulario: siempre
     * se recalcula en la capa de logica para evitar inconsistencias.
     *
     * @return subtotal con dos decimales
     */
    public BigDecimal calcularSubtotal() {
        if (precio == null) {
            return BigDecimal.ZERO;
        }
        return precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
    }

    public int getIdLinea() {
        return idLinea;
    }

    public void setIdLinea(int idLinea) {
        this.idLinea = idLinea;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
}
