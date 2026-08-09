<%--
    Modulo de Historial de Pedidos (tabla TblHistorial).

    Bitacora de estados que permite rastrear un pedido y auditar las
    acciones realizadas sobre cada producto.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Historial"/>
<c:set var="moduloActivo" value="historial"/>
<c:set var="enEdicion" value="${not empty historialEnEdicion}"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Historial y rastreo de pedidos</h1>
<p class="descripcion-modulo">
    Traza de los estados por los que pasa cada producto dentro de un pedido.
</p>

<div class="tarjeta">
    <h2>Filtrar el historial por pedido</h2>

    <form action="${pageContext.request.contextPath}/historial" method="get">
        <div class="rejilla-formulario">
            <div class="campo">
                <label for="idPedidoFiltro">Pedido</label>
                <select id="idPedidoFiltro" name="idPedidoFiltro">
                    <option value="">Todos los pedidos</option>
                    <c:forEach var="pedido" items="${listaPedidos}">
                        <option value="${pedido.idPedido}"
                                ${idPedidoFiltro eq pedido.idPedido ? 'selected' : ''}>
                            Pedido ${pedido.idPedido} - <c:out value="${pedido.nombreCliente}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit" class="boton-secundario">Aplicar filtro</button>
            <a class="boton boton-secundario"
               href="${pageContext.request.contextPath}/historial">Quitar filtro</a>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>${enEdicion ? 'Editar registro del historial' : 'Agregar registro al historial'}</h2>

    <form action="${pageContext.request.contextPath}/historial" method="post">
        <input type="hidden" name="idPedidoHistorico" value="${historialEnEdicion.idPedidoHistorico}">

        <div class="rejilla-formulario">
            <div class="campo">
                <label for="idPedido">Pedido</label>
                <select id="idPedido" name="idPedido" required>
                    <option value="">Seleccione el pedido</option>
                    <c:forEach var="pedido" items="${listaPedidos}">
                        <c:set var="pedidoSeleccionado"
                               value="${enEdicion ? historialEnEdicion.idPedido : idPedidoFiltro}"/>
                        <option value="${pedido.idPedido}"
                                ${pedidoSeleccionado eq pedido.idPedido ? 'selected' : ''}>
                            Pedido ${pedido.idPedido} - <c:out value="${pedido.nombreCliente}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="idProducto">Producto</label>
                <select id="idProducto" name="idProducto" required>
                    <option value="">Seleccione el producto</option>
                    <c:forEach var="producto" items="${listaProductos}">
                        <option value="${producto.idProducto}"
                                ${historialEnEdicion.idProducto eq producto.idProducto ? 'selected' : ''}>
                            <c:out value="${producto.nombre}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="estado">Estado registrado</label>
                <select id="estado" name="estado" required>
                    <c:forEach var="estado" items="${estadosValidos}">
                        <option value="${estado}"
                                ${historialEnEdicion.estado eq estado ? 'selected' : ''}>
                            ${estado}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="fecha">Fecha del registro</label>
                <input type="date" id="fecha" name="fecha" value="${historialEnEdicion.fecha}">
                <span class="ayuda">Si se deja vacia se usa la fecha de hoy.</span>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit">${enEdicion ? 'Actualizar registro' : 'Agregar al historial'}</button>
            <c:if test="${enEdicion}">
                <a class="boton boton-secundario"
                   href="${pageContext.request.contextPath}/historial">Cancelar edicion</a>
            </c:if>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>Registros del historial</h2>

    <c:choose>
        <c:when test="${empty listaHistorial}">
            <p class="sin-registros">No hay registros de historial con el criterio seleccionado.</p>
        </c:when>
        <c:otherwise>
            <div class="contenedor-tabla">
                <table class="tabla-datos">
                    <caption>Total de registros: ${listaHistorial.size()}</caption>
                    <thead>
                        <tr>
                            <th scope="col">Registro</th>
                            <th scope="col">Pedido</th>
                            <th scope="col">Cliente</th>
                            <th scope="col">Producto</th>
                            <th scope="col">Fecha</th>
                            <th scope="col">Estado</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="registro" items="${listaHistorial}">
                            <tr>
                                <td>${registro.idPedidoHistorico}</td>
                                <td>${registro.idPedido}</td>
                                <td><c:out value="${registro.nombreCliente}"/></td>
                                <td><c:out value="${registro.nombreProducto}"/></td>
                                <td>${registro.fecha}</td>
                                <td>
                                    <span class="etiqueta ${registro.estado eq 'ANULADO'
                                            ? 'etiqueta-agotado' : 'etiqueta-disponible'}">
                                        <c:out value="${registro.estado}"/>
                                    </span>
                                </td>
                                <td>
                                    <div class="columna-acciones">
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/historial?accion=editar&id=${registro.idPedidoHistorico}">Editar</a>
                                        <a class="boton boton-peligro boton-pequeno"
                                           href="${pageContext.request.contextPath}/historial?accion=eliminar&id=${registro.idPedidoHistorico}"
                                           onclick="return confirm('Confirma eliminar este registro del historial?');">Eliminar</a>
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
