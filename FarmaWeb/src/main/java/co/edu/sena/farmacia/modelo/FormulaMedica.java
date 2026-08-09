package co.edu.sena.farmacia.modelo;

import java.time.LocalDate;

/**
 * Representa la tabla TblFormulaMedica: la prescripcion que respalda la
 * entrega de un medicamento controlado.
 *
 * El sistema debe impedir el almacenamiento de formulas vencidas, regla de
 * negocio definida en el documento de requisitos.
 */
public class FormulaMedica {

    private int idFormula;
    private LocalDate fechaPrescripcion;
    private LocalDate fechaVencimiento;
    private String archivo;
    private int idPedido;
    private int idProducto;

    /** Datos obtenidos por union para mostrarlos en la interfaz. */
    private String nombreProducto;
    private String nombreCliente;

    public FormulaMedica() {
    }

    public FormulaMedica(int idFormula, LocalDate fechaPrescripcion, String archivo) {
        this.idFormula = idFormula;
        this.fechaPrescripcion = fechaPrescripcion;
        this.archivo = archivo;
    }

    /**
     * Verifica si la prescripcion perdio vigencia.
     *
     * @return true cuando la fecha de vencimiento ya paso
     */
    public boolean estaVencida() {
        return fechaVencimiento != null && fechaVencimiento.isBefore(LocalDate.now());
    }

    /**
     * Traduce la vigencia de la formula a un texto para la interfaz.
     *
     * @return VENCIDA o VIGENTE
     */
    public String describirVigencia() {
        return estaVencida() ? "VENCIDA" : "VIGENTE";
    }

    public int getIdFormula() {
        return idFormula;
    }

    public void setIdFormula(int idFormula) {
        this.idFormula = idFormula;
    }

    public LocalDate getFechaPrescripcion() {
        return fechaPrescripcion;
    }

    public void setFechaPrescripcion(LocalDate fechaPrescripcion) {
        this.fechaPrescripcion = fechaPrescripcion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
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

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getVigencia() {
        return describirVigencia();
    }
}
