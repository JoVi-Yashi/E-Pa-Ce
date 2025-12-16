import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

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
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true }
    },
    {
      path: '/eventos',
      name: 'eventos',
      component: EventosView,
      meta: { requiresAuth: true }
    },
    {
      path: '/participaciones',
      name: 'participaciones', 
      component: ParticipacionesView,
      meta: { requiresAuth: true }
    },
    {
      path: '/participantes',
      name: 'participantes',
      component: ParticipantesView,
      meta: { requiresAuth: true }
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
      meta: { requiresAuth: true }
    },
    {
      path: '/modalidades',
      name: 'modalidades',
      component: ModalidadesView,
      meta: { requiresAuth: true }
    },
    { path: '/tipos-evento', name: 'tipos-evento', component: TipoEventosView, meta: { requiresAuth: true } },
    { path: '/roles', name: 'roles', component: RolesView, meta: { requiresAuth: true } },
    { path: '/checkins', name: 'checkins', component: CheckInsView, meta: { requiresAuth: true } },
    { path: '/certificaciones', name: 'certificaciones', component: CertificacionesView, meta: { requiresAuth: true } },
    { path: '/auditoria', name: 'auditoria', component: AuditoriaView, meta: { requiresAuth: true } }
  ],
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login');
  } else {
    next();
  }
});

export default router;