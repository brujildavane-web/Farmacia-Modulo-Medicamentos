<%--
    Plantilla maestra de encabezado.

    Se incluye al inicio de cada pagina de modulo con la directiva include.
    Espera la variable tituloPagina definida antes de la inclusion.

    Elementos JSP aplicados: directiva taglib, expresiones EL, etiquetas
    JSTL, objetos implicitos (session, request) y un scriptlet para borrar
    los mensajes ya mostrados.

    No declara directiva page porque es un fragmento: la pagina que lo
    incluye ya define el contentType de la respuesta.
--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${tituloPagina}"/> | Farmacia en Linea</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>

<a class="salto-contenido" href="#contenido-principal">Saltar al contenido principal</a>

<header class="encabezado-principal">
    <p class="marca">Farmacia en Linea <span>| Sistema de Gestion</span></p>

    <div class="datos-sesion">
        <span>
            Usuario: <strong><c:out value="${sessionScope.usuarioSesion.email}"/></strong>
            (<c:out value="${sessionScope.usuarioSesion.nombreRol}"/>)
        </span>
        <a class="boton-salir" href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
    </div>
</header>

<%@ include file="/WEB-INF/jsp/comunes/menu.jsp" %>

<main class="contenido" id="contenido-principal">

    <%-- Mensajes de resultado publicados por los servlets --%>
    <c:if test="${not empty sessionScope.mensajeExito}">
        <div class="alerta alerta-exito" role="status">
            <c:out value="${sessionScope.mensajeExito}"/>
        </div>
        <% session.removeAttribute("mensajeExito"); %>
    </c:if>

    <c:if test="${not empty sessionScope.mensajeError}">
        <div class="alerta alerta-error" role="alert">
            <c:out value="${sessionScope.mensajeError}"/>
        </div>
        <% session.removeAttribute("mensajeError"); %>
    </c:if>
