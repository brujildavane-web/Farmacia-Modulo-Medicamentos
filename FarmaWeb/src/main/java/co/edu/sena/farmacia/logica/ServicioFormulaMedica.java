package co.edu.sena.farmacia.logica;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import co.edu.sena.farmacia.modelo.FormulaMedica;
import co.edu.sena.farmacia.persistencia.FormulaMedicaDao;
import co.edu.sena.farmacia.util.ConversorDatos;

/**
 * Capa de Logica del modulo de Formulas Medicas.
 *
 * Aplica dos reglas de negocio del proyecto: el sistema no almacena recetas
 * vencidas y solo acepta archivos en formato PDF, JPG o PNG.
 */
public class ServicioFormulaMedica {

    /** Formatos de archivo permitidos para adjuntar la prescripcion. */
    public static final List<String> FORMATOS_PERMITIDOS = Arrays.asList("pdf", "jpg", "jpeg", "png");

    private final FormulaMedicaDao formulaDao = new FormulaMedicaDao();

    /**
     * Consulta las formulas registradas.
     *
     * @return lista de formulas de la mas reciente a la mas antigua
     */
    public List<FormulaMedica> consultarTodos() {
        return formulaDao.listar();
    }

    /**
     * Consulta las formulas adjuntas a un pedido.
     *
     * @param idPedido identificador del pedido
     * @return formulas del pedido
     */
    public List<FormulaMedica> consultarPorPedido(int idPedido) {
        return formulaDao.listarPorPedido(idPedido);
    }

    /**
     * Busca una formula por su identificador.
     *
     * @param idFormula identificador de la formula
     * @return la formula encontrada
     */
    public FormulaMedica consultarPorId(int idFormula) {
        FormulaMedica formula = formulaDao.buscarPorId(idFormula);
        if (formula == null) {
            throw new ExcepcionNegocio("La formula medica solicitada no existe.");
        }
        return formula;
    }

    /**
     * Registra la prescripcion que respalda un medicamento controlado.
     *
     * @param formula datos de la formula
     */
    public void registrar(FormulaMedica formula) {
        validar(formula);

        if (formula.estaVencida()) {
            throw new ExcepcionNegocio("No se puede almacenar una formula medica vencida.");
        }

        formulaDao.insertar(formula);
    }

    /**
     * Modifica una formula existente.
     *
     * @param formula datos nuevos de la formula
     */
    public void modificar(FormulaMedica formula) {
        validar(formula);
        formulaDao.actualizar(formula);
    }

    /**
     * Elimina una formula medica.
     *
     * @param idFormula identificador de la formula
     */
    public void eliminar(int idFormula) {
        formulaDao.eliminar(idFormula);
    }

    /**
     * Cuenta las formulas registradas.
     *
     * @return cantidad de formulas
     */
    public int contar() {
        return formulaDao.contar();
    }

    private void validar(FormulaMedica formula) {
        if (formula.getIdPedido() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el pedido asociado a la formula.");
        }
        if (formula.getIdProducto() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el medicamento prescrito.");
        }
        if (formula.getFechaPrescripcion() == null) {
            throw new ExcepcionNegocio("La fecha de prescripcion es obligatoria.");
        }
        if (formula.getFechaPrescripcion().isAfter(LocalDate.now())) {
            throw new ExcepcionNegocio("La fecha de prescripcion no puede ser posterior a hoy.");
        }
        if (formula.getFechaVencimiento() != null
                && formula.getFechaVencimiento().isBefore(formula.getFechaPrescripcion())) {
            throw new ExcepcionNegocio(
                    "La fecha de vencimiento no puede ser anterior a la de prescripcion.");
        }

        validarFormatoArchivo(formula.getArchivo());
    }

    /**
     * Comprueba que el archivo adjunto tenga una extension permitida.
     *
     * @param nombreArchivo nombre del archivo de la receta
     */
    private void validarFormatoArchivo(String nombreArchivo) {
        if (ConversorDatos.estaVacio(nombreArchivo)) {
            return;
        }

        int posicionPunto = nombreArchivo.lastIndexOf('.');
        if (posicionPunto < 0) {
            throw new ExcepcionNegocio("El archivo debe incluir su extension (PDF, JPG o PNG).");
        }

        String extension = nombreArchivo.substring(posicionPunto + 1).toLowerCase();
        if (!FORMATOS_PERMITIDOS.contains(extension)) {
            throw new ExcepcionNegocio(
                    "El archivo de la formula solo puede estar en formato PDF, JPG o PNG.");
        }
    }
}
