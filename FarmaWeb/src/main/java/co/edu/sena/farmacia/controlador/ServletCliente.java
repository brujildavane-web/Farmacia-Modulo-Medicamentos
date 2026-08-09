package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioCliente;
import co.edu.sena.farmacia.logica.ServicioUsuario;
import co.edu.sena.farmacia.modelo.Cliente;
import co.edu.sena.farmacia.util.ConversorDatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del modulo de Clientes (tabla TblCliente).
 */
@WebServlet(name = "ServletCliente", urlPatterns = {"/clientes"})
public class ServletCliente extends ServletBase {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "clientes.jsp";
    private static final String RUTA = "clientes";

    private final transient ServicioCliente servicioCliente = new ServicioCliente();
    private final transient ServicioUsuario servicioUsuario = new ServicioUsuario();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "listar");

        try {
            if ("editar".equals(accion)) {
                peticion.setAttribute("clienteEnEdicion",
                        servicioCliente.consultarPorId(leerEntero(peticion, "id")));

            } else if ("eliminar".equals(accion)) {
                servicioCliente.eliminar(leerEntero(peticion, "id"));
                publicarExito(peticion, "El cliente fue eliminado correctamente.");
                redirigirAlModulo(peticion, respuesta, RUTA);
                return;
            }

            peticion.setAttribute("listaClientes", servicioCliente.consultarTodos());
            peticion.setAttribute("listaUsuarios", servicioUsuario.consultarTodos());

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        reenviarAVista(peticion, respuesta, VISTA);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        try {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(leerEnteroOpcional(peticion, "idCliente"));
            cliente.setNombre(peticion.getParameter("nombre"));
            cliente.setApellido(peticion.getParameter("apellido"));
            cliente.setDireccion(peticion.getParameter("direccion"));
            cliente.setTelefono(peticion.getParameter("telefono"));
            cliente.setEmail(ConversorDatos.limpiar(peticion.getParameter("email")));
            cliente.setFechaNacimiento(ConversorDatos.aFechaOpcional(
                    peticion.getParameter("fechaNacimiento"), "fecha de nacimiento"));
            cliente.setEdad(ConversorDatos.aEnteroOpcional(peticion.getParameter("edad"), "edad"));
            cliente.setIdUsuario(leerEnteroOpcional(peticion, "idUsuario"));

            if (cliente.getIdCliente() > 0) {
                servicioCliente.modificar(cliente);
                publicarExito(peticion, "El cliente fue actualizado correctamente.");
            } else {
                servicioCliente.registrar(cliente);
                publicarExito(peticion, "El cliente fue registrado correctamente.");
            }

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        redirigirAlModulo(peticion, respuesta, RUTA);
    }
}
