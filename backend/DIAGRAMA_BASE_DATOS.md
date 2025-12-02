# 📊 DIAGRAMA DE BASE DE DATOS E-Pa-Ce

## 🗂️ Estructura Visual

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SISTEMA E-Pa-Ce - BASE DE DATOS                      │
└─────────────────────────────────────────────────────────────────────────┘

┌──────────────────┐
│      Rol         │  (Catálogo de Roles)
├──────────────────┤
│ ID_Rol (PK)      │
│ Nombre_Rol       │
└────────┬─────────┘
         │ 1
         │
         │ N
┌────────┴──────────────────────────────┐
│         Participantes                 │  ★ Usuario con Seguridad
├───────────────────────────────────────┤
│ Documento_Identidad (PK)              │
│ Nombre                                │
│ Apellido                              │
│ Email (UNIQUE)                        │
│ Password (BCrypt Hash)                │
│ Fecha_Creacion                        │
│ RolID_Rol (FK)                        │
│ ─────────────────────────────────     │
│ 🔒 SEGURIDAD:                         │
│ Activo                                │
│ Cuenta_Bloqueada                      │
│ Intentos_Fallidos                     │
│ Ultima_Fecha_Login                    │
│ Fecha_Ultima_Modificacion             │
│ Token_Recuperacion                    │
│ Fecha_Expiracion_Token                │
└────────┬──────────────┬───────────────┘
         │              │
         │ 1            │ N (Auditoría)
         │              │
         │              ▼
         │      ┌──────────────────┐
         │      │    Auditoria     │  ★ Registro Completo
         │      ├──────────────────┤
         │      │ ID_Auditoria (PK)│
         │      │ Fecha_Hora       │
         │      │ IP_Origen        │
         │      │ Entidad_Afectada │
         │      │ Accion           │
         │      │ Descripcion      │
         │      │ Participante (FK)│
         │      │ Datos_Anteriores │ 📦 JSONB
         │      │ Datos_Nuevos     │ 📦 JSONB
         │      └──────────────────┘
         │
         │ N
         │
         ▼
┌────────────────────────┐
│    Participacion       │
├────────────────────────┤
│ ID_Participacion (PK)  │
│ Fecha_Inscripcion      │
│ Participante (FK)      │─────┐
│ Evento (FK)            │─┐   │
│ Estado_Participacion   │ │   │
│ Fecha_Cancelacion      │ │   │
└────────┬───────┬───────┘ │   │
         │       │         │   │
         │ 1     │ 1       │   │
         │       │         │   │
         ▼       ▼         │   │
  ┌──────────┐ ┌──────────┴───┴──────┐
  │ CheckIn  │ │   Certificacion     │  ★ Con Hash SHA-256
  ├──────────┤ ├─────────────────────┤
  │ ID (PK)  │ │ ID_Certificacion    │
  │ Metodo   │ │ Fecha_Emision       │
  │ IP       │ │ RutaPDF             │
  │ Fecha    │ │ Emitido             │
  │ Part(FK) │ │ Codigo_UnicoAPI     │ ⭐ Verificación Pública
  │──────────│ │ Participacion (FK)  │
  │🔒Extra:  │ │ Hash_Verificacion   │ 🔐 SHA-256
  │Ubicacion │ │ Fecha_Descarga      │
  │Dispositiv│ │ Numero_Descargas    │
  └──────────┘ └─────────────────────┘


                    ┌─────────────────┐
                    │    Eventos      │  ★ Con Creador
                    ├─────────────────┤
                    │ ID_Evento (PK)  │
                    │ Nombre          │
                    │ Descripcion     │
                    │ Fecha_Inicio    │
                    │ Fecha_Fin       │
                    │ Duracion_Horas  │
                    │ Aforo_Maximo    │
                    │ Estado          │
                    │ Modalidad (FK)  │─────┐
                    │ Tipo (FK)       │─┐   │
                    │ Fecha_Creacion  │ │   │
                    │ Creado_Por (FK) │ │   │
                    └─────────────────┘ │   │
                                        │   │
                ┌───────────────────────┘   │
                │                           │
                ▼                           ▼
    ┌──────────────────────┐    ┌────────────────────────┐
    │   Tipo_Evento        │    │  Modalidad_Evento      │
    ├──────────────────────┤    ├────────────────────────┤
    │ ID_TipoEvento (PK)   │    │ ID_ModalidadEvento(PK) │
    │ Nombre_TipoEvento    │    │ Nombre_Modalidad       │
    └──────────────────────┘    └────────────────────────┘
    
    📚 Tipos predefinidos:       📱 Modalidades:
    • Conferencia                • Presencial
    • Taller                     • Virtual
    • Seminario                  • Híbrido
    • Webinar
    • Reunión
    • Capacitación
    • Simposio
    • Congreso
```

---

## 🔗 Relaciones Principales

### 1️⃣ **Participantes → Rol** (N:1)

- Un participante tiene **un rol**
- Un rol puede tener **muchos participantes**
- **ON DELETE:** RESTRICT (no se puede borrar rol si tiene usuarios)

### 2️⃣ **Participantes → Participacion** (1:N)

- Un participante puede tener **muchas inscripciones**
- **ON DELETE:** CASCADE (si se borra participante, se borran inscripciones)

### 3️⃣ **Eventos → Participacion** (1:N)

- Un evento puede tener **muchos participantes**
- **ON DELETE:** CASCADE (si se borra evento, se borran inscripciones)

### 4️⃣ **Participacion → CheckIn** (1:1)

- Una inscripción tiene **máximo un check-in**
- **ON DELETE:** CASCADE

### 5️⃣ **Participacion → Certificacion** (1:1)

- Una inscripción tiene **máximo un certificado**
- **ON DELETE:** CASCADE

### 6️⃣ **Eventos → Tipo_Evento** (N:1)

- Un evento tiene **un tipo**
- **ON DELETE:** RESTRICT

### 7️⃣ **Eventos → Modalidad_Evento** (N:1)

- Un evento tiene **una modalidad**
- **ON DELETE:** RESTRICT

### 8️⃣ **Participantes → Auditoria** (1:N)

- Registra **todas las acciones** de un usuario
- **ON DELETE:** SET NULL (mantiene registro aunque se borre usuario)

---

## 🎯 Flujo de Datos Típico

```
1. REGISTRO
   Usuario → Participantes (con rol INVITADO)
   
2. LOGIN
   Verificar email + password
   → Generar JWT
   → Actualizar Ultima_Fecha_Login
   
3. INSCRIPCIÓN A EVENTO
   Participante + Evento → Participacion
   → Estado: INSCRITO
   
4. CHECK-IN
   Participacion → CheckIn
   → Método: QR, MANUAL, NFC, BIOMETRIC
   → Actualizar Estado_Participacion: ASISTIO
   
5. CERTIFICACIÓN
   Participacion (si asistió) → Certificacion
   → Generar PDF
   → Calcular Hash SHA-256
   → Generar Codigo_UnicoAPI
   → Emitido = TRUE
   
6. AUDITORÍA (Automática)
   Cada acción → Auditoria
   → Capturar estado antes/después
   → Registrar IP, usuario, timestamp
```

---

## 📊 Índices Estratégicos

```sql
-- PARTICIPANTES
idx_participante_email         -- Búsqueda por email (login)
idx_participante_rol           -- Filtrar por rol
idx_participante_activo        -- Solo activos

-- EVENTOS  
idx_evento_fecha_inicio        -- Eventos próximos
idx_evento_estado              -- Filtrar por estado
idx_evento_modalidad           -- Por modalidad
idx_evento_tipo                -- Por tipo

-- PARTICIPACION
idx_participacion_participante -- Inscripciones de usuario
idx_participacion_evento       -- Participantes de evento
idx_participacion_estado       -- Por estado

-- CHECKIN
idx_checkin_fecha              -- Check-ins recientes

-- CERTIFICACION
idx_certificacion_codigo       -- Verificación pública
idx_certificacion_emitido      -- Pendientes de emitir

-- AUDITORIA
idx_auditoria_fecha (DESC)     -- Auditoría reciente
idx_auditoria_participante     -- Por usuario
idx_auditoria_entidad          -- Por tabla
idx_auditoria_accion           -- Por tipo de acción
```

---

## 🔐 Seguridad en Capas

```
Capa 1: BASE DE DATOS
├─ Constraints (Check, Unique, FK)
├─ Triggers de auditoría
└─ Validación de formato (email, fechas)

Capa 2: APLICACIÓN (Spring Boot)
├─ Validaciones @NotNull, @Email, @Size
├─ BCrypt para passwords
└─ JWT para autenticación

Capa 3: LÓGICA DE NEGOCIO
├─ Bloqueo por intentos fallidos
├─ Expiración de tokens
└─ Verificación de roles
```

---

## 🎨 Estados del Sistema

### Estados de Evento

- `ACTIVO` → Disponible para inscripciones
- `BORRADOR` → En creación
- `CANCELADO` → Cancelado
- `FINALIZADO` → Ya ocurrió

### Estados de Participación

- `INSCRITO` → Reservó cupo
- `CONFIRMADO` → Confirmó asistencia
- `ASISTIO` → Hizo check-in
- `NO_ASISTIO` → No asistió
- `CANCELADO` → Canceló inscripción

### Métodos de Check-in

- `QR` → Código QR
- `MANUAL` → Ingreso manual
- `NFC` → Tarjeta/chip NFC
- `BIOMETRIC` → Huella/facial

---

## 📦 Datos JSON en Auditoría

Ejemplo de registro en tabla Auditoria:

```json
{
  "Datos_Anteriores": {
    "Email": "antiguo@email.com",
    "Activo": true,
    "RolID_Rol": 4
  },
  "Datos_Nuevos": {
    "Email": "nuevo@email.com",
    "Activo": true,
    "RolID_Rol": 3
  }
}
```

---

Este diagrama complementa la documentación completa en `DATABASE_README.md` 📚

```

