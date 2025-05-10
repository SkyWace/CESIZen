<template>
  <v-app>
    <v-app-bar
      app
      elevation="0"
      color="surface"
      class="border-b"
    >
      <v-container class="d-flex align-center">
        <v-app-bar-title class="text-h5 font-weight-bold text-primary">
          Cesizen
        </v-app-bar-title>
        <v-spacer></v-spacer>
        <v-btn
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          variant="text"
          class="text-none font-weight-medium"
          :class="{ 'text-primary': isActive(item.to) }"
        >
          {{ item.title }}
        </v-btn>
        <v-btn
          v-if="authStore.isAuthenticated"
          variant="text"
          class="text-none font-weight-medium ml-2"
          @click="handleLogout"
        >
          Déconnexion
        </v-btn>
      </v-container>
    </v-app-bar>

    <v-main class="bg-background">
      <v-container class="py-8">
        <router-view />
      </v-container>
    </v-main>

    <v-footer
      app
      class="bg-surface border-t"
    >
      <v-container>
        <v-row align="center" justify="space-between">
          <v-col cols="12" sm="auto">
            <div class="text-body-2 text-medium-emphasis">
              © {{ new Date().getFullYear() }} Cesizen. Tous droits réservés.
            </div>
          </v-col>
          <v-col cols="12" sm="auto">
            <v-btn
              v-for="item in footerLinks"
              :key="item.title"
              :href="item.href"
              variant="text"
              class="text-none text-body-2"
              target="_blank"
            >
              {{ item.title }}
            </v-btn>
          </v-col>
        </v-row>
      </v-container>
    </v-footer>
  </v-app>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const navItems = computed(() => {
  const items = [
    {
      title: 'Accueil',
      icon: 'mdi-home',
      to: '/'
    },
    {
      title: 'Articles',
      icon: 'mdi-newspaper',
      to: '/articles'
    },
    {
      title: 'Exercices',
      icon: 'mdi-dumbbell',
      to: '/exercises'
    }
  ]

  if (authStore.isAuthenticated) {
    items.push({
      title: 'Profil',
      icon: 'mdi-account',
      to: '/profile'
    })
  } else {
    items.push({
      title: 'Connexion',
      icon: 'mdi-login',
      to: '/login'
    })
  }

  return items
})

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const footerLinks = [
  { title: 'Mentions légales', href: '/legal' },
  { title: 'Politique de confidentialité', href: '/privacy' },
  { title: 'Contact', href: '/contact' }
]

const isActive = (path: string) => {
  return route.path === path
}
</script>

<style>
.v-application {
  font-family: 'Inter', sans-serif;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.border-b {
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}

.border-t {
  border-top: 1px solid rgba(0, 0, 0, 0.12);
}
</style>
