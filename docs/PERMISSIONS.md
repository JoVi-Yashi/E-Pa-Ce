# Sistema de Permisos Dinámico - E-Pa-Ce

## 🎯 Cómo Funciona el Sistema de Permisos

El sistema está completamente implementado y funciona de la siguiente manera:

### 1. **Definición de Permisos (Backend)**

Los permisos se definen en el módulo de **Roles**:

- Cada rol tiene un conjunto de permisos específicos
- Los permisos siguen el formato: `MÓDULO:ACCIÓN`
- Ejemplos: `EVENTO:READ`, `PARTICIPANTE:CREATE`, `DASHBOARD:READ`

### 2. **Carga de Permisos (Login)**

Cuando el usuario inicia sesión:

```javascript
// En auth.js - action login()
this.rolePermissions = response.data.rolePermissions || {};
// Ejemplo de rolePermissions:
{
  "ADMIN": ["ALL:ALL", "EVENTO:READ", "PARTICIPANTE:CREATE", ...],
  "OPERADOR": ["DASHBOARD:READ", "EVENTO:READ", ...]
}
```

### 3. **Verificación de Permisos (Frontend)**

El authStore provee dos métodos principales:

#### `hasPermission(permission)` - Para verificar permisos específicos

```javascript
// En cualquier vista:
authStore.hasPermission('EVENTO:READ')  // true o false
authStore.hasPermission('DASHBOARD:READ')
authStore.hasPermission('ALL:ALL')  // Super admin
```

#### `can(action, module, ownerId)` - Para verificaciones complejas

```javascript
// Verifica si puede hacer una acción sobre un módulo
authStore.can('UPDATE', 'PARTICIPANTE', user.documentoIdentidad)
authStore.can('DELETE', 'EVENTO', evento.id)
```

## 📋 Uso en las Vistas

### **Navbar - Ocultar Menús**

```vue
<!-- TopNavbar.vue - YA IMPLEMENTADO -->
<script>
const navItems = computed(() => {
  const items = [
    { name: 'Dashboard', path: '/dashboard', permission: 'DASHBOARD:READ' },
    { name: 'Eventos', path: '/eventos', permission: 'EVENTO:READ' },
    { name: 'Usuarios', path: '/participantes', permission: 'PARTICIPANTE:READ' },
    // ...
  ];
  
  // Filtra los items según permisos
  return items.filter(item => 
    !item.permission || authStore.hasPermission(item.permission)
  );
});
</script>
```

### **Botones de Acción - Ocultar según Permisos**

```vue
<!-- ParticipantesView.vue - EJEMPLO -->
<button 
  v-if="authStore.hasPermission('PARTICIPANTE:CREATE')" 
  @click="openCreateModal"
>
  Nuevo Usuario
</button>

<button 
  v-if="authStore.can('UPDATE', 'PARTICIPANTE', user.documentoIdentidad)" 
  @click="editUser(user)"
>
  Editar
</button>
```

### **Rutas Protegidas - Router**

```javascript
// router/index.js
{
  path: '/roles',
  component: RolesView,
  meta: { 
    requiresAuth: true,
    permission: 'ROL:VIEW'  // Requiere este permiso
  }
}
```

## 🔄 Flujo de Actualización de Permisos

### Escenario: Admin cambia permisos de un rol

1. **Admin edita permisos en RolesView.vue**
   - Se actualiza el rol en la BD vía `PUT /api/roles/{id}`
   - Los nuevos permisos se guardan

2. **Usuario afectado necesita refrescar sesión**
   - Los permisos se cargan al hacer LOGIN
   - Para que los cambios surtan efecto, el usuario debe:
     - **Opción A**: Cerrar sesión y volver a entrar
     - **Opción B**: Cambiar de rol (si tiene múltiples roles)
     - **Opción C**: Implementar refresh automático (ver más abajo)

3. **Los cambios se reflejan inmediatamente**
   - El navbar oculta/muestra menús según permisos
   - Los botones se habilitan/deshabilitan
   - Las rutas bloquean acceso no autorizado

## ✅ Estado Actual de Implementación

### **YA IMPLEMENTADO ✅**

- ✅ TopNavbar filtra menús según permisos
- ✅ Vistas principales usan `v-if` con permisos:
  - ParticipantesView (CREATE, UPDATE, DELETE)
  - EventosView (READ, CREATE, UPDATE, DELETE)
  - RolesView (VIEW, MANAGE)
  - CertificacionesView (VIEW, EMITIR, DELETE)
  - CheckInsView (MANUAL, QR)
  - AuditoriaView (VIEW)
- ✅ authStore carga y verifica permisos
- ✅ Backend envía rolePermissions en login
- ✅ LocalStorage persiste permisos

### **OPCIONALES (Mejoras Futuras) 🔮**

- ⏳ Auto-refresh de permisos sin re-login
- ⏳ WebSocket para notificar cambios en tiempo real
- ⏳ Middleware de router para verificar permisos antes de cargar vistas

## 🛡️ Ejemplo Completo de Protección

```vue
<!-- EventosView.vue -->
<template>
  <div>
    <!-- Solo visible si tiene permiso READ -->
    <PageHeader v-if="authStore.hasPermission('EVENTO:READ')" title="Eventos">
      <template #actions>
        <!-- Solo visible si puede CREAR -->
        <button 
          v-if="authStore.hasPermission('EVENTO:CREATE')" 
          @click="openCreateModal"
        >
          Nuevo Evento
        </button>
      </template>
    </PageHeader>

    <!-- Tabla de eventos -->
    <table v-if="authStore.hasPermission('EVENTO:READ')">
      <tr v-for="evento in eventos">
        <td>{{ evento.nombre }}</td>
        <td>
          <!-- Solo puede editar SI tiene UPDATE_ALL O (UPDATE_OWN y es el creador) -->
          <button 
            v-if="authStore.can('UPDATE', 'EVENTO', evento.creadorId)"
            @click="editEvento(evento)"
          >
            Editar
          </button>
          
          <!-- Solo puede eliminar SI tiene DELETE_ALL O (DELETE_OWN y es el creador) -->
          <button 
            v-if="authStore.can('DELETE', 'EVENTO', evento.creadorId)"
            @click="deleteEvento(evento)"
          >
            Eliminar
          </button>
        </td>
      </tr>
    </table>

    <!-- Mensaje si no tiene permisos -->
    <div v-else class="no-permission">
      ❌ No tienes permisos para ver eventos
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
const authStore = useAuthStore();
</script>
```

## 🔧 Verificación Manual

Para verificar que los permisos funcionan:

1. **Inicia sesión como ADMIN**
   - Deberías ver TODOS los menús y opciones

2. **Ve a Roles y edita el rol OPERADOR**
   - Quita el permiso `EVENTO:CREATE`
   - Guarda cambios

3. **Inicia sesión como OPERADOR**
   - Deberías ver el menú "Eventos"
   - Pero NO deberías ver el botón "Nuevo Evento"

4. **Quita el permiso `EVENTO:READ` del OPERADOR**
   - Cierra sesión y vuelve a entrar como OPERADOR
   - El menú "Eventos" NO debería aparecer
   - Si intentas acceder manualmente a `/eventos`, deberías ver acceso denegado

## 📝 Matriz de Permisos por Módulo

| Módulo | Permisos Disponibles |
|--------|---------------------|
| DASHBOARD | `READ` |
| EVENTO | `READ`, `CREATE`, `UPDATE_OWN`, `UPDATE_ALL`, `DELETE_OWN`, `DELETE_ALL` |
| PARTICIPANTE | `READ`, `CREATE`, `UPDATE_OWN`, `UPDATE_ALL`, `DELETE_OWN`, `DELETE_ALL` |
| PARTICIPACION | `READ`, `CRUD_OWN`, `CRUD_ALL` |
| CERTIFICADO | `VIEW`, `EMITIR_OWN`, `EMITIR_ALL`, `DELETE_OWN`, `DELETE_ALL` |
| CHECKIN | `QR`, `MANUAL` |
| ROL | `VIEW`, `MANAGE` |
| AUDITORIA | `VIEW` |
| CONFIGURACION | `VIEW`, `MANAGE` |
| REPORTE | `VIEW` |
| ALL | `ALL` (Super Admin) |

## ⚡ Conclusión

**El sistema de permisos dinámico YA ESTÁ FUNCIONANDO**. Los permisos se definen en el módulo de Roles y controlan completamente qué puede ver y hacer cada usuario. Para que los cambios surtan efecto, el usuario debe **cerrar sesión y volver a entrar** (o cambiar de rol si tiene múltiples roles asignados).

---
*Documentación actualizada: 2025-12-18*
