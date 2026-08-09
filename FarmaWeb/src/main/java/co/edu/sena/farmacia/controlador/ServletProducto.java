package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioProducto;
import co.edu.sena.farmacia.modelo.Producto;
import co.edu.sena.farmacia.util.ConversorDatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del modulo de Productos y Catalogo (tabla TblProducto).
 *
 * Este es el modulo de medicamentos con el que inicio el proyecto, ahora
 * conectado a la base de datos a traves de las capas de logica y persistencia.
 */
@WebServlet(name = "ServletProducto", urlPatterns = {"/productos"})
public class ServletProducto extends ServletBase {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "productos.jsp";
    private static final String RUTA = "productos";

    private final transient ServicioProducto servicioProducto = new ServicioProducto();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "listar");

        try {
            if ("editar".equals(accion)) {
                peticion.setAttribute("productoEnEdicion",
                        servicioProducto.consultarPorId(leerEntero(peticion, "id")));

            } else if ("eliminar".equals(accion)) {
                servicioProducto.eliminar(leerEntero(peticion, "id"));
                publicarExito(peticion, "El producto fue eliminado correctamente.");
                redirigirAlModulo(peticion, respuesta, RUTA);
                return;

            } else if ("buscarsku".equals(accion)) {
                Producto encontrado = servicioProducto.consultarPorSku(peticion.getParameter("sku"));
                peticion.setAttribute("productoEnEdicion", encontrado);
                publicarExito(peticion, "Producto encontrado: " + encontrado.getNombre() + ".");
            }

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
            Producto producto = new Producto();
            producto.setIdProducto(leerEnteroOpcional(peticion, "idProducto"));
            producto.setNombre(peticion.getParameter("nombre"));
            producto.setSkuCode(ConversorDatos.limpiar(peticion.getParameter("skuCode")));
            producto.setDescripcion(peticion.getParameter("descripcion"));
            producto.setPrecio(ConversorDatos.aDecimal(peticion.getParameter("precio"), "precio"));
            producto.setRequiereReceta(ConversorDatos.aBooleano(peticion.getParameter("requiereReceta")));

            if (producto.getIdProducto() > 0) {
                servicioProducto.modificar(producto);
                publicarExito(peticion, "El producto fue actualizado correctamente.");
            } else {
                servicioProducto.registrar(producto);
                publicarExito(peticion, "El producto fue registrado correctamente en el catalogo.");
            }

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        redirigirAlModulo(peticion, respuesta, RUTA);
    }
}
