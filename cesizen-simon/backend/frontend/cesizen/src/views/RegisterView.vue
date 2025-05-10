<template>
  <v-container class="fill-height">
    <v-row justify="center">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card class="elevation-12">
          <v-toolbar
            color="primary"
            dark
            flat
          >
            <v-toolbar-title>Créer un compte</v-toolbar-title>
          </v-toolbar>

          <v-card-text>
            <v-form
              ref="formRef"
              v-model="valid"
              @submit.prevent="handleSubmit"
            >
              <v-text-field
                v-model="form.username"
                label="Nom d'utilisateur"
                :rules="usernameRules"
                required
                prepend-inner-icon="mdi-account"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-text-field
                v-model="form.email"
                label="Email"
                type="email"
                :rules="emailRules"
                required
                prepend-inner-icon="mdi-email"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-text-field
                v-model="form.password"
                label="Mot de passe"
                type="password"
                :rules="passwordRules"
                required
                prepend-inner-icon="mdi-lock"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-text-field
                v-model="form.confirmPassword"
                label="Confirmer le mot de passe"
                type="password"
                :rules="confirmPasswordRules"
                required
                prepend-inner-icon="mdi-lock-check"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-text-field
                v-model="form.firstName"
                label="Prénom"
                :rules="nameRules"
                required
                prepend-inner-icon="mdi-account"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-text-field
                v-model="form.lastName"
                label="Nom"
                :rules="nameRules"
                required
                prepend-inner-icon="mdi-account"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-alert
                v-if="error"
                type="error"
                variant="tonal"
                class="mb-4"
              >
                {{ error }}
              </v-alert>

              <v-btn
                type="submit"
                color="primary"
                block
                size="large"
                :loading="loading"
                :disabled="!valid || loading"
                class="mt-4"
              >
                Créer mon compte
              </v-btn>
            </v-form>
          </v-card-text>

          <v-card-actions class="px-4 pb-4">
            <v-btn
              to="/login"
              variant="text"
              color="primary"
              class="text-none"
            >
              Déjà un compte ? Se connecter
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const valid = ref(false)
const loading = ref(false)
const error = ref('')

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  firstName: '',
  lastName: ''
})

const usernameRules = [
  (v: string) => !!v || 'Le nom d\'utilisateur est requis',
  (v: string) => v.length >= 3 || 'Le nom d\'utilisateur doit contenir au moins 3 caractères'
]

const emailRules = [
  (v: string) => !!v || 'L\'email est requis',
  (v: string) => /.+@.+\..+/.test(v) || 'L\'email doit être valide'
]

const passwordRules = [
  (v: string) => !!v || 'Le mot de passe est requis',
  (v: string) => v.length >= 8 || 'Le mot de passe doit contenir au moins 8 caractères'
]

const confirmPasswordRules = [
  (v: string) => !!v || 'La confirmation du mot de passe est requise',
  (v: string) => v === form.password || 'Les mots de passe ne correspondent pas'
]

const nameRules = [
  (v: string) => !!v || 'Ce champ est requis',
  (v: string) => v.length >= 2 || 'Ce champ doit contenir au moins 2 caractères'
]

async function handleSubmit() {
  if (!valid.value) return

  try {
    loading.value = true
    error.value = ''
    await authStore.register({
      username: form.username,
      email: form.email,
      password: form.password,
      firstName: form.firstName,
      lastName: form.lastName
    })
    router.push('/login')
  } catch (e: any) {
    error.value = e.message || 'Une erreur est survenue lors de l\'inscription'
  } finally {
    loading.value = false
  }
}
</script> 