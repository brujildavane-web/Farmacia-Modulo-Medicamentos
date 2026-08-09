<%--
    Modulo de Usuarios (tabla TblUsuario).

    Incluye dos formularios POST: uno para el registro y edicion del usuario
    y otro exclusivo para el cambio de contrasena.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Usuarios"/>
<c:set var="moduloActivo" value="usuarios"/>
<c:set var="enEdicion" value="${not empty usuarioEnEdicion}"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Gestion de usuarios</h1>
<p class="descripcion-modulo">
    Credenciales de acceso al sistema. Las contrasenas se guardan cifradas con SHA-256.
</p>

<div class="tarjeta">
    <h2>${enEdicion ? 'Editar usuario' : 'Registrar usuario nuevo'}</h2>

    <form action="${pageContext.request.contextPath}/usuarios" method="post">
        <input type="hidden" name="accion" value="guardar">
        <input type="hidden" name="idUsuario" value="${usuarioEnEdicion.idUsuario}">

        <div class="rejilla-formulario">
            <div class="campo">
                <label for="email">Correo electronico</label>
                <input type="email" id="email" name="email" required maxlength="150"
                       value="<c:out value='${usuarioEnEdicion.email}'/>"
                       placeholder="ejemplo: usuario@farmacia.com">
            </div>

            <div class="campo">
                <label for="idRol">Rol asignado</label>
                <select id="idRol" name="idRol" required>
                    <option value="">Seleccione un rol</option>
                    <c:forEach var="rol" items="${listaRoles}">
                        <option value="${rol.idRol}"
                                ${usuarioEnEdicion.idRol eq rol.idRol ? 'selected' : ''}>
                            <c:out value="${rol.nombreRol}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="campo">
                <label for="fechaRegistro">Fecha de registro</label>
                <input type="date" id="fechaRegistro" name="fechaRegistro"
                       value="${usuarioEnEdicion.fechaRegistro}">
                <span class="ayuda">Si se deja vacia se usa la fecha de hoy.</span>
            </div>

            <c:if test="${not enEdicion}">
                <div class="campo">
                    <label for="password">Contrasena</label>
                    <input type="password" id="password" name="password" required minlength="6"
                           placeholder="minimo 6 caracteres">
                </div>
            </c:if>
        </div>

        <div class="acciones-formulario">
            <button type="submit">${enEdicion ? 'Actualizar usuario' : 'Registrar usuario'}</button>
            <c:if test="${enEdicion}">
                <a class="boton boton-secundario"
                   href="${pageContext.request.contextPath}/usuarios">Cancelar edicion</a>
            </c:if>
        </div>
    </form>
</div>

<c:if test="${enEdicion}">
    <div class="tarjeta">
        <h2>Cambiar contrasena de <c:out value="${usuarioEnEdicion.email}"/></h2>

        <form action="${pageContext.request.contextPath}/usuarios" method="post">
            <input type="hidden" name="accion" value="cambiarPassword">
            <input type="hidden" name="idUsuario" value="${usuarioEnEdicion.idUsuario}">

            <div class="rejilla-formulario">
                <div class="campo">
                    <label for="passwordNuevo">Contrasena nueva</label>
                    <input type="password" id="passwordNuevo" name="password" required minlength="6"
                           placeholder="minimo 6 caracteres">
                </div>
            </div>

            <div class="acciones-formulario">
                <button type="submit">Cambiar contrasena</button>
            </div>
        </form>
    </div>
</c:if>

<div class="tarjeta">
    <h2>Usuarios registrados</h2>

    <c:choose>
        <c:when test="${empty listaUsuarios}">
            <p class="sin-registros">Todavia no hay usuarios registrados.</p>
        </c:when>
        <c:otherwise>
            <div class="contenedor-tabla">
                <table class="tabla-datos">
                    <caption>Total de usuarios: ${listaUsuarios.size()}</caption>
                    <thead>
                        <tr>
                            <th scope="col">Codigo</th>
                            <th scope="col">Correo</th>
                            <th scope="col">Rol</th>
                            <th scope="col">Fecha de registro</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="usuario" items="${listaUsuarios}">
                            <tr>
                                <td>${usuario.idUsuario}</td>
                                <td><c:out value="${usuario.email}"/></td>
                                <td><span class="etiqueta etiqueta-disponible">
                                    <c:out value="${usuario.nombreRol}"/></span></td>
                                <td>${usuario.fechaRegistro}</td>
                                <td>
                                    <div class="columna-acciones">
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/usuarios?accion=editar&id=${usuario.idUsuario}">Editar</a>
                                        <a class="boton boton-peligro boton-pequeno"
                                           href="${pageContext.request.contextPath}/usuarios?accion=eliminar&id=${usuario.idUsuario}"
                                           onclick="return confirm('Confirma eliminar el usuario ${usuario.email}?');">Eliminar</a>
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
