package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioFarmaceutico;
import co.edu.sena.farmacia.logica.ServicioUsuario;
import co.edu.sena.farmacia.modelo.Farmaceutico;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del modulo de Farmaceuticos (tabla TblFarmaceutico).
 */
@WebServlet(name = "ServletFarmaceutico", urlPatterns = {"/farmaceuticos"})
public class ServletFarmaceutico extends ServletBase {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "farmaceuticos.jsp";
    private static final String RUTA = "farmaceuticos";

    private final transient ServicioFarmaceutico servicioFarmaceutico = new ServicioFarmaceutico();
    private final transient ServicioUsuario servicioUsuario = new ServicioUsuario();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "listar");

        try {
            if ("editar".equals(accion)) {
                peticion.setAttribute("farmaceuticoEnEdicion",
                        servicioFarmaceutico.consultarPorId(leerEntero(peticion, "id")));

            } else if ("eliminar".equals(accion)) {
                servicioFarmaceutico.eliminar(leerEntero(peticion, "id"));
                publicarExito(peticion, "El farmaceutico fue eliminado correctamente.");
                redirigirAlModulo(peticion, respuesta, RUTA);
                return;
            }

            peticion.setAttribute("listaFarmaceuticos", servicioFarmaceutico.consultarTodos());
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
            Farmaceutico farmaceutico = new Farmaceutico();
            farmaceutico.setIdFarmaceutico(leerEnteroOpcional(peticion, "idFarmaceutico"));
            farmaceutico.setRegistroProfesional(peticion.getParameter("registroProfesional"));
            farmaceutico.setEspecialidad(peticion.getParameter("especialidad"));
            farmaceutico.setNombre(peticion.getParameter("nombre"));
            farmaceutico.setApellido(peticion.getParameter("apellido"));
            farmaceutico.setTelefono(peticion.getParameter("telefono"));
            farmaceutico.setIdUsuario(leerEnteroOpcional(peticion, "idUsuario"));

            if (farmaceutico.getIdFarmaceutico() > 0) {
                servicioFarmaceutico.modificar(farmaceutico);
                publicarExito(peticion, "El farmaceutico fue actualizado correctamente.");
            } else {
                servicioFarmaceutico.registrar(farmaceutico);
                publicarExito(peticion, "El farmaceutico fue registrado correctamente.");
            }

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        redirigirAlModulo(peticion, respuesta, RUTA);
    }
}
