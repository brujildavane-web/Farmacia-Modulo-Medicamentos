<%--
    Modulo de Farmaceuticos (tabla TblFarmaceutico).
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Farmaceuticos"/>
<c:set var="moduloActivo" value="farmaceuticos"/>
<c:set var="enEdicion" value="${not empty farmaceuticoEnEdicion}"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Gestion de farmaceuticos</h1>
<p class="descripcion-modulo">
    Profesionales autorizados para validar formulas medicas y confirmar pedidos.
</p>

<div class="tarjeta">
    <h2>${enEdicion ? 'Editar farmaceutico' : 'Registrar farmaceutico nuevo'}</h2>

    <form action="${pageContext.request.contextPath}/farmaceuticos" method="post">
        <input type="hidden" name="idFarmaceutico" value="${farmaceuticoEnEdicion.idFarmaceutico}">

        <div class="rejilla-formulario">
            <div class="campo">
                <label for="nombre">Nombre</label>
                <input type="text" id="nombre" name="nombre" required maxlength="50"
                       value="<c:out value='${farmaceuticoEnEdicion.nombre}'/>"
                       placeholder="ejemplo: Carlos">
            </div>

            <div class="campo">
                <label for="apellido">Apellido</label>
                <input type="text" id="apellido" name="apellido" required maxlength="50"
                       value="<c:out value='${farmaceuticoEnEdicion.apellido}'/>"
                       placeholder="ejemplo: Ruiz">
            </div>

            <div class="campo">
                <label for="registroProfesional">Registro profesional</label>
                <input type="text" id="registroProfesional" name="registroProfesional"
                       required maxlength="50"
                       value="<c:out value='${farmaceuticoEnEdicion.registroProfesional}'/>"
                       placeholder="ejemplo: RP-2026-0451">
                <span class="ayuda">Obligatorio por normativa sanitaria; no se puede repetir.</span>
            </div>

            <div class="campo">
                <label for="especialidad">Especialidad</label>
                <input type="text" id="especialidad" name="especialidad" maxlength="30"
                       value="<c:out value='${farmaceuticoEnEdicion.especialidad}'/>"
                       placeholder="ejemplo: Regencia de farmacia">
            </div>

            <div class="campo">
                <label for="telefono">Telefono</label>
                <input type="text" id="telefono" name="telefono" maxlength="20"
                       value="<c:out value='${farmaceuticoEnEdicion.telefono}'/>"
                       placeholder="ejemplo: 3109876543">
            </div>

            <div class="campo">
                <label for="idUsuario">Usuario del sistema</label>
                <select id="idUsuario" name="idUsuario" required>
                    <option value="">Seleccione el usuario asociado</option>
                    <c:forEach var="usuario" items="${listaUsuarios}">
                        <option value="${usuario.idUsuario}"
                                ${farmaceuticoEnEdicion.idUsuario eq usuario.idUsuario ? 'selected' : ''}>
                            <c:out value="${usuario.email}"/> (<c:out value="${usuario.nombreRol}"/>)
                        </option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit">${enEdicion ? 'Actualizar farmaceutico' : 'Registrar farmaceutico'}</button>
            <c:if test="${enEdicion}">
                <a class="boton boton-secundario"
                   href="${pageContext.request.contextPath}/farmaceuticos">Cancelar edicion</a>
            </c:if>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>Farmaceuticos registrados</h2>

    <c:choose>
        <c:when test="${empty listaFarmaceuticos}">
            <p class="sin-registros">Todavia no hay farmaceuticos registrados.</p>
        </c:when>
        <c:otherwise>
            <div class="contenedor-tabla">
                <table class="tabla-datos">
                    <caption>Total de farmaceuticos: ${listaFarmaceuticos.size()}</caption>
                    <thead>
                        <tr>
                            <th scope="col">Codigo</th>
                            <th scope="col">Nombre completo</th>
                            <th scope="col">Registro profesional</th>
                            <th scope="col">Especialidad</th>
                            <th scope="col">Telefono</th>
                            <th scope="col">Usuario</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="farmaceutico" items="${listaFarmaceuticos}">
                            <tr>
                                <td>${farmaceutico.idFarmaceutico}</td>
                                <td><c:out value="${farmaceutico.nombreCompleto}"/></td>
                                <td><c:out value="${farmaceutico.registroProfesional}"/></td>
                                <td><c:out value="${farmaceutico.especialidad}"/></td>
                                <td><c:out value="${farmaceutico.telefono}"/></td>
                                <td><c:out value="${farmaceutico.emailUsuario}"/></td>
                                <td>
                                    <div class="columna-acciones">
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/farmaceuticos?accion=editar&id=${farmaceutico.idFarmaceutico}">Editar</a>
                                        <a class="boton boton-peligro boton-pequeno"
                                           href="${pageContext.request.contextPath}/farmaceuticos?accion=eliminar&id=${farmaceutico.idFarmaceutico}"
                                           onclick="return confirm('Confirma eliminar al farmaceutico ${farmaceutico.nombreCompleto}?');">Eliminar</a>
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
