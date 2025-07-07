<template>
  <div class="dashboard">
    <v-row>
      <v-col cols="12" md="4">
        <v-card class="stat-card">
          <v-card-text>
            <div class="d-flex align-center">
              <v-icon size="large" color="primary" class="me-3">mdi-account-group</v-icon>
              <div>
                <div class="text-h4 font-weight-bold">{{ totalUsers }}</div>
                <div class="text-subtitle-1 text-medium-emphasis">Total Users</div>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card class="stat-card">
          <v-card-text>
            <div class="d-flex align-center">
              <v-icon size="large" color="success" class="me-3">mdi-account-check</v-icon>
              <div>
                <div class="text-h4 font-weight-bold">{{ activeUsers }}</div>
                <div class="text-subtitle-1 text-medium-emphasis">Active Users</div>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card class="stat-card">
          <v-card-text>
            <div class="d-flex align-center">
              <v-icon size="large" color="info" class="me-3">mdi-lungs</v-icon>
              <div>
                <div class="text-h4 font-weight-bold">{{ totalExercises }}</div>
                <div class="text-subtitle-1 text-medium-emphasis">Breathing Exercises</div>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mt-4">
      <v-col cols="12">
        <v-card>
          <v-card-title>Subscription Trends</v-card-title>
          <v-card-text>
            <SubscriptionChart />
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mt-4">
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>User Roles Distribution</v-card-title>
          <v-card-text>
            <div class="d-flex justify-space-between align-center mb-4">
              <div class="d-flex align-center">
                <v-icon color="error" class="me-2">mdi-shield-account</v-icon>
                <span>Admins: {{ adminCount }}</span>
              </div>
              <div class="d-flex align-center">
                <v-icon color="primary" class="me-2">mdi-account</v-icon>
                <span>Users: {{ userCount }}</span>
              </div>
            </div>
            <v-progress-linear
              :model-value="(adminCount / totalUsers) * 100"
              color="error"
              height="20"
              class="mb-2"
            >
              <template v-slot:default="{ value }">
                <strong>{{ Math.ceil(value) }}%</strong>
              </template>
            </v-progress-linear>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>Recent Activity</v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item
                v-for="(activity, index) in recentActivities"
                :key="index"
                :prepend-icon="activity.icon"
                :title="activity.title"
                :subtitle="activity.time"
              >
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <section class="moderation-section">
      <h2>Modération des avis d'exercice</h2>
      <div v-if="loadingFeedbacks">Chargement...</div>
      <div v-else-if="errorFeedbacks">{{ errorFeedbacks }}</div>
      <table v-else class="feedback-table">
        <thead>
          <tr>
            <th>Exercice</th>
            <th>Utilisateur</th>
            <th>Note</th>
            <th>Commentaire</th>
            <th>Date</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="fb in feedbacks" :key="fb.id">
            <td>{{ fb.exerciseId }}</td>
            <td>{{ fb.userName }}</td>
            <td>{{ fb.rating }}</td>
            <td>{{ fb.comment }}</td>
            <td>{{ new Date(fb.createdAt).toLocaleString() }}</td>
            <td>
              <button @click="deleteFeedback(fb)" title="Supprimer" style="color: red; font-size: 1.2em;">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import SubscriptionChart from '@/components/SubscriptionChart.vue'

interface User {
  id: number
  username: string
  roles: string[]
}

interface Activity {
  icon: string
  title: string
  time: string
}

interface Feedback {
  id: number;
  exerciseId: number;
  userId: number;
  userName: string;
  comment: string;
  rating: number;
  createdAt: string;
}

const authStore = useAuthStore()
const users = ref<User[]>([])
const totalExercises = ref(0)
const loading = ref(true)
const feedbacks = ref<Feedback[]>([])
const loadingFeedbacks = ref(false)
const errorFeedbacks = ref('')

// Computed properties for statistics
const totalUsers = computed(() => users.value.length)
const activeUsers = computed(() => users.value.length) // You might want to add an 'active' status to users
const adminCount = computed(() => users.value.filter(user => user.roles.includes('ROLE_ADMIN')).length)
const userCount = computed(() => users.value.filter(user => user.roles.includes('ROLE_USER')).length)

// Mock recent activities - you can replace this with real data
const recentActivities = ref<Activity[]>([
  {
    icon: 'mdi-account-plus',
    title: 'New user registered',
    time: '2 minutes ago'
  },
  {
    icon: 'mdi-lungs',
    title: 'New breathing exercise added',
    time: '1 hour ago'
  },
  {
    icon: 'mdi-account-edit',
    title: 'User profile updated',
    time: '3 hours ago'
  }
])

const fetchData = async () => {
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
  try {
    loading.value = true
    // Fetch users
    const usersResponse = await axios.get<User[]>(apiUrl+'/users', {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    users.value = usersResponse.data

    // Fetch total exercises
    const exercisesResponse = await axios.get(apiUrl+'/breathing-exercises/count', {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    totalExercises.value = exercisesResponse.data
  } catch (error) {
    console.error('Error fetching dashboard data:', error)
  } finally {
    loading.value = false
  }
}

async function fetchFeedbacks() {
  loadingFeedbacks.value = true;
  errorFeedbacks.value = '';
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
  try {
    const response = await axios.get(apiUrl+'/feedback', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    });
    feedbacks.value = response.data;
  } catch (e) {
    errorFeedbacks.value = 'Erreur lors du chargement des avis.';
  } finally {
    loadingFeedbacks.value = false;
  }
}

async function deleteFeedback(feedback: Feedback) {
  if (!confirm('Supprimer cet avis ?')) return;
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
  try {
    await axios.delete(`${apiUrl}/exercises/${feedback.exerciseId}/feedback/${feedback.id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    });
    await fetchFeedbacks();
  } catch (e) {
    alert('Erreur lors de la suppression.');
  }
}

onMounted(() => {
  fetchData()
  fetchFeedbacks()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.v-progress-linear {
  border-radius: 4px;
}

.moderation-section {
  margin-top: 2.5rem;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  padding: 1.5rem;
}
.feedback-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}
.feedback-table th, .feedback-table td {
  border: 1px solid #eee;
  padding: 0.5rem 0.7rem;
  text-align: left;
}
.feedback-table th {
  background: #f5f5f5;
}
</style> 