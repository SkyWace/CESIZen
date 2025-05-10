import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'

const vuetify = createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        dark: false,
        colors: {
          primary: '#4A90E2',
          secondary: '#50E3C2',
          accent: '#F5A623',
          error: '#FF5252',
          info: '#2196F3',
          success: '#4CAF50',
          warning: '#FFC107',
          background: '#F8F9FA',
          surface: '#FFFFFF',
          'on-primary': '#FFFFFF',
          'on-secondary': '#000000',
          'on-background': '#2C3E50',
          'on-surface': '#2C3E50'
        }
      }
    }
  }
})

const app = createApp(App)

// Initialisation des plugins dans l'ordre correct
app.use(vuetify)  // Vuetify doit être initialisé en premier
app.use(createPinia())
app.use(router)

// Attendre que le DOM soit prêt avant de monter l'application
window.addEventListener('DOMContentLoaded', () => {
  app.mount('#app')
}) 