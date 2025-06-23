-- Insertar alumno faltante para usuario ID 56
INSERT INTO alumnos (idAlumno, numeroTarjeta, dniFrente, dniFondo, tramite, cuentaCorriente)
VALUES 
(56, '4543543543255352', 'https://example.com/dni56_front.png', 'https://example.com/dni56_back.png', 'TR5678', 10000.00);

-- Verificar que se insertó correctamente
SELECT * FROM alumnos WHERE idAlumno = 56;

-- Verificar también el usuario correspondiente
SELECT * FROM usuarios WHERE idUsuario = 56; 