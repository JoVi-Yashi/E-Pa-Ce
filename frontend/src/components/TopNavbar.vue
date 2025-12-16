<template>
  <nav class="navbar" v-if="showNavbar">
    <div class="nav-container">
      <div class="nav-left">
        <router-link to="/" class="nav-logo">
          <h2>E-Pa-Ce</h2>
        </router-link>
      </div>

      <div class="nav-center">
        <ul class="nav-menu" :class="{ active: isMenuActive }">
          <li class="nav-item" v-for="item in navItems" :key="item.name">
            <router-link 
              :to="item.path" 
              class="nav-link"
              active-class="active"
              @click="closeMenu"
            >
              <svg class="nav-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path :d="item.icon" />
              </svg>
              <span>{{ item.name }}</span>
            </router-link>
          </li>
        </ul>
      </div>

      <div class="nav-right">
        <div class="nav-user" v-if="isAuthenticated">
          <!-- User Profile Trigger -->
          <div class="user-profile" @click="editProfile" title="Editar Perfil">
              <div class="user-avatar">
                <span>{{ userInitials }}</span>
              </div>
              <span class="user-info">{{ user?.nombre || 'Usuario' }}</span>
          </div>
          
          <button class="logout-btn" @click="logout" title="Cerrar Sesión">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <polyline points="16 17 21 12 16 7" />
              <line x1="21" y1="12" x2="9" y2="12" />
            </svg>
          </button>
        </div>
        <div class="nav-auth" v-else>
           <router-link to="/login" class="login-link">Ingresar</router-link>
        </div>

        <div class="nav-toggle" @click="toggleMenu">
          <span class="bar"></span>
          <span class="bar"></span>
          <span class="bar"></span>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();
const isMenuActive = ref(false);
const isAuthenticated = computed(() => authStore.isAuthenticated);
const user = computed(() => authStore.user);

const userInitials = computed(() => {
  if (user.value && user.value.nombre) {
    return user.value.nombre.substring(0, 2).toUpperCase();
  }
  return 'U';
});

// Navigation items
const navItems = [
  { name: 'Inicio', path: '/', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z' },
  { name: 'Eventos', path: '/eventos', icon: 'M4 19.5A2.5 2.5 0 0 1 6.5 17H20' }, 
  { name: 'Usuarios', path: '/participantes', icon: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2 M16 3.13a4 4 0 0 1 0 7.75' }, 
  { name: 'Participaciones', path: '/participaciones', icon: 'M9 11l3 3L22 4 M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11' }, 
  { name: 'Check-Ins', path: '/checkins', icon: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5' },
  { name: 'Configuración', path: '/configuracion', icon: 'M12 20a8 8 0 1 0 0-16 8 8 0 0 0 0 16zm0 0v-8m0 0l-4 4m4-4l4 4' }, // Using a generic settings icon approximation or refresh icon
  { name: 'Roles', path: '/roles', icon: 'M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z' },
];

const showNavbar = computed(() => {
  const hideOnRoutes = ['/login', '/register'];
  return !hideOnRoutes.includes(router.currentRoute.value.path);
});

const toggleMenu = () => {
  isMenuActive.value = !isMenuActive.value;
};

const closeMenu = () => {
  isMenuActive.value = false;
};

const logout = () => {
  authStore.logout();
  router.push('/login');
};

const editProfile = () => {
  router.push('/profile');
};

const handleClickOutside = (event) => {
  const navContainer = document.querySelector('.nav-container');
  if (navContainer && !navContainer.contains(event.target)) {
    closeMenu();
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
.navbar {
  background: var(--primary-gradient, linear-gradient(135deg, #667eea, #764ba2));
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 0.8rem 1.5rem;
  position: sticky;
  top: 0;
  z-index: 1000;
  border-radius: 0 0 20px 20px;
  backdrop-filter: blur(10px);
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Layout Stability: Fixed Flex Basis */
.nav-left {
  flex: 0 0 200px; /* Fixed width for logo area */
}

.nav-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-right {
  flex: 0 0 200px; /* Fixed width for user area matches left */
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.nav-logo h2 {
  color: white;
  margin: 0;
  font-size: 1.5rem;
  font-weight: 800;
  letter-spacing: 1px;
}

.nav-logo {
    text-decoration: none;
}

.nav-menu {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 0.5rem;
  background: rgba(255, 255, 255, 0.1);
  padding: 0.3rem;
  border-radius: 50px;
}

.nav-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 25px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  font-size: 0.9rem;
  white-space: nowrap;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.15);
  color: white;
}

.nav-link.active {
  background-color: white;
  color: #667eea;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  font-weight: 700;
}

.nav-icon {
  opacity: 0.8;
}

.nav-link.active .nav-icon {
  opacity: 1;
}

/* User Section */
.nav-user {
  display: flex;
  align-items: center;
  gap: 1rem;
  color: white;
}

.user-profile {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;
    padding: 0.25rem 0.5rem;
    border-radius: 50px;
    transition: background 0.2s;
}

.user-profile:hover {
    background: rgba(255,255,255,0.1);
}

.user-avatar {
  width: 35px;
  height: 35px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.user-info {
  font-weight: 600;
  max-width: 100px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.login-link {
  color: white;
  font-weight: 600;
  padding: 0.5rem 1rem;
  border: 1px solid rgba(255,255,255,0.4);
  border-radius: 20px;
  transition: 0.3s;
}

.login-link:hover {
  background: white;
  color: #667eea;
}

.logout-btn {
  background: transparent;
  padding: 0.4rem;
  border: none;
  color: rgba(255, 255, 255, 0.8);
  box-shadow: none;
  cursor: pointer;
}

.logout-btn:hover {
  color: #ff6b6b;
  background: rgba(255, 255, 255, 0.1);
  transform: none;
}

/* Mobile Toggle */
.nav-toggle {
  display: none;
  flex-direction: column;
  cursor: pointer;
  margin-left: 1rem;
}

.bar {
  width: 24px;
  height: 3px;
  background-color: white;
  margin: 3px 0;
  border-radius: 3px;
}

/* Responsive styles */
@media screen and (max-width: 1100px) {
  .nav-center {
    position: absolute; 
  }

  .nav-menu {
    position: fixed;
    left: -100%;
    top: 0;
    flex-direction: column;
    background: #4a5568; 
    height: 100vh;
    width: 250px;
    padding: 6rem 1rem 2rem;
    transition: 0.3s ease-in-out;
    box-shadow: 4px 0 15px rgba(0,0,0,0.1);
    border-radius: 0;
    align-items: flex-start;
    z-index: 999;
  }

  .nav-menu.active {
    left: 0;
  }

  .nav-toggle {
    display: flex;
    z-index: 1001;
  }
  
  .nav-item {
    width: 100%;
  }
  
  .nav-link {
    padding: 1rem;
    border-radius: 10px;
    justify-content: flex-start;
  }
  
  .nav-right {
    flex: 1; /* allow expansion on mobile */
  }
  
  .nav-user {
    display: none; 
  }
  
  .nav-auth {
    display: none;
  }
}
</style>