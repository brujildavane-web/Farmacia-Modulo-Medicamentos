<%--
    Modulo de Favoritos (tabla TblFavorito).

    Al tener llave primaria compuesta, los enlaces de la tabla envian por GET
    los dos identificadores que conforman la llave.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Favoritos"/>
<c:set var="moduloActivo" value="favoritos"/>
<c:set var="enEdicion" value="${not empty favoritoEnEdicion}"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Productos favoritos de los clientes</h1>
<p class="descripcion-modulo">
    Marcaciones que hacen los clientes para repetir sus compras con rapidez.
</p>

<div class="tarjeta">
    <h2>${enEdicion ? 'Editar marcacion' : 'Marcar producto como favorito'}</h2>

    <form action="${pageContext.request.contextPath}/favoritos" method="post">
        <input type="hidden" name="accion" value="${enEdicion ? 'actualizar' : 'guardar'}">

        <div class="rejilla-formulario">
            <%--
                En modo edicion la llave compuesta no se puede cambiar: se envia
                en campos ocultos y los selectores quedan deshabilitados para
                que el usuario vea el dato sin poder alterarlo.
            --%>
            <c:if test="${enEdicion}">
                <input type="hidden" name="idCliente" value="${favoritoEnEdicion.idCliente}">
                <input type="hidden" name="idProducto" value="${favoritoEnEdicion.idProducto}">
            </c:if>

            <div class="campo">
                <label for="idCliente">Cliente</label>
                <select id="idCliente" name="${enEdicion ? 'clienteVisible' : 'idCliente'}"
                        ${enEdicion ? 'disabled' : 'required'}>
                    <option value="">Seleccione el cliente</option>
                    <c:forEach var="cliente" items="${listaClientes}">
                        <option value="${cliente.idCliente}"
                                ${favoritoEnEdicion.idCliente eq cliente.idCliente ? 'selected' : ''}>
                            <c:out value="${cliente.nombreCompleto}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="idProducto">Producto</label>
                <select id="idProducto" name="${enEdicion ? 'productoVisible' : 'idProducto'}"
                        ${enEdicion ? 'disabled' : 'required'}>
                    <option value="">Seleccione el producto</option>
                    <c:forEach var="producto" items="${listaProductos}">
                        <option value="${producto.idProducto}"
                                ${favoritoEnEdicion.idProducto eq producto.idProducto ? 'selected' : ''}>
                            <c:out value="${producto.nombre}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="fechaMarcacion">Fecha de marcacion</label>
                <input type="date" id="fechaMarcacion" name="fechaMarcacion"
                       value="${favoritoEnEdicion.fechaMarcacion}">
                <span class="ayuda">Si se deja vacia se usa la fecha de hoy.</span>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit">${enEdicion ? 'Actualizar marcacion' : 'Marcar como favorito'}</button>
            <c:if test="${enEdicion}">
                <a class="boton boton-secundario"
                   href="${pageContext.request.contextPath}/favoritos">Cancelar edicion</a>
            </c:if>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>Favoritos registrados</h2>

    <c:choose>
        <c:when test="${empty listaFavoritos}">
            <p class="sin-registros">Todavia no hay productos marcados como favoritos.</p>
        </c:when>
        <c:otherwise>
            <div class="contenedor-tabla">
                <table class="tabla-datos">
                    <caption>Total de marcaciones: ${listaFavoritos.size()}</caption>
                    <thead>
                        <tr>
                            <th scope="col">Cliente</th>
                            <th scope="col">Producto</th>
                            <th scope="col">Fecha de marcacion</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="favorito" items="${listaFavoritos}">
                            <tr>
                                <td><c:out value="${favorito.nombreCliente}"/></td>
                                <td><c:out value="${favorito.nombreProducto}"/></td>
                                <td>${favorito.fechaMarcacion}</td>
                                <td>
                                    <div class="columna-acciones">
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/favoritos?accion=editar&idCliente=${favorito.idCliente}&idProducto=${favorito.idProducto}">Editar</a>
                                        <a class="boton boton-peligro boton-pequeno"
                                           href="${pageContext.request.contextPath}/favoritos?accion=eliminar&idCliente=${favorito.idCliente}&idProducto=${favorito.idProducto}"
                                           onclick="return confirm('Confirma quitar este producto de los favoritos?');">Quitar</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/jsp/comunes/pie.jsp" %>
