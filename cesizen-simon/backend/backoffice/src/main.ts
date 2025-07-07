import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import '@mdi/font/css/materialdesignicons.css'

const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi
    }
  },
  theme: {
    defaultTheme: 'light'
  }
})

async function setupApp() {
  const app = createApp(App)
  const pinia = createPinia()
  
  app.use(pinia)
  app.use(router)
  app.use(vuetify)

  // Mount the app first to ensure Vue context is available
  app.mount('#app')

  // Now initialize auth store within Vue context
  const authStore = useAuthStore()
  authStore.initialize()

  try {
    await authStore.checkAuth()
  } catch (error) {
    console.error('Failed to check authentication state:', error)
  }
}

setupApp() 