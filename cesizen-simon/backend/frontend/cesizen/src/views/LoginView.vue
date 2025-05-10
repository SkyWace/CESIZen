<template>
  <div class="login">
    <v-row justify="center">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card class="pa-4">
          <v-card-title class="text-h5 text-center mb-4">
            Connexion
          </v-card-title>

          <v-form @submit.prevent="handleLogin" v-model="isFormValid">
            <v-text-field
              v-model="username"
              label="Nom d'utilisateur"
              :rules="usernameRules"
              required
              variant="outlined"
              class="mb-4"
            ></v-text-field>

            <v-text-field
              v-model="password"
              label="Mot de passe"
              type="password"
              :rules="passwordRules"
              required
              variant="outlined"
              class="mb-4"
            ></v-text-field>

            <v-alert
              v-if="error"
              type="error"
              class="mb-4"
            >
              {{ error }}
            </v-alert>

            <v-btn
              type="submit"
              color="primary"
              block
              :loading="loading"
              :disabled="!isFormValid || loading"
            >
              Se connecter
            </v-btn>

            <div class="text-center mt-4">
              <span class="text-body-2 text-medium-emphasis">Pas encore de compte ?</span>
              <v-btn
                to="/register"
                variant="text"
                color="primary"
                class="text-none font-weight-medium"
              >
                Créer un compte
              </v-btn>
            </div>
          </v-form>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')
const isFormValid = ref(false)

const usernameRules = [
  (v: string) => !!v || 'Le nom d\'utilisateur est requis',
  (v: string) => v.length >= 3 || 'Le nom d\'utilisateur doit contenir au moins 3 caractères'
]

const passwordRules = [
  (v: string) => !!v || 'Le mot de passe est requis',
  (v: string) => v.length >= 6 || 'Le mot de passe doit contenir au moins 6 caractères'
]

const handleLogin = async () => {
  try {
    loading.value = true
    error.value = ''
    await authStore.login(username.value, password.value)
    router.push('/')
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Une erreur est survenue lors de la connexion'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login {
  max-width: 1200px;
  margin: 0 auto;
}
</style> 