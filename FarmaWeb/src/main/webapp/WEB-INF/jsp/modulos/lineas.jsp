<%--
    Modulo de Detalle de Pedidos (tabla TblLineaPedido).

    El precio y el subtotal los calcula la capa de logica con el precio
    vigente del catalogo, por eso el formulario solo pide la cantidad.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Detalle de pedidos"/>
<c:set var="moduloActivo" value="lineas"/>
<c:set var="enEdicion" value="${not empty lineaEnEdicion}"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Detalle de los pedidos</h1>
<p class="descripcion-modulo">
    Productos incluidos en cada pedido. Al guardar, el sistema valida el stock,
    exige formula medica si el producto es controlado y recalcula el total.
</p>

<div class="tarjeta">
    <h2>Filtrar el detalle por pedido</h2>

    <form action="${pageContext.request.contextPath}/lineas" method="get">
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
               href="${pageContext.request.contextPath}/lineas">Quitar filtro</a>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>${enEdicion ? 'Editar linea del pedido' : 'Agregar producto a un pedido'}</h2>

    <form action="${pageContext.request.contextPath}/lineas" method="post">
        <input type="hidden" name="idLinea" value="${lineaEnEdicion.idLinea}">

        <div class="rejilla-formulario">
            <div class="campo">
                <label for="idPedido">Pedido</label>
                <select id="idPedido" name="idPedido" required>
                    <option value="">Seleccione el pedido</option>
                    <c:forEach var="pedido" items="${listaPedidos}">
                        <c:set var="pedidoSeleccionado"
                               value="${enEdicion ? lineaEnEdicion.idPedido : idPedidoFiltro}"/>
                        <option value="${pedido.idPedido}"
                                ${pedidoSeleccionado eq pedido.idPedido ? 'selected' : ''}>
                            Pedido ${pedido.idPedido} - <c:out value="${pedido.nombreCliente}"/>
                            (${pedido.estado})
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
                                ${lineaEnEdicion.idProducto eq producto.idProducto ? 'selected' : ''}>
                            <c:out value="${producto.nombre}"/> -
                            stock ${producto.stockTotal}
                            <c:if test="${producto.requiereReceta}">(requiere receta)</c:if>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="cantidad">Cantidad</label>
                <input type="number" id="cantidad" name="cantidad" required min="1" step="1"
                       value="${lineaEnEdicion.cantidad}" placeholder="1">
                <span class="ayuda">
                    El precio unitario y el subtotal los calcula el sistema.
                </span>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit">${enEdicion ? 'Actualizar linea' : 'Agregar al pedido'}</button>
            <c:if test="${enEdicion}">
                <a class="boton boton-secundario"
                   href="${pageContext.request.contextPath}/lineas">Cancelar edicion</a>
            </c:if>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>Lineas registradas</h2>

    <c:choose>
        <c:when test="${empty listaLineas}">
            <p class="sin-registros">No hay lineas registradas con el criterio seleccionado.</p>
        </c:when>
        <c:otherwise>
            <div class="contenedor-tabla">
                <table class="tabla-datos">
                    <caption>Total de lineas: ${listaLineas.size()}</caption>
                    <thead>
                        <tr>
                            <th scope="col">Linea</th>
                            <th scope="col">Pedido</th>
                            <th scope="col">Producto</th>
                            <th scope="col">Cantidad</th>
                            <th scope="col">Precio unitario</th>
                            <th scope="col">Subtotal</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="linea" items="${listaLineas}">
                            <tr>
                                <td>${linea.idLinea}</td>
                                <td>${linea.idPedido}</td>
                                <td><c:out value="${linea.nombreProducto}"/></td>
                                <td>${linea.cantidad}</td>
                                <td>$<fmt:formatNumber value="${linea.precio}"
                                        type="number" minFractionDigits="2"/></td>
                                <td>$<fmt:formatNumber value="${linea.subtotal}"
                                        type="number" minFractionDigits="2"/></td>
                                <td>
                                    <div class="columna-acciones">
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/lineas?accion=editar&id=${linea.idLinea}">Editar</a>
                                        <a class="boton boton-peligro boton-pequeno"
                                           href="${pageContext.request.contextPath}/lineas?accion=eliminar&id=${linea.idLinea}"
                                           onclick="return confirm('Confirma eliminar esta linea del pedido?');">Eliminar</a>
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
