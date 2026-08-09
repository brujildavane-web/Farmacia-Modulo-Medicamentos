package co.edu.sena.farmacia.logica;

import java.util.List;

import co.edu.sena.farmacia.modelo.Rol;
import co.edu.sena.farmacia.persistencia.RolDao;
import co.edu.sena.farmacia.util.ConversorDatos;

/**
 * Capa de Logica del modulo de Roles.
 *
 * Valida los datos del perfil de acceso antes de enviarlos a la base de datos
 * y garantiza que no se repitan nombres de rol.
 */
public class ServicioRol {

    private final RolDao rolDao = new RolDao();

    /**
     * Consulta los roles definidos en el sistema.
     *
     * @return lista de roles ordenada por nombre
     */
    public List<Rol> consultarTodos() {
        return rolDao.listar();
    }

    /**
     * Busca un rol por su identificador.
     *
     * @param idRol identificador del rol
     * @return el rol encontrado
     */
    public Rol consultarPorId(int idRol) {
        Rol rol = rolDao.buscarPorId(idRol);
        if (rol == null) {
            throw new ExcepcionNegocio("El rol solicitado no existe.");
        }
        return rol;
    }

    /**
     * Registra un rol nuevo despues de validar sus datos.
     *
     * @param rol datos del rol
     */
    public void registrar(Rol rol) {
        validar(rol);
        verificarNombreDisponible(rol.getNombreRol(), 0);
        rolDao.insertar(rol);
    }

    /**
     * Modifica un rol existente.
     *
     * @param rol datos nuevos del rol, incluido su identificador
     */
    public void modificar(Rol rol) {
        validar(rol);
        verificarNombreDisponible(rol.getNombreRol(), rol.getIdRol());
        rolDao.actualizar(rol);
    }

    /**
     * Elimina un rol del sistema.
     *
     * @param idRol identificador del rol
     */
    public void eliminar(int idRol) {
        rolDao.eliminar(idRol);
    }

    /**
     * Cuenta los roles registrados.
     *
     * @return cantidad de roles
     */
    public int contar() {
        return rolDao.contar();
    }

    /**
     * Comprueba que los campos obligatorios del rol vengan diligenciados.
     */
    private void validar(Rol rol) {
        if (ConversorDatos.estaVacio(rol.getNombreRol())) {
            throw new ExcepcionNegocio("El nombre del rol es obligatorio.");
        }
        if (ConversorDatos.estaVacio(rol.getPermisos())) {
            throw new ExcepcionNegocio("Debe describir los permisos del rol.");
        }
    }

    /**
     * Evita crear dos roles con el mismo nombre.
     *
     * @param nombreRol nombre que se quiere usar
     * @param idRolActual identificador del rol que se edita, o 0 si es nuevo
     */
    private void verificarNombreDisponible(String nombreRol, int idRolActual) {
        for (Rol existente : rolDao.listar()) {
            boolean mismoNombre = existente.getNombreRol().equalsIgnoreCase(nombreRol.trim());
            if (mismoNombre && existente.getIdRol() != idRolActual) {
                throw new ExcepcionNegocio("Ya existe un rol registrado con el nombre " + nombreRol + ".");
            }
        }
    }
}
