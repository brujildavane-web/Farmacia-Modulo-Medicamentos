package co.edu.sena.farmacia.logica;

import java.util.List;

import co.edu.sena.farmacia.modelo.Farmaceutico;
import co.edu.sena.farmacia.persistencia.FarmaceuticoDao;
import co.edu.sena.farmacia.util.ConversorDatos;

/**
 * Capa de Logica del modulo de Farmaceuticos.
 *
 * Exige el registro profesional que la normativa sanitaria requiere para
 * dispensar medicamentos y evita que dos profesionales lo compartan.
 */
public class ServicioFarmaceutico {

    private final FarmaceuticoDao farmaceuticoDao = new FarmaceuticoDao();

    /**
     * Consulta los farmaceuticos registrados.
     *
     * @return lista de farmaceuticos ordenada por apellido
     */
    public List<Farmaceutico> consultarTodos() {
        return farmaceuticoDao.listar();
    }

    /**
     * Busca un farmaceutico por su identificador.
     *
     * @param idFarmaceutico identificador del profesional
     * @return el farmaceutico encontrado
     */
    public Farmaceutico consultarPorId(int idFarmaceutico) {
        Farmaceutico farmaceutico = farmaceuticoDao.buscarPorId(idFarmaceutico);
        if (farmaceutico == null) {
            throw new ExcepcionNegocio("El farmaceutico solicitado no existe.");
        }
        return farmaceutico;
    }

    /**
     * Registra un farmaceutico nuevo.
     *
     * @param farmaceutico datos del profesional
     */
    public void registrar(Farmaceutico farmaceutico) {
        validar(farmaceutico);
        verificarRegistroDisponible(farmaceutico.getRegistroProfesional(), 0);
        farmaceuticoDao.insertar(farmaceutico);
    }

    /**
     * Modifica los datos de un farmaceutico existente.
     *
     * @param farmaceutico datos nuevos del profesional
     */
    public void modificar(Farmaceutico farmaceutico) {
        validar(farmaceutico);
        verificarRegistroDisponible(
                farmaceutico.getRegistroProfesional(), farmaceutico.getIdFarmaceutico());
        farmaceuticoDao.actualizar(farmaceutico);
    }

    /**
     * Elimina un farmaceutico del sistema.
     *
     * @param idFarmaceutico identificador del profesional
     */
    public void eliminar(int idFarmaceutico) {
        farmaceuticoDao.eliminar(idFarmaceutico);
    }

    /**
     * Cuenta los farmaceuticos registrados.
     *
     * @return cantidad de farmaceuticos
     */
    public int contar() {
        return farmaceuticoDao.contar();
    }

    private void validar(Farmaceutico farmaceutico) {
        if (ConversorDatos.estaVacio(farmaceutico.getNombre())) {
            throw new ExcepcionNegocio("El nombre del farmaceutico es obligatorio.");
        }
        if (ConversorDatos.estaVacio(farmaceutico.getApellido())) {
            throw new ExcepcionNegocio("El apellido del farmaceutico es obligatorio.");
        }
        if (ConversorDatos.estaVacio(farmaceutico.getRegistroProfesional())) {
            throw new ExcepcionNegocio(
                    "El registro profesional es obligatorio para dispensar medicamentos.");
        }
        if (farmaceutico.getIdUsuario() <= 0) {
            throw new ExcepcionNegocio("Debe asociar el farmaceutico a un usuario del sistema.");
        }
    }

    /**
     * Impide que dos profesionales usen el mismo registro profesional.
     *
     * @param registroProfesional numero de registro a verificar
     * @param idFarmaceuticoActual identificador del profesional que se edita, o 0 si es nuevo
     */
    private void verificarRegistroDisponible(String registroProfesional, int idFarmaceuticoActual) {
        for (Farmaceutico existente : farmaceuticoDao.listar()) {
            boolean mismoRegistro = existente.getRegistroProfesional() != null
                    && existente.getRegistroProfesional().equalsIgnoreCase(registroProfesional.trim());

            if (mismoRegistro && existente.getIdFarmaceutico() != idFarmaceuticoActual) {
                throw new ExcepcionNegocio(
                        "El registro profesional " + registroProfesional + " ya esta asignado.");
            }
        }
    }
}
