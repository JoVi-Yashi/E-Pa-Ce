# Manual Técnico y Guía del Código: E-Pa-Ce Backend

Este documento proporciona una explicación exhaustiva del código fuente del backend de E-Pa-Ce. Está diseñado para que cualquier desarrollador pueda entender la arquitectura, el flujo de datos y cómo extender el sistema.

## 1. Arquitectura del Sistema

El proyecto sigue los principios de **Clean Architecture** (Arquitectura Limpia) y **Hexagonal Architecture** simplificada. El objetivo es separar las responsabilidades en capas bien definidas para facilitar el mantenimiento y las pruebas.

### Las Capas del Sistema

1. **Capa de Controladores (`internal/controllers`)**:

    - **Responsabilidad**: Es la puerta de entrada HTTP. Recibe las peticiones (JSON, parámetros URL), las valida superficialmente y llama al servicio correspondiente.
    - **No contiene**: Lógica de negocio compleja ni consultas SQL directas.
    - **Salida**: Retorna respuestas HTTP estandarizadas (JSON).

2. **Capa de Servicios (`internal/services`)**:

    - **Responsabilidad**: Contiene la **lógica de negocio**. Aquí es donde ocurren las validaciones de reglas (ej: "no se puede inscribir si el aforo está lleno", "la fecha de fin debe ser posterior a la de inicio").
    - **Orquestación**: Coordina múltiples repositorios o utilidades (ej: guardar en BD y luego generar un PDF).
    - **Auditoría**: Se encarga de registrar las acciones importantes en el log de auditoría.

3. **Capa de Repositorios (`internal/repositories`)**:

    - **Responsabilidad**: Abstrae el acceso a datos. Es la única capa que interactúa directamente con la base de datos (GORM).
    - **Operaciones**: CRUD (Create, Read, Update, Delete) y consultas complejas.

4. **Capa de Modelos (`internal/models`)**:

    - **Responsabilidad**: Define la estructura de los datos (tablas de base de datos) y sus relaciones.

5. **Capa de DTOs (`internal/dto`)**:
    - **Responsabilidad**: (Data Transfer Objects) Define las estructuras de datos que se usan para la comunicación entre el cliente (Frontend) y el servidor. Separa el modelo de base de datos de lo que se expone públicamente.

---

## 2. Estructura de Directorios Explicada

```text
backend/
├── cmd/
│   └── api/
│       └── main.go        # PUNTO DE ENTRADA. Aquí arranca todo.
├── internal/              # Código privado de la aplicación (no importable por otros proyectos)
│   ├── config/            # Carga de variables de entorno y conexión a BD
│   ├── constants/         # Textos fijos, mensajes de error y constantes del sistema
│   ├── controllers/       # Manejadores de rutas HTTP (Gin)
│   ├── dto/               # Estructuras de entrada/salida de la API
│   ├── middleware/        # Interceptores de peticiones (Auth, CORS, Logs)
│   ├── models/            # Definiciones de tablas (Structs de Go con tags GORM)
│   ├── repositories/      # Consultas SQL y acceso a datos
│   ├── services/          # Lógica de negocio
│   └── utils/             # Herramientas (Generador PDF, JWT, Validadores)
├── storage/               # Carpeta para guardar archivos generados (PDFs, QRs)
├── go.mod                 # Definición de dependencias del proyecto
└── README.md              # Documentación general
```

---

## 3. Flujo de una Petición (Ejemplo: Crear Evento)

Para entender el código, sigamos el viaje de una petición para crear un evento:

1. **Router (`main.go`)**:

    - Recibe `POST /api/events`.
    - Verifica el token JWT (`middleware.AuthMiddleware`).
    - Pasa el control a `eventController.CreateEvent`.

2. **Controlador (`event_controller.go`)**:

    - Recibe el JSON del body.
    - Lo convierte a un struct `dto.CreateEventRequest`.
    - Valida formatos básicos (ej: que el nombre no esté vacío).
    - Llama a `eventService.CreateEvent(req)`.

3. **Servicio (`event_service.go`)**:

    - Recibe el DTO.
    - Valida reglas de negocio: `validateEventDates` (Fecha inicio < Fecha fin).
    - Convierte el DTO a un modelo `models.Event`.
    - Llama a `eventRepo.Create(event)`.
    - Si todo sale bien, llama a `auditRepo.Create` para dejar registro.

4. **Repositorio (`event_repository.go`)**:

    - Recibe el modelo `models.Event`.
    - Ejecuta `db.Create(event)` usando GORM para insertar en PostgreSQL.
    - Retorna error o éxito.

5. **Respuesta**:
    - El error o éxito burbujea hacia arriba hasta el Controlador.
    - El Controlador usa `utils.CreatedResponse` para enviar un JSON 201 al cliente.

---

## 4. Componentes Clave y Tecnologías

### Inyección de Dependencias (Dependency Injection)

Verás mucho este patrón en `main.go`. En lugar de que un controlador cree su propio servicio, se lo "inyectamos" al crearlo.

```go
// En main.go
eventRepo := repositories.NewEventRepository(db)           // Crear Repo
eventService := services.NewEventService(eventRepo, ...)   // Inyectar Repo al Servicio
eventController := controllers.NewEventController(eventService) // Inyectar Servicio al Controller
```

**¿Por qué?** Facilita el testing y hace el código más modular.

### GORM (ORM)

Usamos GORM para no escribir SQL a mano.

- `AutoMigrate`: Crea las tablas automáticamente basadas en los structs de `models`.
- `Preload`: Se usa para cargar relaciones (ej: traer el Evento cuando consulto una Inscripción).

### Gin Web Framework

Es el framework HTTP.

- `gin.Context`: Objeto que contiene toda la info de la petición (headers, body, params).
- `ShouldBindJSON`: Parsea el JSON del body al struct de Go.

### Autenticación JWT

- **Login**: Genera un token firmado con una clave secreta (`utils/jwt.go`).
- **Middleware**: Intercepta cada petición, lee el header `Authorization`, valida el token y extrae el ID del usuario. Si falla, corta la petición con 401 Unauthorized.

### Generación de PDFs

Usamos `gofpdf`. El código en `utils/pdf_generator.go` dibuja el certificado "a mano" (coordenadas X, Y) para tener control total del diseño.

---

## 5. Guía para Extender el Código

### ¿Cómo agregar una nueva funcionalidad?

Supongamos que quieres agregar "Comentarios en Eventos".

1. **Modelo**: Crea `internal/models/comment.go`. Define el struct y las relaciones.
2. **Repositorio**: Crea `internal/repositories/comment_repository.go`. Agrega métodos `Create`, `FindByEvent`.
3. **DTO**: Crea `internal/dto/comment_dto.go`. Define qué datos recibes (Request) y qué envías (Response).
4. **Servicio**: Crea `internal/services/comment_service.go`. Agrega la lógica (ej: validar que el usuario asistió al evento antes de comentar).
5. **Controlador**: Crea `internal/controllers/comment_controller.go`. Maneja las rutas HTTP.
6. **Main**: En `main.go`, inicializa el repo, servicio y controlador, y registra las rutas (`POST /events/:id/comments`).

---

## 6. Puntos de Atención (Troubleshooting)

- **Ciclos de Importación**: Go prohíbe que el paquete A importe B y B importe A. Por eso usamos una estructura jerárquica (Controller -> Service -> Repo).
- **Database Locked**: Si usas SQLite (aunque aquí usamos Postgres), podrías ver esto. En Postgres, asegúrate de cerrar transacciones si las usas manualmente.
- **CORS**: Si el frontend falla con error de CORS, revisa `internal/config/config.go` y asegúrate de que `CORS_ALLOWED_ORIGINS` incluya la URL de tu frontend.

---

## 7. Glosario de Archivos Importantes

- `internal/config/database.go`: Configuración crítica de la conexión a BD.
- `internal/utils/response.go`: Funciones helper para responder JSON siempre con el mismo formato (`success`, `message`, `data`).
- `internal/middleware/error_handler.go`: Captura "Panics" (crashes) para que el servidor no se detenga y loguea el error.
