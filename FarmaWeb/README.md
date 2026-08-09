# FarmaWeb — Módulos del Sistema de Gestión de Farmacia en Línea

Aplicación web Java que implementa los **13 módulos** derivados del modelo relacional
del proyecto formativo (evidencia GA6‐220501096‐AA2‐EV02), organizada en las tres capas
definidas en el documento de estándares de codificación GA7‐220501096‐AA1‐EV02.

## Cumplimiento de los ítems de la evidencia

| Ítem | Dónde se evidencia |
|---|---|
| 1. Formularios HTML con servlets | 13 páginas JSP con formularios `<form>` que envían a 16 servlets en `co.edu.sena.farmacia.controlador` |
| 2. Métodos GET y POST para parámetros | `doGet` atiende `?accion=listar\|editar\|eliminar\|detalle` y los filtros; `doPost` recibe los datos de los formularios |
| 3. Elementos de JSP | Directivas `page`, `taglib` e `include`; expresiones EL; etiquetas JSTL (`c:forEach`, `c:if`, `c:choose`, `c:out`, `c:set`, `fmt:formatNumber`); objetos implícitos (`session`, `request`, `pageContext`, `response`); scriptlets; plantillas maestras reutilizables |
| 4. Versionamiento del código | Repositorio Git con `.gitignore`, commits descriptivos por módulo y repositorio remoto en GitHub |

## Arquitectura en tres capas

```
src/main/java/co/edu/sena/farmacia/
├── modelo/          Clases del dominio (POO: herencia Persona → Cliente, Farmaceutico)
├── persistencia/    DAOs con JDBC + PreparedStatement (13 DAOs + interfaz DaoGenerico)
├── logica/          Servicios con las reglas de negocio y validaciones (13 servicios)
├── controlador/     Servlets (IGU ↔ Lógica) + filtro de seguridad
└── util/            Conexión a la base de datos, cifrado de contraseñas, conversiones

src/main/webapp/
├── css/estilos.css              Paleta e identidad visual del proyecto
├── index.jsp                    Punto de entrada
└── WEB-INF/jsp/
    ├── comunes/                 Plantillas maestras (cabecera, menú, pie)
    ├── login.jsp, panel.jsp, error.jsp
    └── modulos/                 Una página por cada uno de los 13 módulos
```

## Identidad visual aplicada

Tomada de la evidencia GA5‐220501095‐AA1‐EV04:

| Rol | Color |
|---|---|
| Principal | `#2E7D32` verde (salud y seguridad) |
| Secundario | `#FFFFFF` blanco (fondo, legibilidad) |
| Complementario | `#F5F5F5` gris claro (separación de secciones) |
| Hover / activo | `#1B5E20` |
| Estados de error y "Agotado" | `#C62828` |
| Alertas de caducidad | `#F9A825` |

Tipografía Roboto/Arial, contraste WCAG, etiquetas `<label>` vinculadas, enlace de salto
al contenido y diseño responsive.

## Requisitos previos

1. **JDK 17 o superior**
   Descargar Eclipse Temurin 17 (LTS) desde <https://adoptium.net/temurin/releases/?version=17>
   e instalar marcando la opción *Set JAVA_HOME variable*.
   Verificar en una terminal nueva: `java -version`

2. **Apache Maven 3.9+**
   Descargar el archivo *Binary zip* desde <https://maven.apache.org/download.cgi>,
   descomprimir en `C:\maven` y añadir `C:\maven\bin` a la variable `Path`.
   Verificar: `mvn -v`

3. **MySQL o MariaDB en ejecución**
   Abrir el panel de control de XAMPP y pulsar **Start** en la fila *MySQL*.

> No hace falta instalar Tomcat: Maven lo descarga y lo ejecuta automáticamente.

## Puesta en marcha

### 1. Crear la base de datos con datos de prueba

```bash
C:\xampp\mysql\bin\mysql.exe -u root < src/main/resources/sql/bd_farmacia_completa.sql
```

O bien abrir <http://localhost/phpmyadmin>, pestaña **SQL**, y pegar el contenido de
`src/main/resources/sql/bd_farmacia_completa.sql`.

Al final el script muestra una tabla con el conteo de registros de las 13 tablas.

### 2. Ajustar la conexión si es necesario

Editar `src/main/resources/db.properties` si el usuario o la contraseña de MySQL
son distintos de los valores por defecto de XAMPP (`root` sin contraseña).

### 3. Compilar y ejecutar

```bash
cd FarmaWeb
mvn clean package
mvn cargo:run
```

La primera ejecución descarga Tomcat 10.1 (unos 15 MB) y las dependencias.

### 4. Abrir la aplicación

<http://localhost:8080/farmaweb>

### Usuarios de prueba

| Correo | Contraseña | Rol |
|---|---|---|
| admin@farmacia.com | admin123 | Administrador |
| carlos.ruiz@farmacia.com | farma123 | Farmacéutico |
| laura.gomez@correo.com | cliente123 | Cliente |

Las contraseñas se almacenan cifradas con SHA‐256; nunca en texto plano.

## Los 13 módulos y su tabla

| Módulo | Ruta | Tabla |
|---|---|---|
| Roles | `/roles` | `TblRol` |
| Usuarios | `/usuarios` | `TblUsuario` |
| Clientes | `/clientes` | `TblCliente` |
| Farmacéuticos | `/farmaceuticos` | `TblFarmaceutico` |
| Productos | `/productos` | `TblProducto` |
| Inventario por lotes | `/lotes` | `TblLoteProducto` |
| Favoritos | `/favoritos` | `TblFavorito` |
| Pedidos | `/pedidos` | `TblPedido` |
| Detalle de pedidos | `/lineas` | `TblLineaPedido` |
| Fórmulas médicas | `/formulas` | `TblFormulaMedica` |
| Historial | `/historial` | `TblHistorial` |
| Pasarelas de pago | `/pasarelas` | `TblPasarelaPago` |
| Transacciones | `/transacciones` | `TblTransaccionPago` |

## Reglas de negocio implementadas en la capa de lógica

- El subtotal de cada línea y el total del pedido **siempre** se recalculan en el
  servidor con el precio vigente del catálogo.
- No se puede vender más unidades de las disponibles en los lotes no vencidos.
- Los medicamentos marcados como controlados exigen una fórmula médica vigente
  asociada al pedido antes de poder agregarse.
- No se almacenan fórmulas médicas vencidas ni lotes con fecha de vencimiento pasada.
- Solo se aceptan archivos de receta en formato PDF, JPG o PNG.
- El sistema avisa si se intenta aprobar dos veces el pago del mismo pedido.
- Correos de usuario, nombres de rol, códigos SKU, registros profesionales y
  proveedores de pago no se pueden duplicar.
- La edad del cliente se calcula a partir de su fecha de nacimiento.
- El inventario alerta los lotes que vencen en 30 días o menos.

## Despliegue alternativo en un Tomcat instalado

```bash
mvn clean package
```

Copiar `target/farmaweb.war` a la carpeta `webapps` de Tomcat 10.1 y abrir
<http://localhost:8080/farmaweb>.

## Convenciones de código aplicadas

Según GA7‐220501096‐AA1‐EV02: clases en `PascalCase`, atributos y métodos en
`camelCase` (métodos iniciando con verbo), constantes en `UPPER_CASE`, indentación
de 4 espacios, llave de apertura al final de la declaración, línea en blanco entre
métodos y nombres descriptivos autodocumentados.
