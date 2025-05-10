<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card class="elevation-12">
          <v-toolbar color="primary" dark flat>
            <v-toolbar-title>Login</v-toolbar-title>
          </v-toolbar>
          <v-card-text>
            <v-form @submit.prevent="handleLogin" ref="form">
              <v-text-field
                v-model="username"
                label="Username"
                name="username"
                prepend-icon="mdi-account"
                type="text"
                :error-messages="errors.username"
                @input="errors.username = ''"
                required
              ></v-text-field>
              <v-text-field
                v-model="password"
                label="Password"
                name="password"
                prepend-icon="mdi-lock"
                type="password"
                :error-messages="errors.password"
                @input="errors.password = ''"
                required
              ></v-text-field>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn 
              color="primary" 
              @click="handleLogin"
              :loading="loading"
              :disabled="loading"
            >
              Login
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import axios from 'axios'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const loading = ref(false)
const errors = ref({
  username: '',
  password: ''
})

const snackbar = ref({
  show: false,
  text: '',
  color: 'error'
})

const showSnackbar = (text: string, color: string = 'error') => {
  snackbar.value = {
    show: true,
    text,
    color
  }
}

const handleLogin = async () => {
  // Reset errors
  errors.value = {
    username: '',
    password: ''
  }

  // Validate form
  if (!username.value) {
    errors.value.username = 'Username is required'
    return
  }
  if (!password.value) {
    errors.value.password = 'Password is required'
    return
  }

  loading.value = true

  try {
    const success = await authStore.login(username.value, password.value)
    if (success) {
      showSnackbar('Login successful', 'success')
      router.push('/')
    }
  } catch (error) {
    console.error('Login error:', error)
    if (axios.isAxiosError(error)) {
      if (error.response?.status === 401) {
        showSnackbar('Invalid username or password')
      } else if (error.response?.data?.message) {
        showSnackbar(error.response.data.message)
      } else {
        showSnackbar('An error occurred during login')
      }
    } else {
      showSnackbar('An unexpected error occurred')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.v-container {
  background-color: #f5f5f5;
}
</style> 