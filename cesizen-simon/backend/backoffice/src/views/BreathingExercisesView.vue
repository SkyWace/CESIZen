<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            Breathing Exercises
            <v-spacer></v-spacer>
            <v-btn color="primary" @click="openCreateDialog">
              <v-icon start>mdi-plus</v-icon>
              Add Exercise
            </v-btn>
          </v-card-title>

          <v-card-text>
            <v-data-table
              :headers="headers"
              :items="exercises"
              :loading="loading"
              class="elevation-1"
            >
              <template v-slot:item.actions="{ item }">
                <v-icon
                  size="small"
                  class="me-2"
                  @click="editItem(item)"
                >
                  mdi-pencil
                </v-icon>
                <v-icon
                  size="small"
                  @click="deleteItem(item)"
                >
                  mdi-delete
                </v-icon>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Edit Dialog -->
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ formTitle }}</span>
        </v-card-title>

        <v-card-text>
          <v-form ref="form" v-model="valid">
            <v-text-field
              v-model="editedItem.nom"
              label="Name"
              required
            ></v-text-field>

            <v-textarea
              v-model="editedItem.description"
              label="Description"
              required
            ></v-textarea>

            <v-text-field
              v-model="editedItem.type"
              label="Type"
              required
            ></v-text-field>

            <v-text-field
              v-model.number="editedItem.dureeInspiration"
              label="Inhale Duration (seconds)"
              type="number"
              required
            ></v-text-field>

            <v-text-field
              v-model.number="editedItem.dureeExpiration"
              label="Exhale Duration (seconds)"
              type="number"
              required
            ></v-text-field>

            <v-text-field
              v-model.number="editedItem.dureeApnee"
              label="Hold Duration (seconds)"
              type="number"
              required
            ></v-text-field>

            <v-text-field
              v-model.number="editedItem.nombreCycles"
              label="Nombre de cycles"
              type="number"
              min="1"
              required
            ></v-text-field>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="error" variant="text" @click="close">Cancel</v-btn>
          <v-btn color="primary" variant="text" @click="save" :disabled="!valid">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      timeout="3000"
    >
      {{ snackbar.text }}
    </v-snackbar>
  </v-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

interface BreathingExercise {
  idExercice?: number
  nom: string
  description: string
  type: string
  dureeInspiration: number
  dureeExpiration: number
  dureeApnee: number
  nombreCycles: number
}

const authStore = useAuthStore()
const loading = ref(false)
const exercises = ref<BreathingExercise[]>([])
const dialog = ref(false)
const valid = ref(false)
const form = ref()

const headers = [
  { title: 'Name', key: 'nom' },
  { title: 'Description', key: 'description' },
  { title: 'Type', key: 'type' },
  { title: 'Inhale (s)', key: 'dureeInspiration' },
  { title: 'Exhale (s)', key: 'dureeExpiration' },
  { title: 'Hold (s)', key: 'dureeApnee' },
  { title: 'Cycles', key: 'nombreCycles' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const defaultItem: BreathingExercise = {
  nom: '',
  description: '',
  type: '',
  dureeInspiration: 4,
  dureeExpiration: 4,
  dureeApnee: 4,
  nombreCycles: 5
}

const editedItem = ref<BreathingExercise>({ ...defaultItem })
const editedIndex = ref(-1)

const formTitle = computed(() => {
  return editedIndex.value === -1 ? 'New Exercise' : 'Edit Exercise'
})

const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
})

const showSnackbar = (text: string, color: string = 'success') => {
  snackbar.value = {
    show: true,
    text,
    color
  }
}

const fetchExercises = async () => {
  try {
    loading.value = true
    if (!authStore.token) {
      showSnackbar('Authentication required', 'error')
      return
    }

    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
    const response = await axios.get<BreathingExercise[]>(apiUrl+'/exercises', {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    exercises.value = response.data
  } catch (error) {
    console.error('Error fetching exercises:', error)
    if (axios.isAxiosError(error) && error.response?.status === 401) {
      showSnackbar('Session expired. Please login again.', 'error')
      authStore.logout()
    } else {
      showSnackbar('Error fetching exercises', 'error')
    }
  } finally {
    loading.value = false
  }
}

const openCreateDialog = () => {
  editedIndex.value = -1
  editedItem.value = { ...defaultItem }
  dialog.value = true
}

const editItem = (item: BreathingExercise) => {
  editedIndex.value = exercises.value.findIndex(e => e.idExercice === item.idExercice)
  editedItem.value = { ...item }
  dialog.value = true
}

const close = () => {
  dialog.value = false
  editedItem.value = { ...defaultItem }
  form.value?.reset()
}

const save = async () => {
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
  if (!form.value.validate()) return
  if (!authStore.token) {
    showSnackbar('Authentication required', 'error')
    return
  }

  try {
    if (editedIndex.value > -1 && editedItem.value.idExercice) {
      // Update existing exercise
      await axios.put(
        `${apiUrl}/exercises/${editedItem.value.idExercice}`,
        editedItem.value,
        {
          headers: {
            Authorization: `Bearer ${authStore.token}`
          }
        }
      )
      showSnackbar('Exercise updated successfully')
    } else {
      // Create new exercise
      await axios.post(
        apiUrl+'/exercises/official',
        editedItem.value,
        {
          headers: {
            Authorization: `Bearer ${authStore.token}`
          }
        }
      )
      showSnackbar('Exercise created successfully')
    }
    await fetchExercises()
    close()
  } catch (error) {
    console.error('Error saving exercise:', error)
    if (axios.isAxiosError(error) && error.response?.status === 401) {
      showSnackbar('Session expired. Please login again.', 'error')
      authStore.logout()
    } else {
      showSnackbar('Error saving exercise', 'error')
    }
  }
}

const deleteItem = async (item: BreathingExercise) => {
  if (!item.idExercice) return
  if (!authStore.token) {
    showSnackbar('Authentication required', 'error')
    return
  }
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

  if (confirm('Are you sure you want to delete this exercise?')) {
    try {
      await axios.delete(`${apiUrl}/exercises/${item.idExercice}`, {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      })
      await fetchExercises()
      showSnackbar('Exercise deleted successfully')
    } catch (error) {
      console.error('Error deleting exercise:', error)
      if (axios.isAxiosError(error) && error.response?.status === 401) {
        showSnackbar('Session expired. Please login again.', 'error')
        authStore.logout()
      } else {
        showSnackbar('Error deleting exercise', 'error')
      }
    }
  }
}

onMounted(() => {
  fetchExercises()
})
</script> 