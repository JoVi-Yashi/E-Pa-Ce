# Documentación del API REST - E-Pa-Ce

Este documento describe los endpoints disponibles en la API del sistema **E-Pa-Ce**.

URL Base: `http://localhost:8081`

---

## 1. Autenticación (`/api/auth`)

Encargado del registro e inicio de sesión de usuarios.

| Método | Endpoint | Descripción | Body Request | Respuesta Exitosa |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/login` | Iniciar sesión | `LoginRequest` (email, password) | `JwtResponse` (token, user info) |
| **POST** | `/api/auth/signup` | Registrar nuevo usuario | `SignupRequest` (documentoIdentidad, nombre, apellido, email, password, rolId) | `MessageResponse` |

---

## 2. Gestión de Eventos (`/api/eventos`)

CRUD para la gestión de eventos académicos o sociales.

| Método | Endpoint | Descripción | Body Request | Respuesta Exitosa |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/eventos` | Listar todos los eventos | N/A | `List<EventoResponse>` |
| **GET** | `/api/eventos/{id}` | Obtener evento por ID | N/A | `EventoResponse` |
| **POST** | `/api/eventos` | Crear nuevo evento | `EventoRequest` | `EventoResponse` |
| **PUT** | `/api/eventos/{id}` | Actualizar evento | `EventoRequest` | `EventoResponse` |
| **DELETE** | `/api/eventos/{id}` | Eliminar evento | N/A | 204 No Content |

---

## 3. Modalidades de Evento (`/api/modalidades`)

Gestión de modalidades (Presencial, Virtual, Híbrido, etc.).

| Método | Endpoint | Descripción | Body Request | Respuesta Exitosa |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/modalidades` | Listar modalidades | N/A | `List<ModalidadEventoDTO>` |
| **GET** | `/api/modalidades/{id}` | Obtener modalidad por ID | N/A | `ModalidadEventoDTO` |
| **POST** | `/api/modalidades` | Crear modalidad | `ModalidadEventoDTO` | `ModalidadEventoDTO` |
| **PUT** | `/api/modalidades/{id}` | Actualizar modalidad | `ModalidadEventoDTO` | `ModalidadEventoDTO` |
| **DELETE** | `/api/modalidades/{id}` | Eliminar modalidad | N/A | 204 No Content |

---

## 4. Tipos de Evento (`/api/tipos-evento`)

Gestión de tipos de evento (Conferencia, Taller, Seminario, etc.).

| Método | Endpoint | Descripción | Body Request | Respuesta Exitosa |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/tipos-evento` | Listar tipos de evento | N/A | `List<TipoEventoDTO>` |
| **GET** | `/api/tipos-evento/{id}` | Obtener tipo por ID | N/A | `TipoEventoDTO` |
| **POST** | `/api/tipos-evento` | Crear tipo de evento | `TipoEventoDTO` | `TipoEventoDTO` |
| **PUT** | `/api/tipos-evento/{id}` | Actualizar tipo de evento | `TipoEventoDTO` | `TipoEventoDTO` |
| **DELETE** | `/api/tipos-evento/{id}` | Eliminar tipo de evento | N/A | 204 No Content |

---

## 5. Roles (`/api/roles`)

Gestión de roles de usuario (ADMIN, MONITOR, INVITADO, etc.).

| Método | Endpoint | Descripción | Body Request | Respuesta Exitosa |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/roles` | Listar roles | N/A | `List<RolDTO>` |
| **GET** | `/api/roles/{id}` | Obtener rol por ID | N/A | `RolDTO` |
| **POST** | `/api/roles` | Crear rol | `RolDTO` | `RolDTO` |
| **PUT** | `/api/roles/{id}` | Actualizar rol | `RolDTO` | `RolDTO` |
| **DELETE** | `/api/roles/{id}` | Eliminar rol | N/A | 204 No Content |

---

## 6. Participación / Inscripciones (`/api/participaciones`)

Gestión de inscripciones de participantes a eventos.

| Método | Endpoint | Descripción | Body Request | Respuesta Exitosa |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/participaciones` | Listar todas las inscripciones | N/A | `List<ParticipacionResponse>` |
| **GET** | `/api/participaciones/{id}` | Obtener inscripción por ID | N/A | `ParticipacionResponse` |
| **POST** | `/api/participaciones` | Inscribir participante a evento | `ParticipacionRequest` (eventoId, participanteDocumento) | `ParticipacionResponse` |
| **DELETE** | `/api/participaciones/{id}` | Cancelar inscripción | N/A | 204 No Content |
| **GET** | `/api/participaciones/evento/{id}` | Listar inscritos por Evento ID | N/A | `List<ParticipacionResponse>` |
| **GET** | `/api/participaciones/participante/{doc}` | Listar por Participante Doc | N/A | `List<ParticipacionResponse>` |

---

## 7. Check-In / Asistencia (`/api/checkins`)

Registro y control de asistencia a eventos.

| Método | Endpoint | Descripción | Body Request / Params | Respuesta Exitosa |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/checkins` | Listar todos los check-ins | N/A | `List<CheckInResponse>` |
| **GET** | `/api/checkins/{id}` | Obtener check-in por ID | N/A | `CheckInResponse` |
| **POST** | `/api/checkins` | Crear check-in manual | `CheckInRequest` (participacionId, metodo, ip) | `CheckInResponse` |
| **POST** | `/api/checkins/qr` | Check-in por Código QR | Query Param: `?codigo=...` | `CheckInResponse` |
| **DELETE** | `/api/checkins/{id}` | Eliminar check-in | N/A | 204 No Content |
| **GET** | `/api/checkins/evento/{id}` | Listar asistencia por Evento | N/A | `List<CheckInResponse>` |

---

## 8. Certificaciones (`/api/certificaciones`)

Emisión y verificación de certificados digitales.

| Método | Endpoint | Descripción | Body Request | Respuesta Exitosa |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/certificaciones` | Listar certificados emitidos | N/A | `List<CertificacionResponse>` |
| **GET** | `/api/certificaciones/{id}` | Obtener certificado por ID | N/A | `CertificacionResponse` |
| **GET** | `/api/certificaciones/verificar/{codigo}` | Verificar validez por Código | N/A | `CertificacionResponse` |
| **POST** | `/api/certificaciones/emitir` | Emitir certificado (Req. Asistencia) | `CertificacionRequest` (participacionId) | `CertificacionResponse` |
| **DELETE** | `/api/certificaciones/{id}` | Eliminar certificado | N/A | 204 No Content |
| **GET** | `/api/certificaciones/evento/{id}` | Listar por Evento | N/A | `List<CertificacionResponse>` |
| **GET** | `/api/certificaciones/participante/{doc}`| Listar por Participante | N/A | `List<CertificacionResponse>` |

---

## 9. Auditoría (`/api/auditoria`)

Registro de acciones y monitoreo (Solo Admins/Monitores).

| Método | Endpoint | Descripción | Requisito de Rol | Respuesta Exitosa |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/auditoria` | Ver todos los logs | ADMIN o MONITOR | `List<AuditoriaResponse>` |
| **GET** | `/api/auditoria/participante/{id}` | Ver logs de un usuario | ADMIN | `List<AuditoriaResponse>` |
