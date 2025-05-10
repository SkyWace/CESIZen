<template>
  <v-app>
    <v-main>
      <v-container fluid class="pa-6 pb-16 main-container">
        <RouterView />
      </v-container>
    </v-main>
    <Navbar v-if="!isLoginPage" />
  </v-app>
</template>

<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import { VApp, VMain, VContainer } from 'vuetify/components'
import Navbar from './components/Navbar.vue'
import { computed } from 'vue'

const route = useRoute()
const isLoginPage = computed(() => route.path === '/login')
</script>

<style>
.v-application {
  background: #f8f9fa !important;
}

.v-main {
  min-height: 100vh;
  padding-bottom: 80px; /* Ajoute de l'espace pour la barre de navigation */
}

.main-container {
  max-width: 1400px;
}

/* Consistent card styling */
.v-card {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05) !important;
}

/* Consistent button styling */
.v-btn {
  text-transform: none;
  letter-spacing: 0.5px;
}

/* Consistent table styling */
.v-table {
  border-radius: 8px;
  overflow: hidden;
}

/* Consistent form field styling */
.v-field {
  border-radius: 6px;
}
</style>
