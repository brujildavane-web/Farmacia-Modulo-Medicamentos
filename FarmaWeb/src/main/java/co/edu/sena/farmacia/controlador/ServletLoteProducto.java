package co.edu.sena.farmacia.controlador;

import java.io.IOException;

import co.edu.sena.farmacia.logica.ServicioLoteProducto;
import co.edu.sena.farmacia.logica.ServicioProducto;
import co.edu.sena.farmacia.modelo.LoteProducto;
import co.edu.sena.farmacia.util.ConversorDatos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador del modulo de Inventario por lotes (tabla TblLoteProducto).
 *
 * Permite filtrar los lotes por producto usando un parametro GET y descontar
 * stock mediante el metodo POST.
 */
@WebServlet(name = "ServletLoteProducto", urlPatterns = {"/lotes"})
public class ServletLoteProducto extends ServletBase {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "lotes.jsp";
    private static final String RUTA = "lotes";

    private final transient ServicioLoteProducto servicioLote = new ServicioLoteProducto();
    private final transient ServicioProducto servicioProducto = new ServicioProducto();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "listar");

        try {
            if ("editar".equals(accion)) {
                peticion.setAttribute("loteEnEdicion",
                        servicioLote.consultarPorId(leerEntero(peticion, "id")));

            } else if ("eliminar".equals(accion)) {
                servicioLote.eliminar(leerEntero(peticion, "id"));
                publicarExito(peticion, "El lote fue eliminado del inventario.");
                redirigirAlModulo(peticion, respuesta, RUTA);
                return;
            }

            cargarListaDeLotes(peticion);
            peticion.setAttribute("listaProductos", servicioProducto.consultarTodos());

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        reenviarAVista(peticion, respuesta, VISTA);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = leerAccion(peticion, "guardar");

        try {
            if ("descontar".equals(accion)) {
                servicioLote.descontarStock(leerEntero(peticion, "idLote"),
                        ConversorDatos.aDouble(peticion.getParameter("cantidad"), "cantidad"));
                publicarExito(peticion, "El stock del lote fue descontado correctamente.");

            } else {
                guardarLote(peticion);
            }

        } catch (RuntimeException error) {
            publicarErrorDeExcepcion(peticion, error);
        }

        redirigirAlModulo(peticion, respuesta, RUTA);
    }

    /**
     * Carga todos los lotes o solo los de un producto, segun el filtro GET.
     *
     * @param peticion peticion HTTP en curso
     */
    private void cargarListaDeLotes(HttpServletRequest peticion) {
        int idProductoFiltro = leerEnteroOpcional(peticion, "idProductoFiltro");

        if (idProductoFiltro > 0) {
            peticion.setAttribute("listaLotes", servicioLote.consultarPorProducto(idProductoFiltro));
            peticion.setAttribute("idProductoFiltro", idProductoFiltro);
        } else {
            peticion.setAttribute("listaLotes", servicioLote.consultarTodos());
        }
    }

    /**
     * Arma el lote con los parametros del formulario y lo guarda.
     *
     * @param peticion peticion HTTP con los datos enviados por POST
     */
    private void guardarLote(HttpServletRequest peticion) {
        LoteProducto lote = new LoteProducto();
        lote.setIdLote(leerEnteroOpcional(peticion, "idLote"));
        lote.setFechaVencimiento(ConversorDatos.aFecha(
                peticion.getParameter("fechaVencimiento"), "fecha de vencimiento"));
        lote.setRegistroSanitario(peticion.getParameter("registroSanitario"));
        lote.setStockActual(ConversorDatos.aDouble(peticion.getParameter("stockActual"), "stock"));
        lote.setMarca(peticion.getParameter("marca"));
        lote.setIdProducto(leerEnteroOpcional(peticion, "idProducto"));

        if (lote.getIdLote() > 0) {
            servicioLote.modificar(lote);
            publicarExito(peticion, "El lote fue actualizado correctamente.");
        } else {
            servicioLote.registrar(lote);
            publicarExito(peticion, "El lote fue registrado en el inventario.");
        }
    }
}
