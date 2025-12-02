-- =====================================================
-- Script de inicialización de la base de datos EPaCe
-- =====================================================

-- =====================================================
-- 1. Inserción de ROLES
-- =====================================================
-- Los roles definen los niveles de acceso en el sistema

INSERT INTO "Rol" ("ID_Rol", "Nombre_Rol") VALUES
(1, 'ADMIN'),
(2, 'OPERADOR'),
(3, 'MONITOR'),
(4, 'INVITADO')
ON CONFLICT ("ID_Rol") DO NOTHING;

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
    "Fecha_Creacion",
    "RolID_Rol"
) VALUES (
    1000000001,
    'Admin',
    'Sistema',
    'admin@epace.com',
    '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', -- admin123
    CURRENT_TIMESTAMP,
    1
)
ON CONFLICT ("Documento_Identidad") DO NOTHING;

-- =====================================================
-- 5. Usuarios de prueba para cada rol
-- =====================================================
-- Contraseña para todos: test123

-- Operador
INSERT INTO "Participantes" (
    "Documento_Identidad",
    "Nombre",
    "Apellido",
    "Email",
    "Password",
    "Fecha_Creacion",
    "RolID_Rol"
) VALUES (
    1000000002,
    'Operador',
    'Prueba',
    'operador@epace.com',
    '$2a$10$N.hCNsD2GUr1VsaLECxFUODLF9LNn2xH.P0C6t2LYyU7h3mNGdY1u', -- test123
    CURRENT_TIMESTAMP,
    2
)
ON CONFLICT ("Documento_Identidad") DO NOTHING;

-- Monitor
INSERT INTO "Participantes" (
    "Documento_Identidad",
    "Nombre",
    "Apellido",
    "Email",
    "Password",
    "Fecha_Creacion",
    "RolID_Rol"
) VALUES (
    1000000003,
    'Monitor',
    'Prueba',
    'monitor@epace.com',
    '$2a$10$N.hCNsD2GUr1VsaLECxFUODLF9LNn2xH.P0C6t2LYyU7h3mNGdY1u', -- test123
    CURRENT_TIMESTAMP,
    3
)
ON CONFLICT ("Documento_Identidad") DO NOTHING;

-- Invitado
INSERT INTO "Participantes" (
    "Documento_Identidad",
    "Nombre",
    "Apellido",
    "Email",
    "Password",
    "Fecha_Creacion",
    "RolID_Rol"
) VALUES (
    1000000004,
    'Invitado',
    'Prueba',
    'invitado@epace.com',
    '$2a$10$N.hCNsD2GUr1VsaLECxFUODLF9LNn2xH.P0C6t2LYyU7h3mNGdY1u', -- test123
    CURRENT_TIMESTAMP,
    4
)
ON CONFLICT ("Documento_Identidad") DO NOTHING;

-- =====================================================
-- 6. Verificación
-- =====================================================

SELECT 'Roles creados:' AS info;
SELECT * FROM "Rol";

SELECT 'Usuarios de prueba creados:' AS info;
SELECT "Documento_Identidad", "Nombre", "Apellido", "Email", r."Nombre_Rol"
FROM "Participantes" p
JOIN "Rol" r ON p."RolID_Rol" = r."ID_Rol";
