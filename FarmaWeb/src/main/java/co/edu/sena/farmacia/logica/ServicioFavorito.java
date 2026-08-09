package co.edu.sena.farmacia.logica;

import java.time.LocalDate;
import java.util.List;

import co.edu.sena.farmacia.modelo.Favorito;
import co.edu.sena.farmacia.persistencia.FavoritoDao;

/**
 * Capa de Logica del modulo de Favoritos.
 *
 * Permite que un cliente marque productos para futuras compras evitando
 * marcaciones duplicadas del mismo producto.
 */
public class ServicioFavorito {

    private final FavoritoDao favoritoDao = new FavoritoDao();

    /**
     * Consulta todas las marcaciones registradas.
     *
     * @return lista de favoritos
     */
    public List<Favorito> consultarTodos() {
        return favoritoDao.listar();
    }

    /**
     * Busca una marcacion por su llave compuesta.
     *
     * @param idCliente identificador del cliente
     * @param idProducto identificador del producto
     * @return la marcacion encontrada
     */
    public Favorito consultarPorLlave(int idCliente, int idProducto) {
        Favorito favorito = favoritoDao.buscarPorLlave(idCliente, idProducto);
        if (favorito == null) {
            throw new ExcepcionNegocio("La marcacion de favorito solicitada no existe.");
        }
        return favorito;
    }

    /**
     * Marca un producto como favorito de un cliente.
     *
     * @param favorito datos de la marcacion
     */
    public void registrar(Favorito favorito) {
        validar(favorito);

        if (favoritoDao.buscarPorLlave(favorito.getIdCliente(), favorito.getIdProducto()) != null) {
            throw new ExcepcionNegocio("El cliente ya tiene este producto marcado como favorito.");
        }

        if (favorito.getFechaMarcacion() == null) {
            favorito.setFechaMarcacion(LocalDate.now());
        }

        favoritoDao.insertar(favorito);
    }

    /**
     * Actualiza la fecha de una marcacion existente.
     *
     * @param favorito datos de la marcacion
     */
    public void modificar(Favorito favorito) {
        validar(favorito);

        if (favorito.getFechaMarcacion() == null) {
            favorito.setFechaMarcacion(LocalDate.now());
        }

        favoritoDao.actualizar(favorito);
    }

    /**
     * Quita un producto de los favoritos de un cliente.
     *
     * @param idCliente identificador del cliente
     * @param idProducto identificador del producto
     */
    public void eliminar(int idCliente, int idProducto) {
        favoritoDao.eliminar(idCliente, idProducto);
    }

    /**
     * Cuenta las marcaciones de favoritos.
     *
     * @return cantidad de favoritos
     */
    public int contar() {
        return favoritoDao.contar();
    }

    private void validar(Favorito favorito) {
        if (favorito.getIdCliente() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el cliente.");
        }
        if (favorito.getIdProducto() <= 0) {
            throw new ExcepcionNegocio("Debe seleccionar el producto.");
        }
        if (favorito.getFechaMarcacion() != null
                && favorito.getFechaMarcacion().isAfter(LocalDate.now())) {
            throw new ExcepcionNegocio("La fecha de marcacion no puede ser posterior a hoy.");
        }
    }
}
