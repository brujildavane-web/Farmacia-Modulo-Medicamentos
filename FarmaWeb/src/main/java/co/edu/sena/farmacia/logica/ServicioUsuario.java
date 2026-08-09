package co.edu.sena.farmacia.logica;

import java.time.LocalDate;
import java.util.List;

import co.edu.sena.farmacia.modelo.Usuario;
import co.edu.sena.farmacia.persistencia.UsuarioDao;
import co.edu.sena.farmacia.util.ConversorDatos;
import co.edu.sena.farmacia.util.EncriptadorPassword;

/**
 * Capa de Logica del modulo de Usuarios y Seguridad.
 *
 * Cifra las contrasenas, valida el formato del correo, impide correos
 * repetidos y autentica el ingreso al sistema.
 */
public class ServicioUsuario {

    /** Longitud minima exigida a las contrasenas de los usuarios. */
    private static final int LONGITUD_MINIMA_PASSWORD = 6;

    private final UsuarioDao usuarioDao = new UsuarioDao();

    /**
     * Consulta los usuarios registrados con el nombre de su rol.
     *
     * @return lista de usuarios ordenada por correo
     */
    public List<Usuario> consultarTodos() {
        return usuarioDao.listar();
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param idUsuario identificador del usuario
     * @return el usuario encontrado
     */
    public Usuario consultarPorId(int idUsuario) {
        Usuario usuario = usuarioDao.buscarPorId(idUsuario);
        if (usuario == null) {
            throw new ExcepcionNegocio("El usuario solicitado no existe.");
        }
        return usuario;
    }

    /**
     * Verifica las credenciales digitadas en el formulario de ingreso.
     *
     * @param email correo del usuario
     * @param passwordPlano contrasena sin cifrar
     * @return el usuario autenticado
     */
    public Usuario autenticar(String email, String passwordPlano) {
        if (ConversorDatos.estaVacio(email) || ConversorDatos.estaVacio(passwordPlano)) {
            throw new ExcepcionNegocio("Debe digitar su correo y su contrasena.");
        }

        Usuario usuario = usuarioDao.buscarPorEmail(email.trim());
        if (usuario == null || !EncriptadorPassword.coincide(passwordPlano, usuario.getPassword())) {
            throw new ExcepcionNegocio("Las credenciales digitadas no son correctas.");
        }

        return usuario;
    }

    /**
     * Registra un usuario nuevo cifrando su contrasena.
     *
     * @param usuario datos del usuario
     * @param passwordPlano contrasena escogida por el usuario
     */
    public void registrar(Usuario usuario, String passwordPlano) {
        validar(usuario);
        validarPassword(passwordPlano);
        verificarEmailDisponible(usuario.getEmail(), 0);

        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(LocalDate.now());
        }

        usuario.setPassword(EncriptadorPassword.generarHash(passwordPlano));
        usuarioDao.insertar(usuario);
    }

    /**
     * Modifica los datos de un usuario sin alterar su contrasena.
     *
     * @param usuario datos nuevos del usuario
     */
    public void modificar(Usuario usuario) {
        validar(usuario);
        verificarEmailDisponible(usuario.getEmail(), usuario.getIdUsuario());

        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(consultarPorId(usuario.getIdUsuario()).getFechaRegistro());
        }

        usuarioDao.actualizar(usuario);
    }

    /**
     * Cambia la contrasena de un usuario.
     *
     * @param idUsuario identificador del usuario
     * @param passwordPlano contrasena nueva sin cifrar
     */
    public void cambiarPassword(int idUsuario, String passwordPlano) {
        validarPassword(passwordPlano);
        usuarioDao.actualizarPassword(idUsuario, EncriptadorPassword.generarHash(passwordPlano));
    }

    /**
     * Elimina un usuario del sistema.
     *
     * @param idUsuario identificador del usuario
     */
    public void eliminar(int idUsuario) {
        usuarioDao.eliminar(idUsuario);
    }

    /**
     * Cuenta los usuarios registrados.
     *
     * @return cantidad de usuarios
     */
    public int contar() {
        return usuarioDao.contar();
    }

    private void validar(Usuario usuario) {
        if (ConversorDatos.estaVacio(usuario.getEmail())) {
            throw new ExcepcionNegocio("El correo electronico es obligatorio.");
        }
        if (!usuario.getEmail().contains("@") || !usuario.getEmail().contains(".")) {
            throw new ExcepcionNegocio("El correo electronico no tiene un formato valido.");
        }
        if (usuario.getIdRol() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el rol del usuario.");
        }
    }

    private void validarPassword(String passwordPlano) {
        if (ConversorDatos.estaVacio(passwordPlano)) {
            throw new ExcepcionNegocio("La contrasena es obligatoria.");
        }
        if (passwordPlano.trim().length() < LONGITUD_MINIMA_PASSWORD) {
            throw new ExcepcionNegocio(
                    "La contrasena debe tener al menos " + LONGITUD_MINIMA_PASSWORD + " caracteres.");
        }
    }

    /**
     * Impide registrar dos usuarios con el mismo correo electronico.
     *
     * @param email correo que se quiere usar
     * @param idUsuarioActual identificador del usuario que se edita, o 0 si es nuevo
     */
    private void verificarEmailDisponible(String email, int idUsuarioActual) {
        Usuario existente = usuarioDao.buscarPorEmail(email.trim());
        if (existente != null && existente.getIdUsuario() != idUsuarioActual) {
            throw new ExcepcionNegocio("El correo " + email + " ya esta registrado en el sistema.");
        }
    }
}
