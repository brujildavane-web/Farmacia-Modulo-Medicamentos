<%--
    Pagina de error generica declarada en web.xml para los codigos 404 y 500.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error | Farmacia en Linea</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>

<div class="pantalla-ingreso">
    <div class="tarjeta-ingreso">
        <h1>La pagina solicitada no esta disponible</h1>

        <div class="alerta alerta-error" role="alert">
            Codigo de estado: ${pageContext.errorData.statusCode}
        </div>

        <p>Verifique la direccion o regrese al tablero del sistema.</p>

        <a class="boton" href="${pageContext.request.contextPath}/panel">Volver al tablero</a>
    </div>
</div>

</body>
</html>
