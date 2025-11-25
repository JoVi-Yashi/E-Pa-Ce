# Catálogo de Requerimientos - Sistema E-Pa-Ce

## 📋 Información del Proyecto

| Campo           | Valor                                            |
| --------------- | ------------------------------------------------ |
| **Proyecto**    | E-Pa-Ce - Sistema Integral de Gestión de Eventos |
| **Versión**     | 1.0.0                                            |
| **Fecha**       | 2025-11-24                                       |
| **Tecnologías** | Go (Gin + GORM), Vue.js, PostgreSQL              |

---

## 🎯 Requerimientos Funcionales

### RF-01: Gestión de Eventos

#### RF-01.1: Crear Evento

- **Descripción**: El sistema debe permitir crear eventos con información completa
- **Prioridad**: Alta
- **Entradas**: Nombre, descripción, fecha inicio, fecha fin, aforo máximo, modalidad, tipo
- **Salidas**: Evento creado con ID único y estado ACTIVO
- **Validaciones**:
  - Nombre: 3-30 caracteres
  - Fecha inicio < Fecha fin
  - Aforo máximo ≥ 0
  - Modalidad y tipo deben existir en catálogo

#### RF-01.2: Listar Eventos

- **Descripción**: El sistema debe mostrar lista de eventos con filtros
- **Prioridad**: Alta
- **Filtros disponibles**:
  - Estado (Activo, Cancelado, Finalizado, Borrador)
  - Modalidad (Presencial, Virtual, Híbrido)
  - Tipo de evento
  - Rango de fechas
- **Salidas**: Lista paginada de eventos con información resumida

#### RF-01.3: Actualizar Evento

- **Descripción**: El sistema debe permitir modificar eventos existentes
- **Prioridad**: Alta
- **Restricciones**:
  - No se puede reducir aforo por debajo de inscritos actuales
  - Solo usuarios con rol Admin u Operador pueden actualizar
- **Auditoría**: Se debe registrar quién y cuándo modificó

#### RF-01.4: Eliminar Evento (Soft Delete)

- **Descripción**: El sistema debe permitir eliminación lógica de eventos
- **Prioridad**: Media
- **Comportamiento**: El evento se marca como eliminado pero permanece en BD
- **Restricciones**: Solo Admin puede eliminar

#### RF-01.5: Calcular Duración Automática

- **Descripción**: El sistema debe calcular duración en horas entre fechas
- **Prioridad**: Media
- **Cálculo**: (Fecha_Fin - Fecha_Inicio) en horas decimales

---

### RF-02: Gestión de Participantes

#### RF-02.1: Registrar Participante

- **Descripción**: El sistema debe permitir registro de nuevos participantes
- **Prioridad**: Alta
- **Entradas**: Documento, nombre, apellido, email, password, rol
- **Validaciones**:
  - Email único en el sistema
  - Documento único en el sistema
  - Email con formato válido
  - Password mínimo 8 caracteres
- **Seguridad**: Password debe almacenarse hasheado (bcrypt)

#### RF-02.2: Autenticar Participante

- **Descripción**: El sistema debe permitir login con email y password
- **Prioridad**: Alta
- **Salidas**: Token JWT con información del usuario y rol
- **Seguridad**: Máximo 5 intentos fallidos, luego bloqueo temporal

#### RF-02.3: Gestionar Roles

- **Descripción**: El sistema debe soportar 4 roles con permisos diferenciados
- **Prioridad**: Alta
- **Roles**:
  - **Admin**: Acceso total
  - **Operador**: Gestión de eventos y participantes
  - **Monitor**: Visualización y check-in
  - **Invitado**: Solo consulta de eventos públicos

#### RF-02.4: Ver Historial de Participación

- **Descripción**: Participante puede ver sus eventos pasados y futuros
- **Prioridad**: Media
- **Información mostrada**:
  - Eventos inscritos
  - Estado de check-in
  - Certificados disponibles

---

### RF-03: Inscripciones (Participación)

#### RF-03.1: Inscribir Participante a Evento

- **Descripción**: El sistema debe permitir inscripción a eventos
- **Prioridad**: Alta
- **Validaciones**:
  - Evento debe estar ACTIVO
  - Debe haber cupos disponibles
  - Participante no debe estar ya inscrito
- **Salidas**: Código único (UUID) para check-in y certificación

#### RF-03.2: Generar Código Único

- **Descripción**: El sistema debe generar código UUID v4 por inscripción
- **Prioridad**: Alta
- **Formato**: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
- **Unicidad**: Debe ser único en toda la tabla Participacion

#### RF-03.3: Cancelar Inscripción

- **Descripción**: Participante puede cancelar su inscripción
- **Prioridad**: Media
- **Restricciones**: Solo antes de que inicie el evento

#### RF-03.4: Listar Inscritos por Evento

- **Descripción**: Operador puede ver lista de inscritos a un evento
- **Prioridad**: Alta
- **Información**: Nombre, email, fecha inscripción, estado check-in

---

### RF-04: Check-In

#### RF-04.1: Realizar Check-In con Código QR

- **Descripción**: El sistema debe permitir check-in escaneando QR
- **Prioridad**: Alta
- **Proceso**:
  1. Escanear QR que contiene código único
  2. Validar código en BD
  3. Registrar check-in con fecha, hora, IP y método
- **Validaciones**:
  - Código debe existir
  - No debe haber check-in previo
  - Evento debe estar en curso o próximo a iniciar

#### RF-04.2: Realizar Check-In Manual

- **Descripción**: Monitor puede registrar check-in ingresando código
- **Prioridad**: Alta
- **Método**: MANUAL
- **Auditoría**: Se registra quién realizó el check-in manual

#### RF-04.3: Prevenir Check-In Duplicado

- **Descripción**: El sistema debe impedir múltiples check-ins
- **Prioridad**: Alta
- **Comportamiento**: Si ya existe check-in, retornar error descriptivo

#### RF-04.4: Registrar Datos de Check-In

- **Descripción**: El sistema debe guardar metadatos del check-in
- **Prioridad**: Alta
- **Datos a registrar**:
  - Fecha y hora exacta
  - Dirección IP origen
  - Método (QR, MANUAL)
  - ID de participación

---

### RF-05: Certificación Digital

#### RF-05.1: Generar Certificado PDF

- **Descripción**: El sistema debe generar PDFs automáticamente
- **Prioridad**: Alta
- **Trigger**: Al finalizar evento, para participantes con check-in
- **Contenido del PDF**:
  - Nombre completo del participante
  - Nombre del evento
  - Duración en horas
  - Fecha de emisión
  - Código único verificable
- **Almacenamiento**: Ruta del PDF en campo RutaPDF

#### RF-05.2: Descargar Certificado

- **Descripción**: Participante puede descargar su certificado
- **Prioridad**: Alta
- **Restricciones**: Solo si el certificado ha sido emitido (Emitido = true)

#### RF-05.3: Verificar Certificado por API

- **Descripción**: API pública para verificar autenticidad
- **Prioridad**: Alta
- **Endpoint**: `GET /api/public/verify-certificate/:code`
- **Entrada**: Código único del certificado
- **Salida JSON**:

  ```json
  {
    "valid": true,
    "participant_name": "Juan Pérez",
    "event_name": "Taller de Go",
    "issue_date": "2024-11-24",
    "duration_hours": 8
  }
  ```

- **Sin autenticación**: Endpoint público

#### RF-05.4: Marcar Certificado como Emitido

- **Descripción**: Al generar PDF, marcar Emitido = true
- **Prioridad**: Alta
- **Fecha de emisión**: Timestamp actual

---

### RF-06: Auditoría

#### RF-06.1: Registrar Acciones Automáticamente

- **Descripción**: El sistema debe auditar acciones críticas
- **Prioridad**: Alta
- **Acciones a auditar**:
  - CREATED, UPDATED, DELETED en Eventos
  - CREATED en Participantes
  - ENROLLMENT en Participacion
  - CHECKIN_SUCCESS, CHECKIN_FAILED en Check-In
  - CERTIFICATE_ISSUED en Certificacion
- **Datos registrados**:
  - Fecha y hora
  - IP origen
  - Entidad afectada
  - Acción realizada
  - Descripción del cambio
  - Usuario que realizó la acción

#### RF-06.2: Consultar Logs de Auditoría

- **Descripción**: Admin puede consultar historial de auditoría
- **Prioridad**: Media
- **Filtros**:
  - Por entidad
  - Por acción
  - Por usuario
  - Por rango de fechas
- **Exportación**: Permitir exportar a CSV

---

### RF-07: Dashboard y Reportes

#### RF-07.1: Dashboard con Métricas

- **Descripción**: Mostrar métricas clave del sistema
- **Prioridad**: Media
- **Métricas**:
  - Total eventos activos/finalizados/cancelados
  - Total participantes registrados
  - Tasa de asistencia promedio
  - Eventos con mayor/menor asistencia
  - Tendencias por mes
- **Actualización**: Tiempo real o cache de 5 minutos

#### RF-07.2: Reportes Personalizados

- **Descripción**: Generar reportes con filtros personalizados
- **Prioridad**: Baja
- **Formatos de exportación**: CSV, PDF
- **Filtros disponibles**:
  - Modalidad
  - Tipo de evento
  - Rango de fechas
  - Estado

---

### RF-08: Importación Masiva

#### RF-08.1: Importar Participantes desde CSV

- **Descripción**: Cargar múltiples participantes desde archivo
- **Prioridad**: Media
- **Formato CSV**: documento,nombre,apellido,email,rol
- **Proceso**:
  1. Cargar archivo
  2. Previsualizar datos
  3. Validar cada fila
  4. Mostrar errores
  5. Confirmar importación
- **Validaciones**:
  - Formato de email
  - Documentos únicos
  - Emails únicos
  - Rol válido

#### RF-08.2: Importar Inscripciones Masivas

- **Descripción**: Inscribir múltiples participantes a un evento
- **Prioridad**: Baja
- **Formato CSV**: email
- **Validaciones**:
  - Todos los emails deben existir
  - Debe haber cupos suficientes
- **Salida**: CSV con códigos únicos generados

#### RF-08.3: Reporte de Errores en Importación

- **Descripción**: Mostrar errores específicos por fila
- **Prioridad**: Media
- **Información del error**:
  - Número de fila
  - Campo con error
  - Descripción del error

---

### RF-09: Modalidades y Tipos de Evento

#### RF-09.1: Gestionar Modalidades

- **Descripción**: CRUD de modalidades de evento
- **Prioridad**: Media
- **Modalidades iniciales**:
  - Presencial
  - Virtual
  - Híbrido

#### RF-09.2: Gestionar Tipos de Evento

- **Descripción**: CRUD de tipos de evento
- **Prioridad**: Media
- **Tipos iniciales**:
  - Capacitación
  - Taller
  - Conferencia
  - Seminario
  - Webinar
  - Torneo

---

## 🔒 Requerimientos No Funcionales

### RNF-01: Rendimiento

#### RNF-01.1: Tiempo de Respuesta

- **Descripción**: Las operaciones deben ser rápidas
- **Criterio**:
  - Consultas simples: < 200ms
  - Consultas complejas: < 1s
  - Generación de PDF: < 3s

#### RNF-01.2: Concurrencia

- **Descripción**: Soportar múltiples usuarios simultáneos
- **Criterio**: Mínimo 100 usuarios concurrentes sin degradación

#### RNF-01.3: Escalabilidad

- **Descripción**: El sistema debe escalar horizontalmente
- **Criterio**: Arquitectura stateless para permitir múltiples instancias

---

### RNF-02: Seguridad

#### RNF-02.1: Autenticación

- **Descripción**: Acceso solo con credenciales válidas
- **Implementación**: JWT con expiración de 24 horas

#### RNF-02.2: Autorización

- **Descripción**: Control de acceso basado en roles (RBAC)
- **Implementación**: Middleware que valida permisos por endpoint

#### RNF-02.3: Protección de Datos Sensibles

- **Descripción**: Passwords y datos sensibles protegidos
- **Implementación**:
  - Passwords hasheados con bcrypt (cost 12)
  - HTTPS en producción
  - Sanitización de inputs

#### RNF-02.4: Prevención de Ataques

- **Descripción**: Protección contra ataques comunes
- **Implementación**:
  - SQL Injection: Uso de ORM (GORM)
  - XSS: Sanitización de inputs
  - CSRF: Tokens CSRF
  - Rate Limiting: Máximo 100 req/min por IP

---

### RNF-03: Disponibilidad

#### RNF-03.1: Uptime

- **Descripción**: El sistema debe estar disponible
- **Criterio**: 99.5% uptime (máximo 3.65 horas de downtime/mes)

#### RNF-03.2: Recuperación ante Fallos

- **Descripción**: El sistema debe recuperarse de errores
- **Implementación**:
  - Graceful shutdown
  - Health checks
  - Logs de errores

---

### RNF-04: Mantenibilidad

#### RNF-04.1: Código Limpio

- **Descripción**: Código legible y bien documentado
- **Criterio**:
  - Comentarios en español
  - Funciones con documentación
  - Nombres descriptivos

#### RNF-04.2: Arquitectura Limpia

- **Descripción**: Separación de responsabilidades
- **Implementación**: Arquitectura en capas (Controllers, Services, Repositories)

#### RNF-04.3: Versionado

- **Descripción**: Control de versiones del código
- **Implementación**: Git con commits descriptivos

---

### RNF-05: Usabilidad

#### RNF-05.1: Interfaz Intuitiva

- **Descripción**: UI fácil de usar
- **Criterio**: Usuario nuevo debe poder realizar tareas básicas sin capacitación

#### RNF-05.2: Mensajes de Error Claros

- **Descripción**: Errores descriptivos para el usuario
- **Criterio**: Mensajes en español, explicando qué salió mal y cómo corregirlo

#### RNF-05.3: Responsive Design

- **Descripción**: Funcional en diferentes dispositivos
- **Criterio**: Compatible con desktop, tablet y móvil

---

### RNF-06: Compatibilidad

#### RNF-06.1: Navegadores

- **Descripción**: Funcional en navegadores modernos
- **Criterio**: Chrome 90+, Firefox 88+, Safari 14+, Edge 90+

#### RNF-06.2: Base de Datos

- **Descripción**: PostgreSQL como SGBD
- **Versión**: PostgreSQL 12 o superior

---

### RNF-07: Auditoría y Trazabilidad

#### RNF-07.1: Logs Completos

- **Descripción**: Registro de todas las acciones críticas
- **Retención**: Mínimo 1 año

#### RNF-07.2: Integridad de Datos

- **Descripción**: Los datos no deben corromperse
- **Implementación**:
  - Transacciones ACID
  - Validaciones en múltiples capas
  - Backups diarios

---

### RNF-08: Documentación

#### RNF-08.1: Documentación Técnica

- **Descripción**: Documentación completa del sistema
- **Contenido**:
  - Arquitectura del sistema
  - Modelo de datos
  - API endpoints
  - Guías de instalación y despliegue

#### RNF-08.2: Documentación de Usuario

- **Descripción**: Manuales para usuarios finales
- **Contenido**:
  - Guía de uso por rol
  - FAQs
  - Videos tutoriales

---

## 📊 Resumen de Requerimientos

| Categoría                         | Cantidad |
| --------------------------------- | -------- |
| **Requerimientos Funcionales**    | 32       |
| **Requerimientos No Funcionales** | 16       |
| **TOTAL**                         | **48**   |

---

## 🎯 Matriz de Trazabilidad

| Requerimiento | Historia de Usuario | Prioridad |
| ------------- | ------------------- | --------- |
| RF-01.1       | HU-01               | Alta      |
| RF-01.2       | HU-02               | Alta      |
| RF-02.1       | HU-05               | Alta      |
| RF-03.1       | HU-06               | Alta      |
| RF-04.1       | HU-08               | Alta      |
| RF-05.1       | HU-11               | Alta      |
| RF-05.3       | HU-13               | Alta      |
| RF-06.1       | HU-14               | Alta      |

---

**Fecha de última actualización**: 2025-11-24  
**Versión del documento**: 1.0.0
