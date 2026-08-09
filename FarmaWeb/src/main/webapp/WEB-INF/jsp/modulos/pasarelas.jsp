<%--
    Modulo de Pasarelas de Pago (tabla TblPasarelaPago).

    La llave publica se muestra enmascarada en la tabla como buena practica
    de seguridad, aunque se pueda editar en el formulario.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Pasarelas de pago"/>
<c:set var="moduloActivo" value="pasarelas"/>
<c:set var="enEdicion" value="${not empty pasarelaEnEdicion}"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Pasarelas de pago habilitadas</h1>
<p class="descripcion-modulo">
    Proveedores certificados con los que la farmacia procesa los pagos en linea.
</p>

<div class="tarjeta">
    <h2>${enEdicion ? 'Editar pasarela' : 'Habilitar pasarela nueva'}</h2>

    <form action="${pageContext.request.contextPath}/pasarelas" method="post">
        <input type="hidden" name="idPasarela" value="${pasarelaEnEdicion.idPasarela}">

        <div class="rejilla-formulario">
            <div class="campo">
                <label for="nombreProveedor">Nombre del proveedor</label>
                <input type="text" id="nombreProveedor" name="nombreProveedor"
                       required maxlength="50"
                       value="<c:out value='${pasarelaEnEdicion.nombreProveedor}'/>"
                       placeholder="ejemplo: PayU, MercadoPago o Stripe">
            </div>

            <div class="campo">
                <label for="apiKeyPublica">Llave publica (API key)</label>
                <input type="text" id="apiKeyPublica" name="apiKeyPublica"
                       required minlength="8" maxlength="50"
                       value="<c:out value='${pasarelaEnEdicion.apiKeyPublica}'/>"
                       placeholder="ejemplo: pk_test_51ABCdef">
                <span class="ayuda">Solo la llave publica: nunca se guardan llaves privadas.</span>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit">${enEdicion ? 'Actualizar pasarela' : 'Habilitar pasarela'}</button>
            <c:if test="${enEdicion}">
                <a class="boton boton-secundario"
                   href="${pageContext.request.contextPath}/pasarelas">Cancelar edicion</a>
            </c:if>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>Pasarelas registradas</h2>

    <c:choose>
        <c:when test="${empty listaPasarelas}">
            <p class="sin-registros">Todavia no hay pasarelas de pago habilitadas.</p>
        </c:when>
        <c:otherwise>
            <div class="contenedor-tabla">
                <table class="tabla-datos">
                    <caption>Total de pasarelas: ${listaPasarelas.size()}</caption>
                    <thead>
                        <tr>
                            <th scope="col">Codigo</th>
                            <th scope="col">Proveedor</th>
                            <th scope="col">Llave publica</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="pasarela" items="${listaPasarelas}">
                            <tr>
                                <td>${pasarela.idPasarela}</td>
                                <td><c:out value="${pasarela.nombreProveedor}"/></td>
                                <td><c:out value="${pasarela.apiKeyEnmascarada}"/></td>
                                <td>
                                    <div class="columna-acciones">
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/pasarelas?accion=editar&id=${pasarela.idPasarela}">Editar</a>
                                        <a class="boton boton-peligro boton-pequeno"
                                           href="${pageContext.request.contextPath}/pasarelas?accion=eliminar&id=${pasarela.idPasarela}"
                                           onclick="return confirm('Confirma eliminar la pasarela ${pasarela.nombreProveedor}?');">Eliminar</a>
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
