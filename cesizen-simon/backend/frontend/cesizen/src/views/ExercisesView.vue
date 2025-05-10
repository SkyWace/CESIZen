<template>
  <div class="exercises">
    <!-- En-tête avec illustration -->
    <div class="exercises-header">
      <div class="header-content">
        <h1 class="text-h3 font-weight-bold text-primary">
          Exercices de respiration
        </h1>
        <p class="text-subtitle-1 text-medium-emphasis">
          Des exercices guidés pour retrouver calme et énergie
        </p>
      </div>
      <v-btn
        v-if="authStore.isAuthenticated"
        color="primary"
        variant="elevated"
        class="text-none"
        prepend-icon="mdi-plus"
        @click="showCreateDialog = true"
      >
        Créer un exercice
      </v-btn>
    </div>

    <v-alert
      v-if="error"
      type="error"
      variant="tonal"
      class="mb-4"
    >
      {{ error }}
    </v-alert>

    <v-progress-circular
      v-if="loading"
      indeterminate
      color="primary"
      class="ma-4"
    ></v-progress-circular>

    <div v-else>
      <!-- Exercices officiels -->
      <div class="section-header">
        <h2 class="text-h5 font-weight-bold">
          <v-icon icon="mdi-star" color="primary" class="me-2"></v-icon>
          Exercices officiels
        </h2>
        <p class="text-body-2 text-medium-emphasis">
          Des exercices validés par nos experts pour vous accompagner
        </p>
      </div>

      <v-row>
        <v-col
          v-for="exercise in officialExercises"
          :key="exercise.idExercice"
          cols="12"
          sm="6"
          md="4"
        >
          <v-card class="exercise-card h-100">
            <v-card-title class="d-flex align-center">
              <v-icon
                :icon="getExerciseIcon(exercise.type)"
                color="primary"
                class="me-2"
              ></v-icon>
              {{ exercise.nom }}
            </v-card-title>
            <v-card-text>
              <p class="text-body-2 text-medium-emphasis mb-4">{{ exercise.description }}</p>
              <div class="d-flex flex-wrap gap-2">
                <v-chip
                  color="primary"
                  variant="tonal"
                  size="small"
                  class="text-none"
                >
                  <v-icon start icon="mdi-repeat" size="small"></v-icon>
                  {{ exercise.nombreCycles }} cycles
                </v-chip>
                <v-chip
                  color="info"
                  variant="tonal"
                  size="small"
                  class="text-none"
                >
                  <v-icon start icon="mdi-arrow-up" size="small"></v-icon>
                  {{ exercise.dureeInspiration }}s
                </v-chip>
                <v-chip
                  v-if="exercise.dureeApnee > 0"
                  color="secondary"
                  variant="tonal"
                  size="small"
                  class="text-none"
                >
                  <v-icon start icon="mdi-pause" size="small"></v-icon>
                  {{ exercise.dureeApnee }}s
                </v-chip>
                <v-chip
                  color="warning"
                  variant="tonal"
                  size="small"
                  class="text-none"
                >
                  <v-icon start icon="mdi-arrow-down" size="small"></v-icon>
                  {{ exercise.dureeExpiration }}s
                </v-chip>
              </div>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-tooltip location="top">
                <template v-slot:activator="{ props }">
                  <v-btn
                    color="primary"
                    variant="tonal"
                    v-bind="props"
                    :disabled="true"
                    prepend-icon="mdi-cellphone"
                  >
                    Disponible sur mobile
                  </v-btn>
                </template>
                <span>Exécutez cet exercice sur l'application mobile</span>
              </v-tooltip>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>

      <!-- Exercices personnels -->
      <template v-if="authStore.isAuthenticated">
        <div class="section-header mt-8">
          <h2 class="text-h5 font-weight-bold">
            <v-icon icon="mdi-account" color="primary" class="me-2"></v-icon>
            Mes exercices
          </h2>
          <p class="text-body-2 text-medium-emphasis">
            Vos exercices personnalisés
          </p>
        </div>

        <v-row>
          <v-col
            v-for="exercise in personalExercises"
            :key="exercise.idExercice"
            cols="12"
            sm="6"
            md="4"
          >
            <v-card class="exercise-card h-100">
              <v-card-title class="d-flex align-center">
                <v-icon
                  icon="mdi-dots-grid"
                  color="primary"
                  class="me-2"
                ></v-icon>
                {{ exercise.nom }}
                <v-spacer></v-spacer>
                <v-btn
                  icon="mdi-delete"
                  variant="text"
                  color="error"
                  size="small"
                  @click="confirmDelete(exercise)"
                ></v-btn>
              </v-card-title>
              <v-card-text>
                <p class="text-body-2 text-medium-emphasis mb-4">{{ exercise.description }}</p>
                <div class="d-flex flex-wrap gap-2">
                  <v-chip
                    color="primary"
                    variant="tonal"
                    size="small"
                    class="text-none"
                  >
                    <v-icon start icon="mdi-repeat" size="small"></v-icon>
                    {{ exercise.nombreCycles }} cycles
                  </v-chip>
                  <v-chip
                    color="info"
                    variant="tonal"
                    size="small"
                    class="text-none"
                  >
                    <v-icon start icon="mdi-arrow-up" size="small"></v-icon>
                    {{ exercise.dureeInspiration }}s
                  </v-chip>
                  <v-chip
                    v-if="exercise.dureeApnee > 0"
                    color="secondary"
                    variant="tonal"
                    size="small"
                    class="text-none"
                  >
                    <v-icon start icon="mdi-pause" size="small"></v-icon>
                    {{ exercise.dureeApnee }}s
                  </v-chip>
                  <v-chip
                    color="warning"
                    variant="tonal"
                    size="small"
                    class="text-none"
                  >
                    <v-icon start icon="mdi-arrow-down" size="small"></v-icon>
                    {{ exercise.dureeExpiration }}s
                  </v-chip>
                </div>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-tooltip location="top">
                  <template v-slot:activator="{ props }">
                    <v-btn
                      color="primary"
                      variant="tonal"
                      v-bind="props"
                      :disabled="true"
                      prepend-icon="mdi-cellphone"
                    >
                      Disponible sur mobile
                    </v-btn>
                  </template>
                  <span>Exécutez cet exercice sur l'application mobile</span>
                </v-tooltip>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </template>
    </div>

    <!-- Dialog de création d'exercice -->
    <v-dialog v-model="showCreateDialog" max-width="600px">
      <v-card class="pa-4">
        <v-card-title class="text-h5 mb-4">
          <v-icon icon="mdi-plus-circle" color="primary" class="me-2"></v-icon>
          Créer un exercice
        </v-card-title>

        <v-form @submit.prevent="handleCreateExercise">
          <v-row>
            <v-col cols="12">
              <v-text-field
                v-model="createForm.nom"
                label="Nom de l'exercice"
                :rules="[v => !!v || 'Le nom est requis']"
                required
                variant="outlined"
                prepend-inner-icon="mdi-format-title"
              ></v-text-field>
            </v-col>

            <v-col cols="12">
              <v-textarea
                v-model="createForm.description"
                label="Description"
                variant="outlined"
                prepend-inner-icon="mdi-text"
                rows="3"
              ></v-textarea>
            </v-col>

            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="createForm.dureeInspiration"
                label="Durée inspiration (secondes)"
                type="number"
                min="1"
                :rules="[v => v > 0 || 'La durée doit être supérieure à 0']"
                required
                variant="outlined"
                prepend-inner-icon="mdi-arrow-up"
              ></v-text-field>
            </v-col>

            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="createForm.dureeExpiration"
                label="Durée expiration (secondes)"
                type="number"
                min="1"
                :rules="[v => v > 0 || 'La durée doit être supérieure à 0']"
                required
                variant="outlined"
                prepend-inner-icon="mdi-arrow-down"
              ></v-text-field>
            </v-col>

            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="createForm.dureeApnee"
                label="Durée apnée (secondes)"
                type="number"
                min="0"
                variant="outlined"
                prepend-inner-icon="mdi-pause"
              ></v-text-field>
            </v-col>

            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="createForm.nombreCycles"
                label="Nombre de cycles"
                type="number"
                min="1"
                :rules="[v => v > 0 || 'Le nombre de cycles doit être supérieur à 0']"
                required
                variant="outlined"
                prepend-inner-icon="mdi-repeat"
              ></v-text-field>
            </v-col>
          </v-row>

          <v-alert
            v-if="createError"
            type="error"
            variant="tonal"
            class="mb-4"
          >
            {{ createError }}
          </v-alert>

          <v-alert
            v-if="createSuccess"
            type="success"
            variant="tonal"
            class="mb-4"
          >
            Exercice créé avec succès !
          </v-alert>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              color="grey-darken-1"
              variant="text"
              @click="showCreateDialog = false"
            >
              Annuler
            </v-btn>
            <v-btn
              type="submit"
              color="primary"
              :loading="loading"
              prepend-icon="mdi-check"
            >
              Créer
            </v-btn>
          </v-card-actions>
        </v-form>
      </v-card>
    </v-dialog>

    <!-- Dialog de confirmation de suppression -->
    <v-dialog v-model="showDeleteDialog" max-width="400px">
      <v-card>
        <v-card-title class="text-h5">
          <v-icon icon="mdi-alert" color="error" class="me-2"></v-icon>
          Confirmer la suppression
        </v-card-title>
        <v-card-text>
          Êtes-vous sûr de vouloir supprimer l'exercice "{{ exerciseToDelete?.nom }}" ?
          Cette action est irréversible.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="text"
            @click="showDeleteDialog = false"
          >
            Annuler
          </v-btn>
          <v-btn
            color="error"
            variant="tonal"
            @click="handleDelete"
            :loading="loading"
          >
            Supprimer
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import exerciseService, { type Exercise } from '@/services/exerciseService'

const router = useRouter()
const authStore = useAuthStore()

const officialExercises = ref<Exercise[]>([])
const personalExercises = ref<Exercise[]>([])
const loading = ref(true)
const error = ref('')

const showCreateDialog = ref(false)
const createForm = ref({
  nom: '',
  description: '',
  dureeInspiration: 4,
  dureeExpiration: 6,
  dureeApnee: 0,
  nombreCycles: 5
})

const createError = ref('')
const createSuccess = ref(false)

// Gestion de la suppression
const showDeleteDialog = ref(false)
const exerciseToDelete = ref<Exercise | null>(null)

function confirmDelete(exercise: Exercise) {
  exerciseToDelete.value = exercise
  showDeleteDialog.value = true
}

async function handleDelete() {
  if (!exerciseToDelete.value) return
  
  try {
    await exerciseService.deleteExercise(exerciseToDelete.value.idExercice)
    showDeleteDialog.value = false
    await loadExercises()
  } catch (e: any) {
    error.value = e.message || 'Erreur lors de la suppression de l\'exercice'
  }
}

function getExerciseIcon(type: string): string {
  const icons: Record<string, string> = {
    'Cohérence cardiaque': 'mdi-heart-pulse',
    'Respiration carrée': 'mdi-square-outline',
    '4-7-8': 'mdi-numeric-8',
    'Soleil': 'mdi-weather-sunny'
  }
  return icons[type] || 'mdi-dots-grid'
}

async function loadExercises() {
  loading.value = true
  error.value = ''
  try {
    officialExercises.value = await exerciseService.getOfficialExercises()
    if (authStore.isAuthenticated) {
      personalExercises.value = await exerciseService.getPersonalExercises()
    }
  } catch (e: any) {
    error.value = e.message || 'Erreur lors du chargement des exercices'
  } finally {
    loading.value = false
  }
}

async function handleCreateExercise() {
  createError.value = ''
  createSuccess.value = false
  
  try {
    await exerciseService.createExercise({
      ...createForm.value,
      official: false
    })
    createSuccess.value = true
    showCreateDialog.value = false
    await loadExercises()
  } catch (e: any) {
    createError.value = e.message || 'Erreur lors de la création de l\'exercice'
  }
}

onMounted(() => {
  loadExercises()
})
</script>

<style scoped>
.exercises {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.exercises-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-content {
  flex: 1;
}

.section-header {
  margin-bottom: 24px;
}

.exercise-card {
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
}

.exercise-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1) !important;
}

.gap-2 {
  gap: 8px;
}
</style> 