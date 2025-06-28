create database chefnet

CREATE TABLE usuarios (
	idUsuario INT NOT NULL IDENTITY CONSTRAINT pk_usuarios PRIMARY KEY,
	mail VARCHAR(150) UNIQUE,
	nickname VARCHAR(100) NOT NULL,
	password VARCHAR(40) NOT NULL,
	habilitado VARCHAR(2) CONSTRAINT chk_habilitado CHECK (habilitado IN ('Si', 'No')),
	nombre VARCHAR(150),
	direccion VARCHAR(150),
	avatar VARCHAR(300), -- url de la imagen del avatar
	tipo VARCHAR(50) NULL,
	medio_pago VARCHAR(100) NULL,
	codigo_recuperacion VARCHAR(10) NULL,
	verification_code_sent_at DATETIME2 NULL,
	rol VARCHAR(20) null 
);


create table alumnos (
	idAlumno int not null constraint pk_alumnos primary key,
	numeroTarjeta varchar(12),
	dniFrente varchar(300), -- url de la imagen del dni
	dniFondo varchar(300), -- url de la imagen del dni
	tramite varchar(12),
    cuentaCorriente decimal(12,2),
	idCronograma int,
	constraint fk_alumnos_cronograma foreign key (idCronograma) references cronogramaCursos(idCronograma),
	constraint fk_alumnos_usuarios foreign key (idAlumno) references usuarios(idUsuario)
)

create table tiposReceta(
	idTipo int not null identity constraint pk_tipos primary key,
	descripcion varchar(250)
)

create table recetas(
	idReceta int not null identity constraint pk_recetas primary key,
	idUsuario int,
	nombreReceta varchar(500),
	descripcionReceta varchar(1000),
	fotoPrincipal varchar(300), -- url de la imagen del plato, siempre tiene al menos una les demas estarán en la tabla fotos.
	porciones int,
	cantidadPersonas int,
	idTipo int,
	fecha date,
	autorizada bit default 0, -- 0 = pendiente, 1 = autorizada
	instrucciones varchar(4000), -- Campo para almacenar las instrucciones de la receta
	constraint fk_recetas_usuarios foreign key (idUsuario) references usuarios,
	constraint fk_recetas_tipos foreign key (idTipo) references tiposReceta
)

create table ingredientes( 
	idIngrediente int not null identity constraint pk_ingredientes primary key,
	nombre varchar(200),
	cantidad FLOAT,
	unidadMedida VARCHAR(50),
	idReceta int, -- Corregido para referenciar recetas
	constraint fk_ingredientes_recetas foreign key (idReceta) references recetas(idReceta)
)


create table unidades(
	idUnidad int not null identity constraint pk_unidades primary key,
	descripcion varchar(50) not null
)

create table utilizados (
	idUtilizado int not null identity constraint pk_utilizados primary key,
	idReceta int,
	idIngrediente int,
	cantidad int,
	idUnidad int,
	observaciones varchar(500), /*Son comentarios sobre el ingrediente*/
	constraint fk_utilizados_recetas foreign key (idReceta) references recetas,
	constraint fk_utilizados_ingredientes foreign key (idIngrediente) references ingredientes,
	constraint fk_utilizados_unidades foreign key (idUnidad) references unidades
)

create table calificaciones(
	idCalificacion int not null identity constraint pk_calificaciones primary key,
	idusuario int, /*usuario de la calificacion no de la receta*/
	idReceta int,
	calificacion int, /*Si no utiliza un valr numerico hay que cambiar el tipo*/
	comentarios varchar(500),
	autorizados tinyint DEFAULT (0) null,
	constraint fk_calificaciones_usuarios foreign key (idusuario) references usuarios(idUsuario),
	constraint fk_calificaciones_recetas foreign key (idReceta) references recetas(idReceta),
	constraint uk_usuario_receta unique (idusuario, idReceta)
)

create table conversiones(
	idConversion int not null identity constraint pk_conversiones primary key,
	idUnidadOrigen int not null,
	idUnidadDestino int not null,
	factorConversiones float,
	constraint fk_unidad_origen foreign key (idUnidadOrigen) references unidades (idUnidad),
	constraint fk_unidad_destino foreign key (idUnidadDestino) references unidades (idUnidad)
)

create table pasos(
	idPaso int not null identity constraint pk_pasos primary key,
	idReceta int,
	nroPaso int,
	texto varchar(4000),
	constraint fk_pasos_recetas foreign key (idReceta) references recetas
)

create table fotos(
	idfoto int not null identity constraint pk_fotos primary key,
	idReceta int not null,
	urlFoto varchar(300),
	extension varchar(5),
	constraint fk_fotos_recetas foreign key (idReceta) references recetas
)

create table multimedia(
	idContenido int not null identity constraint pk_multimedia primary key,
	idPaso int not null,
	tipo_contenido varchar(10) constraint chk_tipo_contenido check (tipo_contenido in ('foto','video','audio')),
	extension varchar(5),
	urlContenido varchar(300),
	constraint fk_multimedia_pasos foreign key (idPaso) references pasos
)

create table sedes(
    idSede int not null identity constraint pk_sedes primary key,
    nombreSede varchar(150) not null,
    direccionSede varchar(250) not null,
    telefonoSede varchar(15),
    mailSede varchar(150),
    whatsApp varchar(15),
    tipoBonificacion varchar(20),
    bonificacionCursos decimal(10,2)
    tipoPromocion varchar(20),
    promocionCursos decimal(10,2)
)

create table cursos(
    idCurso int not null identity constraint pk_cursos primary key, 
    descripcion varchar(300),
    contenidos varchar(500),
    requerimientos varchar(500),
    duracion int,
    precio decimal(12,2), 
    modalidad varchar(20) constraint chk_tipo_curso check (modalidad in ('presencial','remoto','virtual'))
)

create table cronogramaCursos(
    idCronograma int not null identity constraint pk_cronogramaCursos primary key, 
    idSede int not null,
    idCurso int not null,
    fechaInicio date,
    fechaFin date, 
    vacantesDisponibles int,
    constraint fk_cronogramaCursos_sedes foreign key (idSede) references sedes,
    constraint fk_cronogramaCursos_cursos foreign key (idCurso) references cursos
) 

create table asistenciaCursos(
    idAsistencia int not null identity constraint pk_asistenciaCursos primary key, 
    idAlumno int not null,
    idCronograma int not null,
    fecha datetime,
    constraint fk_asistenciaCursos_alumnos foreign key (idAlumno) references alumnos,
    constraint fk_asistenciaCursos_cronograma foreign key (idCronograma) references cronogramaCursos
)

CREATE TABLE inscripciones (
    idInscripcion INT NOT NULL IDENTITY PRIMARY KEY,
    idAlumno INT NOT NULL,
    idCronograma INT NOT NULL,
    fechaInscripcion DATETIME DEFAULT GETDATE(),
	estadoInscripcion VARCHAR(20) CHECK (estadoInscripcion IN ('inscrito', 'cancelado')),
    estadoPago VARCHAR(20) CHECK (estadoPago IN ('pendiente', 'pagado', 'fallido')),
    monto DECIMAL(12,2),
    constraint fk_inscripciones_alumnos FOREIGN KEY (idAlumno) REFERENCES alumnos(idAlumno),
    constraint fk_inscripciones_cronograma FOREIGN KEY (idCronograma) REFERENCES cronogramaCursos(idCronograma)
)

create table recetas_a_intentar (
	idReceta int not null,
	idUsuario int not null,
	completada bit default 0,
	fechaCompletada datetime null,
	fechaAgregada datetime default getdate(),
	constraint pk_recetas_a_intentar primary key (idReceta, idUsuario),
	constraint fk_recetas_a_intentar_recetas foreign key (idReceta) references recetas,
	constraint fk_recetas_a_intentar_usuarios foreign key (idUsuario) references usuarios
)

-- Insertar usuarios
INSERT INTO usuarios (mail, nickname, password, habilitado, nombre, direccion, avatar, rol)
VALUES 
('juan.perez@mail.com', 'Juanito', 'password123', 'Si', 'Juan Pérez', 'Calle Falsa 123', 'https://example.com/avatar1.png', 'usuario'),
('maria.lopez@mail.com', 'MariaL', 'pass456', 'Si', 'María López', 'Av. Siempre Viva 456', 'https://example.com/avatar2.png', 'usuario'),
('pedro.garcia@mail.com', 'PGarcia', 'pedro789', 'Si', 'Pedro García', 'Calle Luna 789', 'https://example.com/avatar3.png', 'admin');

-- Insertar alumnos
INSERT INTO alumnos (idAlumno, numeroTarjeta, dniFrente, dniFondo, tramite, cuentaCorriente)
VALUES 
(1, '123456789012', 'https://example.com/dni1_front.png', 'https://example.com/dni1_back.png', 'AB1234', 5000.00),
(2, '987654321098', 'https://example.com/dni2_front.png', 'https://example.com/dni2_back.png', 'CD5678', 7500.00);

-- Insertar tipos de receta
INSERT INTO tiposReceta (descripcion)
VALUES 
('Postres'),
('Ensaladas'),
('Sopas');

-- Insertar recetas con instrucciones
INSERT INTO recetas (idUsuario, nombreReceta, descripcionReceta, fotoPrincipal, porciones, cantidadPersonas, idTipo, fecha, autorizada, instrucciones)
VALUES 
(1, 'Tarta de Manzana', 'Una deliciosa tarta de manzana con canela.', 'https://example.com/tarta_manzana.png', 8, 4, 1, '2024-01-15', 1, 'Precalentar el horno a 180°C.
Mezclar la harina con el azúcar en un bol.
Pelar y cortar las manzanas en rodajas finas.
Colocar las manzanas sobre la masa.
Hornear durante 45 minutos hasta dorar.'),
(2, 'Ensalada César', 'Clásica ensalada césar con pollo y aderezo.', 'https://example.com/ensalada_cesar.png', 2, 1, 2, '2024-01-16', 1, 'Lavar y cortar la lechuga en trozos.
Cocinar el pollo a la plancha y cortarlo en tiras.
Mezclar la lechuga con el pollo.
Agregar el aderezo césar y mezclar bien.
Servir inmediatamente.'),
(1, 'Brownies de Chocolate', 'Brownies húmedos con chispas de chocolate y nueces.', 'https://example.com/brownies.png', 12, 6, 1, '2024-01-20', 0, 'Derretir el chocolate con manteca.
Batir los huevos con azúcar.
Incorporar el chocolate derretido.
Agregar harina y mezclar.
Hornear 25 minutos.'),
(2, 'Sopa de Tomate', 'Cremosa sopa de tomate con albahaca fresca.', 'https://example.com/sopa_tomate.png', 4, 2, 3, '2024-01-21', 0, 'Sofreír cebolla en aceite.
Agregar tomates pelados.
Cocinar 15 minutos.
Licuar hasta obtener consistencia cremosa.
Condimentar con sal y albahaca.'),
(1, 'Pizza Margherita', 'Pizza clásica con tomate, mozzarella y albahaca.', 'https://example.com/pizza.png', 4, 2, 1, '2024-01-22', 0, 'Extender la masa de pizza.
Cubrir con salsa de tomate.
Agregar mozzarella en trozos.
Hornear a 220°C por 12 minutos.
Decorar con albahaca fresca.');

-- Insertar ingredientes con recetas asociadas
INSERT INTO ingredientes (nombre, cantidad, unidadMedida, idReceta)
VALUES 
('Harina', 200, 'gramos', 1),
('Manzanas', 3, 'piezas', 1),
('Azúcar', 150, 'gramos', 1),
('Pollo', 250, 'gramos', 2),
('Lechuga', 1, 'unidad', 2),
('Aderezo César', 2, 'cucharadas', 2);

-- Insertar unidades
INSERT INTO unidades (descripcion)
VALUES 
('gramos'),
('kilogramos'),
('piezas'),
('tazas');

-- Insertar utilizados
INSERT INTO utilizados (idReceta, idIngrediente, cantidad, idUnidad, observaciones)
VALUES 
(1, 1, 200, 1, 'Usar harina refinada.'),
(1, 2, 3, 3, 'Manzanas frescas.'),
(2, 3, 1, 3, 'Pechuga de pollo cocida.'),
(2, 4, 1, 3, 'Lechuga romana fresca.');

-- Insertar calificaciones
INSERT INTO calificaciones (idusuario, idReceta, calificacion, comentarios)
VALUES 
(3, 1, 5, 'Deliciosa receta, muy bien explicada.'),
(1, 2, 4, 'Fácil de preparar, pero mejoraría el aderezo.');

-- Insertar conversiones
INSERT INTO conversiones (idUnidadOrigen, idUnidadDestino, factorConversiones)
VALUES 
(1, 2, 0.001),
(2, 1, 1000),
(3, 4, 0.25);

-- Insertar pasos
INSERT INTO pasos (idReceta, nroPaso, texto)
VALUES 
(1, 1, 'Precalentar el horno a 180 grados.'),
(1, 2, 'Mezclar harina y azúcar.'),
(1, 3, 'Agregar las manzanas en rodajas.'),
(2, 1, 'Lavar y cortar la lechuga.'),
(2, 2, 'Cocinar el pollo y cortarlo en trozos.');

-- Insertar fotos
INSERT INTO fotos (idReceta, urlFoto, extension)
VALUES 
(1, 'https://example.com/tarta_manzana_step1.png', '.png'),
(2, 'https://example.com/ensalada_cesar_step1.png', '.png');

-- Insertar multimedia
INSERT INTO multimedia (idPaso, tipo_contenido, extension, urlContenido)
VALUES 
(1, 'foto', '.jpg', 'https://example.com/precalentar_horno.jpg'),
(2, 'video', '.mp4', 'https://example.com/mezclar_harina.mp4');

-- Insertar sedes
INSERT INTO sedes (nombreSede, direccionSede, telefonoSede, mailSede, whatsApp, tipoBonificacion, bonificacionCursos, tipoPromocion, promocionCursos)
VALUES 
('Sede Central', 'Av. Principal 123', '555-1234', 'central@mail.com', '555-5678', 'Descuento', 10.00, '2x1', 0.00);

-- Insertar cursos
INSERT INTO cursos (descripcion, contenidos, requerimientos, duracion, precio, modalidad)
VALUES 
('Curso de Pastelería', 'Aprenderás técnicas básicas de pastelería.', 'Sin requisitos previos.', 20, 1500.00, 'presencial');

-- Insertar cronogramaCursos
INSERT INTO cronogramaCursos (idSede, idCurso, fechaInicio, fechaFin, vacantesDisponibles)
VALUES 
(1, 1, '2025-04-01', '2025-05-01', 20);

-- Insertar asistenciaCursos
INSERT INTO asistenciaCursos (idAlumno, idCronograma, fecha)
VALUES 
(1, 1, '2025-04-01 10:00:00');

-- Actualizar tabla existente para agregar nuevos campos (ejecutar si la tabla ya existe)
ALTER TABLE recetas_a_intentar ADD completada bit default 0;
ALTER TABLE recetas_a_intentar ADD fechaCompletada datetime null;
ALTER TABLE recetas_a_intentar ADD fechaAgregada datetime default getdate();

-- Insertar datos de prueba en recetas_a_intentar
INSERT INTO recetas_a_intentar (idReceta, idUsuario, completada, fechaCompletada, fechaAgregada)
VALUES 
(1, 1, 0, null, getdate()), 
(2, 1, 1, '2024-01-25', '2024-01-20'),  
(1, 2, 0, null, getdate());  


-- PRIMERO: Corregir la estructura de la tabla sedes (falta una coma)
ALTER TABLE sedes ADD CONSTRAINT temp_check CHECK (1=1);
ALTER TABLE sedes DROP CONSTRAINT temp_check;



-- Crear tabla para códigos de verificación
CREATE TABLE verification_codes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    code VARCHAR(6) NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    expires_at DATETIME2 NOT NULL,
    used BIT DEFAULT 0
);

-- Crear índices para la tabla verification_codes
CREATE INDEX idx_email ON verification_codes (email);
CREATE INDEX idx_expires ON verification_codes (expires_at);

-- Agregar campos de estado a usuarios
ALTER TABLE usuarios 
ADD registration_status VARCHAR(20) DEFAULT 'pending'
CHECK (registration_status IN ('pending', 'incomplete', 'complete'));

ALTER TABLE usuarios 
ADD verification_code_sent_at DATETIME2 NULL;