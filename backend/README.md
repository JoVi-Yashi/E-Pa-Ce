# Sistema E-Pa-Ce - Backend

Sistema Integral de Gestión de Eventos + Participantes + Certificación Digital

## 🚀 Tecnologías

- **Spring Boot 4.0.0**
- **Java 17**
- **PostgreSQL**
- **Spring Security + JWT**
- **Hibernate/JPA**

## 📋 Requisitos Previos

- Java 17 o superior
- PostgreSQL 12 o superior
- Maven 3.6+

## ⚙️ Configuración

### 1. Base de Datos

Crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE "EPaCe";
```

### 2. Variables de Entorno (Opcional)

Puedes configurar las siguientes variables de entorno o usar los valores por defecto:

```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=EPaCe
DB_USERNAME=postgres
DB_PASSWORD=tu_contraseña
```

### 3. Configuración JWT

El archivo `application.properties` ya incluye una clave JWT. **IMPORTANTE**: Cambiar en producción.

## 🏃 Ejecución

### 1. Compilar el proyecto

```bash
mvn clean install
```

### 2. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

O ejecutar el JAR generado:

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 3. Inicializar la Base de Datos

Una vez que la aplicación esté ejecutándose, las tablas se crearán automáticamente (DDL-auto=update).

Para insertar los roles y usuarios de prueba, ejecuta el script SQL:

```bash
psql -U postgres -d EPaCe -f src/main/resources/init-db.sql
```

## 👥 Usuarios de Prueba

Después de ejecutar el script `init-db.sql`, tendrás los siguientes usuarios:

| Email | Contraseña | Rol |
|-------|------------|-----|
| <admin@epace.com> | admin123 | ADMIN |
| <operador@epace.com> | test123 | OPERADOR |
| <monitor@epace.com> | test123 | MONITOR |
| <invitado@epace.com> | test123 | INVITADO |

## 🔐 API Endpoints

### Autenticación (Públicos)

#### Login

```http
POST /api/auth/signin
Content-Type: application/json

{
  "email": "admin@epace.com",
  "password": "admin123"
}
```

**Respuesta:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1000000001,
  "email": "admin@epace.com",
  "roles": ["ROLE_ADMIN"]
}
```

#### Registro

```http
POST /api/auth/signup
Content-Type: application/json

{
  "documentoIdentidad": 1234567890,
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "rolId": 4
}
```

### Endpoints de Prueba

#### Público

```http
GET /api/test/all
```

#### Requiere autenticación

```http
GET /api/test/participant
Authorization: Bearer {token}
```

```http
GET /api/test/monitor
Authorization: Bearer {token}
```

```http
GET /api/test/operator
Authorization: Bearer {token}
```

```http
GET /api/test/admin
Authorization: Bearer {token}
```

## 🗄️ Estructura de la Base de Datos

### Tablas Principales

1. **Rol** - Roles del sistema (Admin, Operador, Monitor, Invitado)
2. **Participantes** - Usuarios del sistema
3. **Eventos** - Eventos programados
4. **Tipo_Evento** - Tipos de eventos (Conferencia, Taller, etc.)
5. **Modalidad_Evento** - Modalidades (Presencial, Virtual, Híbrido)
6. **Participacion** - Relación participante-evento
7. **CheckIn** - Registro de asistencia
8. **Certificacion** - Certificados digitales
9. **Auditoria** - Registro de auditoría

## 📝 Roles y Permisos

### ADMIN

- Acceso completo al sistema
- Gestión de usuarios
- Gestión de eventos
- Configuración del sistema

### OPERADOR

- Gestión de eventos
- Gestión de participaciones
- Emisión de certificados

### MONITOR

- Registro de check-in
- Consulta de participantes
- Visualización de eventos

### INVITADO

- Inscripción a eventos
- Consulta de certificados propios

## 🔧 Configuración Avanzada

### Cambiar el tiempo de expiración del JWT

En `application.properties`:

```properties
# Tiempo en milisegundos (86400000 = 24 horas)
app.jwtExpirationMs=86400000
```

### Cambiar a modo producción

1. Cambiar el `ddl-auto` a `validate`:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

2. Desactivar logs SQL:

```properties
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=WARN
```

3. Generar nueva clave JWT:

```bash
openssl rand -base64 64
```

## 📚 Documentación Adicional

- [Spring Security](https://spring.io/projects/spring-security)
- [JWT](https://jwt.io/)
- [Hibernate](https://hibernate.org/)

## 🐛 Troubleshooting

### Error de conexión a la base de datos

Verificar que PostgreSQL esté ejecutándose y las credenciales sean correctas.

### Error de token JWT

Verificar que el token se envíe en el header `Authorization: Bearer {token}`.

### Error de roles

Asegurarse de que los roles estén en mayúsculas en la base de datos.
