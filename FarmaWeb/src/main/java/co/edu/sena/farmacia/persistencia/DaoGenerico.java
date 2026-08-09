package co.edu.sena.farmacia.persistencia;

import java.util.List;

/**
 * Contrato comun de las operaciones CRUD de la capa de Persistencia.
 *
 * Todos los modulos del sistema realizan las mismas cinco operaciones sobre
 * su tabla, por lo que esta interfaz generica evita repetir la definicion
 * en cada DAO y permite tratar los modulos de forma uniforme.
 *
 * @param <T> clase del modelo que administra el DAO
 */
public interface DaoGenerico<T> {

    /**
     * Consulta todos los registros de la tabla.
     *
     * @return lista ordenada de registros, vacia si la tabla no tiene datos
     */
    List<T> listar();

    /**
     * Busca un registro por su llave primaria.
     *
     * @param identificador llave primaria del registro
     * @return el registro encontrado, o null si no existe
     */
    T buscarPorId(int identificador);

    /**
     * Guarda un registro nuevo en la tabla.
     *
     * @param entidad datos que se van a insertar
     */
    void insertar(T entidad);

    /**
     * Actualiza los datos de un registro existente.
     *
     * @param entidad datos nuevos, incluida la llave primaria
     */
    void actualizar(T entidad);

    /**
     * Borra un registro de la tabla.
     *
     * @param identificador llave primaria del registro a borrar
     */
    void eliminar(int identificador);

    /**
     * Cuenta los registros existentes en la tabla.
     *
     * @return cantidad total de registros
     */
    int contar();
}
