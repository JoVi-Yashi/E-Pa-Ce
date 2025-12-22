# E-Pa-Ce (Eventos - Participantes - Certificaciones)

Plataforma integral para la gestión de eventos, control de asistencia mediante códigos QR y emisión automatizada de certificaciones digitales.

## 🚀 Cómo iniciar el proyecto

### 1. Requisitos previos

- **Java 17 o superior** (Desarrollado con JDK 24)
- **Node.js 20+** y npm
- **PostgreSQL 15+**
- **Docker** (Opcional, para ejecución con contenedores)

### 2. Configuración de Base de Datos

1. Crea una base de datos llamada `EPaCe`.
2. Las credenciales por defecto se encuentran en `backend/src/main/resources/application.properties`.
3. El sistema usa **Flyway** para migraciones automáticas del esquema.

### 3. Ejecución Local (Desarrollo)

#### Backend

```bash
cd backend
./mvnw spring-boot:run
```

El servidor iniciará en `http://localhost:8081`.

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`.

### 4. Ejecución con Docker

Si tienes Docker Desktop instalado:

```bash
docker-compose up --build
```

## 🔐 Usuarios de Prueba

| Usuario | Email | Contraseña |
| :--- | :--- | :--- |
| **Administrador** | `admin@epace.com` | `admin123` |
| **Operador** | `operador@epace.com` | `test123` |
| **Monitor** | `monitor@epace.com` | `test123` |

## 📁 Estructura del Proyecto

- `backend/`: API REST construida con Spring Boot 3 y Spring Security (JWT).
- `frontend/`: Aplicación SPA con Vue 3, Vite y Pinia.
- `docs/`: Documentación detallada del sistema (API, Permisos, Guía de Pruebas).

## 🛠️ Variables de Entorno importantes

### Backend (`.env` o variables de sistema)

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `MAIL_USERNAME`, `MAIL_PASSWORD` (Para recuperación de contraseña vía Gmail)

### Frontend

- `VITE_API_URL`: URL base de la API (por defecto `http://localhost:8081/api`)

---
Desarrollado por **Johao - SENA Fabrica (2025)**
