-- =====================================================
-- SCRIPT COMPLETO DE BASE DE DATOS E-Pa-Ce
-- Sistema Integral de Gestión de Eventos + Participantes + Certificación Digital
-- PostgreSQL 12+
-- =====================================================

-- =====================================================
-- 0. ELIMINAR Y RECREAR LA BASE DE DATOS
-- =====================================================
-- NOTA: Ejecutar estas líneas desde psql como superusuario
-- o desde un cliente conectado a otra base de datos (ej: postgres)

-- Desconectar todas las conexiones activas a la base de datos
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'EPaCe'
  AND pid <> pg_backend_pid();

-- Eliminar la base de datos si existe
DROP DATABASE IF EXISTS "EPaCe";

-- Crear la base de datos
CREATE DATABASE "EPaCe"
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Spain.1252'
    LC_CTYPE = 'Spanish_Spain.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Conectarse a la base de datos EPaCe
\c EPaCe

-- =====================================================
-- 1. EXTENSIONES
-- =====================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- 2. CREAR TABLAS
-- =====================================================

-- -----------------------------------------------------
-- Tabla: Rol
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "Rol" (
    "ID_Rol" SMALLSERIAL PRIMARY KEY,
    "Nombre_Rol" VARCHAR(20) NOT NULL UNIQUE,
    CONSTRAINT "chk_rol_nombre_not_empty" CHECK (LENGTH(TRIM("Nombre_Rol")) > 0)
);

COMMENT ON TABLE "Rol" IS 'Catálogo de roles del sistema';
COMMENT ON COLUMN "Rol"."ID_Rol" IS 'Identificador único del rol';
COMMENT ON COLUMN "Rol"."Nombre_Rol" IS 'Nombre del rol (ADMIN, OPERADOR, MONITOR, INVITADO)';

-- -----------------------------------------------------
-- Tabla: Tipo_Evento
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "Tipo_Evento" (
    "ID_TipoEvento" SERIAL PRIMARY KEY,
    "Nombre_TipoEvento" VARCHAR(20) NOT NULL UNIQUE,
    CONSTRAINT "chk_tipo_evento_nombre_not_empty" CHECK (LENGTH(TRIM("Nombre_TipoEvento")) > 0)
);

COMMENT ON TABLE "Tipo_Evento" IS 'Catálogo de tipos de eventos';
COMMENT ON COLUMN "Tipo_Evento"."ID_TipoEvento" IS 'Identificador único del tipo de evento';
COMMENT ON COLUMN "Tipo_Evento"."Nombre_TipoEvento" IS 'Nombre del tipo (Conferencia, Taller, etc.)';

-- -----------------------------------------------------
-- Tabla: Modalidad_Evento
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "Modalidad_Evento" (
    "ID_ModalidadEvento" SERIAL PRIMARY KEY,
    "Nombre_ModalidadEvento" VARCHAR(20) NOT NULL UNIQUE,
    CONSTRAINT "chk_modalidad_nombre_not_empty" CHECK (LENGTH(TRIM("Nombre_ModalidadEvento")) > 0)
);

COMMENT ON TABLE "Modalidad_Evento" IS 'Catálogo de modalidades de eventos';
COMMENT ON COLUMN "Modalidad_Evento"."ID_ModalidadEvento" IS 'Identificador único de la modalidad';
COMMENT ON COLUMN "Modalidad_Evento"."Nombre_ModalidadEvento" IS 'Nombre de la modalidad (Presencial, Virtual, Híbrido)';

-- -----------------------------------------------------
-- Tabla: Participantes
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "Participantes" (
    "Documento_Identidad" BIGINT PRIMARY KEY,
    "Nombre" VARCHAR(25) NOT NULL,
    "Apellido" VARCHAR(25) NOT NULL,
    "Email" VARCHAR(45) NOT NULL UNIQUE,
    "Password" VARCHAR(100) NOT NULL,
    "Fecha_Creacion" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "RolID_Rol" SMALLINT NOT NULL,
    "Activo" BOOLEAN NOT NULL DEFAULT TRUE,
    "Cuenta_Bloqueada" BOOLEAN NOT NULL DEFAULT FALSE,
    "Intentos_Fallidos" INTEGER NOT NULL DEFAULT 0,
    "Ultima_Fecha_Login" TIMESTAMP,
    "Fecha_Ultima_Modificacion" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "Token_Recuperacion" VARCHAR(100),
    "Fecha_Expiracion_Token" TIMESTAMP,
    
    CONSTRAINT "fk_participante_rol" FOREIGN KEY ("RolID_Rol") 
        REFERENCES "Rol"("ID_Rol") 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    
    CONSTRAINT "chk_documento_positivo" CHECK ("Documento_Identidad" > 0),
    CONSTRAINT "chk_nombre_not_empty" CHECK (LENGTH(TRIM("Nombre")) > 0),
    CONSTRAINT "chk_apellido_not_empty" CHECK (LENGTH(TRIM("Apellido")) > 0),
    CONSTRAINT "chk_email_formato" CHECK ("Email" ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT "chk_intentos_no_negativos" CHECK ("Intentos_Fallidos" >= 0)
);

COMMENT ON TABLE "Participantes" IS 'Usuarios del sistema con autenticación';
COMMENT ON COLUMN "Participantes"."Documento_Identidad" IS 'Documento de identidad único del participante';
COMMENT ON COLUMN "Participantes"."Password" IS 'Contraseña hasheada con BCrypt';
COMMENT ON COLUMN "Participantes"."Activo" IS 'Indica si la cuenta está activa';
COMMENT ON COLUMN "Participantes"."Cuenta_Bloqueada" IS 'Indica si la cuenta está bloqueada por seguridad';
COMMENT ON COLUMN "Participantes"."Intentos_Fallidos" IS 'Contador de intentos fallidos de login';

-- Índices para Participantes
CREATE INDEX "idx_participante_email" ON "Participantes"("Email");
CREATE INDEX "idx_participante_rol" ON "Participantes"("RolID_Rol");
CREATE INDEX "idx_participante_activo" ON "Participantes"("Activo");

-- -----------------------------------------------------
-- Tabla: Eventos
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "Eventos" (
    "ID_Evento" SERIAL PRIMARY KEY,
    "Nombre" VARCHAR(30) NOT NULL,
    "Descripcion" VARCHAR(150),
    "Fecha_Inicio" TIMESTAMP NOT NULL,
    "Fecha_Fin" TIMESTAMP NOT NULL,
    "Duracion_Horas" REAL,
    "Aforo_Maximo" INTEGER,
    "Estado" VARCHAR(10) DEFAULT 'ACTIVO',
    "Modalidad_EventoID_ModalidadEvento" INTEGER NOT NULL,
    "Tipo_EventoID_TipoEvento" INTEGER NOT NULL,
    "Fecha_Creacion" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "Creado_Por" BIGINT,
    
    CONSTRAINT "fk_evento_modalidad" FOREIGN KEY ("Modalidad_EventoID_ModalidadEvento") 
        REFERENCES "Modalidad_Evento"("ID_ModalidadEvento") 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    
    CONSTRAINT "fk_evento_tipo" FOREIGN KEY ("Tipo_EventoID_TipoEvento") 
        REFERENCES "Tipo_Evento"("ID_TipoEvento") 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    
    CONSTRAINT "fk_evento_creador" FOREIGN KEY ("Creado_Por") 
        REFERENCES "Participantes"("Documento_Identidad") 
        ON DELETE SET NULL 
        ON UPDATE CASCADE,
    
    CONSTRAINT "chk_fecha_fin_despues_inicio" CHECK ("Fecha_Fin" > "Fecha_Inicio"),
    CONSTRAINT "chk_aforo_positivo" CHECK ("Aforo_Maximo" IS NULL OR "Aforo_Maximo" > 0),
    CONSTRAINT "chk_duracion_positiva" CHECK ("Duracion_Horas" IS NULL OR "Duracion_Horas" > 0),
    CONSTRAINT "chk_estado_valido" CHECK ("Estado" IN ('ACTIVO', 'CANCELADO', 'FINALIZADO', 'BORRADOR'))
);

COMMENT ON TABLE "Eventos" IS 'Eventos programados en el sistema';
COMMENT ON COLUMN "Eventos"."Estado" IS 'Estado del evento: ACTIVO, CANCELADO, FINALIZADO, BORRADOR';
COMMENT ON COLUMN "Eventos"."Creado_Por" IS 'Documento del participante que creó el evento';

-- Índices para Eventos
CREATE INDEX "idx_evento_fecha_inicio" ON "Eventos"("Fecha_Inicio");
CREATE INDEX "idx_evento_estado" ON "Eventos"("Estado");
CREATE INDEX "idx_evento_modalidad" ON "Eventos"("Modalidad_EventoID_ModalidadEvento");
CREATE INDEX "idx_evento_tipo" ON "Eventos"("Tipo_EventoID_TipoEvento");

-- -----------------------------------------------------
-- Tabla: Participacion
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "Participacion" (
    "ID_Participacion" SERIAL PRIMARY KEY,
    "Fecha_Inscripcion" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "ParticipanteDocumento_Identidad" BIGINT NOT NULL,
    "EventoID_Evento" INTEGER NOT NULL,
    "Estado_Participacion" VARCHAR(20) DEFAULT 'INSCRITO',
    "Fecha_Cancelacion" TIMESTAMP,
    
    CONSTRAINT "fk_participacion_participante" FOREIGN KEY ("ParticipanteDocumento_Identidad") 
        REFERENCES "Participantes"("Documento_Identidad") 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    CONSTRAINT "fk_participacion_evento" FOREIGN KEY ("EventoID_Evento") 
        REFERENCES "Eventos"("ID_Evento") 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    CONSTRAINT "uk_participacion_unica" UNIQUE ("ParticipanteDocumento_Identidad", "EventoID_Evento"),
    CONSTRAINT "chk_estado_participacion_valido" CHECK ("Estado_Participacion" IN ('INSCRITO', 'CONFIRMADO', 'CANCELADO', 'ASISTIO', 'NO_ASISTIO'))
);

COMMENT ON TABLE "Participacion" IS 'Relación entre participantes y eventos';
COMMENT ON COLUMN "Participacion"."Estado_Participacion" IS 'Estado: INSCRITO, CONFIRMADO, CANCELADO, ASISTIO, NO_ASISTIO';

-- Índices para Participacion
CREATE INDEX "idx_participacion_participante" ON "Participacion"("ParticipanteDocumento_Identidad");
CREATE INDEX "idx_participacion_evento" ON "Participacion"("EventoID_Evento");
CREATE INDEX "idx_participacion_estado" ON "Participacion"("Estado_Participacion");

-- -----------------------------------------------------
-- Tabla: CheckIn
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "CheckIn" (
    "ID_CheckIn" SERIAL PRIMARY KEY,
    "Metodo_CheckIn" VARCHAR(10),
    "IPCheckIn" VARCHAR(15),
    "Fecha_HoraCheckIn" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "ParticipacionID_Participacion" INTEGER NOT NULL UNIQUE,
    "Ubicacion" VARCHAR(100),
    "Dispositivo" VARCHAR(50),
    
    CONSTRAINT "fk_checkin_participacion" FOREIGN KEY ("ParticipacionID_Participacion") 
        REFERENCES "Participacion"("ID_Participacion") 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    CONSTRAINT "chk_metodo_valido" CHECK ("Metodo_CheckIn" IN ('QR', 'MANUAL', 'NFC', 'BIOMETRIC'))
);

COMMENT ON TABLE "CheckIn" IS 'Registro de asistencia a eventos';
COMMENT ON COLUMN "CheckIn"."Metodo_CheckIn" IS 'Método usado: QR, MANUAL, NFC, BIOMETRIC';

-- Índices para CheckIn
CREATE INDEX "idx_checkin_fecha" ON "CheckIn"("Fecha_HoraCheckIn");

-- -----------------------------------------------------
-- Tabla: Certificacion
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "Certificacion" (
    "ID_Certificacion" SERIAL PRIMARY KEY,
    "Fecha_Emision" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "RutaPDF" VARCHAR(200),
    "Emitido" BOOLEAN DEFAULT FALSE,
    "Codigo_UnicoAPI" VARCHAR(40) UNIQUE,
    "ParticipacionID_Participacion" INTEGER NOT NULL UNIQUE,
    "Hash_Verificacion" VARCHAR(64),
    "Fecha_Descarga" TIMESTAMP,
    "Numero_Descargas" INTEGER DEFAULT 0,
    
    CONSTRAINT "fk_certificacion_participacion" FOREIGN KEY ("ParticipacionID_Participacion") 
        REFERENCES "Participacion"("ID_Participacion") 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    CONSTRAINT "chk_descargas_no_negativas" CHECK ("Numero_Descargas" >= 0)
);

COMMENT ON TABLE "Certificacion" IS 'Certificados digitales emitidos';
COMMENT ON COLUMN "Certificacion"."Codigo_UnicoAPI" IS 'Código único para verificación pública del certificado';
COMMENT ON COLUMN "Certificacion"."Hash_Verificacion" IS 'Hash SHA-256 del PDF para verificación de integridad';

-- Índices para Certificacion
CREATE INDEX "idx_certificacion_codigo" ON "Certificacion"("Codigo_UnicoAPI");
CREATE INDEX "idx_certificacion_emitido" ON "Certificacion"("Emitido");

-- -----------------------------------------------------
-- Tabla: Auditoria
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "Auditoria" (
    "ID_Auditoria" SERIAL PRIMARY KEY,
    "Fecha_Hora" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "IP_Origen" VARCHAR(45),
    "Entidad_Afectada" VARCHAR(50),
    "Accion" VARCHAR(20),
    "Descripcion_Cambio" TEXT,
    "ParticipanteDocumento_Identidad" BIGINT,
    "Datos_Anteriores" JSONB,
    "Datos_Nuevos" JSONB,
    
    CONSTRAINT "fk_auditoria_participante" FOREIGN KEY ("ParticipanteDocumento_Identidad") 
        REFERENCES "Participantes"("Documento_Identidad") 
        ON DELETE SET NULL 
        ON UPDATE CASCADE,
    
    CONSTRAINT "chk_accion_valida" CHECK ("Accion" IN ('INSERT', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'FAILED_LOGIN'))
);

COMMENT ON TABLE "Auditoria" IS 'Registro de auditoría del sistema';
COMMENT ON COLUMN "Auditoria"."IP_Origen" IS 'Dirección IP desde donde se realizó la acción';
COMMENT ON COLUMN "Auditoria"."Datos_Anteriores" IS 'Estado anterior en formato JSON';
COMMENT ON COLUMN "Auditoria"."Datos_Nuevos" IS 'Estado nuevo en formato JSON';

-- Índices para Auditoria
CREATE INDEX "idx_auditoria_fecha" ON "Auditoria"("Fecha_Hora" DESC);
CREATE INDEX "idx_auditoria_participante" ON "Auditoria"("ParticipanteDocumento_Identidad");
CREATE INDEX "idx_auditoria_entidad" ON "Auditoria"("Entidad_Afectada");
CREATE INDEX "idx_auditoria_accion" ON "Auditoria"("Accion");

-- =====================================================
-- 3. TRIGGERS PARA AUDITORÍA AUTOMÁTICA
-- =====================================================

-- Función genérica para auditoría
CREATE OR REPLACE FUNCTION audit_trigger_func()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        INSERT INTO "Auditoria" ("Entidad_Afectada", "Accion", "Descripcion_Cambio", "Datos_Anteriores")
        VALUES (TG_TABLE_NAME, 'DELETE', 'Registro eliminado', row_to_json(OLD));
        RETURN OLD;
    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO "Auditoria" ("Entidad_Afectada", "Accion", "Descripcion_Cambio", "Datos_Anteriores", "Datos_Nuevos")
        VALUES (TG_TABLE_NAME, 'UPDATE', 'Registro actualizado', row_to_json(OLD), row_to_json(NEW));
        RETURN NEW;
    ELSIF (TG_OP = 'INSERT') THEN
        INSERT INTO "Auditoria" ("Entidad_Afectada", "Accion", "Descripcion_Cambio", "Datos_Nuevos")
        VALUES (TG_TABLE_NAME, 'INSERT', 'Registro creado', row_to_json(NEW));
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Trigger para Participantes
CREATE TRIGGER audit_participantes
    AFTER INSERT OR UPDATE OR DELETE ON "Participantes"
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

-- Trigger para Eventos
CREATE TRIGGER audit_eventos
    AFTER INSERT OR UPDATE OR DELETE ON "Eventos"
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

-- Trigger para actualizar Fecha_Ultima_Modificacion en Participantes
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW."Fecha_Ultima_Modificacion" = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_participante_modtime
    BEFORE UPDATE ON "Participantes"
    FOR EACH ROW EXECUTE FUNCTION update_modified_column();

-- =====================================================
-- 4. INSERCIÓN DE DATOS INICIALES
-- =====================================================

-- -----------------------------------------------------
-- 4.1 Roles
-- -----------------------------------------------------
INSERT INTO "Rol" ("ID_Rol", "Nombre_Rol") VALUES
(1, 'ADMIN'),
(2, 'OPERADOR'),
(3, 'MONITOR'),
(4, 'INVITADO')
ON CONFLICT ("ID_Rol") DO NOTHING;

-- Resetear secuencia
SELECT setval('"Rol_ID_Rol_seq"', (SELECT MAX("ID_Rol") FROM "Rol"));

-- -----------------------------------------------------
-- 4.2 Tipos de Evento
-- -----------------------------------------------------
INSERT INTO "Tipo_Evento" ("Nombre_TipoEvento") VALUES
('Conferencia'),
('Taller'),
('Seminario'),
('Webinar'),
('Reunión'),
('Capacitación'),
('Simposio'),
('Congreso')
ON CONFLICT ("Nombre_TipoEvento") DO NOTHING;

-- -----------------------------------------------------
-- 4.3 Modalidades de Evento
-- -----------------------------------------------------
INSERT INTO "Modalidad_Evento" ("Nombre_ModalidadEvento") VALUES
('Presencial'),
('Virtual'),
('Híbrido')
ON CONFLICT ("Nombre_ModalidadEvento") DO NOTHING;

-- -----------------------------------------------------
-- 4.4 Usuarios de Prueba
-- -----------------------------------------------------
-- Contraseñas (todas hasheadas con BCrypt):
-- admin@epace.com: admin123 -> $2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6
-- otros: test123 -> $2a$10$N.hCNsD2GUr1VsaLECxFUODLF9LNn2xH.P0C6t2LYyU7h3mNGdY1u

INSERT INTO "Participantes" (
    "Documento_Identidad",
    "Nombre",
    "Apellido",
    "Email",
    "Password",
    "RolID_Rol",
    "Activo"
) VALUES 
-- ADMIN
(1000000001, 'Admin', 'Sistema', 'admin@epace.com', 
 '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 1, TRUE),

-- OPERADOR
(1000000002, 'Operador', 'Prueba', 'operador@epace.com', 
 '$2a$10$N.hCNsD2GUr1VsaLECxFUODLF9LNn2xH.P0C6t2LYyU7h3mNGdY1u', 2, TRUE),

-- MONITOR
(1000000003, 'Monitor', 'Prueba', 'monitor@epace.com', 
 '$2a$10$N.hCNsD2GUr1VsaLECxFUODLF9LNn2xH.P0C6t2LYyU7h3mNGdY1u', 3, TRUE),

-- INVITADO
(1000000004, 'Invitado', 'Prueba', 'invitado@epace.com', 
 '$2a$10$N.hCNsD2GUr1VsaLECxFUODLF9LNn2xH.P0C6t2LYyU7h3mNGdY1u', 4, TRUE)
ON CONFLICT ("Documento_Identidad") DO NOTHING;

-- =====================================================
-- 5. VISTAS ÚTILES
-- =====================================================

-- Vista de participantes con rol
CREATE OR REPLACE VIEW "v_ParticipantesConRol" AS
SELECT 
    p."Documento_Identidad",
    p."Nombre",
    p."Apellido",
    p."Email",
    r."Nombre_Rol" AS "Rol",
    p."Activo",
    p."Cuenta_Bloqueada",
    p."Fecha_Creacion",
    p."Ultima_Fecha_Login"
FROM "Participantes" p
JOIN "Rol" r ON p."RolID_Rol" = r."ID_Rol";

-- Vista de eventos con detalles
CREATE OR REPLACE VIEW "v_EventosDetalle" AS
SELECT 
    e."ID_Evento",
    e."Nombre",
    e."Descripcion",
    e."Fecha_Inicio",
    e."Fecha_Fin",
    e."Duracion_Horas",
    e."Aforo_Maximo",
    e."Estado",
    te."Nombre_TipoEvento" AS "Tipo",
    me."Nombre_ModalidadEvento" AS "Modalidad",
    COUNT(DISTINCT pa."ID_Participacion") AS "Total_Inscritos",
    COUNT(DISTINCT c."ID_CheckIn") AS "Total_Asistentes"
FROM "Eventos" e
LEFT JOIN "Tipo_Evento" te ON e."Tipo_EventoID_TipoEvento" = te."ID_TipoEvento"
LEFT JOIN "Modalidad_Evento" me ON e."Modalidad_EventoID_ModalidadEvento" = me."ID_ModalidadEvento"
LEFT JOIN "Participacion" pa ON e."ID_Evento" = pa."EventoID_Evento"
LEFT JOIN "CheckIn" c ON pa."ID_Participacion" = c."ParticipacionID_Participacion"
GROUP BY e."ID_Evento", te."Nombre_TipoEvento", me."Nombre_ModalidadEvento";

-- =====================================================
-- 6. VERIFICACIÓN
-- =====================================================
\echo '====================================================='
\echo 'BASE DE DATOS EPaCe CREADA EXITOSAMENTE'
\echo '====================================================='
\echo ''
\echo 'TABLAS CREADAS:'
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_type = 'BASE TABLE'
ORDER BY table_name;

\echo ''
\echo 'ROLES DISPONIBLES:'
SELECT * FROM "Rol";

\echo ''
\echo 'USUARIOS DE PRUEBA:'
SELECT 
    "Documento_Identidad",
    "Nombre",
    "Apellido",
    "Email",
    r."Nombre_Rol" AS "Rol"
FROM "Participantes" p
JOIN "Rol" r ON p."RolID_Rol" = r."ID_Rol"
ORDER BY "Documento_Identidad";

\echo ''
\echo '====================================================='
\echo 'SCRIPT COMPLETADO'
\echo '====================================================='
