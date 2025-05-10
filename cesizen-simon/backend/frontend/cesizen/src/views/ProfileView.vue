<template>
  <div class="profile">
    <div class="d-flex align-center mb-6">
      <h1 class="text-h3 font-weight-bold text-primary">
        Mon Profil
      </h1>
      <v-spacer></v-spacer>
      <v-btn
        color="primary"
        variant="outlined"
        class="text-none me-2"
        prepend-icon="mdi-cog"
        @click="showEditDialog = true"
      >
        Modifier le profil
      </v-btn>
      <v-btn
        color="primary"
        variant="outlined"
        class="text-none"
        prepend-icon="mdi-lock"
        @click="showPasswordDialog = true"
      >
        Changer le mot de passe
      </v-btn>
    </div>

    <v-row>
      <v-col cols="12" md="4">
        <v-card class="mb-4">
          <v-card-item>
            <template v-slot:prepend>
              <v-avatar
                color="primary"
                size="64"
                class="rounded-lg"
              >
                <v-icon
                  icon="mdi-account"
                  color="white"
                  size="32"
                ></v-icon>
              </v-avatar>
            </template>
            <v-card-title class="text-h5 font-weight-bold">
              {{ authStore.currentUser?.username }}
            </v-card-title>
            <v-card-subtitle class="text-medium-emphasis">
              {{ authStore.currentUser?.email }}
            </v-card-subtitle>
          </v-card-item>
          <v-card-text>
            <v-list density="compact">
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-account-details" color="primary"></v-icon>
                </template>
                <v-list-item-title>Nom complet</v-list-item-title>
                <v-list-item-subtitle>{{ authStore.currentUser?.firstName }} {{ authStore.currentUser?.lastName }}</v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>

        <v-card>
          <v-card-title class="text-h6 font-weight-bold">
            Objectifs
          </v-card-title>
          <v-card-text>
            <v-list density="compact">
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-target" color="primary"></v-icon>
                </template>
                <v-list-item-title>Exercices par semaine</v-list-item-title>
                <v-list-item-subtitle>
                  <v-progress-linear
                    :model-value="(stats.totalExercises / 10) * 100"
                    color="primary"
                    height="8"
                    rounded
                  ></v-progress-linear>
                  <div class="text-caption text-right mt-1">
                    {{ stats.totalExercises }} / 10
                  </div>
                </v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="8">
        <v-card class="mb-4">
          <v-card-title class="text-h6 font-weight-bold">
            Statistiques
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" sm="6" md="3">
                <v-card
                  variant="outlined"
                  class="text-center pa-4"
                >
                  <div class="text-h4 font-weight-bold text-primary mb-2">
                    {{ stats.totalExercises }}
                  </div>
                  <div class="text-subtitle-2 text-medium-emphasis">
                    Exercices complétés
                  </div>
                </v-card>
              </v-col>
              <v-col cols="12" sm="6" md="3">
                <v-card
                  variant="outlined"
                  class="text-center pa-4"
                >
                  <div class="text-h4 font-weight-bold text-secondary mb-2">
                    {{ stats.totalMinutes }}
                  </div>
                  <div class="text-subtitle-2 text-medium-emphasis">
                    Minutes de pratique
                  </div>
                </v-card>
              </v-col>
              <v-col cols="12" sm="6" md="3">
                <v-card
                  variant="outlined"
                  class="text-center pa-4"
                >
                  <div class="text-h4 font-weight-bold text-accent mb-2">
                    {{ stats.currentStreak }}
                  </div>
                  <div class="text-subtitle-2 text-medium-emphasis">
                    Jours consécutifs
                  </div>
                </v-card>
              </v-col>
              <v-col cols="12" sm="6" md="3">
                <v-card
                  variant="outlined"
                  class="text-center pa-4"
                >
                  <div class="text-h4 font-weight-bold text-info mb-2">
                    {{ stats.bestStreak }}
                  </div>
                  <div class="text-subtitle-2 text-medium-emphasis">
                    Meilleure série
                  </div>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>

        <v-card>
          <v-card-title class="text-h6 font-weight-bold">
            Derniers exercices
          </v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item
                v-for="exercise in recentExercises"
                :key="exercise.id"
                :title="exercise.exerciseName"
                :subtitle="`${new Date(exercise.completedAt).toLocaleDateString('fr-FR')} - ${formatDuration(exercise.duration)}`"
              >
                <template v-slot:prepend>
                  <v-avatar
                    color="primary"
                    size="40"
                    class="rounded-lg"
                  >
                    <v-icon
                      icon="mdi-lungs"
                      color="white"
                    ></v-icon>
                  </v-avatar>
                </template>
                <template v-slot:append>
                  <v-tooltip location="top">
                    <template v-slot:activator="{ props }">
                      <v-btn
                        icon="mdi-cellphone"
                        variant="text"
                        color="primary"
                        v-bind="props"
                        disabled
                      ></v-btn>
                    </template>
                    <span>Disponible uniquement sur l'application mobile</span>
                  </v-tooltip>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Dialog de modification du profil -->
    <v-dialog v-model="showEditDialog" max-width="600px">
      <v-card class="pa-4">
        <v-card-title class="text-h5 mb-4">
          Modifier le profil
        </v-card-title>

        <v-form @submit.prevent="handleUpdateProfile" v-model="isFormValid">
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.username"
                label="Nom d'utilisateur"
                :rules="usernameRules"
                required
                variant="outlined"
                class="mb-4"
              ></v-text-field>
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.email"
                label="Email"
                type="email"
                :rules="emailRules"
                required
                variant="outlined"
                class="mb-4"
              ></v-text-field>
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.firstName"
                label="Prénom"
                :rules="nameRules"
                required
                variant="outlined"
                class="mb-4"
              ></v-text-field>
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.lastName"
                label="Nom"
                :rules="nameRules"
                required
                variant="outlined"
                class="mb-4"
              ></v-text-field>
            </v-col>
          </v-row>

          <v-alert
            v-if="error"
            type="error"
            class="mb-4"
          >
            {{ error }}
          </v-alert>

          <v-alert
            v-if="success"
            type="success"
            class="mb-4"
          >
            {{ success }}
          </v-alert>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              color="grey-darken-1"
              variant="text"
              @click="showEditDialog = false"
            >
              Annuler
            </v-btn>
            <v-btn
              type="submit"
              color="primary"
              :loading="loading"
              :disabled="!isFormValid || loading"
            >
              Enregistrer
            </v-btn>
          </v-card-actions>
        </v-form>
      </v-card>
    </v-dialog>

    <!-- Dialog de changement de mot de passe -->
    <v-dialog v-model="showPasswordDialog" max-width="600px">
      <v-card class="pa-4">
        <v-card-title class="text-h5 mb-4">
          Changer le mot de passe
        </v-card-title>

        <v-form @submit.prevent="handleUpdatePassword" v-model="isPasswordFormValid">
          <v-row>
            <v-col cols="12">
              <v-text-field
                v-model="passwordForm.currentPassword"
                label="Mot de passe actuel"
                type="password"
                :rules="passwordRules"
                required
                variant="outlined"
                class="mb-4"
              ></v-text-field>
            </v-col>

            <v-col cols="12">
              <v-text-field
                v-model="passwordForm.newPassword"
                label="Nouveau mot de passe"
                type="password"
                :rules="passwordRules"
                required
                variant="outlined"
                class="mb-4"
              ></v-text-field>
            </v-col>
          </v-row>

          <v-alert
            v-if="passwordError"
            type="error"
            class="mb-4"
          >
            {{ passwordError }}
          </v-alert>

          <v-alert
            v-if="passwordSuccess"
            type="success"
            class="mb-4"
          >
            {{ passwordSuccess }}
          </v-alert>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              color="grey-darken-1"
              variant="text"
              @click="showPasswordDialog = false"
            >
              Annuler
            </v-btn>
            <v-btn
              type="submit"
              color="primary"
              :loading="passwordLoading"
              :disabled="!isPasswordFormValid || passwordLoading"
            >
              Changer le mot de passe
            </v-btn>
          </v-card-actions>
        </v-form>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import exerciseService, { type Exercise, type BreathingExerciseHistory } from '@/services/exerciseService'

const authStore = useAuthStore()
const router = useRouter()
const loading = ref(false)
const error = ref('')
const success = ref('')
const isFormValid = ref(false)
const showEditDialog = ref(false)
const showPasswordDialog = ref(false)

const form = ref({
  username: '',
  email: '',
  firstName: '',
  lastName: ''
})

const passwordForm = ref({
  currentPassword: '',
  newPassword: ''
})

const passwordLoading = ref(false)
const passwordError = ref('')
const passwordSuccess = ref('')
const isPasswordFormValid = ref(false)

const stats = ref({
  totalExercises: 0,
  totalMinutes: 0,
  currentStreak: 0,
  bestStreak: 0
})

const recentExercises = ref<BreathingExerciseHistory[]>([])

const usernameRules = [
  (v: string) => !!v || 'Le nom d\'utilisateur est requis',
  (v: string) => v.length >= 3 || 'Le nom d\'utilisateur doit contenir au moins 3 caractères'
]

const emailRules = [
  (v: string) => !!v || 'L\'email est requis',
  (v: string) => /.+@.+\..+/.test(v) || 'L\'email doit être valide'
]

const nameRules = [
  (v: string) => !!v || 'Ce champ est requis',
  (v: string) => v.length >= 2 || 'Ce champ doit contenir au moins 2 caractères'
]

const passwordRules = [
  (v: string) => !!v || 'Le mot de passe est requis',
  (v: string) => v.length >= 6 || 'Le mot de passe doit contenir au moins 6 caractères'
]

const getExerciseColor = (id: number) => {
  const colors = {
    1: 'primary',
    2: 'secondary',
    3: 'accent'
  }
  return colors[id as keyof typeof colors] || 'primary'
}

onMounted(async () => {
  try {
    console.log('ProfileView mounted, loading data...')
    
    // Charger les données du profil
    await authStore.fetchProfile()
    const user = authStore.currentUser
    if (user) {
      form.value = {
        username: user.username,
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName
      }
    }

    console.log('Fetching breathing history...')
    // Charger l'historique des exercices
    const history = await exerciseService.getBreathingHistory()
    console.log('Breathing history received:', history)
    recentExercises.value = history

    // Calculer les statistiques à partir de l'historique
    stats.value = {
      totalExercises: history.length,
      totalMinutes: Math.round(history.reduce((acc, exercise) => acc + exercise.duration, 0) / 60),
      currentStreak: calculateCurrentStreak(history),
      bestStreak: calculateBestStreak(history)
    }
  } catch (err: any) {
    console.error('Error loading profile data:', err)
    error.value = err.response?.data?.message || 'Une erreur est survenue lors du chargement des données'
  }
})

// Fonction pour calculer la série actuelle
const calculateCurrentStreak = (history: BreathingExerciseHistory[]): number => {
  if (history.length === 0) return 0

  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  let streak = 0
  let currentDate = today

  while (true) {
    const hasExerciseToday = history.some(exercise => {
      const exerciseDate = new Date(exercise.completedAt)
      exerciseDate.setHours(0, 0, 0, 0)
      return exerciseDate.getTime() === currentDate.getTime()
    })

    if (!hasExerciseToday) break

    streak++
    currentDate.setDate(currentDate.getDate() - 1)
  }

  return streak
}

// Fonction pour calculer la meilleure série
const calculateBestStreak = (history: BreathingExerciseHistory[]): number => {
  if (history.length === 0) return 0

  const dates = history.map(exercise => {
    const date = new Date(exercise.completedAt)
    date.setHours(0, 0, 0, 0)
    return date.getTime()
  }).sort((a, b) => a - b)

  let currentStreak = 1
  let bestStreak = 1

  for (let i = 1; i < dates.length; i++) {
    const diffDays = (dates[i] - dates[i - 1]) / (1000 * 60 * 60 * 24)
    
    if (diffDays === 1) {
      currentStreak++
      bestStreak = Math.max(bestStreak, currentStreak)
    } else {
      currentStreak = 1
    }
  }

  return bestStreak
}

const getExerciseIcon = (type: string) => {
  const icons = {
    'SQUARE': 'mdi-square-outline',
    'DEEP': 'mdi-lungs',
    'HEART': 'mdi-heart-outline',
    'CUSTOM': 'mdi-dots-grid'
  }
  return icons[type as keyof typeof icons] || 'mdi-dots-grid'
}

const handleUpdateProfile = async () => {
  try {
    loading.value = true
    error.value = ''
    success.value = ''
    await authStore.updateProfile(form.value)
    success.value = 'Profil mis à jour avec succès'
    showEditDialog.value = false
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Une erreur est survenue lors de la mise à jour du profil'
  } finally {
    loading.value = false
  }
}

const handleUpdatePassword = async () => {
  try {
    passwordLoading.value = true
    passwordError.value = ''
    passwordSuccess.value = ''
    await authStore.updatePassword(
      passwordForm.value.currentPassword,
      passwordForm.value.newPassword
    )
    passwordSuccess.value = 'Mot de passe mis à jour avec succès'
    passwordForm.value = {
      currentPassword: '',
      newPassword: ''
    }
    showPasswordDialog.value = false
  } catch (err: any) {
    passwordError.value = err.response?.data?.message || 'Une erreur est survenue lors de la mise à jour du mot de passe'
  } finally {
    passwordLoading.value = false
  }
}

// Fonction pour formater la durée
const formatDuration = (seconds: number): string => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  if (minutes === 0) {
    return `${seconds}s`
  }
  return `${minutes}min${remainingSeconds > 0 ? ` ${remainingSeconds}s` : ''}`
}

// Fonction pour rediriger vers l'exercice
const redoExercise = (exerciseId: number) => {
  router.push({ name: 'BreathingExerciseRun', params: { id: exerciseId } })
}
</script>

<style scoped>
.profile {
  max-width: 1200px;
  margin: 0 auto;
}
</style> 