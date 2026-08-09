<%--
    Pagina de entrada de la aplicacion.

    Usa el objeto implicito response para llevar al usuario al formulario de
    ingreso o directamente al tablero si ya tiene una sesion abierta.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%
    boolean tieneSesion = session.getAttribute("usuarioSesion") != null;
    String destino = tieneSesion ? "panel" : "login";
    response.sendRedirect(request.getContextPath() + "/" + destino);
%>
