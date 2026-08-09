<%--
    Modulo de Transacciones de Pago (tabla TblTransaccionPago).

    El sistema avisa si se intenta aprobar dos veces el pago del mismo pedido,
    como control basico de deteccion de fraude.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Transacciones de pago"/>
<c:set var="moduloActivo" value="transacciones"/>
<c:set var="enEdicion" value="${not empty transaccionEnEdicion}"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Transacciones de pago</h1>
<p class="descripcion-modulo">
    Registro de los pagos procesados por las pasarelas para cada pedido.
</p>

<div class="tarjeta">
    <h2>Filtrar transacciones por pedido</h2>

    <form action="${pageContext.request.contextPath}/transacciones" method="get">
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
               href="${pageContext.request.contextPath}/transacciones">Quitar filtro</a>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>${enEdicion ? 'Editar transaccion' : 'Registrar pago de un pedido'}</h2>

    <form action="${pageContext.request.contextPath}/transacciones" method="post">
        <input type="hidden" name="idTransaccion" value="${transaccionEnEdicion.idTransaccion}">

        <div class="rejilla-formulario">
            <div class="campo">
                <label for="idPedido">Pedido</label>
                <select id="idPedido" name="idPedido" required>
                    <option value="">Seleccione el pedido</option>
                    <c:forEach var="pedido" items="${listaPedidos}">
                        <c:set var="pedidoSeleccionado"
                               value="${enEdicion ? transaccionEnEdicion.idPedido : idPedidoFiltro}"/>
                        <option value="${pedido.idPedido}"
                                ${pedidoSeleccionado eq pedido.idPedido ? 'selected' : ''}>
                            Pedido ${pedido.idPedido} - <c:out value="${pedido.nombreCliente}"/>
                            (total $<fmt:formatNumber value="${pedido.total}"
                                    type="number" minFractionDigits="2"/>)
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="idPasarela">Pasarela utilizada</label>
                <select id="idPasarela" name="idPasarela" required>
                    <option value="">Seleccione la pasarela</option>
                    <c:forEach var="pasarela" items="${listaPasarelas}">
                        <option value="${pasarela.idPasarela}"
                                ${transaccionEnEdicion.idPasarela eq pasarela.idPasarela ? 'selected' : ''}>
                            <c:out value="${pasarela.nombreProveedor}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="valor">Valor pagado</label>
                <input type="number" id="valor" name="valor" required min="1" step="0.01"
                       value="${transaccionEnEdicion.valor}" placeholder="0.00">
            </div>

            <div class="campo">
                <label for="estadoTransaccion">Estado de la transaccion</label>
                <select id="estadoTransaccion" name="estadoTransaccion" required>
                    <c:forEach var="estado" items="${estadosValidos}">
                        <option value="${estado}"
                                ${transaccionEnEdicion.estadoTransaccion eq estado ? 'selected' : ''}>
                            ${estado}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="fechaPago">Fecha y hora del pago</label>
                <input type="datetime-local" id="fechaPago" name="fechaPago"
                       value="${transaccionEnEdicion.fechaPago}">
                <span class="ayuda">Si se deja vacia se usa la fecha y hora actual.</span>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit">${enEdicion ? 'Actualizar transaccion' : 'Registrar pago'}</button>
            <c:if test="${enEdicion}">
                <a class="boton boton-secundario"
                   href="${pageContext.request.contextPath}/transacciones">Cancelar edicion</a>
            </c:if>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>Transacciones registradas</h2>

    <c:choose>
        <c:when test="${empty listaTransacciones}">
            <p class="sin-registros">No hay transacciones con el criterio seleccionado.</p>
        </c:when>
        <c:otherwise>
            <div class="contenedor-tabla">
                <table class="tabla-datos">
                    <caption>Total de transacciones: ${listaTransacciones.size()}</caption>
                    <thead>
                        <tr>
                            <th scope="col">Transaccion</th>
                            <th scope="col">Pedido</th>
                            <th scope="col">Cliente</th>
                            <th scope="col">Pasarela</th>
                            <th scope="col">Valor</th>
                            <th scope="col">Fecha de pago</th>
                            <th scope="col">Estado</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="transaccion" items="${listaTransacciones}">
                            <tr>
                                <td>${transaccion.idTransaccion}</td>
                                <td>${transaccion.idPedido}</td>
                                <td><c:out value="${transaccion.nombreCliente}"/></td>
                                <td><c:out value="${transaccion.nombreProveedor}"/></td>
                                <td>$<fmt:formatNumber value="${transaccion.valor}"
                                        type="number" minFractionDigits="2"/></td>
                                <td>${transaccion.fechaPago}</td>
                                <td>
                                    <span class="etiqueta ${transaccion.aprobada
                                            ? 'etiqueta-disponible' : 'etiqueta-advertencia'}">
                                        <c:out value="${transaccion.estadoTransaccion}"/>
                                    </span>
                                </td>
                                <td>
                                    <div class="columna-acciones">
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/transacciones?accion=editar&id=${transaccion.idTransaccion}">Editar</a>
                                        <a class="boton boton-peligro boton-pequeno"
                                           href="${pageContext.request.contextPath}/transacciones?accion=eliminar&id=${transaccion.idTransaccion}"
                                           onclick="return confirm('Confirma eliminar la transaccion ${transaccion.idTransaccion}?');">Eliminar</a>
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
