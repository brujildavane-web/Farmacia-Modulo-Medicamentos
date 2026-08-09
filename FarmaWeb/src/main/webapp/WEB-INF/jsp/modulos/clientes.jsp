<%--
    Modulo de Clientes (tabla TblCliente).

    La edad se calcula en la capa de logica a partir de la fecha de
    nacimiento, por eso el campo aparece como informativo.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Clientes"/>
<c:set var="moduloActivo" value="clientes"/>
<c:set var="enEdicion" value="${not empty clienteEnEdicion}"/>
<%@ include file="/WEB-INF/jsp/comunes/cabecera.jsp" %>

<h1 class="titulo-modulo">Gestion de clientes</h1>
<p class="descripcion-modulo">
    Datos de contacto y domicilio de las personas que compran en la farmacia.
</p>

<div class="tarjeta">
    <h2>${enEdicion ? 'Editar cliente' : 'Registrar cliente nuevo'}</h2>

    <form action="${pageContext.request.contextPath}/clientes" method="post">
        <input type="hidden" name="idCliente" value="${clienteEnEdicion.idCliente}">

        <div class="rejilla-formulario">
            <div class="campo">
                <label for="nombre">Nombre</label>
                <input type="text" id="nombre" name="nombre" required maxlength="50"
                       value="<c:out value='${clienteEnEdicion.nombre}'/>"
                       placeholder="ejemplo: Laura">
            </div>

            <div class="campo">
                <label for="apellido">Apellido</label>
                <input type="text" id="apellido" name="apellido" required maxlength="50"
                       value="<c:out value='${clienteEnEdicion.apellido}'/>"
                       placeholder="ejemplo: Gomez">
            </div>

            <div class="campo">
                <label for="direccion">Direccion de entrega</label>
                <input type="text" id="direccion" name="direccion" maxlength="250"
                       value="<c:out value='${clienteEnEdicion.direccion}'/>"
                       placeholder="ejemplo: Calle 10 # 4-25, Popayan">
            </div>

            <div class="campo">
                <label for="telefono">Telefono</label>
                <input type="text" id="telefono" name="telefono" maxlength="20"
                       value="<c:out value='${clienteEnEdicion.telefono}'/>"
                       placeholder="ejemplo: 3001234567">
            </div>

            <div class="campo">
                <label for="email">Correo de contacto</label>
                <input type="email" id="email" name="email" maxlength="150"
                       value="<c:out value='${clienteEnEdicion.email}'/>"
                       placeholder="ejemplo: cliente@correo.com">
            </div>

            <div class="campo">
                <label for="fechaNacimiento">Fecha de nacimiento</label>
                <input type="date" id="fechaNacimiento" name="fechaNacimiento"
                       value="${clienteEnEdicion.fechaNacimiento}">
                <span class="ayuda">Con este dato el sistema calcula la edad automaticamente.</span>
            </div>

            <div class="campo">
                <label for="edad">Edad</label>
                <input type="number" id="edad" name="edad" min="0" max="120"
                       value="${clienteEnEdicion.edad}"
                       placeholder="solo si no registra la fecha">
            </div>

            <div class="campo">
                <label for="idUsuario">Usuario del sistema</label>
                <select id="idUsuario" name="idUsuario" required>
                    <option value="">Seleccione el usuario asociado</option>
                    <c:forEach var="usuario" items="${listaUsuarios}">
                        <option value="${usuario.idUsuario}"
                                ${clienteEnEdicion.idUsuario eq usuario.idUsuario ? 'selected' : ''}>
                            <c:out value="${usuario.email}"/> (<c:out value="${usuario.nombreRol}"/>)
                        </option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="acciones-formulario">
            <button type="submit">${enEdicion ? 'Actualizar cliente' : 'Registrar cliente'}</button>
            <c:if test="${enEdicion}">
                <a class="boton boton-secundario"
                   href="${pageContext.request.contextPath}/clientes">Cancelar edicion</a>
            </c:if>
        </div>
    </form>
</div>

<div class="tarjeta">
    <h2>Clientes registrados</h2>

    <c:choose>
        <c:when test="${empty listaClientes}">
            <p class="sin-registros">Todavia no hay clientes registrados.</p>
        </c:when>
        <c:otherwise>
            <div class="contenedor-tabla">
                <table class="tabla-datos">
                    <caption>Total de clientes: ${listaClientes.size()}</caption>
                    <thead>
                        <tr>
                            <th scope="col">Codigo</th>
                            <th scope="col">Nombre completo</th>
                            <th scope="col">Telefono</th>
                            <th scope="col">Correo</th>
                            <th scope="col">Edad</th>
                            <th scope="col">Usuario</th>
                            <th scope="col">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cliente" items="${listaClientes}">
                            <tr>
                                <td>${cliente.idCliente}</td>
                                <td><c:out value="${cliente.nombreCompleto}"/></td>
                                <td><c:out value="${cliente.telefono}"/></td>
                                <td><c:out value="${cliente.email}"/></td>
                                <td>${cliente.edad}</td>
                                <td><c:out value="${cliente.emailUsuario}"/></td>
                                <td>
                                    <div class="columna-acciones">
                                        <a class="boton boton-secundario boton-pequeno"
                                           href="${pageContext.request.contextPath}/clientes?accion=editar&id=${cliente.idCliente}">Editar</a>
                                        <a class="boton boton-peligro boton-pequeno"
                                           href="${pageContext.request.contextPath}/clientes?accion=eliminar&id=${cliente.idCliente}"
                                           onclick="return confirm('Confirma eliminar al cliente ${cliente.nombreCompleto}?');">Eliminar</a>
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
