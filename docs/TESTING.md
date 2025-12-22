# Guía de Pruebas - Sistema de Permisos Dinámico

## ✅ Cómo Probar que los Permisos Funcionan

### Paso 1: Verificar Configuración Inicial

1. **Inicia el backend y frontend**

   ```bash
   # Backend (en una terminal)
   cd backend
   ./mvnw spring-boot:run
   
   # Frontend (en otra terminal)
   cd frontend
   npm run dev
   ```

2. **Accede a la aplicación**
   - URL: <http://localhost:5173>

### Paso 2: Prueba como ADMIN

1. **Inicia sesión como ADMIN**
   - Email: <admin@epace.com> (o el que hayas configurado)
   - Password: (tu contraseña de admin)

2. **Verifica que puedas ver TODO**
   - ✅ Menú completo con todas las opciones
   - ✅ Dashboard, Eventos, Usuarios, Participaciones, Certificados, Check-In, Roles, Auditoría, Atributos

3. **Ve al módulo de Roles** (`/roles`)
   - Deberías ver todos los roles: ADMIN, OPERADOR, MONITOR, INVITADO

### Paso 3: Modificar Permisos de OPERADOR

1. **Edita el rol OPERADOR**
   - Busca el rol "OPERADOR" en la lista
   - Haz clic en el icono de editar (lápiz)

2. **Gestiona los permisos**
   - Verás todos los módulos con switches para activar/desactivar acciones
   - **PRUEBA 1**: Desactiva `EVENTO:CREATE`
   - **PRUEBA 2**: Desactiva `EVENTO:READ` completo
   - **PRUEBA 3**: Desactiva `DASHBOARD:READ`
   - Guarda los cambios

### Paso 4: Prueba como OPERADOR

1. **Cierra sesión** (Click en tu avatar → Cerrar Sesión)

2. **Inicia sesión como OPERADOR**
   - Email: <operador@epace.com> (o crea un usuario con rol OPERADOR)
   - Password: (contraseña)

3. **Verifica los cambios según las pruebas:**

#### PRUEBA 1: Sin `EVENTO:CREATE`

- ✅ El menú "Eventos" SÍ aparece (tiene READ)
- ✅ Puede ver la lista de eventos
- ❌ El botón "Nuevo Evento" NO aparece
- ❌ No puede crear eventos

#### PRUEBA 2: Sin `EVENTO:READ`

- ❌ El menú "Eventos" NO aparece
- ❌ Si intenta acceder a `/eventos` manualmente:
  - Es redirigido al home
  - Aparece notificación: "❌ No tienes permisos para acceder a esta sección"

#### PRUEBA 3: Sin `DASHBOARD:READ`

- ❌ El menú "Dashboard" NO aparece
- ❌ Si intenta acceder a `/dashboard` manualmente:
  - Es redirigido al home
  - Aparece notificación de error

### Paso 5: Verificar Protección de Rutas

1. **Intenta acceder manualmente a rutas protegidas**
   - Escribe en el navegador: `http://localhost:5173/eventos`
   - Si NO tienes `EVENTO:READ`:
     - ✅ Eres redirigido a `/`
     - ✅ Aparece notificación de error

2. **Intenta acceder sin autenticación**
   - Cierra sesión
   - Escribe: `http://localhost:5173/dashboard`
   - ✅ Eres redirigido a `/login`
   - ✅ Aparece: "Debes iniciar sesión para acceder a esta página"

### Paso 6: Verificar Actualización Dinámica

**Escenario**: Admin cambia permisos mientras OPERADOR está logueado

1. **En una ventana**: Usuario OPERADOR logueado
2. **En otra ventana (navegador incógnito)**: ADMIN logueado

**Por parte del ADMIN:**

1. Edita el rol OPERADOR
2. Quita el permiso `PARTICIPANTE:READ`
3. Guarda cambios

**Por parte del OPERADOR (ventana original):**

1. Los cambios NO se reflejan inmediatamente (es correcto)
2. **Para ver los cambios, debe**:
   - **Opción A**: Cerrar sesión y volver a entrar ✅
   - **Opción B**: Cambiar de rol (si tiene múltiples roles) ✅
   - **Opción C**: Refrescar la página (F5) - NO funcionará, necesita re-login

3. Después de re-login:
   - ✅ El menú "Usuarios" ya NO aparece
   - ✅ No puede acceder a `/participantes`

## 📋 Checklist de Verificación

### Navbar Dinámico

- [ ] El navbar muestra solo los módulos con permisos READ
- [ ] Si quitas `EVENTO:READ`, el menú "Eventos" desaparece
- [ ] Si quitas `DASHBOARD:READ`, el menú "Dashboard" desaparece

### Botones de Acción

- [ ] Botón "Nuevo Evento" solo aparece con `EVENTO:CREATE`
- [ ] Botón "Editar" solo aparece con permisos UPDATE
- [ ] Botón "Eliminar" solo aparece con permisos DELETE

### Protección de Rutas

- [ ] Acceso a `/eventos` bloqueado sin `EVENTO:READ`
- [ ] Acceso a `/roles` bloqueado sin `ROL:VIEW`
- [ ] Acceso a `/auditoria` bloqueado sin `AUDITORIA:VIEW`
- [ ] Redirección a `/` con notificación de error

### Actualización de Permisos

- [ ] Cambios en permisos NO se reflejan hasta re-login
- [ ] Después de re-login, los cambios SÍ se reflejan
- [ ] LocalStorage actualizado con nuevos permisos

## 🐛 Problemas Conocidos

### "Los cambios no se ven sin cerrar sesión"

✅ **Esto es correcto**. Los permisos se cargan al hacer login y se guardan en:

- Estado de Pinia (`authStore.rolePermissions`)
- LocalStorage (`rolePermissions`)

Para aplicar cambios instantáneos (mejora futura):

- Implementar WebSocket para notificar cambios
- Agregar botón "Refrescar permisos" que llame al backend
- Auto-refresh cada X minutos

## 📊 Matriz de Permisos de Ejemplo

### ADMIN (Super Usuario)

```
ALL:ALL (acceso total a todo)
```

### OPERADOR (Usuario Avanzado)

```
DASHBOARD:READ
EVENTO:READ, EVENTO:CREATE, EVENTO:UPDATE_OWN, EVENTO:DELETE_OWN
PARTICIPANTE:READ, PARTICIPANTE:CREATE
PARTICIPACION:READ, PARTICIPACION:CRUD_OWN
CERTIFICADO:VIEW, CERTIFICADO:EMITIR_OWN
CHECKIN:QR, CHECKIN:MANUAL
CONFIGURACION:VIEW
```

### MONITOR (Usuario Básico)

```
DASHBOARD:READ
EVENTO:READ
PARTICIPANTE:READ
PARTICIPACION:READ
CHECKIN:QR
```

### INVITADO (Solo Lectura)

```
DASHBOARD:READ
EVENTO:READ
```

## 🎯 Resultado Esperado

Después de estas pruebas, debes confirmar:

1. ✅ Los permisos definidos en Roles controlan completamente la UI
2. ✅ Si quitas un permiso, el usuario NO ve esa opción
3. ✅ Las rutas están protegidas (no se puede acceder manualmente)
4. ✅ Los cambios requieren re-login para aplicarse
5. ✅ El sistema es dinámico y seguro

---

## 🚀 Prueba Rápida de 5 Minutos

1. Login como ADMIN
2. Ve a Roles → Edita OPERADOR → Quita `EVENTO:CREATE` → Guarda
3. Logout
4. Login como OPERADOR
5. Ve a Eventos
6. ✅ Verificar: NO hay botón "Nuevo Evento"

**Si esto funciona, TODO el sistema de permisos está funcionando correctamente! 🎉**

---
*Guía de pruebas actualizada: 2025-12-18*
