-- V2: Restricciones adicionales e índices de optimización

-- Índices para mejorar el rendimiento de búsquedas frecuentes
CREATE INDEX idx_participante_email ON participante(email);
CREATE INDEX idx_participante_nombre_completo ON participante(nombre, apellido);

CREATE INDEX idx_evento_fecha_inicio ON evento(fecha_inicio);
CREATE INDEX idx_evento_estado ON evento(estado);

CREATE INDEX idx_participacion_codigo ON participacion(codigo_unico_api);
CREATE INDEX idx_participacion_participante ON participacion(participante_id);
CREATE INDEX idx_participacion_evento ON participacion(evento_id);

CREATE INDEX idx_check_in_participacion ON check_in(participacion_id);
CREATE INDEX idx_check_in_tipo ON check_in(tipo_accion);

CREATE INDEX idx_certificacion_codigo ON certificacion(codigo_unico_api);

CREATE INDEX idx_auditoria_fecha ON auditoria(fecha_hora);
CREATE INDEX idx_auditoria_participante ON auditoria(participante_id);


-- Restricciones adicionales de validación
ALTER TABLE evento ADD CONSTRAINT check_aforo_positivo CHECK (aforo_maximo > 0 OR aforo_maximo IS NULL);
ALTER TABLE evento ADD CONSTRAINT check_aforo_actual_no_negativo CHECK (aforo_actual >= 0);
ALTER TABLE evento ADD CONSTRAINT check_fechas_coherentes CHECK (fecha_fin >= fecha_inicio);
