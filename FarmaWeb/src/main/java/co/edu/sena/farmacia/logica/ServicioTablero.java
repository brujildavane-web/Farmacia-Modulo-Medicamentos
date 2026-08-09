package co.edu.sena.farmacia.logica;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Capa de Logica del tablero administrativo.
 *
 * Reune los indicadores de los trece modulos para mostrarlos en la pantalla
 * de inicio del sistema, dando soporte a los reportes y estadisticas.
 */
public class ServicioTablero {

    private final ServicioRol servicioRol = new ServicioRol();
    private final ServicioUsuario servicioUsuario = new ServicioUsuario();
    private final ServicioCliente servicioCliente = new ServicioCliente();
    private final ServicioFarmaceutico servicioFarmaceutico = new ServicioFarmaceutico();
    private final ServicioProducto servicioProducto = new ServicioProducto();
    private final ServicioLoteProducto servicioLote = new ServicioLoteProducto();
    private final ServicioFavorito servicioFavorito = new ServicioFavorito();
    private final ServicioPedido servicioPedido = new ServicioPedido();
    private final ServicioLineaPedido servicioLinea = new ServicioLineaPedido();
    private final ServicioHistorial servicioHistorial = new ServicioHistorial();
    private final ServicioFormulaMedica servicioFormula = new ServicioFormulaMedica();
    private final ServicioPasarelaPago servicioPasarela = new ServicioPasarelaPago();
    private final ServicioTransaccionPago servicioTransaccion = new ServicioTransaccionPago();

    /**
     * Cuenta los registros existentes en cada modulo del sistema.
     *
     * Se usa LinkedHashMap para que las tarjetas del tablero conserven el
     * orden en que se declaran.
     *
     * @return nombre del modulo y su cantidad de registros
     */
    public Map<String, Integer> contarRegistrosPorModulo() {
        Map<String, Integer> conteos = new LinkedHashMap<>();
        conteos.put("Roles", servicioRol.contar());
        conteos.put("Usuarios", servicioUsuario.contar());
        conteos.put("Clientes", servicioCliente.contar());
        conteos.put("Farmaceuticos", servicioFarmaceutico.contar());
        conteos.put("Productos", servicioProducto.contar());
        conteos.put("Lotes", servicioLote.contar());
        conteos.put("Favoritos", servicioFavorito.contar());
        conteos.put("Pedidos", servicioPedido.contar());
        conteos.put("Lineas de pedido", servicioLinea.contar());
        conteos.put("Historial", servicioHistorial.contar());
        conteos.put("Formulas medicas", servicioFormula.contar());
        conteos.put("Pasarelas de pago", servicioPasarela.contar());
        conteos.put("Transacciones", servicioTransaccion.contar());
        return conteos;
    }

    /**
     * Calcula el total facturado por la farmacia.
     *
     * @return suma de los pedidos no anulados
     */
    public BigDecimal calcularTotalVentas() {
        return servicioPedido.calcularTotalVentas();
    }

    /**
     * Cuenta los lotes que exigen atencion por proximidad de caducidad.
     *
     * @return cantidad de lotes vencidos o por vencer
     */
    public int contarAlertasCaducidad() {
        return servicioLote.contarAlertasCaducidad();
    }
}
