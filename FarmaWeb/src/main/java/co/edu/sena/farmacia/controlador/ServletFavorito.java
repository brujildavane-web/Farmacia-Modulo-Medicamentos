package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioCliente;
import co.edu.sena.farmacia.logica.ServicioFavorito;
import co.edu.sena.farmacia.logica.ServicioProducto;
import co.edu.sena.farmacia.modelo.Favorito;
import co.edu.sena.farmacia.util.ConversorDatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del modulo de Favoritos (tabla TblFavorito).
 *
 * Como la llave primaria es compuesta, las acciones de editar y eliminar
 * reciben dos parametros GET: el cliente y el producto.
 */
@WebServlet(name = "ServletFavorito", urlPatterns = {"/favoritos"})
public class ServletFavorito extends ServletBase {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "favoritos.jsp";
    private static final String RUTA = "favoritos";

    private final transient ServicioFavorito servicioFavorito = new ServicioFavorito();
    private final transient ServicioCliente servicioCliente = new ServicioCliente();
    private final transient ServicioProducto servicioProducto = new ServicioProducto();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "listar");

        try {
            if ("editar".equals(accion)) {
                peticion.setAttribute("favoritoEnEdicion", servicioFavorito.consultarPorLlave(
                        leerEntero(peticion, "idCliente"), leerEntero(peticion, "idProducto")));

            } else if ("eliminar".equals(accion)) {
                servicioFavorito.eliminar(
                        leerEntero(peticion, "idCliente"), leerEntero(peticion, "idProducto"));
                publicarExito(peticion, "El producto fue retirado de los favoritos.");
                redirigirAlModulo(peticion, respuesta, RUTA);
                return;
            }

            peticion.setAttribute("listaFavoritos", servicioFavorito.consultarTodos());
            peticion.setAttribute("listaClientes", servicioCliente.consultarTodos());
            peticion.setAttribute("listaProductos", servicioProducto.consultarTodos());

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        reenviarAVista(peticion, respuesta, VISTA);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        try {
            Favorito favorito = new Favorito();
            favorito.setIdCliente(leerEnteroOpcional(peticion, "idCliente"));
            favorito.setIdProducto(leerEnteroOpcional(peticion, "idProducto"));
            favorito.setFechaMarcacion(ConversorDatos.aFechaOpcional(
                    peticion.getParameter("fechaMarcacion"), "fecha de marcacion"));

            boolean esEdicion = "actualizar".equals(leerAccion(peticion, "guardar"));

            if (esEdicion) {
                servicioFavorito.modificar(favorito);
                publicarExito(peticion, "La marcacion de favorito fue actualizada.");
            } else {
                servicioFavorito.registrar(favorito);
                publicarExito(peticion, "El producto fue marcado como favorito.");
            }

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        redirigirAlModulo(peticion, respuesta, RUTA);
    }
}
