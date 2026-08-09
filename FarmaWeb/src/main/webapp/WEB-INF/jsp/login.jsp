<%--
    Formulario de ingreso al sistema.

    Envia las credenciales al ServletLogin por el metodo POST para que la
    contrasena no quede visible en la barra de direcciones.
    Cada campo tiene su etiqueta label vinculada por el atributo for, segun
    los criterios de accesibilidad WCAG del proyecto.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ingreso | Farmacia en Linea</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>

<div class="pantalla-ingreso">
    <div class="tarjeta-ingreso">

        <h1>Farmacia en Linea</h1>
        <p>Ingrese sus credenciales para administrar los modulos del sistema.</p>

        <c:if test="${not empty sessionScope.mensajeError}">
            <div class="alerta alerta-error" role="alert">
                <c:out value="${sessionScope.mensajeError}"/>
            </div>
            <% session.removeAttribute("mensajeError"); %>
        </c:if>

        <c:if test="${baseDatosDisponible eq false}">
            <div class="alerta alerta-advertencia" role="alert">
                No hay conexion con la base de datos <strong>bd_farmacia</strong>.
                Inicie MySQL en el panel de XAMPP antes de ingresar.
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">

            <div class="campo">
                <label for="email">Correo electronico</label>
                <input type="email" id="email" name="email" required
                       autocomplete="username"
                       value="<c:out value='${emailDigitado}'/>"
                       placeholder="ejemplo: admin@farmacia.com">
            </div>

            <div class="campo">
                <label for="password">Contrasena</label>
                <input type="password" id="password" name="password" required
                       autocomplete="current-password"
                       placeholder="minimo 6 caracteres">
            </div>

            <button type="submit">Ingresar</button>
        </form>

        <div class="credenciales-demo">
            <strong>Usuarios de prueba cargados por el script SQL:</strong><br>
            admin@farmacia.com / admin123 (Administrador)<br>
            carlos.ruiz@farmacia.com / farma123 (Farmaceutico)<br>
            laura.gomez@correo.com / cliente123 (Cliente)
        </div>

    </div>
</div>

</body>
</html>
