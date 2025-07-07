import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import ModerationFeedbackView from '@/views/ModerationFeedbackView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/users',
      name: 'users',
      component: () => import('../views/UsersView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import('../views/ArticleListView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/articles/pending',
      name: 'pending-articles',
      component: () => import('../views/PendingArticlesView.vue'),
      meta: { requiresAuth: true, requiresSuperAdmin: true }
    },
    {
      path: '/breathing-exercises',
      name: 'breathing-exercises',
      component: () => import('../views/BreathingExercisesView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/articles/create',
      name: 'article-create',
      component: () => import('../views/ArticleCreateView.vue'),
      meta: { requiresAuth: true, requiresSuperAdmin: true }
    },
    {
      path: '/articles/edit/:id',
      name: 'article-edit',
      component: () => import('../views/ArticleEditView.vue'),
      meta: { requiresAuth: true, requiresSuperAdmin: true }
    },
    {
      path: '/moderation-feedback',
      name: 'ModerationFeedback',
      component: ModerationFeedbackView
    },
    // Catch all route for 404
    {
      path: '/:pathMatch(.*)*',
      redirect: '/'
    }
  ]
})

// Navigation guard
router.beforeEach((to, _, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin)
  const requiresSuperAdmin = to.matched.some(record => record.meta.requiresSuperAdmin)

  if (requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (requiresAdmin && !authStore.isAdmin) {
    next('/')
  } else if (requiresSuperAdmin && !authStore.isSuperAdmin) {
    next('/')
  } else {
    next()
  }
})

export default router 
