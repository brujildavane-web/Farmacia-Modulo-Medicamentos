<%--
    Menu de navegacion de los trece modulos del sistema.

    Fragmento incluido por cabecera.jsp con la directiva include, por lo que
    no declara su propia directiva page: hereda la de la pagina que lo incluye.

    Refleja el mapa de navegacion definido en la evidencia
    GA5-220501095-AA1-EV05 y marca el modulo activo comparando la ruta
    solicitada con el atributo moduloActivo que publica cada pagina.
--%>
<nav class="menu-modulos" aria-label="Modulos del sistema">
    <c:set var="raiz" value="${pageContext.request.contextPath}"/>

    <a href="${raiz}/panel" class="${moduloActivo eq 'panel' ? 'activo' : ''}">Tablero</a>
    <a href="${raiz}/roles" class="${moduloActivo eq 'roles' ? 'activo' : ''}">Roles</a>
    <a href="${raiz}/usuarios" class="${moduloActivo eq 'usuarios' ? 'activo' : ''}">Usuarios</a>
    <a href="${raiz}/clientes" class="${moduloActivo eq 'clientes' ? 'activo' : ''}">Clientes</a>
    <a href="${raiz}/farmaceuticos" class="${moduloActivo eq 'farmaceuticos' ? 'activo' : ''}">Farmaceuticos</a>
    <a href="${raiz}/productos" class="${moduloActivo eq 'productos' ? 'activo' : ''}">Productos</a>
    <a href="${raiz}/lotes" class="${moduloActivo eq 'lotes' ? 'activo' : ''}">Inventario</a>
    <a href="${raiz}/favoritos" class="${moduloActivo eq 'favoritos' ? 'activo' : ''}">Favoritos</a>
    <a href="${raiz}/pedidos" class="${moduloActivo eq 'pedidos' ? 'activo' : ''}">Pedidos</a>
    <a href="${raiz}/lineas" class="${moduloActivo eq 'lineas' ? 'activo' : ''}">Detalle pedidos</a>
    <a href="${raiz}/formulas" class="${moduloActivo eq 'formulas' ? 'activo' : ''}">Formulas medicas</a>
    <a href="${raiz}/historial" class="${moduloActivo eq 'historial' ? 'activo' : ''}">Historial</a>
    <a href="${raiz}/pasarelas" class="${moduloActivo eq 'pasarelas' ? 'activo' : ''}">Pasarelas</a>
    <a href="${raiz}/transacciones" class="${moduloActivo eq 'transacciones' ? 'activo' : ''}">Transacciones</a>
</nav>
