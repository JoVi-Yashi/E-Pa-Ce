<template>
  <nav class="navbar" v-if="showNavbar">
    <div class="nav-container">
      <div class="nav-left">
        <router-link to="/" class="nav-logo">
          <img :src="logoUrl" alt="E-Pa-Ce Logo" />
        </router-link>
      </div>

        <div 
          class="nav-center" 
          :class="{ active: isMenuActive, 'can-scroll': canScrollRight }" 
          ref="navCenterRef"
          @click.self="closeMenu"
          @wheel.prevent="handleWheel"
          @mousedown="startDrag"
          @mouseleave="stopDrag"
          @mouseup="stopDrag"
          @mousemove="doDrag"
          @scroll="checkScroll"
        >
          <ul class="nav-menu">
            <li class="nav-item" v-for="item in navItems" :key="item.name">
              <router-link 
                :to="item.path" 
                class="nav-link"
                active-class="active"
                @click="closeMenu"
                @dragstart.prevent
              >
                <svg class="nav-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" v-html="item.icon">
                </svg>
                <span>{{ item.name }}</span>
              </router-link>
            </li>
          </ul>
          
          <!-- Scroll Indicator -->
          <div v-if="canScrollRight" class="scroll-indicator-hint" @click="scrollRight">
             <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"></path><path d="m12 5 7 7-7 7"></path></svg>
          </div>
        </div>

      <div class="nav-right" style="overflow: visible;">
        <div class="nav-user" v-if="isAuthenticated" ref="userMenuRef" style="overflow: visible;">
           <!-- User Profile Trigger with Dropdown -->
           <div class="user-profile" @click.stop="toggleUserMenu" :class="{ active: showUserMenu }">
               <div class="user-avatar">
                 <img v-if="user?.fotoPerfil" :src="user.fotoPerfil" class="nav-avatar-img" alt="Avatar" />
                 <span v-else>{{ userInitials }}</span>
               </div>
               <span class="user-info">{{ user?.nombre || 'Usuario' }}</span>
               <svg class="chevron" :class="{ rotate: showUserMenu }" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9l6 6 6-6"/></svg>
           </div>
           
           <!-- Dropdown Menu -->
           <div class="user-dropdown" v-if="showUserMenu">
              <div class="dropdown-header">
                 <strong>{{ user?.nombre }} {{ user?.apellido }}</strong>
                 <span class="role-label">{{ displayRole }}</span>
              </div>
              <div class="dropdown-connector"></div>
              <ul class="dropdown-list">
                 <li @click="editProfile">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                    Editar Perfil
                 </li>
                 <!-- Show 'Cambiar Rol' only if user has distinct roles functionality -->
                 <li @click="openRoleSwitcher">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle><polyline points="17 11 19 13 23 9"></polyline></svg>
                    Cambiar Rol
                 </li>
                 <li class="divider"></li>
                 <li @click="logout" class="danger">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
                    Cerrar Sesión
                 </li>
              </ul>
           </div>
        </div>
        <div class="nav-auth" v-else>
           <router-link to="/login" class="login-link">
             <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
               <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"></path>
               <polyline points="10 17 15 12 10 7"></polyline>
               <line x1="15" y1="12" x2="3" y2="12"></line>
             </svg>
             <span>Ingresar</span>
           </router-link>
        </div>

        <div class="nav-toggle" :class="{ active: isMenuActive }" @click="toggleMenu">
          <span class="bar"></span>
          <span class="bar"></span>
          <span class="bar"></span>
        </div>
      </div>
    </div>
  </nav>

  <!-- Role Switcher Modal -->
  <BaseModal
      :show="showRoleModal"
      title="Cambiar Rol Activo"
      maxWidth="500px"
      @close="showRoleModal = false"
  >
      <div class="role-modal-content">
        <p class="role-instruction">Selecciona el rol con el que deseas trabajar en esta sesión:</p>
        <div class="role-options-grid">
          <div 
            v-for="role in getUserRoles" 
            :key="role.raw"
            class="role-card-option"
            :class="{ active: role.raw === authStore.rawActiveRole }"
            @click="selectRole(role.raw)"
          >
            <div class="role-card-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
              </svg>
            </div>
            <div class="role-card-details">
              <span class="role-card-name">{{ role.display }}</span>
              <span class="role-card-status">{{ role.raw === authStore.rawActiveRole ? 'Sesión Activa' : 'Disponible' }}</span>
            </div>
            <div class="role-card-check" v-if="role.raw === authStore.rawActiveRole">
              <div class="check-circle">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
          <button class="btn-secondary" @click="showRoleModal = false">Cancelar</button>
      </template>
  </BaseModal>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useAuthStore } from '../../stores/auth';
import { useRouter } from 'vue-router';
import logoUrl from '../../assets/image/EPaCe_logo.png';
import { useNotificationStore } from '../../stores/notifications';
import { useConfirmStore } from '../../stores/confirm';
import BaseModal from '../modals/BaseModal.vue';

const notificationStore = useNotificationStore();
const confirmStore = useConfirmStore();

const authStore = useAuthStore();
const router = useRouter();
const isMenuActive = ref(false);
const showUserMenu = ref(false);
const userMenuRef = ref(null);
const isAuthenticated = computed(() => authStore.isAuthenticated);
const user = computed(() => authStore.user);

const navCenterRef = ref(null);
const canScrollRight = ref(false);
let isDragging = false;
let startX;
let scrollLeft;

const checkScroll = () => {
    if (!navCenterRef.value) return;
    const { scrollLeft, scrollWidth, clientWidth } = navCenterRef.value;
    // Show indicator if there is more than 20px left to scroll
    canScrollRight.value = scrollWidth > clientWidth && (scrollLeft + clientWidth) < (scrollWidth - 20);
};

const scrollRight = () => {
    if (navCenterRef.value) {
        navCenterRef.value.scrollBy({ left: 150, behavior: 'smooth' });
    }
};

const userInitials = computed(() => {
  if (user.value && user.value.nombre) {
    const n = user.value.nombre.charAt(0);
    const a = user.value.apellido ? user.value.apellido.charAt(0) : '';
    return (n + a).toUpperCase();
  }
  return 'U';
});

// Scroll Logic
const handleWheel = (e) => {
  if (window.innerWidth <= 1100) return; // Don't interfere on mobile layout
  if (navCenterRef.value) {
     // Convert vertical scroll to horizontal scroll
     navCenterRef.value.scrollLeft += e.deltaY;
  }
};

const startDrag = (e) => {
  if (window.innerWidth <= 1100) return;
  // Don't start drag if clicking Scrollbar (though scrollbar is hidden in CSS, good practice)
  isDragging = true;
  if(navCenterRef.value) {
    navCenterRef.value.classList.add('grabbing');
    startX = e.pageX - navCenterRef.value.offsetLeft;
    scrollLeft = navCenterRef.value.scrollLeft;
  }
};

const stopDrag = () => {
  isDragging = false;
  if(navCenterRef.value) navCenterRef.value.classList.remove('grabbing');
};

const doDrag = (e) => {
  if (!isDragging) return;
  e.preventDefault();
  const x = e.pageX - navCenterRef.value.offsetLeft;
  const walk = (x - startX) * 2; // Scroll-fast multiplier
  navCenterRef.value.scrollLeft = scrollLeft - walk;
};

// Navigation items
// Navigation items with permissions
const navItems = computed(() => {
  const items = [
    { name: 'Inicio', path: '/', icon: '<path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V9z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline>' },
    { name: 'Dashboard', path: '/dashboard', icon: '<rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect>', permission: 'DASHBOARD:READ' },
    { name: 'Eventos', path: '/eventos', icon: '<rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line>', permission: 'EVENTO:READ' },
    { name: 'Directorio', path: '/participantes', icon: '<path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle><line x1="20" y1="8" x2="20" y2="14"></line><line x1="23" y1="11" x2="17" y2="11"></line>', permission: 'PARTICIPANTE:READ' },
    { name: 'Participaciones', path: '/participaciones', icon: '<polyline points="9 11 12 14 22 4"></polyline><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"></path>', permission: 'PARTICIPACION:READ' }, 
    { name: 'Certificados', path: '/certificaciones', icon: '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline>', permission: 'CERTIFICADO:READ' },
    { name: 'Asistencia', path: '/checkins', icon: '<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline>', permission: 'CHECKIN:READ' },
    { name: 'Roles', path: '/roles', icon: '<rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path>', permission: 'ROL:READ' },
    { name: 'Auditoría', path: '/auditoria', icon: '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>', permission: 'AUDITORIA:READ' },
    { name: 'Atributos', path: '/configuracion', icon: '<path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>', permission: 'CONFIGURACION:READ' },
  ];

  return items.filter(item => {
    // Especial restriction for Dashboard: only for Admins
    if (item.name === 'Dashboard' && !authStore.isAdmin) return false;
    
    // Standard permission check
    return !item.permission || authStore.hasPermission(item.permission);
  });
});


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

const toggleUserMenu = () => {
    showUserMenu.value = !showUserMenu.value;
};

// Role Switcher Modal State
const showRoleModal = ref(false);

// Get user roles from store (clean, without ROLE_ prefix and permissions)
const getUserRoles = computed(() => authStore.userRoles);

// Get current active role from store
const currentActiveRole = computed(() => authStore.currentActiveRole);

// Display role for the dropdown header (shows active role)
const displayRole = computed(() => currentActiveRole.value);

const logout = async () => {
  const confirmed = await confirmStore.ask({
     title: 'Cerrar Sesión',
     message: '¿Estás seguro de que deseas cerrar tu sesión actual?',
     confirmText: 'Cerrar Sesión',
     type: 'warning'
  });
  
  if (!confirmed) return;

  authStore.logout();
  router.push('/login');
  showUserMenu.value = false;
  notificationStore.showInfo('Sesión cerrada correctamente');
};

const editProfile = () => {
  router.push('/profile');
  showUserMenu.value = false;
};

const openRoleSwitcher = () => {
    const roles = getUserRoles.value;
    if (roles.length <= 1) {
        notificationStore.showInfo("Solo tienes un rol asignado: " + (roles[0]?.display || 'Usuario'));
    } else {
        showRoleModal.value = true;
    }
    showUserMenu.value = false;
};

const selectRole = async (roleName) => {
    await authStore.switchRole(roleName);
    showRoleModal.value = false;
};

const handleClickOutside = (event) => {
  const navContainer = document.querySelector('.nav-container');
  if (navContainer && !navContainer.contains(event.target)) {
    closeMenu();
  }
  
  if (userMenuRef.value && !userMenuRef.value.contains(event.target)) {
      showUserMenu.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
  window.addEventListener('resize', checkScroll);
  // Initial check after a short delay for layout to settle
  setTimeout(checkScroll, 500);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
  window.removeEventListener('resize', checkScroll);
});
</script>

<style scoped>
.grabbing {
    cursor: grabbing;
    cursor: -webkit-grabbing;
}

.navbar {
  background: var(--primary-gradient, linear-gradient(135deg, #667eea, #764ba2));
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 6px 1.5rem;
  position: sticky;
  top: 0;
  z-index: 1000;
  border-radius: 0 0 20px 20px;
  backdrop-filter: blur(10px);
}


.nav-container {
  width: 100%;
  padding: 0 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Fluid Layout */
.nav-left {
  flex: 0 0 auto; 
  min-width: 150px;
}

.nav-center {
  flex: 1;
  display: flex;
  min-width: 0; 
  overflow-x: auto; /* Allow horizontal scroll */
  
  /* Snap Scrolling Logic */
  scroll-snap-type: x mandatory;
  scroll-behavior: smooth;
  
  /* Hide scrollbar by default but keep functionality */
  scrollbar-width: none; 
  -ms-overflow-style: none;
  
  /* Mask to show fading edges indication */
  -webkit-mask-image: linear-gradient(to right, transparent, black 5%, black 95%, transparent);
  mask-image: linear-gradient(to right, transparent, black 5%, black 95%, transparent);
}

.scroll-indicator-hint {
    position: absolute;
    right: 15px;
    top: 50%;
    transform: translateY(-50%);
    width: 38px;
    height: 38px;
    background: white;
    color: #6366f1;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 15px rgba(0,0,0,0.15);
    cursor: pointer;
    z-index: 10;
    animation: bounceRight 2s infinite;
    pointer-events: auto;
    border: 2px solid #e2e8f0;
}

@keyframes bounceRight {
    0%, 20%, 50%, 80%, 100% { transform: translateY(-50%) translateX(0); }
    40% { transform: translateY(-50%) translateX(5px); }
    60% { transform: translateY(-50%) translateX(3px); }
}

.nav-center::-webkit-scrollbar { 
    display: none; 
}

.nav-right {
  flex: 0 0 auto;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  min-width: 150px;
}

.nav-logo img {
  height: 65px;
  width: auto;
  object-fit: contain;
  margin: 0;
}

@media (max-width: 600px) {
  .nav-logo img {
    height: 50px;
  }
}

.nav-logo {
    text-decoration: none;
    display: flex;
    align-items: center;
}

.nav-menu {
  display: flex;
  list-style: none;
  margin: 0 auto; /* Center when fitting, overflow properly when not */
  padding: 0;
  gap: 1rem;
  background: rgba(255, 255, 255, 0.1);
  padding: 0.3rem 0.6rem; 
  border-radius: 50px;
  width: max-content; 
}

/* Item styling for snap */
.nav-item {
  scroll-snap-align: center;
  flex: 0 0 auto; 
}

.nav-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  padding: 0.5rem 0.8rem;
  border-radius: 25px;
  transition: background-color 0.3s ease, color 0.3s ease, box-shadow 0.3s ease; /* Exclude border transition */
  display: flex;
  align-items: center;
  gap: 8px; 
  font-weight: 600; 
  font-size: 0.85rem;
  white-space: nowrap;
  border: 1px solid transparent; 
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.15);
  color: white;
}

.nav-link.active {
  background-color: white;
  color: #667eea;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
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
  overflow: hidden;
}

.nav-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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
  font-weight: 700;
  padding: 0.6rem 1.4rem;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-decoration: none;
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
}

.login-link:hover {
  background: white;
  color: #6366f1;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.login-link svg {
  transition: transform 0.3s ease;
}

.login-link:hover svg {
  transform: translateX(3px);
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

@media screen and (max-width: 1100px) {
  .navbar {
    border-radius: 0;
    padding: 0;
    height: 60px;
    display: flex;
    align-items: center;
    backdrop-filter: none; /* Prevent creating a new containing block for fixed children */
    position: fixed; /* Keep header bar fixed at top too */
    top: 0;
    left: 0;
    width: 100%;
  }

  .nav-container {
    padding: 0 1rem;
    height: 100%;
    max-width: 100%;
  }

  /* Top Bar on Mobile: Logo and Profile */
  .nav-left {
    min-width: 0;
    flex: 0 0 auto;
  }
  
  .nav-logo img {
    height: 35px;
  }

  .nav-right {
    min-width: 0;
    flex: 0 0 auto;
    display: flex;
    align-items: center;
    overflow: visible;
  }

  /* Hide User Info and Chevron on Mobile */
  .user-info, .chevron {
    display: none !important;
  }

  .user-profile {
    padding: 4px;
    background: rgba(255,255,255,0.2);
    border-radius: 50%;
  }

  .user-avatar {
    width: 32px;
    height: 32px;
    border-width: 1px;
    font-size: 0.8rem;
  }

  /* Hide Toggle Button */
  .nav-toggle {
    display: none !important;
  }

  /* Fixed Bottom Bar for Menu */
  .nav-center {
    position: fixed !important;
    bottom: 0 !important;
    top: auto !important; /* Force it bottom */
    left: 0 !important;
    width: 100% !important;
    height: 65px !important;
    background: white !important;
    box-shadow: 0 -4px 15px rgba(0,0,0,0.08) !important;
    z-index: 9999 !important; /* Ensure it's above EVERYTHING */
    padding: 0 !important;
    display: flex !important;
    opacity: 1 !important;
    visibility: visible !important;
    overflow-x: auto !important;
    -webkit-overflow-scrolling: touch !important;
    mask-image: none !important;
    -webkit-mask-image: none !important;
    border-top: 1px solid #edf2f7 !important;
    flex: none !important;
    margin: 0 !important;
  }

  .nav-menu {
    position: static !important;
    flex-direction: row !important;
    height: 100% !important;
    width: 100% !important;
    padding: 0 0.5rem !important;
    gap: 0 !important;
    background: transparent !important;
    border-radius: 0 !important;
    box-shadow: none !important;
    transform: none !important;
    align-items: center !important;
    justify-content: space-between !important;
  }

  .nav-item {
    flex: 1 !important;
    min-width: 0 !important;
    max-width: 25% !important;
  }

  .nav-link {
    flex-direction: column !important;
    gap: 2px !important;
    padding: 4px !important;
    color: #a0aec0 !important;
    border-radius: 0 !important;
    font-size: 0.6rem !important;
    font-weight: 500 !important;
    height: 100% !important;
    justify-content: center !important;
    background: transparent !important;
  }

  .nav-link.active {
    color: #667eea !important;
    background: transparent !important;
  }

  /* Indicator line for active item */
  .nav-link.active::after {
    content: '';
    position: absolute;
    top: 0;
    left: 20%;
    right: 20%;
    height: 3px;
    background: #667eea;
    border-radius: 0 0 3px 3px;
  }

  .nav-icon {
    width: 20px !important;
    height: 20px !important;
    margin-bottom: 2px !important;
  }

  /* Bottom Dropdown for User */
  .user-dropdown {
    position: fixed !important;
    bottom: 75px !important; /* Just above bottom nav */
    top: auto !important;
    right: 10px !important;
    left: 10px !important;
    width: auto !important;
    max-width: none !important;
    transform-origin: bottom right !important;
    z-index: 10000 !important;
    border-radius: 16px !important;
  }
}

/* Dropdown Styles */
.nav-user {
    position: relative;
    /* Ensure it doesn't get clipped if parents have overflow hidden */
}

.user-profile {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;
    padding: 0.25rem 0.5rem;
    border-radius: 50px;
    transition: background 0.2s;
    user-select: none;
}

.user-profile:hover, .user-profile.active {
    background: rgba(255,255,255,0.1);
}

.chevron {
    opacity: 0.7;
    transition: transform 0.2s;
}
.chevron.rotate {
    transform: rotate(180deg);
}

.user-dropdown {
    position: absolute;
    top: calc(100% + 8px);
    right: 0;
    width: 240px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06), 0 10px 15px -3px rgba(0,0,0,0.1);    
    padding: 0;
    z-index: 2000; /* Higher than navbar */
    overflow: hidden;
    transform-origin: top right;
    animation: scaleIn 0.2s ease-out;
}

@keyframes scaleIn {
    from { opacity: 0; transform: scale(0.95); }
    to { opacity: 1; transform: scale(1); }
}

.dropdown-header {
    background: #f8fafc;
    padding: 1rem;
    border-bottom: 1px solid #e2e8f0;
    text-align: left;
}

.dropdown-header strong {
    display: block;
    color: #1a202c;
    font-size: 0.95rem;
    margin-bottom: 0.25rem;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.role-label {
    background: #edf2f7;
    color: #4a5568;
    padding: 2px 8px;
    border-radius: 6px;
    font-size: 0.75rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.dropdown-list {
    list-style: none;
    padding: 0.5rem 0;
    margin: 0;
}

.dropdown-list li {
    padding: 0.75rem 1rem;
    display: flex;
    align-items: center;
    gap: 12px;
    color: #4a5568;
    font-size: 0.9rem;
    cursor: pointer;
    transition: all 0.2s;
}

.dropdown-list li:hover {
    background: #f7fafc;
    color: #2b6cb0;
    padding-left: 1.25rem; /* subtle slide effect */
}

.dropdown-list li svg {
    opacity: 0.7;
}

.dropdown-list li:hover svg {
    opacity: 1;
}

.dropdown-list li.danger {
    color: #e53e3e;
}
.dropdown-list li.danger:hover {
    background: #fff5f5;
    color: #c53030;
}

.dropdown-list .divider {
    height: 1px;
    background: #edf2f7;
    margin: 0.5rem 0;
    padding: 0;
    pointer-events: none;
}

/* Role Switcher Modal */
.role-modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 3000;
    backdrop-filter: blur(4px);
    animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
}

.role-modal {
    background: white;
    border-radius: 16px;
    width: 90%;
    max-width: 400px;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
    animation: scaleUp 0.2s ease-out;
}

@keyframes scaleUp {
    from { transform: scale(0.95); opacity: 0; }
    to { transform: scale(1); opacity: 1; }
}

/* Role Switcher Premium Styles */
.role-modal-content {
    padding: 0.5rem 0;
}

.role-instruction {
    color: #64748b;
    font-size: 0.9rem;
    margin-bottom: 1.5rem;
}

.role-options-grid {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.role-card-option {
    display: flex;
    align-items: center;
    padding: 1.25rem;
    background: #f8fafc;
    border: 2px solid #e2e8f0;
    border-radius: 16px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
}

.role-card-option:hover {
    border-color: #667eea;
    background: white;
    transform: translateY(-2px);
    box-shadow: 0 10px 15px -3px rgba(102, 126, 234, 0.1);
}

.role-card-option.active {
    border-color: #667eea;
    background: linear-gradient(to right, #f8faff, white);
    box-shadow: 0 4px 6px -1px rgba(102, 126, 234, 0.1);
}

.role-card-icon {
    width: 48px;
    height: 48px;
    background: white;
    color: #94a3b8;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 1.25rem;
    border: 1px solid #e2e8f0;
    transition: all 0.3s;
}

.active .role-card-icon {
    background: var(--primary-gradient);
    color: white;
    border: none;
    box-shadow: 0 4px 10px rgba(102, 126, 234, 0.3);
}

.role-card-details {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.role-card-name {
    font-weight: 700;
    font-size: 1rem;
    color: #1e293b;
}

.role-card-status {
    font-size: 0.75rem;
    color: #94a3b8;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.025em;
}

.active .role-card-status {
    color: #667eea;
}

.role-card-check {
    margin-left: auto;
}

.check-circle {
    width: 24px;
    height: 24px;
    background: #22c55e;
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 5px rgba(34, 197, 94, 0.3);
}

@keyframes slideInUp {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.role-card-option {
    animation: slideInUp 0.3s ease-out forwards;
}

.role-card-option:nth-child(2) { animation-delay: 0.1s; }
.role-card-option:nth-child(3) { animation-delay: 0.2s; }
.role-card-option:nth-child(4) { animation-delay: 0.3s; }

.role-modal-header {
    padding: 1.25rem 1.5rem;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.role-modal-header h3 {
    margin: 0;
    font-size: 1.1rem;
    color: #1a202c;
}

.role-modal-header .close-btn {
    background: transparent;
    border: none;
    font-size: 1.5rem;
    color: #a0aec0;
    cursor: pointer;
    padding: 0;
    line-height: 1;
}

.role-modal-header .close-btn:hover {
    color: #4a5568;
}

.role-modal-body {
    padding: 1.5rem;
}

.role-instruction {
    color: #718096;
    font-size: 0.9rem;
    margin: 0 0 1rem;
}

.role-options {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
}

.role-option {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1rem;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s;
}

.role-option:hover {
    border-color: #667eea;
    background: #f7fafc;
}

.role-option.active {
    border-color: #667eea;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
}

.role-icon {
    width: 48px;
    height: 48px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    flex-shrink: 0;
}

.role-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
}

.role-name {
    font-weight: 600;
    color: #2d3748;
    font-size: 1rem;
}

.role-active-badge {
    background: #48bb78;
    color: white;
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 0.7rem;
    font-weight: 600;
    text-transform: uppercase;
    width: fit-content;
}

.role-check {
    color: #48bb78;
    flex-shrink: 0;
}
</style>