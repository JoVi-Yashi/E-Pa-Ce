-- =====================================================
-- Script de inicialización de la base de datos EPaCe
-- =====================================================

-- =====================================================
-- 1. Inserción de ROLES
-- =====================================================
-- Los roles definen los niveles de acceso en el sistema
-- DEBEN coincidir con el Enum AppRole.java

INSERT INTO "Rol" ("ID_Rol", "Nombre_Rol") VALUES
(1, 'ADMIN'),
(2, 'USER'),
(3, 'ORGANIZADOR'),
(4, 'INVITADO')
ON CONFLICT ("ID_Rol") DO UPDATE SET "Nombre_Rol" = EXCLUDED."Nombre_Rol";

-- Ajustar la secuencia de IDs para la tabla Rol para evitar duplicados si se insertan nuevos
-- Esto es necesario porque insertamos IDs manuales arriba
SELECT setval(pg_get_serial_sequence('"Rol"', 'ID_Rol'), coalesce(max("ID_Rol"), 1), true) FROM "Rol";

-- =====================================================
-- 2. Inserción de TIPOS DE EVENTO
-- =====================================================

INSERT INTO "Tipo_Evento" ("Nombre_TipoEvento") VALUES
('Conferencia'),
('Taller'),
('Seminario'),
('Webinar'),
('Reunión')
ON CONFLICT DO NOTHING;

-- =====================================================
-- 3. Inserción de MODALIDADES DE EVENTO
-- =====================================================

INSERT INTO "Modalidad_Evento" ("Nombre_ModalidadEvento") VALUES
('Presencial'),
('Virtual'),
('Híbrido')
ON CONFLICT DO NOTHING;

-- =====================================================
-- 4. Usuario administrador de prueba
-- =====================================================
-- Contraseña: admin123 (encriptada con BCrypt)
-- IMPORTANTE: Cambiar en producción

INSERT INTO "Participantes" (
    "Documento_Identidad",
    "Nombre",
    "Apellido",
    "Email",
    "Password",
    "Fecha_Creacion"
) VALUES (
    1000000001,
    'Admin',
    'Sistema',
    'admin@epace.com',
    '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', -- admin123
    CURRENT_TIMESTAMP
)
ON CONFLICT ("Documento_Identidad") DO NOTHING;

-- =====================================================
-- 5. Usuarios de prueba para cada rol
-- =====================================================
-- Contraseña para todos: test123

-- USER (Antes Operador)
INSERT INTO "Participantes" (
    "Documento_Identidad",
    "Nombre",
    "Apellido",
    "Email",
    "Password",
    "Fecha_Creacion"
) VALUES (
    1000000002,
    'User',
    'Prueba',
    'user@epace.com',
    '$2a$10$N.hCNsD2GUr1VsaLECxFUODLF9LNn2xH.P0C6t2LYyU7h3mNGdY1u', -- test123
    CURRENT_TIMESTAMP
)
ON CONFLICT ("Documento_Identidad") DO NOTHING;

-- ORGANIZADOR (Antes Monitor)
INSERT INTO "Participantes" (
    "Documento_Identidad",
    "Nombre",
    "Apellido",
    "Email",
    "Password",
    "Fecha_Creacion"
) VALUES (
    1000000003,
    'Organizador',
    'Prueba',
    'organizador@epace.com',
    '$2a$10$N.hCNsD2GUr1VsaLECxFUODLF9LNn2xH.P0C6t2LYyU7h3mNGdY1u', -- test123
    CURRENT_TIMESTAMP
)
ON CONFLICT ("Documento_Identidad") DO NOTHING;

-- Invitado
INSERT INTO "Participantes" (
    "Documento_Identidad",
    "Nombre",
    "Apellido",
    "Email",
    "Password",
    "Fecha_Creacion"
) VALUES (
    1000000004,
    'Invitado',
    'Prueba',
    'invitado@epace.com',
    '$2a$10$N.hCNsD2GUr1VsaLECxFUODLF9LNn2xH.P0C6t2LYyU7h3mNGdY1u', -- test123
    CURRENT_TIMESTAMP
)
ON CONFLICT ("Documento_Identidad") DO NOTHING;

-- =====================================================
-- 6. Asignación de Roles (Tabla Intermedia)
-- =====================================================

INSERT INTO "Participante_Roles" ("Participante_Documento", "Rol_ID") VALUES
(1000000001, 1), -- Admin -> ADMIN
(1000000002, 2), -- User -> USER
(1000000003, 3), -- Organizador -> ORGANIZADOR
(1000000004, 4)  -- Invitado -> INVITADO
ON CONFLICT DO NOTHING;

-- =====================================================
-- 7. Verificación
-- =====================================================

SELECT 'Roles creados:' AS info;
SELECT * FROM "Rol";

SELECT 'Usuarios de prueba creados:' AS info;
SELECT p."Documento_Identidad", p."Nombre", p."Email", r."Nombre_Rol"
FROM "Participantes" p
JOIN "Participante_Roles" pr ON p."Documento_Identidad" = pr."Participante_Documento"
JOIN "Rol" r ON pr."Rol_ID" = r."ID_Rol";
