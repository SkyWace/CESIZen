<template>
  <v-dialog v-model="dialog" max-width="500px">
    <template v-slot:activator="{ props }">
      <v-btn color="primary" v-bind="props" class="mb-4">
        Create User
      </v-btn>
    </template>

    <v-card>
      <v-card-title>
        <span class="text-h5">Create New User</span>
      </v-card-title>

      <v-card-text>
        <v-form ref="form" v-model="valid" @submit.prevent="save">
          <v-text-field
            v-model="editedItem.username"
            label="Username"
            :rules="[v => !!v || 'Username is required']"
            required
          ></v-text-field>

          <v-text-field
            v-model="editedItem.email"
            label="Email"
            :rules="[
              v => !!v || 'Email is required',
              v => /.+@.+\..+/.test(v) || 'Email must be valid'
            ]"
            required
          ></v-text-field>

          <v-text-field
            v-model="editedItem.password"
            label="Password"
            type="password"
            :rules="[v => !!v || 'Password is required']"
            required
          ></v-text-field>

          <v-text-field
            v-model="editedItem.firstName"
            label="First Name"
            :rules="[v => !!v || 'First name is required']"
            required
          ></v-text-field>

          <v-text-field
            v-model="editedItem.lastName"
            label="Last Name"
            :rules="[v => !!v || 'Last name is required']"
            required
          ></v-text-field>

          <v-select
            v-model="editedItem.roles"
            :items="roleOptions"
            label="Roles"
            multiple
            chips
            :rules="[v => v.length > 0 || 'At least one role is required']"
            required
          ></v-select>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="error" variant="text" @click="close">Cancel</v-btn>
        <v-btn color="primary" variant="text" @click="save" :disabled="!valid">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

interface SignupRequest {
  username: string
  email: string
  password: string
  firstName: string
  lastName: string
  roles: string[]
}

const emit = defineEmits(['user-created'])
const authStore = useAuthStore()

const dialog = ref(false)
const valid = ref(false)
const form = ref()

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

const defaultItem: SignupRequest = {
  username: '',
  email: '',
  password: '',
  firstName: '',
  lastName: '',
  roles: []
}

const editedItem = reactive<SignupRequest>({ ...defaultItem })

const roleOptions = [
  { title: 'Super Admin', value: 'ROLE_SUPER_ADMIN' },
  { title: 'Admin', value: 'ROLE_ADMIN' },
  { title: 'User', value: 'ROLE_USER' }
]

const close = () => {
  dialog.value = false
  Object.assign(editedItem, defaultItem)
  form.value?.reset()
}

const save = async () => {
  if (!form.value.validate()) return

  try {
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
    const response = await axios.post(apiUrl+'/users', editedItem, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    emit('user-created', response.data)
    close()
  } catch (error) {
    console.error('Error creating user:', error)
    if (axios.isAxiosError(error)) {
      if (error.response?.data) {
        showSnackbar(error.response.data, 'error')
      } else {
        showSnackbar('Error creating user', 'error')
      }
    } else {
      showSnackbar('An unexpected error occurred', 'error')
    }
  }
}
</script> 