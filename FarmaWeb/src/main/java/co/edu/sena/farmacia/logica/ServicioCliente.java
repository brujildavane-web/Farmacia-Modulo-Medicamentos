package co.edu.sena.farmacia.logica;

import java.time.LocalDate;
import java.util.List;

import co.edu.sena.farmacia.modelo.Cliente;
import co.edu.sena.farmacia.persistencia.ClienteDao;
import co.edu.sena.farmacia.util.ConversorDatos;

/**
 * Capa de Logica del modulo de Clientes.
 *
 * Valida los datos obligatorios del cliente y calcula su edad a partir de
 * la fecha de nacimiento para que el dato nunca quede desactualizado.
 */
public class ServicioCliente {

    /** Edad maxima admitida como dato valido de registro. */
    private static final int EDAD_MAXIMA_VALIDA = 120;

    private final ClienteDao clienteDao = new ClienteDao();

    /**
     * Consulta los clientes registrados.
     *
     * @return lista de clientes ordenada por apellido
     */
    public List<Cliente> consultarTodos() {
        return clienteDao.listar();
    }

    /**
     * Busca un cliente por su identificador.
     *
     * @param idCliente identificador del cliente
     * @return el cliente encontrado
     */
    public Cliente consultarPorId(int idCliente) {
        Cliente cliente = clienteDao.buscarPorId(idCliente);
        if (cliente == null) {
            throw new ExcepcionNegocio("El cliente solicitado no existe.");
        }
        return cliente;
    }

    /**
     * Registra un cliente nuevo.
     *
     * @param cliente datos del cliente
     */
    public void registrar(Cliente cliente) {
        validar(cliente);
        sincronizarEdad(cliente);
        clienteDao.insertar(cliente);
    }

    /**
     * Modifica los datos de un cliente existente.
     *
     * @param cliente datos nuevos del cliente
     */
    public void modificar(Cliente cliente) {
        validar(cliente);
        sincronizarEdad(cliente);
        clienteDao.actualizar(cliente);
    }

    /**
     * Elimina un cliente del sistema.
     *
     * @param idCliente identificador del cliente
     */
    public void eliminar(int idCliente) {
        clienteDao.eliminar(idCliente);
    }

    /**
     * Cuenta los clientes registrados.
     *
     * @return cantidad de clientes
     */
    public int contar() {
        return clienteDao.contar();
    }

    private void validar(Cliente cliente) {
        if (ConversorDatos.estaVacio(cliente.getNombre())) {
            throw new ExcepcionNegocio("El nombre del cliente es obligatorio.");
        }
        if (ConversorDatos.estaVacio(cliente.getApellido())) {
            throw new ExcepcionNegocio("El apellido del cliente es obligatorio.");
        }
        if (cliente.getIdUsuario() <= 0) {
            throw new ExcepcionNegocio("Debe asociar el cliente a un usuario del sistema.");
        }
        if (cliente.getEmail() != null && !cliente.getEmail().contains("@")) {
            throw new ExcepcionNegocio("El correo del cliente no tiene un formato valido.");
        }
        if (cliente.getFechaNacimiento() != null
                && cliente.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new ExcepcionNegocio("La fecha de nacimiento no puede ser posterior a hoy.");
        }
    }

    /**
     * Recalcula la edad cuando se conoce la fecha de nacimiento.
     *
     * Si el usuario no registro la fecha, conserva la edad digitada siempre
     * que sea un valor razonable.
     */
    private void sincronizarEdad(Cliente cliente) {
        Integer edadCalculada = cliente.calcularEdadActual();

        if (edadCalculada != null) {
            cliente.setEdad(edadCalculada);
            return;
        }

        if (cliente.getEdad() != null
                && (cliente.getEdad() < 0 || cliente.getEdad() > EDAD_MAXIMA_VALIDA)) {
            throw new ExcepcionNegocio("La edad digitada no es valida.");
        }
    }
}
