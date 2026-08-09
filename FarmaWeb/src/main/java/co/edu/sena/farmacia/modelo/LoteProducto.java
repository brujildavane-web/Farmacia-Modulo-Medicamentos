package co.edu.sena.farmacia.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Representa la tabla TblLoteProducto: cada ingreso de un producto al
 * inventario con su registro sanitario y su fecha de vencimiento.
 *
 * Es la relacion de composicion con Producto que permite la trazabilidad
 * exigida por la normativa sanitaria.
 */
public class LoteProducto {

    /** Dias de anticipacion con que se avisa que un lote esta por caducar. */
    public static final int DIAS_ALERTA_CADUCIDAD = 30;

    private int idLote;
    private LocalDate fechaVencimiento;
    private String registroSanitario;
    private double stockActual;
    private String marca;
    private int idProducto;

    /** Nombre del producto obtenido por union, para mostrarlo en la interfaz. */
    private String nombreProducto;

    public LoteProducto() {
    }

    public LoteProducto(int idLote, LocalDate fechaVencimiento, double stockActual, int idProducto) {
        this.idLote = idLote;
        this.fechaVencimiento = fechaVencimiento;
        this.stockActual = stockActual;
        this.idProducto = idProducto;
    }

    /**
     * Verifica si el lote ya paso su fecha de vencimiento.
     *
     * @return true cuando el lote no se puede dispensar
     */
    public boolean estaVencido() {
        return fechaVencimiento != null && fechaVencimiento.isBefore(LocalDate.now());
    }

    /**
     * Calcula los dias que faltan para que el lote caduque.
     *
     * @return dias restantes, negativo si el lote ya vencio
     */
    public long calcularDiasParaVencer() {
        if (fechaVencimiento == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
    }

    /**
     * Determina si el lote debe generar una alerta de caducidad al administrador.
     *
     * @return true cuando faltan menos de 30 dias para el vencimiento
     */
    public boolean requiereAlertaCaducidad() {
        long diasRestantes = calcularDiasParaVencer();
        return diasRestantes <= DIAS_ALERTA_CADUCIDAD;
    }

    /**
     * Clasifica el estado del lote para mostrarlo con color en la interfaz.
     *
     * @return VENCIDO, POR VENCER o VIGENTE
     */
    public String describirEstado() {
        if (estaVencido()) {
            return "VENCIDO";
        }
        if (requiereAlertaCaducidad()) {
            return "POR VENCER";
        }
        return "VIGENTE";
    }

    public int getIdLote() {
        return idLote;
    }

    public void setIdLote(int idLote) {
        this.idLote = idLote;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getRegistroSanitario() {
        return registroSanitario;
    }

    public void setRegistroSanitario(String registroSanitario) {
        this.registroSanitario = registroSanitario;
    }

    public double getStockActual() {
        return stockActual;
    }

    public void setStockActual(double stockActual) {
        this.stockActual = stockActual;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
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

    public String getEstado() {
        return describirEstado();
    }

    public long getDiasParaVencer() {
        return calcularDiasParaVencer();
    }
}
