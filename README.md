# ModuloFarma — Codificación de módulos del software

Evidencia **GA7‐220501096‐AA2‐EV01** del proyecto formativo *Sistema de Gestión de
Farmacia en Línea*.
SENA — Tecnólogo en Análisis y Desarrollo de Software — Ficha 3235909.


## Contenido del repositorio

| Carpeta / archivo | Descripción |
|---|---|
| [FarmaWeb/](FarmaWeb/) | **Entrega actual.** Aplicación web Java con los 13 módulos del modelo relacional: Servlets, JSP, JDBC y arquitectura en tres capas. Ver [FarmaWeb/README.md](FarmaWeb/README.md) |
| [script.js](script.js), [config/](config/), [controllers/](controllers/), [public/](public/) | Primera versión del módulo de medicamentos en Node.js, conservada como evidencia del avance anterior |
| [Script_BD_Farmacia.sql](Script_BD_Farmacia.sql) | Script DDL original del modelo relacional (GA6‐220501096‐AA2‐EV02) |
| [BD_Evidencia_Farmacia.mwb](BD_Evidencia_Farmacia.mwb) | Modelo relacional en MySQL Workbench |

## Por qué dos proyectos

La primera entrega se construyó con Node.js nativo. Los criterios de evaluación de
esta evidencia exigen **formularios HTML con servlets**, **uso de los métodos GET y
POST** y **elementos de JSP**, que son tecnologías de la plataforma Java; además, el
documento de estándares de codificación GA7‐220501096‐AA1‐EV02 ya define Java como
el lenguaje del proyecto.

Por eso los módulos se desarrollaron en `FarmaWeb/` sobre Java (Jakarta Servlets +
JSP + JDBC), y la versión en Node.js se conserva intacta para documentar la
trazabilidad del avance.

## Cómo ejecutar

Las instrucciones completas de instalación, creación de la base de datos y ejecución
están en [FarmaWeb/README.md](FarmaWeb/README.md).

Resumen:

```bash
# 1. Iniciar MySQL desde el panel de XAMPP
# 2. Crear la base de datos con datos de prueba
C:\xampp\mysql\bin\mysql.exe -u root < FarmaWeb/src/main/resources/sql/bd_farmacia_completa.sql

# 3. Compilar y levantar el servidor (Tomcat embebido)
cd FarmaWeb
mvn clean package
mvn cargo:run

# 4. Abrir http://localhost:8080/farmaweb
```

Requiere **JDK 17** y **Maven 3.9+** instalados.

## Los 13 módulos desarrollados

Roles · Usuarios · Clientes · Farmacéuticos · Productos · Inventario por lotes ·
Favoritos · Pedidos · Detalle de pedidos · Fórmulas médicas · Historial ·
Pasarelas de pago · Transacciones de pago

Cada módulo ofrece consulta, registro, edición y eliminación, con validaciones de
negocio en la capa de lógica.

## Identidad visual

Paleta definida en la evidencia GA5‐220501095‐AA1‐EV04:
verde `#2E7D32` (principal), blanco `#FFFFFF` (secundario), gris `#F5F5F5`
(complementario), tipografía Roboto/Arial y contraste conforme a WCAG.
