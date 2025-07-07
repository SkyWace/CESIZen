<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <div class="d-flex align-center justify-space-between mb-6">
          <h1 class="text-h4">Dashboard</h1>
          <v-btn
            color="primary"
            prepend-icon="mdi-refresh"
            @click="refreshData"
            :loading="loading"
          >
            Refresh
          </v-btn>
        </div>
      </v-col>
    </v-row>

    <!-- Statistics Cards -->
    <v-row>
      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2">
          <v-card-text>
            <div class="d-flex align-center">
              <v-icon size="large" color="primary" class="me-3">mdi-account-group</v-icon>
              <div>
                <div class="text-h4 font-weight-bold">{{ stats.totalUsers }}</div>
                <div class="text-subtitle-1 text-medium-emphasis">Total Users</div>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2">
          <v-card-text>
            <div class="d-flex align-center">
              <v-icon size="large" color="success" class="me-3">mdi-newspaper</v-icon>
              <div>
                <div class="text-h4 font-weight-bold">{{ stats.totalArticles }}</div>
                <div class="text-subtitle-1 text-medium-emphasis">Articles</div>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2">
          <v-card-text>
            <div class="d-flex align-center">
              <v-icon size="large" color="info" class="me-3">mdi-lungs</v-icon>
              <div>
                <div class="text-h4 font-weight-bold">{{ stats.totalExercises }}</div>
                <div class="text-subtitle-1 text-medium-emphasis">Exercises</div>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2">
          <v-card-text>
            <div class="d-flex align-center">
              <v-icon size="large" color="warning" class="me-3">mdi-shield-account</v-icon>
              <div>
                <div class="text-h4 font-weight-bold">{{ stats.adminUsers }}</div>
                <div class="text-subtitle-1 text-medium-emphasis">Admin Users</div>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Main Content -->
    <v-row class="mt-4">
      <!-- Quick Actions -->
      <v-col cols="12" md="4">
        <v-card class="h-100" elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon icon="mdi-lightning-bolt" class="me-2" color="primary" />
            Quick Actions
          </v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item
                v-if="authStore.isAdmin"
                :to="{ name: 'articles' }"
                prepend-icon="mdi-newspaper"
                title="Manage Articles"
                subtitle="Create and edit articles"
                class="mb-2 quick-action-item"
              >
                <template v-slot:append>
                  <v-chip color="primary" size="small">{{ stats.totalArticles }}</v-chip>
                </template>
              </v-list-item>
              <v-list-item
                v-if="authStore.isAdmin"
                :to="{ name: 'users' }"
                prepend-icon="mdi-account-group"
                title="Manage Users"
                subtitle="View and manage user accounts"
                class="mb-2 quick-action-item"
              >
                <template v-slot:append>
                  <v-chip color="primary" size="small">{{ stats.totalUsers }}</v-chip>
                </template>
              </v-list-item>
              <v-list-item
                v-if="authStore.isAdmin"
                :to="{ name: 'breathing-exercises' }"
                prepend-icon="mdi-lungs"
                title="Breathing Exercises"
                subtitle="Manage breathing exercises"
                class="quick-action-item"
              >
                <template v-slot:append>
                  <v-chip color="primary" size="small">{{ stats.totalExercises }}</v-chip>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Recent Activity -->
      <v-col cols="12" md="8">
        <v-card class="h-100" elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon icon="mdi-clock-outline" class="me-2" color="primary" />
            Recent Activity
          </v-card-title>
          <v-card-text>
            <v-timeline density="compact" align="start">
              <v-timeline-item
                v-for="(activity, index) in recentActivities"
                :key="index"
                :dot-color="activity.color"
                size="small"
              >
                <div class="d-flex justify-space-between align-center">
                  <div>
                    <div class="text-subtitle-2 font-weight-bold">{{ activity.title }}</div>
                    <div class="text-caption text-medium-emphasis">{{ activity.description }}</div>
                  </div>
                  <div class="text-caption text-medium-emphasis">{{ activity.time }}</div>
                </div>
              </v-timeline-item>
            </v-timeline>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Welcome Card -->
    <v-row class="mt-4">
      <v-col cols="12">
        <v-card elevation="2">
          <v-card-text>
            <div class="d-flex align-center">
              <v-avatar color="primary" size="64" class="me-4">
                <v-icon icon="mdi-account" size="32" color="white" />
              </v-avatar>
              <div>
                <h2 class="text-h5 mb-2">Welcome back, {{ authStore.fullName }}!</h2>
                <p class="text-body-1 mb-1">
                  <v-icon icon="mdi-calendar" size="small" class="me-1" />
                  {{ currentDate }}
                </p>
                <p class="text-body-1">
                  <v-icon icon="mdi-shield-check" size="small" class="me-1" />
                  <span v-if="authStore.isSuperAdmin">Super Administrator</span>
                  <span v-else-if="authStore.isAdmin">Administrator</span>
                  <span v-else>User</span>
                </p>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import axios from 'axios'

const authStore = useAuthStore()
const loading = ref(false)

// Statistics
const stats = ref({
  totalUsers: 0,
  totalArticles: 0,
  totalExercises: 0,
  adminUsers: 0
})

// Recent activities
const recentActivities = ref([
  {
    title: 'New User Registration',
    description: 'A new user has joined the platform',
    time: '2 minutes ago',
    color: 'success'
  },
  {
    title: 'Article Published',
    description: 'New article "Breathing Techniques" has been published',
    time: '1 hour ago',
    color: 'primary'
  },
  {
    title: 'Exercise Added',
    description: 'New breathing exercise has been added',
    time: '3 hours ago',
    color: 'info'
  }
])

// Current date
const currentDate = computed(() => {
  return new Date().toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
})

// Fetch dashboard data
const fetchData = async () => {
  try {
    loading.value = true
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
    const [usersResponse, articlesResponse, exercisesResponse] = await Promise.all([
      axios.get(apiUrl+'/users', {
        headers: { Authorization: `Bearer ${authStore.token}` }
      }),
      axios.get(apiUrl+'/articles', {
        headers: { Authorization: `Bearer ${authStore.token}` }
      }),
      axios.get(apiUrl+'/breathing-exercises', {
        headers: { Authorization: `Bearer ${authStore.token}` }
      })
    ])

    stats.value = {
      totalUsers: usersResponse.data.length,
      totalArticles: articlesResponse.data.length,
      totalExercises: exercisesResponse.data.length,
      adminUsers: usersResponse.data.filter((user: any) => 
        user.roles.includes('ROLE_ADMIN') || user.roles.includes('ROLE_SUPER_ADMIN')
      ).length
    }
  } catch (error) {
    console.error('Error fetching dashboard data:', error)
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.stat-card {
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.quick-action-item {
  border-radius: 8px;
  transition: background-color 0.2s;
}

.quick-action-item:hover {
  background-color: rgba(var(--v-theme-primary), 0.05);
}

.v-timeline-item {
  min-height: 60px;
}
</style> 