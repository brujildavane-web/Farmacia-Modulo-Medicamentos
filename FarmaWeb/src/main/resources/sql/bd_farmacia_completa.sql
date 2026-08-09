-- ============================================================================
--  Base de datos del Sistema de Gestion de Farmacia en Linea
--  Proyecto formativo SENA - ficha 3235909
--
--  Estructura tomada de la evidencia GA6-220501096-AA2-EV02 (modelo relacional)
--  mas los datos de prueba necesarios para operar los trece modulos de la
--  aplicacion FarmaWeb.
--
--  Ejecucion recomendada desde la consola de XAMPP:
--     C:\xampp\mysql\bin\mysql.exe -u root < bd_farmacia_completa.sql
--  o pegando el contenido en la pestana SQL de phpMyAdmin.
-- ============================================================================

DROP DATABASE IF EXISTS bd_farmacia;
CREATE DATABASE bd_farmacia CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bd_farmacia;

-- ----------------------------------------------------------------- ESTRUCTURA

-- Perfiles de acceso al sistema
CREATE TABLE TblRol (
    Rol_IdRol INT AUTO_INCREMENT PRIMARY KEY,
    Rol_NombreRol VARCHAR(50) NOT NULL,
    Rol_Permisos VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- Credenciales de ingreso, una por persona
CREATE TABLE TblUsuario (
    Usu_IdUsuario INT AUTO_INCREMENT PRIMARY KEY,
    Usu_Email VARCHAR(150) NOT NULL UNIQUE,
    Usu_Password VARCHAR(255) NOT NULL,
    Usu_FechaRegistro DATE NOT NULL,
    TblRol_RolIdRol INT NOT NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (TblRol_RolIdRol)
        REFERENCES TblRol(Rol_IdRol) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Profesionales que validan formulas y despachan medicamentos
CREATE TABLE TblFarmaceutico (
    Far_IdFarmaceutico INT AUTO_INCREMENT PRIMARY KEY,
    Farm_RegistroProfesional VARCHAR(50) NOT NULL,
    Far_Especialidad VARCHAR(30),
    Far_Nombre VARCHAR(50) NOT NULL,
    Far_Apellido VARCHAR(50) NOT NULL,
    Far_Telefono VARCHAR(20),
    TblUsu_UsuIdUsuario INT NOT NULL,
    CONSTRAINT fk_farmaceutico_usuario FOREIGN KEY (TblUsu_UsuIdUsuario)
        REFERENCES TblUsuario(Usu_IdUsuario) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Personas que compran en la farmacia
CREATE TABLE TblCliente (
    Clie_IdCliente INT AUTO_INCREMENT PRIMARY KEY,
    Cli_Nombre VARCHAR(50) NOT NULL,
    Cli_Apellido VARCHAR(50) NOT NULL,
    Cli_Direccion VARCHAR(250),
    Cli_Telefono VARCHAR(20),
    Cli_Email VARCHAR(150),
    Cli_FechaNacimiento DATE,
    Cli_Edad INT,
    TblUsu_UsuIdUsuario INT NOT NULL,
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (TblUsu_UsuIdUsuario)
        REFERENCES TblUsuario(Usu_IdUsuario) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Catalogo de medicamentos y productos
CREATE TABLE TblProducto (
    Pro_IdProducto INT AUTO_INCREMENT PRIMARY KEY,
    Pro_Nombre VARCHAR(200) NOT NULL,
    Pro_SkuCode VARCHAR(50) UNIQUE,
    Pro_Descripcion VARCHAR(300),
    Pro_Precio DECIMAL(10,2) NOT NULL,
    Pro_RequiereReceta BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB;

-- Productos marcados por los clientes para repetir la compra
CREATE TABLE TblFavorito (
    TblCli_ClieIdCliente INT NOT NULL,
    TblPro_ProIdProducto INT NOT NULL,
    Fav_FechaMarcacion DATE NOT NULL,
    PRIMARY KEY (TblCli_ClieIdCliente, TblPro_ProIdProducto),
    CONSTRAINT fk_fav_cliente FOREIGN KEY (TblCli_ClieIdCliente)
        REFERENCES TblCliente(Clie_IdCliente),
    CONSTRAINT fk_fav_producto FOREIGN KEY (TblPro_ProIdProducto)
        REFERENCES TblProducto(Pro_IdProducto)
) ENGINE=InnoDB;

-- Lotes que dan trazabilidad al inventario
CREATE TABLE TblLoteProducto (
    LotP_IdLote INT AUTO_INCREMENT PRIMARY KEY,
    LotP_FechaVencimiento DATE NOT NULL,
    LotP_RegistroSanitario VARCHAR(50) NOT NULL,
    LotP_StockActual FLOAT NOT NULL,
    LotP_Marca VARCHAR(20),
    TblPro_ProIdProducto INT NOT NULL,
    CONSTRAINT fk_lote_producto FOREIGN KEY (TblPro_ProIdProducto)
        REFERENCES TblProducto(Pro_IdProducto)
) ENGINE=InnoDB;

-- Encabezado de los pedidos
CREATE TABLE TblPedido (
    Ped_IdPedido INT AUTO_INCREMENT PRIMARY KEY,
    Ped_Fecha DATETIME NOT NULL,
    Ped_Total DECIMAL(10,2) NOT NULL,
    Ped_Estado VARCHAR(50) NOT NULL,
    TblCli_ClieIdCliente INT NOT NULL,
    TblFar_FarIdFarmaceutico INT NOT NULL,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (TblCli_ClieIdCliente)
        REFERENCES TblCliente(Clie_IdCliente),
    CONSTRAINT fk_pedido_farmaceutico FOREIGN KEY (TblFar_FarIdFarmaceutico)
        REFERENCES TblFarmaceutico(Far_IdFarmaceutico)
) ENGINE=InnoDB;

-- Bitacora de estados de los pedidos
CREATE TABLE TblHistorial (
    His_IdPedidoHistorico INT AUTO_INCREMENT PRIMARY KEY,
    TblPed_PedIdPedido INT NOT NULL,
    His_Fecha DATE NOT NULL,
    TblPro_ProIdProducto INT NOT NULL,
    His_Estado VARCHAR(20),
    CONSTRAINT fk_historial_pedido FOREIGN KEY (TblPed_PedIdPedido)
        REFERENCES TblPedido(Ped_IdPedido),
    CONSTRAINT fk_historial_producto FOREIGN KEY (TblPro_ProIdProducto)
        REFERENCES TblProducto(Pro_IdProducto)
) ENGINE=InnoDB;

-- Detalle de los productos de cada pedido
CREATE TABLE TblLineaPedido (
    Lin_IdLinea INT AUTO_INCREMENT PRIMARY KEY,
    Lin_Cantidad FLOAT NOT NULL,
    Lin_Precio DECIMAL(10,2) NOT NULL,
    Lin_Subtotal DECIMAL(10,2) NOT NULL,
    TblPed_PedIdPedido INT NOT NULL,
    TblPro_ProIdProducto INT NOT NULL,
    CONSTRAINT fk_linea_pedido FOREIGN KEY (TblPed_PedIdPedido)
        REFERENCES TblPedido(Ped_IdPedido),
    CONSTRAINT fk_linea_producto FOREIGN KEY (TblPro_ProIdProducto)
        REFERENCES TblProducto(Pro_IdProducto)
) ENGINE=InnoDB;

-- Prescripciones que respaldan los medicamentos controlados
CREATE TABLE TblFormulaMedica (
    For_IdFormula INT AUTO_INCREMENT PRIMARY KEY,
    For_FechaPrescripcion DATE NOT NULL,
    For_FechaVencimiento DATE,
    For_Archivo VARCHAR(50),
    TblPed_PedIdPedido INT NOT NULL,
    TblPro_ProIdProducto INT NOT NULL,
    CONSTRAINT fk_formula_pedido FOREIGN KEY (TblPed_PedIdPedido)
        REFERENCES TblPedido(Ped_IdPedido),
    CONSTRAINT fk_formula_producto FOREIGN KEY (TblPro_ProIdProducto)
        REFERENCES TblProducto(Pro_IdProducto)
) ENGINE=InnoDB;

-- Proveedores de pago habilitados
CREATE TABLE TblPasarelaPago (
    Pas_IdPasarela INT AUTO_INCREMENT PRIMARY KEY,
    Pas_NombreProveedor CHAR(50) NOT NULL,
    Pas_Apikeypublic VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

-- Pagos procesados por las pasarelas
CREATE TABLE TblTransaccionPago (
    Tran_IdTransaccion INT AUTO_INCREMENT PRIMARY KEY,
    Tran_FechaPago DATETIME NOT NULL,
    Tran_Valor DECIMAL(10,2) NOT NULL,
    Tran_EstadoTransaccion VARCHAR(50) NOT NULL,
    TblPed_PedIdPedido INT NOT NULL,
    TblPas_PasIdPasarela INT NOT NULL,
    CONSTRAINT fk_tran_pedido FOREIGN KEY (TblPed_PedIdPedido)
        REFERENCES TblPedido(Ped_IdPedido),
    CONSTRAINT fk_tran_pasarela FOREIGN KEY (TblPas_PasIdPasarela)
        REFERENCES TblPasarelaPago(Pas_IdPasarela)
) ENGINE=InnoDB;

-- ------------------------------------------------------------- DATOS DE PRUEBA

-- Roles del sistema
INSERT INTO TblRol (Rol_NombreRol, Rol_Permisos) VALUES
    ('Administrador', 'Gestionar usuarios, roles, inventario, reportes y campanas'),
    ('Farmaceutico', 'Validar formulas medicas, confirmar pedidos y despachar medicamentos'),
    ('Cliente', 'Consultar catalogo, realizar pedidos, cargar formulas y marcar favoritos');

-- Usuarios. Las contrasenas estan cifradas con SHA-256, igual que en la aplicacion:
--   admin123    -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
--   farma123    -> 67bd98d5370140b60be4acd261585ca82c1072559e9413f84b8b1f014e10f105
--   cliente123  -> 09a31a7001e261ab1e056182a71d3cf57f582ca9a29cff5eb83be0f0549730a9
INSERT INTO TblUsuario (Usu_Email, Usu_Password, Usu_FechaRegistro, TblRol_RolIdRol) VALUES
    ('admin@farmacia.com',
     '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', CURDATE(), 1),
    ('carlos.ruiz@farmacia.com',
     '67bd98d5370140b60be4acd261585ca82c1072559e9413f84b8b1f014e10f105', CURDATE(), 2),
    ('marta.leon@farmacia.com',
     '67bd98d5370140b60be4acd261585ca82c1072559e9413f84b8b1f014e10f105', CURDATE(), 2),
    ('laura.gomez@correo.com',
     '09a31a7001e261ab1e056182a71d3cf57f582ca9a29cff5eb83be0f0549730a9', CURDATE(), 3),
    ('diego.martinez@correo.com',
     '09a31a7001e261ab1e056182a71d3cf57f582ca9a29cff5eb83be0f0549730a9', CURDATE(), 3);

-- Farmaceuticos
INSERT INTO TblFarmaceutico
    (Farm_RegistroProfesional, Far_Especialidad, Far_Nombre, Far_Apellido, Far_Telefono,
     TblUsu_UsuIdUsuario) VALUES
    ('RP-2026-0451', 'Regencia de farmacia', 'Carlos', 'Ruiz', '3109876543', 2),
    ('RP-2026-0892', 'Farmacia clinica', 'Marta', 'Leon', '3157654321', 3);

-- Clientes
INSERT INTO TblCliente
    (Cli_Nombre, Cli_Apellido, Cli_Direccion, Cli_Telefono, Cli_Email,
     Cli_FechaNacimiento, Cli_Edad, TblUsu_UsuIdUsuario) VALUES
    ('Laura', 'Gomez', 'Calle 10 # 4-25, Popayan', '3001234567',
     'laura.gomez@correo.com', '1992-03-14', 34, 4),
    ('Diego', 'Martinez', 'Carrera 8 # 15-40, Popayan', '3012345678',
     'diego.martinez@correo.com', '1988-11-02', 37, 5);

-- Catalogo de productos
INSERT INTO TblProducto
    (Pro_Nombre, Pro_SkuCode, Pro_Descripcion, Pro_Precio, Pro_RequiereReceta) VALUES
    ('Paracetamol 500 mg', 'MED-001',
     'Analgesico y antipiretico, caja por 20 tabletas', 8500.00, FALSE),
    ('Ibuprofeno 400 mg', 'MED-002',
     'Antiinflamatorio no esteroideo, caja por 10 tabletas', 12300.00, FALSE),
    ('Amoxicilina 500 mg', 'MED-003',
     'Antibiotico de amplio espectro, caja por 15 capsulas', 24900.00, TRUE),
    ('Losartan 50 mg', 'MED-004',
     'Antihipertensivo, caja por 30 tabletas', 18700.00, TRUE),
    ('Suero oral con electrolitos', 'CUI-001',
     'Rehidratacion oral, botella de 500 ml', 5400.00, FALSE),
    ('Gel antibacterial 250 ml', 'CUI-002',
     'Higiene de manos con alcohol al 70 por ciento', 9900.00, FALSE);

-- Lotes del inventario. Las fechas se calculan sobre la fecha actual para que
-- el escenario de prueba siga siendo valido en cualquier momento.
INSERT INTO TblLoteProducto
    (LotP_FechaVencimiento, LotP_RegistroSanitario, LotP_StockActual, LotP_Marca,
     TblPro_ProIdProducto) VALUES
    (DATE_ADD(CURDATE(), INTERVAL 540 DAY), 'INVIMA-2026M-0034', 150, 'Genfar', 1),
    (DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'INVIMA-2026M-0035', 40, 'MK', 1),
    (DATE_ADD(CURDATE(), INTERVAL 400 DAY), 'INVIMA-2026M-0111', 90, 'Bayer', 2),
    (DATE_ADD(CURDATE(), INTERVAL 300 DAY), 'INVIMA-2026M-0220', 60, 'La Sante', 3),
    (DATE_ADD(CURDATE(), INTERVAL 730 DAY), 'INVIMA-2026M-0311', 120, 'Tecnoquimicas', 4),
    (DATE_ADD(CURDATE(), INTERVAL 250 DAY), 'INVIMA-2026M-0450', 200, 'Pisa', 5),
    (DATE_ADD(CURDATE(), INTERVAL 900 DAY), 'INVIMA-2026M-0510', 300, 'Nosotras', 6);

-- Pasarelas de pago
INSERT INTO TblPasarelaPago (Pas_NombreProveedor, Pas_Apikeypublic) VALUES
    ('PayU', 'pk_test_PayU_4Vj8k2026'),
    ('MercadoPago', 'APP_USR_pub_2026_mp001'),
    ('Stripe', 'pk_test_51StripeDemo2026');

-- Pedidos de ejemplo. El total corresponde a la suma de sus lineas.
INSERT INTO TblPedido
    (Ped_Fecha, Ped_Total, Ped_Estado, TblCli_ClieIdCliente, TblFar_FarIdFarmaceutico) VALUES
    (NOW(), 29300.00, 'ENTREGADO', 1, 1),
    (NOW(), 24900.00, 'PENDIENTE', 2, 2);

-- Formulas medicas: se registran antes de las lineas porque el sistema exige
-- prescripcion vigente para los productos controlados.
INSERT INTO TblFormulaMedica
    (For_FechaPrescripcion, For_FechaVencimiento, For_Archivo,
     TblPed_PedIdPedido, TblPro_ProIdProducto) VALUES
    (CURDATE(), DATE_ADD(CURDATE(), INTERVAL 60 DAY), 'receta-cliente-002.pdf', 2, 3);

-- Detalle de los pedidos
INSERT INTO TblLineaPedido
    (Lin_Cantidad, Lin_Precio, Lin_Subtotal, TblPed_PedIdPedido, TblPro_ProIdProducto) VALUES
    (2, 8500.00, 17000.00, 1, 1),
    (1, 12300.00, 12300.00, 1, 2),
    (1, 24900.00, 24900.00, 2, 3);

-- Bitacora de estados
INSERT INTO TblHistorial (TblPed_PedIdPedido, His_Fecha, TblPro_ProIdProducto, His_Estado) VALUES
    (1, CURDATE(), 1, 'CONFIRMADO'),
    (1, CURDATE(), 1, 'ENTREGADO'),
    (2, CURDATE(), 3, 'PENDIENTE');

-- Transacciones de pago
INSERT INTO TblTransaccionPago
    (Tran_FechaPago, Tran_Valor, Tran_EstadoTransaccion, TblPed_PedIdPedido, TblPas_PasIdPasarela)
VALUES
    (NOW(), 29300.00, 'APROBADA', 1, 1),
    (NOW(), 24900.00, 'PENDIENTE', 2, 2);

-- Favoritos
INSERT INTO TblFavorito (TblCli_ClieIdCliente, TblPro_ProIdProducto, Fav_FechaMarcacion) VALUES
    (1, 1, CURDATE()),
    (1, 5, CURDATE()),
    (2, 3, CURDATE());

-- --------------------------------------------------------------- VERIFICACION

SELECT 'Roles' AS Tabla, COUNT(*) AS Registros FROM TblRol
UNION ALL SELECT 'Usuarios', COUNT(*) FROM TblUsuario
UNION ALL SELECT 'Farmaceuticos', COUNT(*) FROM TblFarmaceutico
UNION ALL SELECT 'Clientes', COUNT(*) FROM TblCliente
UNION ALL SELECT 'Productos', COUNT(*) FROM TblProducto
UNION ALL SELECT 'Lotes', COUNT(*) FROM TblLoteProducto
UNION ALL SELECT 'Favoritos', COUNT(*) FROM TblFavorito
UNION ALL SELECT 'Pedidos', COUNT(*) FROM TblPedido
UNION ALL SELECT 'Lineas de pedido', COUNT(*) FROM TblLineaPedido
UNION ALL SELECT 'Historial', COUNT(*) FROM TblHistorial
UNION ALL SELECT 'Formulas medicas', COUNT(*) FROM TblFormulaMedica
UNION ALL SELECT 'Pasarelas de pago', COUNT(*) FROM TblPasarelaPago
UNION ALL SELECT 'Transacciones', COUNT(*) FROM TblTransaccionPago;
