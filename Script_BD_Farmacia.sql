-- Creación base de datos para proyecto de Farmacia

CREATE DATABASE bd_farmacia;
USE bd_farmacia;

-- Tabla para definir los roles de los usuarios
CREATE TABLE TblRol (
    Rol_IdRol INT AUTO_INCREMENT PRIMARY KEY,
    Rol_NombreRol VARCHAR(50) NOT NULL,
    Rol_Permisos VARCHAR(255) NOT NULL
);

-- Tabla de los usuarios que entran al sistema
CREATE TABLE TblUsuario (
    Usu_IdUsuario INT AUTO_INCREMENT PRIMARY KEY,
    Usu_Email VARCHAR(150) NOT NULL UNIQUE,
    Usu_Password VARCHAR(255) NOT NULL,
    Usu_FechaRegistro DATE NOT NULL,
    TblRol_RolIdRol INT NOT NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (TblRol_RolIdRol) 
        REFERENCES TblRol(Rol_IdRol) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Información detallada de los farmacéuticos
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
);

-- Información de los clientes de la farmacia
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
);

-- Catálogo de productos y medicamentos
CREATE TABLE TblProducto (
    Pro_IdProducto INT AUTO_INCREMENT PRIMARY KEY,
    Pro_Nombre VARCHAR(200) NOT NULL,
    Pro_SkuCode VARCHAR(50) UNIQUE,
    Pro_Descripcion VARCHAR(300),
    Pro_Precio DECIMAL(10,2) NOT NULL,
    Pro_RequiereReceta BOOLEAN DEFAULT FALSE
);

-- Tabla para los productos favoritos de los clientes
CREATE TABLE TblFavorito (
    TblCli_ClieIdCliente INT NOT NULL,
    TblPro_ProIdProducto INT NOT NULL,
    Fav_FechaMarcacion DATE NOT NULL,
    PRIMARY KEY (TblCli_ClieIdCliente, TblPro_ProIdProducto),
    CONSTRAINT fk_fav_cliente FOREIGN KEY (TblCli_ClieIdCliente) REFERENCES TblCliente(Clie_IdCliente),
    CONSTRAINT fk_fav_producto FOREIGN KEY (TblPro_ProIdProducto) REFERENCES TblProducto(Pro_IdProducto)
);

-- Control de los lotes de cada producto
CREATE TABLE TblLoteProducto (
    LotP_IdLote INT AUTO_INCREMENT PRIMARY KEY,
    LotP_FechaVencimiento DATE NOT NULL,
    LotP_RegistroSanitario VARCHAR(50) NOT NULL,
    LotP_StockActual FLOAT NOT NULL,
    LotP_Marca VARCHAR(20),
    TblPro_ProIdProducto INT NOT NULL,
    CONSTRAINT fk_lote_producto FOREIGN KEY (TblPro_ProIdProducto) REFERENCES TblProducto(Pro_IdProducto)
);

-- Encabezado de los pedidos realizados
CREATE TABLE TblPedido (
    Ped_IdPedido INT AUTO_INCREMENT PRIMARY KEY,
    Ped_Fecha DATETIME NOT NULL,
    Ped_Total DECIMAL(10,2) NOT NULL,
    Ped_Estado VARCHAR(50) NOT NULL,
    TblCli_ClieIdCliente INT NOT NULL,
    TblFar_FarIdFarmaceutico INT NOT NULL,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (TblCli_ClieIdCliente) REFERENCES TblCliente(Clie_IdCliente),
    CONSTRAINT fk_pedido_farmaceutico FOREIGN KEY (TblFar_FarIdFarmaceutico) REFERENCES TblFarmaceutico(Far_IdFarmaceutico)
);

-- Historial de estados de los pedidos
CREATE TABLE TblHistorial (
    His_IdPedidoHistorico INT AUTO_INCREMENT PRIMARY KEY,
    TblPed_PedIdPedido INT NOT NULL,
    His_Fecha DATE NOT NULL,
    TblPro_ProIdProducto INT NOT NULL,
    His_Estado VARCHAR(20),
    CONSTRAINT fk_historial_pedido FOREIGN KEY (TblPed_PedIdPedido) REFERENCES TblPedido(Ped_IdPedido),
    CONSTRAINT fk_historial_producto FOREIGN KEY (TblPro_ProIdProducto) REFERENCES TblProducto(Pro_IdProducto)
);

-- Detalle de los productos incluidos en cada pedido
CREATE TABLE TblLineaPedido (
    Lin_IdLinea INT AUTO_INCREMENT PRIMARY KEY,
    Lin_Cantidad FLOAT NOT NULL,
    Lin_Precio DECIMAL(10,2) NOT NULL,
    Lin_Subtotal DECIMAL(10,2) NOT NULL,
    TblPed_PedIdPedido INT NOT NULL,
    TblPro_ProIdProducto INT NOT NULL,
    CONSTRAINT fk_linea_pedido FOREIGN KEY (TblPed_PedIdPedido) REFERENCES TblPedido(Ped_IdPedido),
    CONSTRAINT fk_linea_producto FOREIGN KEY (TblPro_ProIdProducto) REFERENCES TblProducto(Pro_IdProducto)
);

-- Registro de fórmulas médicas adjuntas
CREATE TABLE TblFormulaMedica (
    For_IdFormula INT AUTO_INCREMENT PRIMARY KEY,
    For_FechaPrescripcion DATE NOT NULL,
    For_FechaVencimiento DATE,
    For_Archivo VARCHAR(50),
    TblPed_PedIdPedido INT NOT NULL,
    TblPro_ProIdProducto INT NOT NULL,
    CONSTRAINT fk_formula_pedido FOREIGN KEY (TblPed_PedIdPedido) REFERENCES TblPedido(Ped_IdPedido),
    CONSTRAINT fk_formula_producto FOREIGN KEY (TblPro_ProIdProducto) REFERENCES TblProducto(Pro_IdProducto)
);

-- Definición de las pasarelas de pago disponibles
CREATE TABLE TblPasarelaPago (
    Pas_IdPasarela INT AUTO_INCREMENT PRIMARY KEY,
    Pas_NombreProveedor CHAR(50) NOT NULL,
    Pas_Apikeypublic VARCHAR(50) NOT NULL
);

-- Registro de las transacciones de pago de los pedidos
CREATE TABLE TblTransaccionPago (
    Tran_IdTransaccion INT AUTO_INCREMENT PRIMARY KEY,
    Tran_FechaPago DATETIME NOT NULL,
    Tran_Valor DECIMAL(10,2) NOT NULL,
    Tran_EstadoTransaccion VARCHAR(50) NOT NULL,
    TblPed_PedIdPedido INT NOT NULL,
    TblPas_PasIdPasarela INT NOT NULL,
    CONSTRAINT fk_tran_pedido FOREIGN KEY (TblPed_PedIdPedido) REFERENCES TblPedido(Ped_IdPedido),
    CONSTRAINT fk_tran_pasarela FOREIGN KEY (TblPas_PasIdPasarela) REFERENCES TblPasarelaPago(Pas_IdPasarela)
);