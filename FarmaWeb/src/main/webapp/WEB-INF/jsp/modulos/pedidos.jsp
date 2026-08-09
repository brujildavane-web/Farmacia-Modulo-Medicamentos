<%--
    Modulo de Pedidos (tabla TblPedido).

    El total no se digita: lo calcula el sistema sumando las lineas del
    detalle, por eso el campo aparece solo como informacion en la tabla.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Pedidos"/>
<c:set var="moduloActivo" value="pedidos"/>
<c:set var="enEdicion" value="${not empty pedidoEnEdicion}"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Gestion de pedidos</h1>
<p class="descripcion-modulo">
    Encabezado de las compras realizadas por los clientes y atendidas por un farmaceutico.
</p>

<div class="tarjeta">
    <h2>${enEdicion ? 'Editar pedido' : 'Registrar pedido nuevo'}</h2>

    <form action="${pageContext.request.contextPath}/pedidos" method="post">
        <input type="hidden" name="accion" value="guardar">
        <input type="hidden" name="idPedido" value="${pedidoEnEdicion.idPedido}">

        <div class="rejilla-formulario">
            <div class="campo">
                <label for="idCliente">Cliente</label>
                <select id="idCliente" name="idCliente" required>
                    <option value="">Seleccione el cliente</option>
                    <c:forEach var="cliente" items="${listaClientes}">
                        <option value="${cliente.idCliente}"
                                ${pedidoEnEdicion.idCliente eq cliente.idCliente ? 'selected' : ''}>
                            <c:out value="${cliente.nombreCompleto}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="idFarmaceutico">Farmaceutico que atiende</label>
                <select id="idFarmaceutico" name="idFarmaceutico" required>
                    <option value="">Seleccione el farmaceutico</option>
                    <c:forEach var="farmaceutico" items="${listaFarmaceuticos}">
                        <option value="${farmaceutico.idFarmaceutico}"
                                ${pedidoEnEdicion.idFarmaceutico eq farmaceutico.idFarmaceutico ? 'selected' : ''}>
                            <c:out value="${farmaceutico.nombreCompleto}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="estado">Estado del pedido</label>
                <select id="estado" name="estado" required>
                    <c:forEach var="estado" items="${estadosValidos}">
                        <option value="${estado}"
                                ${pedidoEnEdicion.estado eq estado ? 'selected' : ''}>
                            ${estado}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="fecha">Fecha y hora del pedido</label>
                <input type="datetime-local" id="fecha" name="fecha"
                       value="${pedidoEnEdicion.fecha}">
                <span class="ayuda">Si se deja vacia se usa la fecha y hora actual.</span>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit">${enEdicion ? 'Actualizar pedido' : 'Registrar pedido'}</button>
            <c:if test="${enEdicion}">
                <a class="boton boton-secundario"
                   href="${pageContext.request.contextPath}/pedidos">Cancelar edicion</a>
            </c:if>
        </div>
    </form>
</div>

<c:if test="${not empty pedidoConsultado}">
    <div class="tarjeta">
        <h2>
            Detalle del pedido ${pedidoConsultado.idPedido} -
            <c:out value="${pedidoConsultado.nombreCliente}"/>
        </h2>

        <p>
            Estado: <strong>${pedidoConsultado.estado}</strong> |
            Total: <strong>$<fmt:formatNumber value="${pedidoConsultado.total}"
                    type="number" minFractionDigits="2"/></strong> |
            IVA contenido (19%): $<fmt:formatNumber value="${pedidoConsultado.valorImpuesto}"
                    type="number" minFractionDigits="2"/>
        </p>

        <c:choose>
            <c:when test="${empty detalleDelPedido}">
                <p class="sin-registros">
                    Este pedido todavia no tiene productos.
                    <a href="${pageContext.request.contextPath}/lineas?idPedidoFiltro=${pedidoConsultado.idPedido}">
                        Agregar productos al pedido</a>
                </p>
            </c:when>
            <c:otherwise>
                <div class="contenedor-tabla">
                    <table class="tabla-datos">
                        <thead>
                            <tr>
                                <th scope="col">Producto</th>
                                <th scope="col">Cantidad</th>
                                <th scope="col">Precio unitario</th>
                                <th scope="col">Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="linea" items="${detalleDelPedido}">
                                <tr>
                                    <td><c:out value="${linea.nombreProducto}"/></td>
                                    <td>${linea.cantidad}</td>
                                    <td>$<fmt:formatNumber value="${linea.precio}"
                                            type="number" minFractionDigits="2"/></td>
                                    <td>$<fmt:formatNumber value="${linea.subtotal}"
                                            type="number" minFractionDigits="2"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>

<div class="tarjeta">
    <h2>Pedidos registrados</h2>

    <c:choose>
        <c:when test="${empty listaPedidos}">
            <p class="sin-registros">Todavia no hay pedidos registrados.</p>
        </c:when>
        <c:otherwise>
            <div class="contenedor-tabla">
                <table class="tabla-datos">
                    <caption>Total de pedidos: ${listaPedidos.size()}</caption>
                    <thead>
                        <tr>
                            <th scope="col">Pedido</th>
                            <th scope="col">Fecha</th>
                            <th scope="col">Cliente</th>
                            <th scope="col">Farmaceutico</th>
                            <th scope="col">Total</th>
                            <th scope="col">Estado</th>
                            <th scope="col">Cambiar estado</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="pedido" items="${listaPedidos}">
                            <tr>
                                <td>${pedido.idPedido}</td>
                                <td>${pedido.fecha}</td>
                                <td><c:out value="${pedido.nombreCliente}"/></td>
                                <td><c:out value="${pedido.nombreFarmaceutico}"/></td>
                                <td>$<fmt:formatNumber value="${pedido.total}"
                                        type="number" minFractionDigits="2"/></td>
                                <td>
                                    <span class="etiqueta ${pedido.estado eq 'ANULADO'
                                            ? 'etiqueta-agotado' : 'etiqueta-disponible'}">
                                        ${pedido.estado}
                                    </span>
                                </td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/pedidos"
                                          method="post" style="display:flex; gap:6px;">
                                        <input type="hidden" name="accion" value="cambiarEstado">
                                        <input type="hidden" name="idPedido" value="${pedido.idPedido}">
                                        <label class="salto-contenido"
                                               for="estado-${pedido.idPedido}">Estado nuevo</label>
                                        <select id="estado-${pedido.idPedido}" name="estado">
                                            <c:forEach var="estado" items="${estadosValidos}">
                                                <option value="${estado}"
                                                        ${pedido.estado eq estado ? 'selected' : ''}>
                                                    ${estado}
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <button type="submit" class="boton-pequeno">Aplicar</button>
                                    </form>
                                </td>
                                <td>
                                    <div class="columna-acciones">
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/pedidos?accion=detalle&id=${pedido.idPedido}">Ver detalle</a>
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/pedidos?accion=editar&id=${pedido.idPedido}">Editar</a>
                                        <a class="boton boton-peligro boton-pequeno"
                                           href="${pageContext.request.contextPath}/pedidos?accion=eliminar&id=${pedido.idPedido}"
                                           onclick="return confirm('Confirma eliminar el pedido ${pedido.idPedido}?');">Eliminar</a>
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
