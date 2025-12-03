# 📋 Plan de Desarrollo Completo - Sistema E-Pa-Ce

## 🎯 Información del Proyecto

| Campo | Valor |
|-------|-------|
| **Nombre** | E-Pa-Ce - Sistema Integral de Gestión de Eventos |
| **Backend** | Java 17 + Spring Boot 3.2.1 |
| **Frontend** | Vue.js 3 |
| **Base de Datos** | PostgreSQL 12+ |
| **Estado** | Fase 2 en progreso |
| **Fecha de Revisión** | 2025-12-03 |

---

## ✅ FASE 1: PLANIFICACIÓN Y ANÁLISIS - **COMPLETADA ✓**

### Documentación Generada (`docs/`)

| Documento | Estado | Contenido |
|-----------|--------|-----------|
| `PROBLEMATICA.md` | ✅ Completo | Problemática detallada con impacto y solución propuesta |
| `HISTORIAS_USUARIO.md` | ✅ Completo | 20 historias de usuario con criterios de aceptación |
| `CATALOGO_REQUERIMIENTOS.md` | ✅ Completo | 32 RF + 16 RNF = 48 requerimientos totales |
| `MODELO_RELACIONAL.md` | ✅ Completo | 9 tablas en 3FN con relaciones completas |

**Entregables de Fase 1: 100% Completados ✓**

---

## 🔧 FASE 2: DESARROLLO DEL BACKEND (Spring Boot)

### 2.1 Configuración Inicial del Proyecto ✅ **COMPLETADA**

#### ✅ Dependencias en `pom.xml`

- Spring Boot 3.2.1
- Spring Security
- Spring Data JPA
- PostgreSQL Driver
- JWT (jjwt 0.12.3)
- Validation
- Actuator
- Lombok (para simplificar código)

#### ✅ `application.properties` configurado

- Conexión a PostgreSQL
- Configuración JWT
- Hibernate DDL-auto (update)
- Logging SQL habilitado
- HikariCP pool configurado

---

### 2.2 Capa de Persistencia (Entidades JPA) ✅ **COMPLETADA**

#### ✅ Entidades creadas en `persistence/entity/`

1. `UsuarioEntity.java` - Participantes del sistema
2. `RolesEntity.java` - Roles del sistema
3. `EventoEntity.java` - Eventos
4. `TipoEventoEntity.java` - Catálogo de tipos
5. `ModalidadEventoEntity.java` - Catálogo de modalidades
6. `ParticipacionEntity.java` - Inscripciones
7. `CheckInEntity.java` - Registro de asistencia
8. `CertificacionesEntity.java` - Certificados digitales
9. `AuditoriaEntity.java` - Logs de auditoría

**Nota:** Se detectaron archivos duplicados:

- `ParticipanteEntity.java` vs `UsuarioEntity.java`
- `RolEntity.java` vs `RolesEntity.java`
- `CertificacionEntity.java` vs `CertificacionesEntity.java`

**📌 ACCIÓN REQUERIDA:** Revisar y consolidar entidades duplicadas.

---

### 2.3 Capa de Repositorios (Spring Data JPA) ⚠️ **PENDIENTE**

#### ❌ Repositorios a crear en `repositories/`

```
repositories/
├── UsuarioRepository.java
├── RolRepository.java
├── EventoRepository.java
├── TipoEventoRepository.java
├── ModalidadEventoRepository.java
├── ParticipacionRepository.java
├── CheckInRepository.java
├── CertificacionRepository.java
└── AuditoriaRepository.java
```

**Ejemplo de implementación:**

```java
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, String> {
    Optional<UsuarioEntity> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByDocumentoIdentidad(String documento);
}
```

---

### 2.4 Capa de DTOs (Data Transfer Objects) ⚠️ **INCOMPLETA**

#### ✅ DTOs existentes en `dtos/`

- Algunos DTOs básicos ya creados

#### ❌ DTOs pendientes

```
dtos/
├── auth/
│   ├── LoginRequest.java
│   ├── SignupRequest.java
│   └── JwtResponse.java
├── evento/
│   ├── EventoCreateDTO.java
│   ├── EventoResponseDTO.java
│   └── EventoFilterDTO.java
├── participante/
│   ├── ParticipanteCreateDTO.java
│   └── ParticipanteResponseDTO.java
├── checkin/
│   ├── CheckInRequest.java
│   └── CheckInResponseDTO.java
└── certificado/
    ├── CertificadoResponseDTO.java
    └── VerificarCertificadoResponse.java
```

---

### 2.5 Configuración de Seguridad ⚠️ **EN PROGRESO**

#### ⚠️ Estado actual

- `SecurityConfig.java` creado pero con posibles errores
- Archivo abierto actualmente en el editor

#### ❌ Componentes de seguridad pendientes

```
config/
├── SecurityConfig.java          ⚠️ (revisar)
├── JwtUtils.java                ❌ (crear)
├── JwtAuthenticationFilter.java ❌ (crear)
├── AuthEntryPointJwt.java       ❌ (crear)
└── CorsConfig.java              ❌ (opcional)
```

**Funcionalidades requeridas:**

- ✅ Autenticación JWT
- ❌ Filtro JWT para validar tokens
- ❌ Manejo de excepciones de autenticación
- ❌ CORS configurado para Vue.js frontend

---

### 2.6 Capa de Servicios (Lógica de Negocio) ⚠️ **PENDIENTE**

#### ✅ Existente

- `UserDetailsServiceImpl.java` - Para Spring Security

#### ❌ Servicios a crear

```
services/
├── auth/
│   └── AuthService.java
├── evento/
│   ├── EventoService.java
│   └── EventoServiceImpl.java
├── participante/
│   ├── ParticipanteService.java
│   └── ParticipanteServiceImpl.java
├── checkin/
│   ├── CheckInService.java
│   └── CheckInServiceImpl.java
├── certificacion/
│   ├── CertificacionService.java
│   ├── CertificacionServiceImpl.java
│   └── PDFGeneratorService.java
├── auditoria/
│   ├── AuditoriaService.java
│   └── AuditoriaServiceImpl.java
└── importacion/
    ├── CSVImportService.java
    └── CSVImportServiceImpl.java
```

**Funcionalidades críticas por servicio:**

**EventoService:**

- CRUD completo
- Validar aforo
- Calcular duración automática
- Filtros avanzados

**ParticipanteService:**

- CRUD completo
- Inscribir a evento (generar UUID)
- Validar no duplicados
- Historial de participación

**CheckInService:**

- Check-in con QR (validar UUID)
- Check-in manual
- Prevenir duplicados
- Registrar IP y método

**CertificacionService:**

- Generar PDF automáticamente (iText/PDFBox)
- Código único verificable
- API pública de verificación
- Solo para participantes con check-in

**AuditoriaService:**

- Registro automático con AOP (Aspectos)
- Consultar logs con filtros
- Exportar a CSV

**CSVImportService:**

- Previsualizar datos
- Validar cada fila
- Reportar errores específicos
- Transacción con rollback

---

### 2.7 Capa de Controladores (API REST) ⚠️ **INCOMPLETA**

#### ✅ Existentes

- `TestController.java` - Endpoints de prueba
- `restController.java` - Controlador básico

#### ❌ Controladores a crear

```
controllers/
├── AuthController.java          ❌ (POST /api/auth/signin, /signup)
├── EventoController.java        ❌ (CRUD /api/eventos)
├── ParticipanteController.java  ❌ (CRUD /api/participantes)
├── CheckInController.java       ❌ (POST /api/checkin)
├── CertificacionController.java ❌ (GET /api/certificados & API pública)
├── AuditoriaController.java     ❌ (GET /api/auditoria - solo admin)
└── ImportacionController.java   ❌ (POST /api/importar/csv)
```

**Endpoints clave por implementar:**

**AuthController:**

- `POST /api/auth/signin` → Login (retorna JWT)
- `POST /api/auth/signup` → Registro

**EventoController:**

- `GET /api/eventos` → Listar con filtros
- `GET /api/eventos/{id}` → Obtener por ID
- `POST /api/eventos` → Crear (Admin/Operador)
- `PUT /api/eventos/{id}` → Actualizar
- `DELETE /api/eventos/{id}` → Soft delete

**ParticipanteController:**

- `POST /api/participantes/inscribir` → Inscribir a evento
- `GET /api/participantes/{id}/historial` → Historial de eventos

**CheckInController:**

- `POST /api/checkin/qr` → Check-in con QR
- `POST /api/checkin/manual` → Check-in manual (Monitor)
- `GET /api/eventos/{id}/asistencia` → Lista de asistencia

**CertificacionController:**

- `GET /api/certificados/{id}/descargar` → Descargar PDF
- `GET /api/public/verify/{codigo}` → ⚠️ **API PÚBLICA** (sin auth)

---

### 2.8 Módulo de Auditoría (AOP) ❌ **PENDIENTE**

#### Implementación con Spring AOP

```java
@Aspect
@Component
public class AuditoriaAspect {
    
    @Around("@annotation(Auditable)")
    public Object logAuditoria(ProceedingJoinPoint joinPoint) {
        // Registrar acción antes y después
        // Capturar IP, usuario, entidad, etc.
    }
}
```

**Anotación personalizada:**

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    String entidad();
    String accion();
}
```

---

### 2.9 Módulo de Generación de PDFs ❌ **PENDIENTE**

**Dependencia a agregar en `pom.xml`:**

```xml
<!-- iText para generación de PDFs -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
</dependency>
```

**Service a crear:**

```java
@Service
public class PDFGeneratorService {
    
    public byte[] generarCertificado(
        String nombreParticipante,
        String nombreEvento,
        float duracionHoras,
        String codigoVerificacion
    ) {
        // Generar PDF con iText
        // Retornar byte[] para almacenar o enviar
    }
}
```

---

### 2.10 Scripts de Base de Datos ⚠️ **PENDIENTE**

#### ❌ Crear en `src/main/resources/`

```
resources/
├── schema.sql              ❌ (opcional si usas Hibernate)
└── data.sql                ❌ (datos iniciales: roles, tipos, modalidades)
```

**Ejemplo `data.sql`:**

```sql
-- Insertar Roles
INSERT INTO Rol (ID_Rol, Nombre_Rol) VALUES 
(1, 'ADMIN'),
(2, 'OPERADOR'),
(3, 'MONITOR'),
(4, 'INVITADO');

-- Insertar Modalidades
INSERT INTO Modalidad_Evento (ID_ModalidadEvento, Nombre_ModalidadEvento) VALUES 
(1, 'Presencial'),
(2, 'Virtual'),
(3, 'Híbrido');

-- Insertar Tipos de Evento
INSERT INTO Tipo_Evento (ID_TipoEvento, Nombre_TipoEvento) VALUES 
(1, 'Capacitación'),
(2, 'Taller'),
(3, 'Conferencia'),
(4, 'Seminario'),
(5, 'Webinar'),
(6, 'Torneo');
```

---

## 🎨 FASE 3: DESARROLLO DEL FRONTEND (Vue.js)

### 3.1 Estado Actual del Frontend

#### ✅ Existente

- Proyecto Vue.js iniciado en carpeta `fronted/`
- Archivos de configuración básicos

#### ❌ Pendiente

```
fronted/src/
├── api/                          ❌ Capa de comunicación con backend
│   ├── axiosConfig.js
│   ├── authAPI.js
│   ├── eventosAPI.js
│   ├── participantesAPI.js
│   ├── checkinAPI.js
│   └── certificadosAPI.js
├── components/                   ❌ Componentes reutilizables
│   ├── common/
│   │   ├── Navbar.vue
│   │   ├── Footer.vue
│   │   └── Button.vue
│   └── form/
│       ├── InputField.vue
│       └── SelectField.vue
├── features/                     ❌ Organización por dominio
│   ├── auth/
│   │   ├── views/
│   │   │   ├── Login.vue
│   │   │   └── Register.vue
│   │   └── store/
│   │       └── authStore.js
│   ├── eventos/
│   │   ├── views/
│   │   │   ├── EventosList.vue
│   │   │   ├── EventoCreate.vue
│   │   │   └── EventoDetail.vue
│   │   ├── components/
│   │   │   └── EventoCard.vue
│   │   └── store/
│   │       └── eventosStore.js
│   ├── checkin/
│   │   └── views/
│   │       └── CheckIn.vue
│   └── certificados/
│       └── views/
│           └── VerificarCertificado.vue
├── router/                       ❌ Configuración de rutas
│   └── index.js
├── store/                        ❌ Pinia/Vuex global
│   └── index.js
└── main.js                       ✅ Existente
```

---

## 🧪 FASE 4: TESTING Y CALIDAD

### 4.1 Tests Unitarios (JUnit + Mockito) ❌ **PENDIENTE**

```
src/test/java/
└── com/example/backend/
    ├── services/
    │   ├── EventoServiceTest.java
    │   ├── CheckInServiceTest.java
    │   └── CertificacionServiceTest.java
    └── controllers/
        └── AuthControllerTest.java
```

### 4.2 Tests de Integración ❌ **PENDIENTE**

```
src/test/java/
└── integration/
    ├── EventoIntegrationTest.java
    └── CheckInIntegrationTest.java
```

---

## 📦 FASE 5: DESPLIEGUE Y DOCUMENTACIÓN

### 5.1 Containerización ⚠️ **INICIADA**

#### ✅ Existente

- `Dockerfile` creado
- `docker-compose.yml` creado

#### ❌ Pendiente

- Validar y ajustar configuraciones
- Crear scripts de despliegue

### 5.2 Documentación Final ⚠️ **INCOMPLETA**

#### ✅ Existente

- README.md básico
- Documentación técnica en `docs/`

#### ❌ Pendiente

- Manual de usuario por rol
- Documentación de API (Swagger/OpenAPI)
- Guía de despliegue en producción

---

## 📊 RESUMEN DEL ESTADO DEL PROYECTO

| Fase | Progreso | Estado |
|------|----------|--------|
| **Fase 1: Planificación** | 100% | ✅ Completada |
| **Fase 2: Backend** | 40% | ⚠️ En Progreso |
| **Fase 3: Frontend** | 5% | ❌ Iniciada |
| **Fase 4: Testing** | 0% | ❌ Pendiente |
| **Fase 5: Despliegue** | 10% | ❌ Pendiente |
| **TOTAL** | ~31% | ⚠️ En Desarrollo |

---

## 🎯 PRÓXIMOS PASOS INMEDIATOS

### Prioridad Alta (Semana 1-2)

1. **Revisar y corregir `SecurityConfig.java`** ⚠️
2. **Crear todos los Repositorios** (9 interfaces)
3. **Completar DTOs** (mínimo para Auth y Eventos)
4. **Implementar componentes de JWT** (JwtUtils, Filter, EntryPoint)
5. **Crear AuthService y AuthController** (Login/Signup)
6. **Crear EventoService y EventoController** (CRUD básico)

### Prioridad Media (Semana 3-4)

7. **Implementar CheckInService y Controller**
8. **Implementar CertificacionService con PDFGenerator**
9. **Crear API pública de verificación**
10. **Implementar AuditoriaAspect con AOP**
11. **Iniciar frontend Vue.js** (Login + Lista de Eventos)

### Prioridad Baja (Semana 5-6)

12. **Implementar CSVImportService**
13. **Crear Dashboard con métricas**
14. **Tests unitarios**
15. **Documentación con Swagger**
16. **Despliegue con Docker**

---

## 🔍 REVISIONES NECESARIAS

### 📌 Archivos Duplicados a Consolidar

1. **Participante:**
   - `ParticipanteEntity.java` vs `UsuarioEntity.java`
   - **Decisión:** Mantener uno solo según el modelo relacional

2. **Roles:**
   - `RolEntity.java` vs `RolesEntity.java`
   - **Decisión:** Mantener `RolEntity.java`

3. **Certificación:**
   - `CertificacionEntity.java` vs `CertificacionesEntity.java`
   - **Decisión:** Mantener `CertificacionEntity.java`

### 📌 SecurityConfig.java

- Revisar error actual en línea 1
- Validar configuración de JWT
- Asegurar CORS para Vue.js

---

## 📚 Recursos y Herramientas Recomendadas

### Backend

- **iText 7** para PDFs profesionales
- **Lombok** para reducir boilerplate
- **MapStruct** para mapeo automático de DTOs
- **Springdoc OpenAPI** para documentación automática

### Frontend

- **Axios** para peticiones HTTP
- **Pinia** para gestión de estado (recomendado sobre Vuex)
- **Vue Router** para navegación
- **Tailwind CSS** o **Bootstrap Vue** para UI

### Testing

- **JUnit 5** + **Mockito** para backend
- **Vitest** para frontend Vue.js

### DevOps

- **Docker** + **Docker Compose**
- **GitHub Actions** para CI/CD
- **PostgreSQL** en contenedor

---

**Fecha de creación:** 2025-12-03  
**Última actualización:** 2025-12-03  
**Versión:** 1.0  
**Estado:** Documento vivo (actualizar según avance)
