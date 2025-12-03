-- ========================================
-- DATOS INICIALES PARA E-Pa-Ce
-- Este script inserta los datos básicos necesarios para el funcionamiento del sistema
-- ========================================

-- ========================================
-- 1. ROLES DEL SISTEMA
-- ========================================
INSERT INTO Rol (Nombre_Rol) VALUES ('ADMIN');
INSERT INTO Rol (Nombre_Rol) VALUES ('OPERADOR');
INSERT INTO Rol (Nombre_Rol) VALUES ('MONITOR');
INSERT INTO Rol (Nombre_Rol) VALUES ('INVITADO');

-- ========================================
-- 2. MODALIDADES DE EVENTO
-- ========================================
INSERT INTO Modalidad_Evento (Nombre_ModalidadEvento) VALUES ('Presencial');
INSERT INTO Modalidad_Evento (Nombre_ModalidadEvento) VALUES ('Virtual');
INSERT INTO Modalidad_Evento (Nombre_ModalidadEvento) VALUES ('Híbrido');

-- ========================================
-- 3. TIPOS DE EVENTO
-- ========================================
INSERT INTO Tipo_Evento (Nombre_TipoEvento) VALUES ('Capacitación');
INSERT INTO Tipo_Evento (Nombre_TipoEvento) VALUES ('Taller');
INSERT INTO Tipo_Evento (Nombre_TipoEvento) VALUES ('Conferencia');
INSERT INTO Tipo_Evento (Nombre_TipoEvento) VALUES ('Seminario');
INSERT INTO Tipo_Evento (Nombre_TipoEvento) VALUES ('Webinar');
INSERT INTO Tipo_Evento (Nombre_TipoEvento) VALUES ('Torneo');

-- ========================================
-- 4. USUARIO ADMIN POR DEFECTO
-- Contraseña: admin123 (hasheada con BCrypt)
-- ========================================
-- Nota: La contraseña debe ser hasheada. El hash a continuación corresponde a "admin123"
-- Hash BCrypt de "admin123": $2a$10$xVj6NmNFnJYEJpjLxl6MdOOdU97aRp0YaA8x.8LhEqOHx2gJy6xOi

INSERT INTO Participantes (Documento_Identidad, Nombre, Apellido, Email, Password, RolID_Rol, Fecha_Creacion)
VALUES (1000000001, 'Admin', 'Sistema', 'admin@epace.com', '$2a$10$xVj6NmNFnJYEJpjLxl6MdOOdU97aRp0YaA8x.8LhEqOHx2gJy6xOi', 1, CURRENT_TIMESTAMP);

-- Usuario Operador
-- Contraseña: operador123
INSERT INTO Participantes (Documento_Identidad, Nombre, Apellido, Email, Password, RolID_Rol, Fecha_Creacion)
VALUES (1000000002, 'Operador', 'Sistema', 'operador@epace.com', '$2a$10$xVj6NmNFnJYEJpjLxl6MdOOdU97aRp0YaA8x.8LhEqOHx2gJy6xOi', 2, CURRENT_TIMESTAMP);

-- Usuario Monitor
-- Contraseña: monitor123
INSERT INTO Participantes (Documento_Identidad, Nombre, Apellido, Email, Password, RolID_Rol, Fecha_Creacion)
VALUES (1000000003, 'Monitor', 'Sistema', 'monitor@epace.com', '$2a$10$xVj6NmNFnJYEJpjLxl6MdOOdU97aRp0YaA8x.8LhEqOHx2gJy6xOi', 3, CURRENT_TIMESTAMP);

-- Usuario Invitado
-- Contraseña: invitado123
INSERT INTO Participantes (Documento_Identidad, Nombre, Apellido, Email, Password, RolID_Rol, Fecha_Creacion)
VALUES (1000000004, 'Invitado', 'Sistema', 'invitado@epace.com', '$2a$10$xVj6NmNFnJYEJpjLxl6MdOOdU97aRp0YaA8x.8LhEqOHx2gJy6xOi', 4, CURRENT_TIMESTAMP);

-- ========================================
-- 5. EVENTO DE EJEMPLO
-- ========================================
INSERT INTO Eventos (Nombre, Descripcion, Fecha_Inicio, Fecha_Fin, Duracion_Horas, Aforo_Maximo, Estado, Modalidad_EventoID_ModalidadEvento, Tipo_EventoID_TipoEvento)
VALUES (
    'Taller de Spring Boot',
    'Taller introductorio a Spring Boot y desarrollo de APIs REST',
    CURRENT_TIMESTAMP + INTERVAL '7 days',
    CURRENT_TIMESTAMP + INTERVAL '7 days' + INTERVAL '4 hours',
    4.0,
    50,
    'ACTIVO',
    1,  -- Presencial
    2   -- Taller
);

-- ========================================
-- VERIFICACIÓN
-- ========================================
-- Puedes ejecutar estas consultas para verificar que los datos se insertaron correctamente:
-- SELECT * FROM Rol;
-- SELECT * FROM Modalidad_Evento;
-- SELECT * FROM Tipo_Evento;
-- SELECT * FROM Participantes;
-- SELECT * FROM Eventos;
