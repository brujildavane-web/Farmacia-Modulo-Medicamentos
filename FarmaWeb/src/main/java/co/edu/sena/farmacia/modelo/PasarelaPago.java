package co.edu.sena.farmacia.modelo;

/**
 * Representa la tabla TblPasarelaPago: los proveedores de pago en linea
 * habilitados para procesar las compras (PayU, MercadoPago, Stripe).
 */
public class PasarelaPago {

    private int idPasarela;
    private String nombreProveedor;
    private String apiKeyPublica;

    public PasarelaPago() {
    }

    public PasarelaPago(int idPasarela, String nombreProveedor, String apiKeyPublica) {
        this.idPasarela = idPasarela;
        this.nombreProveedor = nombreProveedor;
        this.apiKeyPublica = apiKeyPublica;
    }

    /**
     * Oculta la mayor parte de la llave publica al mostrarla en pantalla.
     *
     * @return llave con solo los ultimos cuatro caracteres visibles
     */
    public String enmascararApiKey() {
        if (apiKeyPublica == null || apiKeyPublica.length() <= 4) {
            return "****";
        }
        return "****" + apiKeyPublica.substring(apiKeyPublica.length() - 4);
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

    public String getApiKeyPublica() {
        return apiKeyPublica;
    }

    public void setApiKeyPublica(String apiKeyPublica) {
        this.apiKeyPublica = apiKeyPublica;
    }

    public String getApiKeyEnmascarada() {
        return enmascararApiKey();
    }
}
