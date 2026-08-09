<%--
    Modulo de Productos y Catalogo (tabla TblProducto).

    Este modulo es la evolucion del formulario de medicamentos con el que
    inicio el proyecto: ahora persiste los datos en MySQL y muestra el stock
    real calculado a partir de los lotes vigentes.

    Contiene un formulario GET (busqueda por codigo SKU) y un formulario POST
    (registro y edicion), cumpliendo el uso de ambos metodos HTTP.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Productos"/>
<c:set var="moduloActivo" value="productos"/>
<c:set var="enEdicion" value="${not empty productoEnEdicion}"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Gestion de inventario - Catalogo de productos</h1>
<p class="descripcion-modulo">
    Medicamentos y productos de la farmacia con su precio y disponibilidad en tiempo real.
</p>

<div class="tarjeta">
    <h2>Buscar producto por codigo SKU o de barras</h2>

    <form action="${pageContext.request.contextPath}/productos" method="get">
        <input type="hidden" name="accion" value="buscarSku">

        <div class="rejilla-formulario">
            <div class="campo">
                <label for="sku">Codigo SKU</label>
                <input type="text" id="sku" name="sku" required
                       placeholder="ejemplo: MED-001">
                <span class="ayuda">
                    Simula el escaneo del codigo de barras: los datos viajan por el metodo GET.
                </span>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit" class="boton-secundario">Buscar producto</button>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>${enEdicion ? 'Editar producto' : 'Registrar producto nuevo'}</h2>

    <form action="${pageContext.request.contextPath}/productos" method="post">
        <input type="hidden" name="idProducto" value="${productoEnEdicion.idProducto}">

        <div class="rejilla-formulario">
            <div class="campo">
                <label for="nombre">Nombre del medicamento</label>
                <input type="text" id="nombre" name="nombre" required maxlength="200"
                       value="<c:out value='${productoEnEdicion.nombre}'/>"
                       placeholder="ejemplo: Paracetamol 500 mg">
            </div>

            <div class="campo">
                <label for="skuCode">Codigo SKU o de barras</label>
                <input type="text" id="skuCode" name="skuCode" maxlength="50"
                       value="<c:out value='${productoEnEdicion.skuCode}'/>"
                       placeholder="ejemplo: MED-001">
                <span class="ayuda">Debe ser unico en todo el catalogo.</span>
            </div>

            <div class="campo">
                <label for="precio">Precio unitario</label>
                <input type="number" id="precio" name="precio" required min="1" step="0.01"
                       value="${productoEnEdicion.precio}"
                       placeholder="0.00">
            </div>

            <div class="campo">
                <label for="descripcion">Descripcion e indicaciones</label>
                <input type="text" id="descripcion" name="descripcion" maxlength="300"
                       value="<c:out value='${productoEnEdicion.descripcion}'/>"
                       placeholder="ejemplo: Analgesico y antipiretico, caja por 20 tabletas">
            </div>

            <div class="campo campo-casilla">
                <input type="checkbox" id="requiereReceta" name="requiereReceta"
                       ${productoEnEdicion.requiereReceta ? 'checked' : ''}>
                <label for="requiereReceta">Requiere formula medica (medicamento controlado)</label>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit">
                ${enEdicion ? 'Actualizar producto' : 'Registrar en base de datos'}
            </button>
            <c:if test="${enEdicion}">
                <a class="boton boton-secundario"
                   href="${pageContext.request.contextPath}/productos">Cancelar edicion</a>
            </c:if>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>Inventario en tiempo real</h2>

    <c:choose>
        <c:when test="${empty listaProductos}">
            <p class="sin-registros">Todavia no hay productos en el catalogo.</p>
        </c:when>
        <c:otherwise>
            <div class="contenedor-tabla">
                <table class="tabla-datos">
                    <caption>
                        Total de productos: ${listaProductos.size()}.
                        El stock suma unicamente los lotes que no estan vencidos.
                    </caption>
                    <thead>
                        <tr>
                            <th scope="col">Codigo</th>
                            <th scope="col">Producto</th>
                            <th scope="col">SKU</th>
                            <th scope="col">Precio</th>
                            <th scope="col">Stock</th>
                            <th scope="col">Disponibilidad</th>
                            <th scope="col">Receta</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="producto" items="${listaProductos}">
                            <tr>
                                <td>${producto.idProducto}</td>
                                <td><c:out value="${producto.nombre}"/></td>
                                <td><c:out value="${producto.skuCode}"/></td>
                                <td>$<fmt:formatNumber value="${producto.precio}"
                                        type="number" minFractionDigits="2"/></td>
                                <td>${producto.stockTotal}</td>
                                <td>
                                    <span class="etiqueta ${producto.disponible
                                            ? 'etiqueta-disponible' : 'etiqueta-agotado'}">
                                        <c:out value="${producto.disponibilidad}"/>
                                    </span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${producto.requiereReceta}">
                                            <span class="etiqueta etiqueta-advertencia">Controlado</span>
                                        </c:when>
                                        <c:otherwise>Venta libre</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="columna-acciones">
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/lotes?idProductoFiltro=${producto.idProducto}">Lotes</a>
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/productos?accion=editar&id=${producto.idProducto}">Editar</a>
                                        <a class="boton boton-peligro boton-pequeno"
                                           href="${pageContext.request.contextPath}/productos?accion=eliminar&id=${producto.idProducto}"
                                           onclick="return confirm('Confirma eliminar el producto ${producto.nombre}?');">Eliminar</a>
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
