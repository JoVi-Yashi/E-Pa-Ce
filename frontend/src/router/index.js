import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { useNotificationStore } from '../stores/notifications';

import LoginView from '../views/LoginView.vue';
import RegisterView from '../views/RegisterView.vue';
import HomeView from '../views/HomeView.vue';
import DashboardView from '../views/DashboardView.vue';
import EventosView from '../views/EventosView.vue';
import ParticipacionesView from '../views/ParticipacionesView.vue';
import ParticipantesView from '../views/ParticipantesView.vue';
import ModalidadesView from '../views/ModalidadesView.vue';
import TipoEventosView from '../views/TipoEventosView.vue';
import RolesView from '../views/RolesView.vue';
import CheckInsView from '../views/CheckInsView.vue';
import CertificacionesView from '../views/CertificacionesView.vue';
import AuditoriaView from '../views/AuditoriaView.vue';
import ForgotPasswordView from '../views/ForgotPasswordView.vue';
import ResetPasswordView from '../views/ResetPasswordView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/forgot-password',
      name: 'forgot-password',
      component: ForgotPasswordView
    },
    {
      path: '/reset-password',
      name: 'reset-password',
      component: ResetPasswordView
    },
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue')
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true, permission: 'DASHBOARD:READ' }
    },
    {
      path: '/eventos',
      name: 'eventos',
      component: EventosView,
      meta: { requiresAuth: true, permission: 'EVENTO:READ' }
    },
    {
      path: '/participaciones',
      name: 'participaciones', 
      component: ParticipacionesView,
      meta: { requiresAuth: true, permission: 'PARTICIPACION:READ' }
    },
    {
      path: '/participantes',
      name: 'participantes',
      component: ParticipantesView,
      meta: { requiresAuth: true, permission: 'PARTICIPANTE:READ' }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/configuracion',
      name: 'configuracion',
      component: () => import('../views/ConfiguracionView.vue'),
      meta: { requiresAuth: true, permission: 'CONFIGURACION:VIEW' }
    },
    {
      path: '/modalidades',
      name: 'modalidades',
      component: ModalidadesView,
      meta: { requiresAuth: true, permission: 'CONFIGURACION:VIEW' }
    },
    { 
      path: '/tipos-evento', 
      name: 'tipos-evento', 
      component: TipoEventosView, 
      meta: { requiresAuth: true, permission: 'CONFIGURACION:READ' } 
    },
    { 
      path: '/roles', 
      name: 'roles', 
      component: RolesView, 
      meta: { requiresAuth: true, permission: 'ROL:READ' } 
    },
    { 
      path: '/checkins', 
      name: 'checkins', 
      component: CheckInsView, 
      meta: { requiresAuth: true, permission: 'CHECKIN:READ' } 
    },
    { 
      path: '/certificaciones', 
      name: 'certificaciones', 
      component: CertificacionesView, 
      meta: { requiresAuth: true, permission: 'CERTIFICADO:READ' } 
    },
    { 
      path: '/auditoria', 
      name: 'auditoria', 
      component: AuditoriaView, 
      meta: { requiresAuth: true, permission: 'AUDITORIA:READ' } 
    }
  ],
});

// Navigation Guard - Verifica autenticación Y permisos
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const notificationStore = useNotificationStore();
  
  // 1. Verificar autenticación
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    notificationStore.showWarning('Debes iniciar sesión para acceder a esta página');
    next('/login');
    return;
  }
  
  // 2. Verificar permisos específicos (si la ruta requiere permiso)
  if (to.meta.permission && authStore.isAuthenticated) {
    if (!authStore.hasPermission(to.meta.permission)) {
      notificationStore.showError('No tienes permisos para acceder a esta sección');
      next('/');  // Redirigir al home
      return;
    }
  }
  
  // 3. Todo OK, continuar
  next();
});

export default router;