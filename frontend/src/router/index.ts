import { useAuthStore } from '@/stores/auth'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/forgot-password',
      name: 'forgotPassword',
      component: () => import('@/views/ForgotPasswordView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/reset-password',
      name: 'resetPassword',
      component: () => import('@/views/ResetPasswordView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/verify-email',
      name: 'verifyEmail',
      component: () => import('@/views/VerifyEmailView.vue'),
      // No auth meta: link must work logged-in or logged-out
    },
    {
      path: '/campaigns',
      name: 'campaigns',
      component: () => import('@/views/CampaignListView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/campaigns/create-new',
      name: 'createCampaign',
      component: () => import('@/views/CreateCampaignView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/campaigns/:id',
      name: 'campaignDetail',
      component: () => import('@/views/CampaignDetailView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/characters',
      name: 'characters',
      component: () => import('@/views/CharacterListView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/characters/create-new-playable',
      name: 'createPlayableCharacter',
      component: () => import('@/views/CreatePlayableCharacterView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/characters/create-new-npc',
      name: 'createNonplayableCharacter',
      component: () => import('@/views/CreateNonplayableCharacterView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/characters/:id',
      name: 'characterDetail',
      component: () => import('@/views/CharacterDetailView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/spells',
      name: 'spells',
      component: () => import('@/views/SpellBrowserView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/settings',
      name: 'settings',
      component: () => import('@/views/SettingsView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/test-component',
      name: 'testComponent',
      component: () => import('@/views/TestComponentView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
    },
  ],
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  if (authStore.isAuthenticated && !authStore.userId) {
    await authStore.fetchCurrentUser()
  }

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return { name: 'login' }
  }

  if (to.meta.requiresGuest && authStore.isAuthenticated) {
    return { name: 'home' }
  }
})

export default router
