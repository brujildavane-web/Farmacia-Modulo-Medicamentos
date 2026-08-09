package co.edu.sena.farmacia.logica;

import java.util.List;

import co.edu.sena.farmacia.modelo.PasarelaPago;
import co.edu.sena.farmacia.persistencia.PasarelaPagoDao;
import co.edu.sena.farmacia.util.ConversorDatos;

/**
 * Capa de Logica del modulo de Pasarelas de Pago.
 *
 * Administra los proveedores de pago certificados con los que la farmacia
 * procesa las compras en linea.
 */
public class ServicioPasarelaPago {

    /** Longitud minima aceptada para la llave publica del proveedor. */
    private static final int LONGITUD_MINIMA_APIKEY = 8;

    private final PasarelaPagoDao pasarelaDao = new PasarelaPagoDao();

    /**
     * Consulta las pasarelas habilitadas.
     *
     * @return lista de pasarelas ordenada por proveedor
     */
    public List<PasarelaPago> consultarTodos() {
        return pasarelaDao.listar();
    }

    /**
     * Busca una pasarela por su identificador.
     *
     * @param idPasarela identificador de la pasarela
     * @return la pasarela encontrada
     */
    public PasarelaPago consultarPorId(int idPasarela) {
        PasarelaPago pasarela = pasarelaDao.buscarPorId(idPasarela);
        if (pasarela == null) {
            throw new ExcepcionNegocio("La pasarela de pago solicitada no existe.");
        }
        return pasarela;
    }

    /**
     * Habilita una pasarela de pago nueva.
     *
     * @param pasarela datos del proveedor
     */
    public void registrar(PasarelaPago pasarela) {
        validar(pasarela);
        verificarProveedorDisponible(pasarela.getNombreProveedor(), 0);
        pasarelaDao.insertar(pasarela);
    }

    /**
     * Modifica los datos de una pasarela existente.
     *
     * @param pasarela datos nuevos del proveedor
     */
    public void modificar(PasarelaPago pasarela) {
        validar(pasarela);
        verificarProveedorDisponible(pasarela.getNombreProveedor(), pasarela.getIdPasarela());
        pasarelaDao.actualizar(pasarela);
    }

    /**
     * Deshabilita una pasarela de pago.
     *
     * @param idPasarela identificador de la pasarela
     */
    public void eliminar(int idPasarela) {
        pasarelaDao.eliminar(idPasarela);
    }

    /**
     * Cuenta las pasarelas habilitadas.
     *
     * @return cantidad de pasarelas
     */
    public int contar() {
        return pasarelaDao.contar();
    }

    private void validar(PasarelaPago pasarela) {
        if (ConversorDatos.estaVacio(pasarela.getNombreProveedor())) {
            throw new ExcepcionNegocio("El nombre del proveedor de pago es obligatorio.");
        }
        if (ConversorDatos.estaVacio(pasarela.getApiKeyPublica())) {
            throw new ExcepcionNegocio("La llave publica del proveedor es obligatoria.");
        }
        if (pasarela.getApiKeyPublica().trim().length() < LONGITUD_MINIMA_APIKEY) {
            throw new ExcepcionNegocio("La llave publica debe tener al menos "
                    + LONGITUD_MINIMA_APIKEY + " caracteres.");
        }
    }

    /**
     * Impide registrar dos veces el mismo proveedor de pago.
     *
     * @param nombreProveedor nombre del proveedor
     * @param idPasarelaActual identificador de la pasarela que se edita, o 0 si es nueva
     */
    private void verificarProveedorDisponible(String nombreProveedor, int idPasarelaActual) {
        for (PasarelaPago existente : pasarelaDao.listar()) {
            boolean mismoProveedor = existente.getNombreProveedor() != null
                    && existente.getNombreProveedor().trim().equalsIgnoreCase(nombreProveedor.trim());

            if (mismoProveedor && existente.getIdPasarela() != idPasarelaActual) {
                throw new ExcepcionNegocio(
                        "El proveedor " + nombreProveedor + " ya esta registrado.");
            }
        }
    }
}
