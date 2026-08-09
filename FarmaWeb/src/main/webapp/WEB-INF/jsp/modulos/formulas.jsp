<%--
    Modulo de Formulas Medicas (tabla TblFormulaMedica).

    Registra las prescripciones que autorizan la entrega de medicamentos
    controlados. El sistema rechaza las formulas ya vencidas.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Formulas medicas"/>
<c:set var="moduloActivo" value="formulas"/>
<c:set var="enEdicion" value="${not empty formulaEnEdicion}"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Gestion de formulas medicas</h1>
<p class="descripcion-modulo">
    Prescripciones que respaldan la venta de medicamentos controlados.
    Formatos de archivo permitidos: <c:out value="${formatosPermitidos}"/>.
</p>

<div class="tarjeta">
    <h2>Filtrar formulas por pedido</h2>

    <form action="${pageContext.request.contextPath}/formulas" method="get">
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
               href="${pageContext.request.contextPath}/formulas">Quitar filtro</a>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>${enEdicion ? 'Editar formula medica' : 'Registrar formula medica'}</h2>

    <form action="${pageContext.request.contextPath}/formulas" method="post">
        <input type="hidden" name="idFormula" value="${formulaEnEdicion.idFormula}">

        <div class="rejilla-formulario">
            <div class="campo">
                <label for="idPedido">Pedido asociado</label>
                <select id="idPedido" name="idPedido" required>
                    <option value="">Seleccione el pedido</option>
                    <c:forEach var="pedido" items="${listaPedidos}">
                        <c:set var="pedidoSeleccionado"
                               value="${enEdicion ? formulaEnEdicion.idPedido : idPedidoFiltro}"/>
                        <option value="${pedido.idPedido}"
                                ${pedidoSeleccionado eq pedido.idPedido ? 'selected' : ''}>
                            Pedido ${pedido.idPedido} - <c:out value="${pedido.nombreCliente}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="idProducto">Medicamento prescrito</label>
                <select id="idProducto" name="idProducto" required>
                    <option value="">Seleccione el medicamento</option>
                    <c:forEach var="producto" items="${listaProductos}">
                        <option value="${producto.idProducto}"
                                ${formulaEnEdicion.idProducto eq producto.idProducto ? 'selected' : ''}>
                            <c:out value="${producto.nombre}"/>
                            <c:if test="${producto.requiereReceta}">(controlado)</c:if>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="fechaPrescripcion">Fecha de prescripcion</label>
                <input type="date" id="fechaPrescripcion" name="fechaPrescripcion" required
                       value="${formulaEnEdicion.fechaPrescripcion}">
            </div>

            <div class="campo">
                <label for="fechaVencimiento">Fecha de vencimiento</label>
                <input type="date" id="fechaVencimiento" name="fechaVencimiento"
                       value="${formulaEnEdicion.fechaVencimiento}">
                <span class="ayuda">No se almacenan formulas con fecha ya cumplida.</span>
            </div>

            <div class="campo">
                <label for="archivo">Archivo adjunto de la receta</label>
                <input type="text" id="archivo" name="archivo" maxlength="50"
                       value="<c:out value='${formulaEnEdicion.archivo}'/>"
                       placeholder="ejemplo: receta-laura-gomez.pdf">
                <span class="ayuda">Solo se aceptan nombres con extension PDF, JPG o PNG.</span>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit">${enEdicion ? 'Actualizar formula' : 'Registrar formula'}</button>
            <c:if test="${enEdicion}">
                <a class="boton boton-secundario"
                   href="${pageContext.request.contextPath}/formulas">Cancelar edicion</a>
            </c:if>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>Formulas registradas</h2>

    <c:choose>
        <c:when test="${empty listaFormulas}">
            <p class="sin-registros">No hay formulas registradas con el criterio seleccionado.</p>
        </c:when>
        <c:otherwise>
            <div class="contenedor-tabla">
                <table class="tabla-datos">
                    <caption>Total de formulas: ${listaFormulas.size()}</caption>
                    <thead>
                        <tr>
                            <th scope="col">Formula</th>
                            <th scope="col">Pedido</th>
                            <th scope="col">Cliente</th>
                            <th scope="col">Medicamento</th>
                            <th scope="col">Prescrita</th>
                            <th scope="col">Vence</th>
                            <th scope="col">Vigencia</th>
                            <th scope="col">Archivo</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="formula" items="${listaFormulas}">
                            <tr>
                                <td>${formula.idFormula}</td>
                                <td>${formula.idPedido}</td>
                                <td><c:out value="${formula.nombreCliente}"/></td>
                                <td><c:out value="${formula.nombreProducto}"/></td>
                                <td>${formula.fechaPrescripcion}</td>
                                <td>${formula.fechaVencimiento}</td>
                                <td>
                                    <span class="etiqueta ${formula.vigencia eq 'VENCIDA'
                                            ? 'etiqueta-agotado' : 'etiqueta-disponible'}">
                                        ${formula.vigencia}
                                    </span>
                                </td>
                                <td><c:out value="${formula.archivo}"/></td>
                                <td>
                                    <div class="columna-acciones">
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/formulas?accion=editar&id=${formula.idFormula}">Editar</a>
                                        <a class="boton boton-peligro boton-pequeno"
                                           href="${pageContext.request.contextPath}/formulas?accion=eliminar&id=${formula.idFormula}"
                                           onclick="return confirm('Confirma eliminar la formula ${formula.idFormula}?');">Eliminar</a>
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
