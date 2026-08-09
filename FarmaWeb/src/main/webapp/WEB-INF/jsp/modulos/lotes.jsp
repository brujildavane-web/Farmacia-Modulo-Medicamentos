<%--
    Modulo de Inventario por lotes (tabla TblLoteProducto).

    Incluye un filtro por producto que viaja por GET, el formulario de
    registro y edicion por POST y una accion POST para descontar stock.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Inventario"/>
<c:set var="moduloActivo" value="lotes"/>
<c:set var="enEdicion" value="${not empty loteEnEdicion}"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Control de lotes e inventario</h1>
<p class="descripcion-modulo">
    Cada ingreso de producto se registra con su lote, registro sanitario y
    fecha de vencimiento para garantizar la trazabilidad.
</p>

<div class="tarjeta">
    <h2>Filtrar lotes por producto</h2>

    <form action="${pageContext.request.contextPath}/lotes" method="get">
        <div class="rejilla-formulario">
            <div class="campo">
                <label for="idProductoFiltro">Producto</label>
                <select id="idProductoFiltro" name="idProductoFiltro">
                    <option value="">Todos los productos</option>
                    <c:forEach var="producto" items="${listaProductos}">
                        <option value="${producto.idProducto}"
                                ${idProductoFiltro eq producto.idProducto ? 'selected' : ''}>
                            <c:out value="${producto.nombre}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit" class="boton-secundario">Aplicar filtro</button>
            <a class="boton boton-secundario"
               href="${pageContext.request.contextPath}/lotes">Quitar filtro</a>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>${enEdicion ? 'Editar lote' : 'Ingresar lote nuevo al inventario'}</h2>

    <form action="${pageContext.request.contextPath}/lotes" method="post">
        <input type="hidden" name="accion" value="guardar">
        <input type="hidden" name="idLote" value="${loteEnEdicion.idLote}">

        <div class="rejilla-formulario">
            <div class="campo">
                <label for="idProducto">Producto</label>
                <select id="idProducto" name="idProducto" required>
                    <option value="">Seleccione el producto</option>
                    <c:forEach var="producto" items="${listaProductos}">
                        <option value="${producto.idProducto}"
                                ${loteEnEdicion.idProducto eq producto.idProducto ? 'selected' : ''}>
                            <c:out value="${producto.nombre}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="registroSanitario">Registro sanitario (numero de lote)</label>
                <input type="text" id="registroSanitario" name="registroSanitario"
                       required maxlength="50"
                       value="<c:out value='${loteEnEdicion.registroSanitario}'/>"
                       placeholder="ejemplo: INVIMA-2026M-0034">
            </div>

            <div class="campo">
                <label for="fechaVencimiento">Fecha de vencimiento</label>
                <input type="date" id="fechaVencimiento" name="fechaVencimiento" required
                       value="${loteEnEdicion.fechaVencimiento}">
                <span class="ayuda">No se admiten lotes con fecha ya cumplida.</span>
            </div>

            <div class="campo">
                <label for="stockActual">Unidades en stock</label>
                <input type="number" id="stockActual" name="stockActual" required min="0" step="1"
                       value="${loteEnEdicion.stockActual}"
                       placeholder="0">
            </div>

            <div class="campo">
                <label for="marca">Marca o laboratorio</label>
                <input type="text" id="marca" name="marca" maxlength="20"
                       value="<c:out value='${loteEnEdicion.marca}'/>"
                       placeholder="ejemplo: Genfar">
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit">${enEdicion ? 'Actualizar lote' : 'Ingresar lote'}</button>
            <c:if test="${enEdicion}">
                <a class="boton boton-secundario"
                   href="${pageContext.request.contextPath}/lotes">Cancelar edicion</a>
            </c:if>
        </div>
    </form>
</div>

<c:if test="${enEdicion}">
    <div class="tarjeta">
        <h2>Descontar stock del lote ${loteEnEdicion.idLote}</h2>

        <form action="${pageContext.request.contextPath}/lotes" method="post">
            <input type="hidden" name="accion" value="descontar">
            <input type="hidden" name="idLote" value="${loteEnEdicion.idLote}">

            <div class="rejilla-formulario">
                <div class="campo">
                    <label for="cantidad">Unidades a descontar</label>
                    <input type="number" id="cantidad" name="cantidad" required min="1" step="1"
                           max="${loteEnEdicion.stockActual}" placeholder="1">
                    <span class="ayuda">
                        Disponible actualmente: ${loteEnEdicion.stockActual} unidades.
                    </span>
                </div>
            </div>

            <div class="acciones-formulario">
                <button type="submit">Descontar del inventario</button>
            </div>
        </form>
    </div>
</c:if>

<div class="tarjeta">
    <h2>Lotes registrados</h2>

    <c:choose>
        <c:when test="${empty listaLotes}">
            <p class="sin-registros">No hay lotes registrados con el criterio seleccionado.</p>
        </c:when>
        <c:otherwise>
            <div class="contenedor-tabla">
                <table class="tabla-datos">
                    <caption>
                        Total de lotes: ${listaLotes.size()}.
                        Los lotes se alertan cuando faltan 30 dias o menos para su vencimiento.
                    </caption>
                    <thead>
                        <tr>
                            <th scope="col">Lote</th>
                            <th scope="col">Producto</th>
                            <th scope="col">Registro sanitario</th>
                            <th scope="col">Vence</th>
                            <th scope="col">Dias restantes</th>
                            <th scope="col">Stock</th>
                            <th scope="col">Marca</th>
                            <th scope="col">Estado</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="lote" items="${listaLotes}">
                            <tr>
                                <td>${lote.idLote}</td>
                                <td><c:out value="${lote.nombreProducto}"/></td>
                                <td><c:out value="${lote.registroSanitario}"/></td>
                                <td>${lote.fechaVencimiento}</td>
                                <td>${lote.diasParaVencer}</td>
                                <td>${lote.stockActual}</td>
                                <td><c:out value="${lote.marca}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${lote.estado eq 'VENCIDO'}">
                                            <span class="etiqueta etiqueta-agotado">VENCIDO</span>
                                        </c:when>
                                        <c:when test="${lote.estado eq 'POR VENCER'}">
                                            <span class="etiqueta etiqueta-advertencia">POR VENCER</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="etiqueta etiqueta-disponible">VIGENTE</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="columna-acciones">
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/lotes?accion=editar&id=${lote.idLote}">Editar</a>
                                        <a class="boton boton-peligro boton-pequeno"
                                           href="${pageContext.request.contextPath}/lotes?accion=eliminar&id=${lote.idLote}"
                                           onclick="return confirm('Confirma eliminar el lote ${lote.idLote}?');">Eliminar</a>
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
