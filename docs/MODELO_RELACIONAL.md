# Modelo Relacional - Sistema E-Pa-Ce

## 📊 Diagrama Entidad-Relación

```mermaid

┌─────────────────┐         ┌──────────────────┐         ┌─────────────────┐
│  Modalidad_     │         │     Eventos      │         │   Tipo_Evento   │
│    Evento       │◄────────│                  │────────►│                 │
└─────────────────┘         └──────────────────┘         └─────────────────┘
                                     │
                                     │ 1
                                     │
                                     │ N
                            ┌────────▼─────────┐
                            │  Participacion   │
                            │  (Enrollment)    │
                            └────────┬─────────┘
                                     │
                        ┌────────────┼────────────┐
                        │ 1          │ 1          │
                        │            │            │
                    ┌───▼──────┐ ┌──▼───────┐ ┌──▼──────────┐
                    │ Check-In │ │Certifica-│ │Participantes│
                    │          │ │  ción    │ │             │
                    └──────────┘ └──────────┘ └──────┬──────┘
                                                      │
                                                      │ N
                                                      │
                                                      │ 1
                                                 ┌────▼────┐
                                                 │   Rol   │
                                                 └─────────┘

                            ┌──────────────┐
                            │  Auditoría   │
                            │  (Logs)      │
                            └──────────────┘
```

---

## 📋 Tablas y Atributos

### 1. Eventos

| Campo                                  | Tipo         | Restricciones              | Descripción                                     |
| -------------------------------------- | ------------ | -------------------------- | ----------------------------------------------- |
| **ID_Evento**                          | INT(10)      | PK, AUTO_INCREMENT, UNIQUE | Identificador único del evento                  |
| Nombre                                 | VARCHAR(30)  | NOT NULL                   | Nombre del evento                               |
| Descripcion                            | VARCHAR(150) | NULL                       | Descripción detallada                           |
| Fecha_Inicio                           | TIMESTAMP    | NOT NULL                   | Fecha y hora de inicio                          |
| Fecha_Fin                              | TIMESTAMP    | NOT NULL                   | Fecha y hora de finalización                    |
| Duracion_Horas                         | FLOAT(10)    | NULL                       | Duración calculada en horas                     |
| Aforo_Maximo                           | INT(10)      | DEFAULT 0                  | Capacidad máxima de participantes               |
| Estado                                 | VARCHAR(10)  | DEFAULT 'ACTIVO'           | Estado: ACTIVO, CANCELADO, FINALIZADO, BORRADOR |
| **Modalidad_EventoID_ModalidadEvento** | INT(10)      | FK                         | Referencia a Modalidad_Evento                   |
| **Tipo_EventoID_TipoEvento**           | INT(10)      | FK                         | Referencia a Tipo_Evento                        |

**Índices:**

- PRIMARY KEY (ID_Evento)
- INDEX idx_estado (Estado)
- INDEX idx_fechas (Fecha_Inicio, Fecha_Fin)
- FOREIGN KEY (Modalidad_EventoID_ModalidadEvento) REFERENCES Modalidad_Evento(ID_ModalidadEvento)
- FOREIGN KEY (Tipo_EventoID_TipoEvento) REFERENCES Tipo_Evento(ID_TipoEvento)

---

### 2. Modalidad_Evento

| Campo                  | Tipo        | Restricciones      | Descripción            |
| ---------------------- | ----------- | ------------------ | ---------------------- |
| **ID_ModalidadEvento** | INT(10)     | PK, AUTO_INCREMENT | Identificador único    |
| Nombre_ModalidadEvento | VARCHAR(20) | NOT NULL, UNIQUE   | Nombre de la modalidad |

**Valores iniciales:**

- Presencial
- Virtual
- Híbrido

**Índices:**

- PRIMARY KEY (ID_ModalidadEvento)
- UNIQUE INDEX idx_nombre_modalidad (Nombre_ModalidadEvento)

---

### 3. Tipo_Evento

| Campo             | Tipo        | Restricciones      | Descripción         |
| ----------------- | ----------- | ------------------ | ------------------- |
| **ID_TipoEvento** | INT(10)     | PK, AUTO_INCREMENT | Identificador único |
| Nombre_TipoEvento | VARCHAR(20) | NOT NULL, UNIQUE   | Nombre del tipo     |

**Valores iniciales:**

- Capacitación
- Taller
- Conferencia
- Seminario
- Webinar
- Torneo

**Índices:**

- PRIMARY KEY (ID_TipoEvento)
- UNIQUE INDEX idx_nombre_tipo (Nombre_TipoEvento)

---

### 4. Participantes

| Campo                   | Tipo         | Restricciones             | Descripción                                |
| ----------------------- | ------------ | ------------------------- | ------------------------------------------ |
| **Documento_Identidad** | VARCHAR(15)  | PK, UNIQUE                | Documento de identidad (cédula, pasaporte) |
| Nombre                  | VARCHAR(25)  | NOT NULL                  | Nombre del participante                    |
| Apellido                | VARCHAR(25)  | NOT NULL                  | Apellido del participante                  |
| Email                   | VARCHAR(45)  | NOT NULL, UNIQUE          | Correo electrónico                         |
| Password                | VARCHAR(100) | NOT NULL                  | Contraseña hasheada (bcrypt)               |
| Fecha_Creacion          | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP | Fecha de registro                          |
| **RolID_Rol**           | SMALLINT(10) | FK                        | Referencia a Rol                           |

**Índices:**

- PRIMARY KEY (Documento_Identidad)
- UNIQUE INDEX idx_email (Email)
- INDEX idx_rol (RolID_Rol)
- FOREIGN KEY (RolID_Rol) REFERENCES Rol(ID_Rol)

---

### 5. Rol

| Campo      | Tipo         | Restricciones      | Descripción         |
| ---------- | ------------ | ------------------ | ------------------- |
| **ID_Rol** | SMALLINT(10) | PK, AUTO_INCREMENT | Identificador único |
| Nombre_Rol | VARCHAR(20)  | NOT NULL, UNIQUE   | Nombre del rol      |

**Valores iniciales:**

- Admin
- Operador
- Monitor
- Invitado

**Índices:**

- PRIMARY KEY (ID_Rol)
- UNIQUE INDEX idx_nombre_rol (Nombre_Rol)

---

### 6. Participacion (Enrollment)

| Campo                               | Tipo        | Restricciones             | Descripción                               |
| ----------------------------------- | ----------- | ------------------------- | ----------------------------------------- |
| **ID_Participacion**                | INT(10)     | PK, AUTO_INCREMENT        | Identificador único                       |
| Fecha_Inscripcion                   | TIMESTAMP   | DEFAULT CURRENT_TIMESTAMP | Fecha de inscripción                      |
| **ParticipanteDocumento_Identidad** | VARCHAR(15) | FK, NOT NULL              | Referencia a Participantes                |
| **EventoID_Evento**                 | INT(10)     | FK, NOT NULL              | Referencia a Eventos                      |
| Codigo_UnicoAPI                     | VARCHAR(40) | UNIQUE, NOT NULL          | Código UUID para check-in y certificación |

**Índices:**

- PRIMARY KEY (ID_Participacion)
- UNIQUE INDEX idx_codigo_unico (Codigo_UnicoAPI)
- INDEX idx_participante (ParticipanteDocumento_Identidad)
- INDEX idx_evento (EventoID_Evento)
- UNIQUE INDEX idx_participante_evento (ParticipanteDocumento_Identidad, EventoID_Evento)
- FOREIGN KEY (ParticipanteDocumento_Identidad) REFERENCES Participantes(Documento_Identidad)
- FOREIGN KEY (EventoID_Evento) REFERENCES Eventos(ID_Evento)

---

### 7. Check-In

| Campo                             | Tipo        | Restricciones             | Descripción                |
| --------------------------------- | ----------- | ------------------------- | -------------------------- |
| **ID_CheckIn**                    | INT(10)     | PK, AUTO_INCREMENT        | Identificador único        |
| Metodo_CheckIn                    | VARCHAR(10) | NOT NULL                  | Método: QR, MANUAL         |
| IP_CheckIn                        | VARCHAR(15) | NULL                      | Dirección IP del check-in  |
| Fecha_HoraCheckIn                 | TIMESTAMP   | DEFAULT CURRENT_TIMESTAMP | Fecha y hora del check-in  |
| **ParticipacionID_Participacion** | INT(10)     | FK, UNIQUE, NOT NULL      | Referencia a Participacion |

**Índices:**

- PRIMARY KEY (ID_CheckIn)
- UNIQUE INDEX idx_participacion (ParticipacionID_Participacion)
- INDEX idx_fecha_checkin (Fecha_HoraCheckIn)
- FOREIGN KEY (ParticipacionID_Participacion) REFERENCES Participacion(ID_Participacion)

**Nota:** La relación es 1:1 con Participacion (un participante solo puede hacer check-in una vez por evento)

---

### 8. Certificacion

| Campo                             | Tipo        | Restricciones             | Descripción                          |
| --------------------------------- | ----------- | ------------------------- | ------------------------------------ |
| **ID_Certificacion**              | INT(10)     | PK, AUTO_INCREMENT        | Identificador único                  |
| Fecha_Emision                     | TIMESTAMP   | DEFAULT CURRENT_TIMESTAMP | Fecha de generación del certificado  |
| RutaPDF                           | VARCHAR(50) | NULL                      | Ruta del archivo PDF generado        |
| Emitido                           | BOOLEAN     | DEFAULT FALSE             | Indica si el certificado fue emitido |
| Codigo_UnicoAPI                   | VARCHAR(40) | UNIQUE, NOT NULL          | Código para verificación pública     |
| **ParticipacionID_Participacion** | INT(10)     | FK, UNIQUE, NOT NULL      | Referencia a Participacion           |

**Índices:**

- PRIMARY KEY (ID_Certificacion)
- UNIQUE INDEX idx_codigo_verificacion (Codigo_UnicoAPI)
- UNIQUE INDEX idx_participacion (ParticipacionID_Participacion)
- INDEX idx_emitido (Emitido)
- FOREIGN KEY (ParticipacionID_Participacion) REFERENCES Participacion(ID_Participacion)

**Nota:** La relación es 1:1 con Participacion (un participante recibe un certificado por evento)

---

### 9. Auditoria

| Campo                               | Tipo         | Restricciones              | Descripción                                        |
| ----------------------------------- | ------------ | -------------------------- | -------------------------------------------------- |
| **ID_Auditoria**                    | INT(10)      | PK, AUTO_INCREMENT, UNIQUE | Identificador único                                |
| Fecha_Hora                          | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  | Fecha y hora de la acción                          |
| IP_Origen                           | VARCHAR(15)  | NULL                       | Dirección IP origen                                |
| Entidad_Afectada                    | VARCHAR(20)  | NOT NULL                   | Entidad: Event, Participant, Enrollment, etc.      |
| Accion                              | VARCHAR(20)  | NOT NULL                   | Acción: CREATED, UPDATED, DELETED, CHECKIN_SUCCESS |
| Descripcion_Cambio                  | VARCHAR(100) | NULL                       | Descripción detallada del cambio                   |
| **ParticipanteDocumento_Identidad** | VARCHAR(15)  | FK, NULL                   | Usuario que realizó la acción (NULL si es sistema) |

**Índices:**

- PRIMARY KEY (ID_Auditoria)
- INDEX idx_fecha (Fecha_Hora)
- INDEX idx_entidad (Entidad_Afectada)
- INDEX idx_accion (Accion)
- INDEX idx_participante (ParticipanteDocumento_Identidad)
- FOREIGN KEY (ParticipanteDocumento_Identidad) REFERENCES Participantes(Documento_Identidad)

---

## 🔗 Relaciones

### Relaciones 1:N (Uno a Muchos)

1. **Modalidad_Evento → Eventos**

   - Una modalidad puede tener muchos eventos
   - Un evento tiene una sola modalidad

2. **Tipo_Evento → Eventos**

   - Un tipo puede tener muchos eventos
   - Un evento tiene un solo tipo

3. **Eventos → Participacion**

   - Un evento puede tener muchas participaciones
   - Una participación pertenece a un solo evento

4. **Participantes → Participacion**

   - Un participante puede tener muchas participaciones
   - Una participación pertenece a un solo participante

5. **Rol → Participantes**

   - Un rol puede tener muchos participantes
   - Un participante tiene un solo rol

6. **Participantes → Auditoria**
   - Un participante puede tener muchos registros de auditoría
   - Un registro de auditoría pertenece a un participante (o es NULL si es del sistema)

### Relaciones 1:1 (Uno a Uno)

1. **Participacion → Check-In**

   - Una participación puede tener un solo check-in
   - Un check-in pertenece a una sola participación

2. **Participacion → Certificacion**
   - Una participación puede tener un solo certificado
   - Un certificado pertenece a una sola participación

---

## 📐 Reglas de Integridad

### Integridad Referencial

- ✅ Todas las FKs deben existir en las tablas referenciadas
- ✅ ON DELETE RESTRICT para evitar eliminaciones en cascada accidentales
- ✅ ON UPDATE CASCADE para mantener consistencia

### Integridad de Dominio

- ✅ Estado de eventos: Solo valores válidos (ACTIVO, CANCELADO, FINALIZADO, BORRADOR)
- ✅ Método de check-in: Solo QR o MANUAL
- ✅ Emails: Formato válido
- ✅ Fechas: Fecha_Inicio < Fecha_Fin

### Integridad de Entidad

- ✅ Todas las tablas tienen clave primaria
- ✅ Claves primarias no nulas y únicas
- ✅ Códigos únicos (UUID) no duplicados

---

## 🔍 Consultas Frecuentes

### 1. Obtener eventos con su modalidad y tipo

```sql
SELECT e.*, m.Nombre_ModalidadEvento, t.Nombre_TipoEvento
FROM Eventos e
LEFT JOIN Modalidad_Evento m ON e.Modalidad_EventoID_ModalidadEvento = m.ID_ModalidadEvento
LEFT JOIN Tipo_Evento t ON e.Tipo_EventoID_TipoEvento = t.ID_TipoEvento
WHERE e.Estado = 'ACTIVO';
```

### 2. Obtener participantes inscritos a un evento con check-in

```sql
SELECT p.Nombre, p.Apellido, p.Email, pa.Fecha_Inscripcion,
       c.Fecha_HoraCheckIn, c.Metodo_CheckIn
FROM Participacion pa
JOIN Participantes p ON pa.ParticipanteDocumento_Identidad = p.Documento_Identidad
LEFT JOIN CheckIn c ON pa.ID_Participacion = c.ParticipacionID_Participacion
WHERE pa.EventoID_Evento = ?;
```

### 3. Verificar certificado por código

```sql
SELECT cert.*, p.Nombre, p.Apellido, e.Nombre AS Evento_Nombre, e.Duracion_Horas
FROM Certificacion cert
JOIN Participacion pa ON cert.ParticipacionID_Participacion = pa.ID_Participacion
JOIN Participantes p ON pa.ParticipanteDocumento_Identidad = p.Documento_Identidad
JOIN Eventos e ON pa.EventoID_Evento = e.ID_Evento
WHERE cert.Codigo_UnicoAPI = ? AND cert.Emitido = TRUE;
```

### 4. Obtener estadísticas de un evento

```sql
SELECT
    e.Nombre,
    e.Aforo_Maximo,
    COUNT(DISTINCT pa.ID_Participacion) AS Total_Inscritos,
    COUNT(DISTINCT c.ID_CheckIn) AS Total_Asistentes,
    COUNT(DISTINCT cert.ID_Certificacion) AS Total_Certificados
FROM Eventos e
LEFT JOIN Participacion pa ON e.ID_Evento = pa.EventoID_Evento
LEFT JOIN CheckIn c ON pa.ID_Participacion = c.ParticipacionID_Participacion
LEFT JOIN Certificacion cert ON pa.ID_Participacion = cert.ParticipacionID_Participacion
WHERE e.ID_Evento = ?
GROUP BY e.ID_Evento;
```

---

## 📊 Normalización

El modelo está en **Tercera Forma Normal (3FN)**:

- ✅ **1FN**: Todos los atributos son atómicos
- ✅ **2FN**: No hay dependencias parciales (todas las FKs dependen completamente de la PK)
- ✅ **3FN**: No hay dependencias transitivas

---

## 🎯 Consideraciones de Diseño

### Decisiones Clave

1. **Código Único en Participacion vs Certificacion**

   - Participacion tiene `Codigo_UnicoAPI` para check-in
   - Certificacion tiene su propio `Codigo_UnicoAPI` para verificación
   - Razón: Separar concerns y permitir regeneración de certificados

2. **Check-In como tabla separada**

   - Permite almacenar metadatos adicionales (IP, método)
   - Facilita auditoría de check-ins
   - Relación 1:1 con Participacion

3. **Duracion_Horas calculada**

   - Se puede calcular automáticamente desde fechas
   - Se almacena para evitar cálculos repetidos
   - Útil para certificados

4. **Soft Delete**
   - Se usa campo `deleted_at` (GORM) en lugar de eliminar físicamente
   - Permite recuperación de datos
   - Mantiene integridad referencial

---

**Versión del modelo**: 1.0.0  
**Última actualización**: 2025-11-24
