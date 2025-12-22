-- V1: Esquema inicial estandarizado
-- Unifica a snake_case, singular, y asegura orden correcto de dependencias.

-- 1. Catálogos Base (Sin dependencias)
CREATE TABLE rol (
    id_rol SERIAL PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE rol_permiso (
    rol_id INTEGER NOT NULL,
    permiso VARCHAR(100) NOT NULL,
    PRIMARY KEY (rol_id, permiso),
    CONSTRAINT fk_rol_permiso_rol FOREIGN KEY (rol_id) REFERENCES rol(id_rol) ON DELETE CASCADE
);

CREATE TABLE tipo_evento (
    id_tipo_evento SERIAL PRIMARY KEY,
    nombre_tipo_evento VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE modalidad_evento (
    id_modalidad_evento SERIAL PRIMARY KEY,
    nombre_modalidad_evento VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Usuarios
CREATE TABLE participante (
    documento_identidad BIGINT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'HABILITADO',
    foto_perfil TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE participante_rol (
    participante_id BIGINT NOT NULL,
    rol_id INTEGER NOT NULL,
    PRIMARY KEY (participante_id, rol_id),
    CONSTRAINT fk_participante_rol_participante FOREIGN KEY (participante_id) REFERENCES participante(documento_identidad) ON DELETE CASCADE,
    CONSTRAINT fk_participante_rol_rol FOREIGN KEY (rol_id) REFERENCES rol(id_rol) ON DELETE CASCADE
);

-- 3. Eventos y Core
CREATE TABLE evento (
    id_evento SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    duracion_horas FLOAT,
    aforo_maximo INTEGER,
    aforo_actual INTEGER DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'BORRADOR',
    tipo_evento_id INTEGER NOT NULL,
    modalidad_evento_id INTEGER NOT NULL,
    creado_por BIGINT,
    CONSTRAINT fk_evento_tipo_evento FOREIGN KEY (tipo_evento_id) REFERENCES tipo_evento(id_tipo_evento),
    CONSTRAINT fk_evento_modalidad_evento FOREIGN KEY (modalidad_evento_id) REFERENCES modalidad_evento(id_modalidad_evento),
    CONSTRAINT fk_evento_creado_por FOREIGN KEY (creado_por) REFERENCES participante(documento_identidad) ON DELETE SET NULL
);

CREATE TABLE participacion (
    id_participacion SERIAL PRIMARY KEY,
    participante_id BIGINT NOT NULL,
    evento_id INTEGER NOT NULL,
    fecha_inscripcion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    codigo_unico_api VARCHAR(100) UNIQUE,
    metodo_inscripcion VARCHAR(20),
    registrado_por BIGINT,
    CONSTRAINT fk_participacion_participante FOREIGN KEY (participante_id) REFERENCES participante(documento_identidad),
    CONSTRAINT fk_participacion_evento FOREIGN KEY (evento_id) REFERENCES evento(id_evento),
    CONSTRAINT fk_participacion_registrado_por FOREIGN KEY (registrado_por) REFERENCES participante(documento_identidad) ON DELETE SET NULL,
    CONSTRAINT uk_participacion_participante_evento UNIQUE (participante_id, evento_id)
);

CREATE TABLE check_in (
    id_check_in SERIAL PRIMARY KEY,
    participacion_id INTEGER NOT NULL,
    fecha_hora_check_in TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo_check_in VARCHAR(20) NOT NULL,
    ip_check_in VARCHAR(45),
    tipo_accion VARCHAR(10) NOT NULL DEFAULT 'ENTRADA',
    CONSTRAINT fk_check_in_participacion FOREIGN KEY (participacion_id) REFERENCES participacion(id_participacion) ON DELETE CASCADE
);

CREATE TABLE certificacion (
    id_certificacion SERIAL PRIMARY KEY,
    participacion_id INTEGER NOT NULL UNIQUE,
    codigo_unico_api VARCHAR(100) UNIQUE,
    fecha_emision TIMESTAMP,
    ruta_pdf VARCHAR(255),
    emitido BOOLEAN DEFAULT FALSE,
    emitido_por BIGINT,
    CONSTRAINT fk_certificacion_participacion FOREIGN KEY (participacion_id) REFERENCES participacion(id_participacion) ON DELETE CASCADE,
    CONSTRAINT fk_certificacion_emitido_por FOREIGN KEY (emitido_por) REFERENCES participante(documento_identidad) ON DELETE SET NULL
);

-- 4. Auxiliares
CREATE TABLE auditoria (
    id_auditoria SERIAL PRIMARY KEY,
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_origen VARCHAR(45),
    entidad_afectada VARCHAR(50),
    accion VARCHAR(50),
    descripcion_cambio TEXT,
    rol_usuario VARCHAR(50),
    participante_id BIGINT,
    CONSTRAINT fk_auditoria_participante FOREIGN KEY (participante_id) REFERENCES participante(documento_identidad) ON DELETE SET NULL
);

CREATE TABLE password_reset_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    participante_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_password_reset_token_participante FOREIGN KEY (participante_id) REFERENCES participante(documento_identidad) ON DELETE CASCADE
);

-- 5. Tabla de Inscripción (Legacy/Migración)
CREATE TABLE inscripcion (
    id_inscripcion BIGSERIAL PRIMARY KEY,
    participante_documento BIGINT NOT NULL,
    evento_id INTEGER NOT NULL,
    fecha_inscripcion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    checked_in BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_check_in TIMESTAMP,
    CONSTRAINT fk_inscripcion_participante FOREIGN KEY (participante_documento) REFERENCES participante(documento_identidad),
    CONSTRAINT fk_inscripcion_evento FOREIGN KEY (evento_id) REFERENCES evento(id_evento),
    CONSTRAINT uk_inscripcion_participante_evento UNIQUE (participante_documento, evento_id)
);


