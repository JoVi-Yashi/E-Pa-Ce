-- V3: Datos iniciales (Catálogos y Usuarios Admin)

-- 1. Roles
INSERT INTO rol (id_rol, nombre_rol) VALUES
(1, 'ADMIN'),
(2, 'OPERADOR'),
(3, 'MONITOR'),
(4, 'INVITADO')
ON CONFLICT (id_rol) DO UPDATE SET nombre_rol = EXCLUDED.nombre_rol;

-- Permisos (Alineados con DataInitializer.java)
INSERT INTO rol_permiso (rol_id, permiso) VALUES
(1, 'ALL:ALL'),
(2, 'DASHBOARD:READ'), (2, 'EVENTO:READ'), (2, 'EVENTO:CREATE'), (2, 'CHECKIN:QR'),
(3, 'EVENTO:READ'), (3, 'CHECKIN:QR'),
(4, 'EVENTO:READ'), (4, 'CERTIFICADO:DOWNLOAD')
ON CONFLICT DO NOTHING;

-- 2. Catálogos de Evento
INSERT INTO tipo_evento (nombre_tipo_evento) VALUES
('Conferencia'), ('Taller'), ('Seminario'), ('Webinar'), ('Reunión')
ON CONFLICT DO NOTHING;

INSERT INTO modalidad_evento (nombre_modalidad_evento) VALUES
('Presencial'), ('Virtual'), ('Híbrido')
ON CONFLICT DO NOTHING;

-- 3. Usuario Administrador Inicial
-- Password: admin123
INSERT INTO participante (documento_identidad, nombre, apellido, email, password, estado)
VALUES (1000000001, 'Admin', 'Sistema', 'admin@epace.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'HABILITADO')
ON CONFLICT DO NOTHING;

INSERT INTO participante_rol (participante_id, rol_id)
VALUES (1000000001, 1)
ON CONFLICT DO NOTHING;

-- Ajustar secuencias
SELECT setval('rol_id_rol_seq', (SELECT MAX(id_rol) FROM rol));
SELECT setval('tipo_evento_id_tipo_evento_seq', (SELECT MAX(id_tipo_evento) FROM tipo_evento));
SELECT setval('modalidad_evento_id_modalidad_evento_seq', (SELECT MAX(id_modalidad_evento) FROM modalidad_evento));
