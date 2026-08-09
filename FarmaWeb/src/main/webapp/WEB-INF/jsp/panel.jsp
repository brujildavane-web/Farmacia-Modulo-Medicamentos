<%--
    Tablero de inicio del sistema.

    Recorre con la etiqueta forEach de JSTL el mapa de indicadores que
    publica el ServletPanel y muestra una tarjeta por cada modulo.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Tablero"/>
<c:set var="moduloActivo" value="panel"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Tablero de gestion</h1>
<p class="descripcion-modulo">
    Resumen de los trece modulos derivados del modelo relacional del proyecto.
</p>

<c:if test="${alertasCaducidad gt 0}">
    <div class="alerta alerta-advertencia" role="alert">
        Atencion: <strong>${alertasCaducidad}</strong> lote(s) del inventario estan vencidos
        o vencen en los proximos 30 dias.
        <a href="${pageContext.request.contextPath}/lotes">Revisar el inventario</a>
    </div>
</c:if>

<div class="tarjeta">
    <h2>Indicadores generales</h2>

    <div class="rejilla-indicadores">
        <div class="indicador indicador-destacado">
            <span class="cifra">
                $<fmt:formatNumber value="${totalVentas}" type="number" minFractionDigits="2"/>
            </span>
            <span class="rotulo">Total facturado (pedidos no anulados)</span>
        </div>

        <c:forEach var="indicador" items="${conteosPorModulo}">
            <div class="indicador">
                <span class="cifra">${indicador.value}</span>
                <span class="rotulo"><c:out value="${indicador.key}"/></span>
            </div>
        </c:forEach>
    </div>
</div>

<div class="tarjeta">
    <h2>Arquitectura del sistema</h2>
    <p>
        La aplicacion esta organizada en las tres capas definidas en el documento
        de estandares de codificacion GA7-220501096-AA1-EV02:
    </p>
    <ul>
        <li><strong>IGU:</strong> paginas JSP con formularios HTML accesibles.</li>
        <li><strong>Logica:</strong> clases Servicio con las reglas de negocio y validaciones.</li>
        <li><strong>Persistencia:</strong> clases DAO que acceden a MySQL mediante JDBC.</li>
    </ul>
    <p>
        Los formularios se comunican con los servlets por los metodos
        <strong>GET</strong> (consultas y filtros) y <strong>POST</strong> (registro,
        edicion y acciones de negocio).
    </p>
</div>

<%@ include file="/WEB-INF/jsp/comunes/pie.jsp" %>
