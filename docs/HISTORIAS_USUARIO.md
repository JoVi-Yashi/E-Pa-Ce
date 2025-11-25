# Historias de Usuario - Sistema E-Pa-Ce

## 📖 Formato de Historias de Usuario

**Como** [rol]  
**Quiero** [funcionalidad]  
**Para** [beneficio/objetivo]

**Criterios de Aceptación:**

- [ ] Criterio 1
- [ ] Criterio 2

---

## 👨‍💼 Módulo: Gestión de Eventos

### HU-01: Crear Evento

**Como** Administrador  
**Quiero** crear un nuevo evento con todos sus detalles  
**Para** organizar capacitaciones, talleres o conferencias

**Criterios de Aceptación:**

- [ ] Puedo ingresar nombre, descripción, fechas de inicio y fin
- [ ] Puedo especificar aforo máximo y duración en horas
- [ ] Puedo seleccionar modalidad (Presencial, Virtual, Híbrido)
- [ ] Puedo seleccionar tipo de evento (Capacitación, Taller, Conferencia, etc.)
- [ ] El sistema valida que la fecha de inicio sea anterior a la fecha de fin
- [ ] El sistema calcula automáticamente la duración si proporciono las fechas
- [ ] El evento se crea con estado "ACTIVO" por defecto

### HU-02: Listar y Filtrar Eventos

**Como** Operador  
**Quiero** ver todos los eventos con filtros avanzados  
**Para** encontrar rápidamente eventos específicos

**Criterios de Aceptación:**

- [ ] Puedo ver una lista de todos los eventos
- [ ] Puedo filtrar por estado (Activo, Cancelado, Finalizado)
- [ ] Puedo filtrar por modalidad (Presencial, Virtual, Híbrido)
- [ ] Puedo filtrar por tipo de evento
- [ ] Puedo filtrar por rango de fechas
- [ ] Puedo ver el aforo actual vs. aforo máximo de cada evento

### HU-03: Actualizar Evento

**Como** Administrador  
**Quiero** modificar los detalles de un evento existente  
**Para** corregir información o adaptarme a cambios

**Criterios de Aceptación:**

- [ ] Puedo editar nombre, descripción y fechas
- [ ] Puedo cambiar el estado del evento
- [ ] Puedo modificar el aforo máximo
- [ ] El sistema registra quién hizo el cambio y cuándo (auditoría)
- [ ] No puedo reducir el aforo por debajo del número de inscritos actual

### HU-04: Cancelar Evento

**Como** Administrador  
**Quiero** cancelar un evento  
**Para** informar a los participantes que no se realizará

**Criterios de Aceptación:**

- [ ] Puedo cambiar el estado del evento a "CANCELADO"
- [ ] El sistema notifica a todos los participantes inscritos
- [ ] Los participantes inscritos pueden ver el estado cancelado
- [ ] Se registra en auditoría quién canceló el evento

---

## 👥 Módulo: Gestión de Participantes

### HU-05: Registrar Participante

**Como** Operador  
**Quiero** registrar un nuevo participante en el sistema  
**Para** que pueda inscribirse a eventos

**Criterios de Aceptación:**

- [ ] Puedo ingresar documento, nombre, apellido y email
- [ ] El sistema valida que el email sea único
- [ ] El sistema valida que el documento sea único
- [ ] Puedo asignar un rol al participante (Participante, Monitor, Operador, Admin)
- [ ] El sistema genera automáticamente la fecha de creación
- [ ] La contraseña se almacena de forma segura (hasheada)

### HU-06: Inscribir Participante a Evento

**Como** Participante  
**Quiero** inscribirme a un evento disponible  
**Para** poder asistir y obtener certificación

**Criterios de Aceptación:**

- [ ] Puedo ver eventos disponibles para inscripción
- [ ] Puedo inscribirme proporcionando mi email
- [ ] El sistema verifica que el evento tenga cupos disponibles
- [ ] El sistema genera un código único para mi inscripción
- [ ] Recibo el código único por email o en pantalla
- [ ] No puedo inscribirme dos veces al mismo evento
- [ ] Se registra la fecha y hora de inscripción

### HU-07: Ver Historial de Participación

**Como** Participante  
**Quiero** ver todos los eventos en los que he participado  
**Para** llevar un registro de mi capacitación

**Criterios de Aceptación:**

- [ ] Puedo ver lista de eventos en los que me he inscrito
- [ ] Puedo ver si he realizado check-in en cada evento
- [ ] Puedo ver si he recibido certificado de cada evento
- [ ] Puedo filtrar por eventos pasados o futuros
- [ ] Puedo descargar mis certificados desde el historial

---

## ✅ Módulo: Check-In

### HU-08: Realizar Check-In con Código QR

**Como** Participante  
**Quiero** hacer check-in escaneando un código QR  
**Para** registrar mi asistencia de forma rápida

**Criterios de Aceptación:**

- [ ] Puedo escanear el código QR de mi inscripción
- [ ] El sistema valida que el código sea válido
- [ ] El sistema registra fecha, hora e IP del check-in
- [ ] El sistema registra el método de check-in (QR)
- [ ] No puedo hacer check-in dos veces
- [ ] Recibo confirmación visual de check-in exitoso

### HU-09: Realizar Check-In Manual

**Como** Monitor  
**Quiero** registrar check-in manualmente ingresando el código  
**Para** ayudar a participantes con problemas técnicos

**Criterios de Aceptación:**

- [ ] Puedo ingresar el código único manualmente
- [ ] El sistema valida el código
- [ ] Se registra el método como "MANUAL"
- [ ] Se registra mi usuario como quien realizó el check-in
- [ ] Puedo ver confirmación del participante que hizo check-in

### HU-10: Ver Listado de Asistencia

**Como** Operador  
**Quiero** ver quiénes han hecho check-in en un evento  
**Para** llevar control de asistencia en tiempo real

**Criterios de Aceptación:**

- [ ] Puedo ver lista de todos los inscritos al evento
- [ ] Puedo ver quiénes han hecho check-in y quiénes no
- [ ] Puedo ver la hora exacta de cada check-in
- [ ] Puedo filtrar solo los que han asistido
- [ ] Puedo exportar el listado de asistencia

---

## 📜 Módulo: Certificación Digital

### HU-11: Generar Certificado Automáticamente

**Como** Sistema  
**Quiero** generar certificados PDF automáticamente  
**Para** entregarlos a participantes que completaron el evento

**Criterios de Aceptación:**

- [ ] El certificado se genera automáticamente al finalizar el evento
- [ ] Solo se genera para participantes que hicieron check-in
- [ ] El PDF incluye: nombre del participante, nombre del evento, duración, fecha
- [ ] El PDF incluye un código único verificable
- [ ] El certificado se almacena en el servidor
- [ ] Se registra la fecha de emisión

### HU-12: Descargar Certificado

**Como** Participante  
**Quiero** descargar mi certificado en PDF  
**Para** tener constancia de mi participación

**Criterios de Aceptación:**

- [ ] Puedo acceder a mis certificados desde mi perfil
- [ ] Puedo descargar el PDF directamente
- [ ] El PDF tiene calidad profesional
- [ ] El nombre del archivo es descriptivo (evento_nombre_participante.pdf)

### HU-13: Verificar Autenticidad de Certificado

**Como** Empleador o Institución  
**Quiero** verificar si un certificado es auténtico  
**Para** confirmar que el participante realmente asistió

**Criterios de Aceptación:**

- [ ] Puedo acceder a una API pública de verificación
- [ ] Puedo ingresar el código único del certificado
- [ ] El sistema me indica si el certificado es válido
- [ ] El sistema me muestra: nombre del participante, evento, fecha de emisión
- [ ] El sistema me indica si el certificado ha sido revocado
- [ ] No necesito autenticación para verificar

---

## 🔍 Módulo: Auditoría

### HU-14: Registrar Todas las Acciones

**Como** Sistema  
**Quiero** registrar automáticamente todas las acciones críticas  
**Para** mantener trazabilidad y seguridad

**Criterios de Aceptación:**

- [ ] Se registra: quién, qué, cuándo, desde dónde (IP)
- [ ] Se registran creaciones, actualizaciones y eliminaciones
- [ ] Se registran check-ins exitosos y fallidos
- [ ] Se registran emisiones de certificados
- [ ] Se registran cambios de estado de eventos

### HU-15: Consultar Logs de Auditoría

**Como** Administrador  
**Quiero** consultar el historial de auditoría  
**Para** investigar incidentes o generar reportes

**Criterios de Aceptación:**

- [ ] Puedo ver todos los logs ordenados por fecha
- [ ] Puedo filtrar por entidad (Evento, Participante, etc.)
- [ ] Puedo filtrar por acción (CREATED, UPDATED, DELETED)
- [ ] Puedo filtrar por usuario
- [ ] Puedo filtrar por rango de fechas
- [ ] Puedo exportar los logs a CSV

---

## 📊 Módulo: Dashboard y Reportes

### HU-16: Ver Dashboard con Métricas

**Como** Administrador  
**Quiero** ver un dashboard con métricas clave  
**Para** tomar decisiones informadas

**Criterios de Aceptación:**

- [ ] Puedo ver total de eventos activos, finalizados y cancelados
- [ ] Puedo ver total de participantes registrados
- [ ] Puedo ver tasa de asistencia promedio
- [ ] Puedo ver eventos con mayor y menor asistencia
- [ ] Puedo ver gráficos de tendencias por mes
- [ ] Puedo filtrar métricas por rango de fechas

### HU-17: Generar Reportes Personalizados

**Como** Operador  
**Quiero** generar reportes con filtros específicos  
**Para** analizar datos según mis necesidades

**Criterios de Aceptación:**

- [ ] Puedo seleccionar qué datos incluir en el reporte
- [ ] Puedo filtrar por modalidad, tipo, fechas
- [ ] Puedo exportar a CSV o PDF
- [ ] El reporte incluye gráficos visuales
- [ ] Puedo guardar configuraciones de reportes frecuentes

---

## 📥 Módulo: Importación Masiva

### HU-18: Importar Participantes desde CSV

**Como** Administrador  
**Quiero** importar múltiples participantes desde un archivo CSV  
**Para** agilizar el registro masivo

**Criterios de Aceptación:**

- [ ] Puedo cargar un archivo CSV con formato específico
- [ ] El sistema me muestra una previsualización de los datos
- [ ] El sistema valida cada fila antes de importar
- [ ] El sistema me muestra errores específicos por fila
- [ ] Puedo corregir errores y volver a intentar
- [ ] El sistema me muestra un resumen: X exitosos, Y con errores
- [ ] Si hay errores críticos, no se importa nada (rollback)

### HU-19: Importar Inscripciones Masivas

**Como** Operador  
**Quiero** inscribir múltiples participantes a un evento desde CSV  
**Para** facilitar inscripciones grupales

**Criterios de Aceptación:**

- [ ] Puedo cargar CSV con emails de participantes
- [ ] El sistema valida que todos los participantes existan
- [ ] El sistema valida que haya cupos suficientes
- [ ] El sistema genera códigos únicos para todos
- [ ] Puedo descargar un CSV con los códigos generados
- [ ] Se envían emails automáticos a todos los inscritos

---

## 🔐 Módulo: Roles y Permisos

### HU-20: Controlar Acceso por Rol

**Como** Sistema  
**Quiero** restringir funcionalidades según el rol del usuario  
**Para** mantener seguridad y organización

**Criterios de Aceptación:**

- [ ] **Admin**: Acceso total al sistema
- [ ] **Operador**: Puede gestionar eventos y participantes, no puede cambiar roles
- [ ] **Monitor**: Solo puede ver eventos y realizar check-ins
- [ ] **Invitado**: Solo puede ver eventos públicos y su propio perfil
- [ ] El sistema valida permisos en cada acción
- [ ] Se muestra mensaje claro si no tiene permisos

---

## 📊 Resumen de Historias de Usuario

| Módulo                   | Cantidad de HU |
| ------------------------ | -------------- |
| Gestión de Eventos       | 4              |
| Gestión de Participantes | 3              |
| Check-In                 | 3              |
| Certificación Digital    | 3              |
| Auditoría                | 2              |
| Dashboard y Reportes     | 2              |
| Importación Masiva       | 2              |
| Roles y Permisos         | 1              |
| **TOTAL**                | **20**         |

---

**Prioridad de Implementación:**

1. 🔴 Alta: HU-01 a HU-10 (Core del sistema)
2. 🟡 Media: HU-11 a HU-15 (Certificación y auditoría)
3. 🟢 Baja: HU-16 a HU-20 (Reportes e importación)
