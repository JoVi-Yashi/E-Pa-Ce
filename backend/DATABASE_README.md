# 🗄️ GUÍA COMPLETA DE BASE DE DATOS - E-Pa-Ce

## 📋 Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Estructura de Tablas](#estructura-de-tablas)
3. [Instalación y Configuración](#instalación-y-configuración)
4. [Características de Seguridad](#características-de-seguridad)
5. [Triggers y Auditoría Automática](#triggers-y-auditoría-automática)
6. [Vistas](#vistas)
7. [Usuarios de Prueba](#usuarios-de-prueba)

---

## 📄 Descripción General

La base de datos **EPaCe** (Eventos + Participantes + Certificación) está diseñada para gestionar:

- ✅ Autenticación y autorización de usuarios
- ✅ Gestión de eventos (conferencias, talleres, seminarios, etc.)
- ✅ Inscripciones y participaciones
- ✅ Check-in de asistencia
- ✅ Emisión de certificados digitales
- ✅ Auditoría completa del sistema

---

## 🗂️ Estructura de Tablas

### 1. **Rol**

Catálogo de roles del sistema.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| ID_Rol | SMALLSERIAL | PK - Identificador único |
| Nombre_Rol | VARCHAR(20) | Nombre del rol (ADMIN, OPERADOR, MONITOR, INVITADO) |

**Roles predefinidos:**

- `ADMIN` - Acceso completo al sistema
- `OPERADOR` - Gestión de eventos y certificados
- `MONITOR` - Registro de check-in
- `INVITADO` - Inscripción a eventos

---

### 2. **Participantes**

Usuarios del sistema con autenticación.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Documento_Identidad | BIGINT | PK - Documento único |
| Nombre | VARCHAR(25) | Nombre del participante |
| Apellido | VARCHAR(25) | Apellido del participante |
| Email | VARCHAR(45) | Email único (username) |
| Password | VARCHAR(100) | Contraseña hasheada (BCrypt) |
| Fecha_Creacion | TIMESTAMP | Fecha de registro |
| RolID_Rol | SMALLINT | FK → Rol |
| **Campos de Seguridad:** | | |
| Activo | BOOLEAN | Cuenta activa (default: TRUE) |
| Cuenta_Bloqueada | BOOLEAN | Indica bloqueo por seguridad |
| Intentos_Fallidos | INTEGER | Contador de intentos fallidos |
| Ultima_Fecha_Login | TIMESTAMP | Última vez que inició sesión |
| Fecha_Ultima_Modificacion | TIMESTAMP | Última modificación del registro |
| Token_Recuperacion | VARCHAR(100) | Token para recuperar contraseña |
| Fecha_Expiracion_Token | TIMESTAMP | Expiración del token |

**Índices:**

- `idx_participante_email` - Búsqueda por email
- `idx_participante_rol` - Filtrado por rol
- `idx_participante_activo` - Solo usuarios activos

**Constraints:**

- Email debe tener formato válido
- Documento debe ser positivo
- Intentos fallidos no pueden ser negativos

---

### 3. **Tipo_Evento**

Catálogo de tipos de eventos.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| ID_TipoEvento | SERIAL | PK - Identificador único |
| Nombre_TipoEvento | VARCHAR(20) | Nombre del tipo (Conferencia, Taller, etc.) |

**Tipos predefinidos:**

- Conferencia
- Taller
- Seminario
- Webinar
- Reunión
- Capacitación
- Simposio
- Congreso

---

### 4. **Modalidad_Evento**

Catálogo de modalidades.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| ID_ModalidadEvento | SERIAL | PK - Identificador único |
| Nombre_ModalidadEvento | VARCHAR(20) | Presencial, Virtual, Híbrido |

---

### 5. **Eventos**

Eventos programados en el sistema.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| ID_Evento | SERIAL | PK - Identificador único |
| Nombre | VARCHAR(30) | Nombre del evento |
| Descripcion | VARCHAR(150) | Descripción breve |
| Fecha_Inicio | TIMESTAMP | Inicio del evento |
| Fecha_Fin | TIMESTAMP | Fin del evento |
| Duracion_Horas | REAL | Duración en horas |
| Aforo_Maximo | INTEGER | Capacidad máxima |
| Estado | VARCHAR(10) | ACTIVO, CANCELADO, FINALIZADO, BORRADOR |
| Modalidad_EventoID_ModalidadEvento | INTEGER | FK → Modalidad_Evento |
| Tipo_EventoID_TipoEvento | INTEGER | FK → Tipo_Evento |
| Fecha_Creacion | TIMESTAMP | Cuándo se creó |
| Creado_Por | BIGINT | FK → Participantes |

**Constraints:**

- Fecha_Fin debe ser posterior a Fecha_Inicio
- Aforo debe ser positivo
- Estado debe ser válido (ACTIVO, CANCELADO, FINALIZADO, BORRADOR)

---

### 6. **Participacion**

Relación entre participantes y eventos (inscripciones).

| Campo | Tipo | Descripción |
|-------|------|-------------|
| ID_Participacion | SERIAL | PK - Identificador único |
| Fecha_Inscripcion | TIMESTAMP | Cuándo se inscribió |
| ParticipanteDocumento_Identidad | BIGINT | FK → Participantes |
| EventoID_Evento | INTEGER | FK → Eventos |
| Estado_Participacion | VARCHAR(20) | INSCRITO, CONFIRMADO, CANCELADO, ASISTIO, NO_ASISTIO |
| Fecha_Cancelacion | TIMESTAMP | Si canceló, cuándo |

**Constraint único:**

- Un participante solo puede inscribirse una vez por evento

---

### 7. **CheckIn**

Registro de asistencia a eventos.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| ID_CheckIn | SERIAL | PK - Identificador único |
| Metodo_CheckIn | VARCHAR(10) | QR, MANUAL, NFC, BIOMETRIC |
| IPCheckIn | VARCHAR(15) | IP desde donde se hizo check-in |
| Fecha_HoraCheckIn | TIMESTAMP | Cuándo hizo check-in |
| ParticipacionID_Participacion | INTEGER | FK → Participacion (UNIQUE) |
| Ubicacion | VARCHAR(100) | Ubicación geográfica |
| Dispositivo | VARCHAR(50) | Dispositivo usado |

---

### 8. **Certificacion**

Certificados digitales emitidos.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| ID_Certificacion | SERIAL | PK - Identificador único |
| Fecha_Emision | TIMESTAMP | Cuándo se emitió |
| RutaPDF | VARCHAR(200) | Ruta del archivo PDF |
| Emitido | BOOLEAN | Si ya fue generado |
| Codigo_UnicoAPI | VARCHAR(40) | Código público de verificación (UNIQUE) |
| ParticipacionID_Participacion | INTEGER | FK → Participacion |
| Hash_Verificacion | VARCHAR(64) | SHA-256 del PDF |
| Fecha_Descarga | TIMESTAMP | Primera descarga |
| Numero_Descargas | INTEGER | Contador de descargas |

---

### 9. **Auditoria**

Registro completo de auditoría del sistema.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| ID_Auditoria | SERIAL | PK - Identificador único |
| Fecha_Hora | TIMESTAMP | Cuándo ocurrió la acción |
| IP_Origen | VARCHAR(45) | IP desde donde se ejecutó |
| Entidad_Afectada | VARCHAR(50) | Tabla afectada |
| Accion | VARCHAR(20) | INSERT, UPDATE, DELETE, LOGIN, LOGOUT, FAILED_LOGIN |
| Descripcion_Cambio | TEXT | Descripción detallada |
| ParticipanteDocumento_Identidad | BIGINT | FK → Participantes |
| Datos_Anteriores | JSONB | Estado anterior (JSON) |
| Datos_Nuevos | JSONB | Estado nuevo (JSON) |

---

## 🚀 Instalación y Configuración

### Opción 1: Script Automático (Windows)

Ejecuta el archivo batch:

```bash
crear-base-datos.bat
```

Este script:

1. ✅ Elimina la base de datos anterior (si existe)
2. ✅ Crea la base de datos nueva
3. ✅ Crea todas las tablas con sus relaciones
4. ✅ Inserta datos iniciales (roles, tipos, usuarios)
5. ✅ Configura triggers de auditoría

### Opción 2: Manual con psql

```bash
# Desde la carpeta del proyecto
cd backend

# Ejecutar el script
psql -U postgres -f src/main/resources/database-complete.sql
```

### Opción 3: Desde pgAdmin

1. Conectarse a PostgreSQL
2. Conectarse a la base de datos `postgres`
3. Abrir Query Tool
4. Cargar el archivo `src/main/resources/database-complete.sql`
5. Ejecutar (F5)

---

## 🔒 Características de Seguridad

### 1. **Bloqueo de Cuenta por Intentos Fallidos**

Los campos `Intentos_Fallidos` y `Cuenta_Bloqueada` permiten implementar:

- Bloqueo automático después de N intentos fallidos
- Registro del número de intentos
- Desbloqueo manual por administrador

### 2. **Recuperación de Contraseña**

Campos: `Token_Recuperacion`, `Fecha_Expiracion_Token`

- Token único generado al solicitar recuperación
- Expira después de X tiempo
- Un solo uso

### 3. **Auditoría Automática**

Triggers automáticos registran:

- Todos los INSERT, UPDATE, DELETE en tablas críticas
- Estado anterior y nuevo (JSON)
- Usuario que realizó la acción
- IP de origen
- Timestamp exacto

### 4. **Integridad de Certificados**

Campo `Hash_Verificacion`:

- SHA-256 del PDF generado
- Permite verificar que el certificado no fue alterado
- Código único público para validación

---

## 🔄 Triggers y Auditoría Automática

### Función de Auditoría

```sql
CREATE OR REPLACE FUNCTION audit_trigger_func()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        INSERT INTO "Auditoria" (...)
        VALUES (TG_TABLE_NAME, 'DELETE', 'Registro eliminado', row_to_json(OLD));
        RETURN OLD;
    -- ... más lógica
END;
$$ LANGUAGE plpgsql;
```

### Triggers Activos

- ✅ `audit_participantes` - Audita cambios en Participantes
- ✅ `audit_eventos` - Audita cambios en Eventos
- ✅ `update_participante_modtime` - Actualiza Fecha_Ultima_Modificacion

---

## 📊 Vistas

### v_ParticipantesConRol

Vista para consultar participantes con su rol.

```sql
SELECT * FROM "v_ParticipantesConRol";
```

### v_EventosDetalle

Vista con información completa de eventos incluyendo estadísticas.

```sql
SELECT * FROM "v_EventosDetalle";
```

---

## 👥 Usuarios de Prueba

| Email | Password | Rol | Documento |
|-------|----------|-----|-----------|
| <admin@epace.com> | **admin123** | ADMIN | 1000000001 |
| <operador@epace.com> | test123 | OPERADOR | 1000000002 |
| <monitor@epace.com> | test123 | MONITOR | 1000000003 |
| <invitado@epace.com> | test123 | INVITADO | 1000000004 |

---

## 🔧 Configuración de Spring Boot

El archivo `application.properties` está configurado para:

```properties
# Crear/Actualizar tablas automáticamente
spring.jpa.hibernate.ddl-auto=update

# Para recrear desde cero en cada inicio (DESARROLLO SOLAMENTE):
# spring.jpa.hibernate.ddl-auto=create-drop
```

**IMPORTANTE:**

- En DESARROLLO: Usa `update` o `create-drop`
- En PRODUCCIÓN: Usa `validate` y gestiona migraciones con Flyway/Liquibase

---

## 📝 Notas Adicionales

### Eliminación en Cascada

| Tabla Padre | Tabla Hija | Acción |
|-------------|------------|--------|
| Participantes | Participacion | CASCADE (si se elimina participante, se eliminan sus inscripciones) |
| Eventos | Participacion | CASCADE |
| Participacion | CheckIn | CASCADE |
| Participacion | Certificacion | CASCADE |
| Rol | Participantes | RESTRICT (no se puede eliminar rol si tiene usuarios) |

### Respaldos Recomendados

```bash
# Backup completo
pg_dump -U postgres EPaCe > backup_epace_$(date +%Y%m%d).sql

# Restore
psql -U postgres -d EPaCe < backup_epace_20250101.sql
```

---

## 🎯 Próximos Pasos

Una vez creada la base de datos:

1. ✅ Ejecutar `crear-base-datos.bat` o script SQL manual
2. ✅ Verificar que las tablas se crearon correctamente
3. ✅ Probar login con usuarios de prueba
4. ✅ Implementar lógica de bloqueo de cuenta
5. ✅ Implementar recuperación de contraseña
6. ✅ Configurar generación de certificados PDF

---

**¿Necesitas ayuda?** Revisa el archivo `IMPLEMENTATION_SUMMARY.md` para más detalles del backend.
