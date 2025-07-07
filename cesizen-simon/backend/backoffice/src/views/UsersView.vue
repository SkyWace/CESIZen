<template>
  <div class="users-view">
    <h1>User Management</h1>
    
    <UserCreateDialog @user-created="handleUserCreated" />
    
    <v-data-table
      :headers="headers"
      :items="users"
      :loading="loading"
      class="elevation-1"
    >
      <template v-slot:item.roles="{ item }">
        <v-chip
          v-for="role in item.roles"
          :key="role"
          class="ma-1"
          :color="role === 'ROLE_SUPER_ADMIN' ? 'purple' : role === 'ROLE_ADMIN' ? 'error' : 'primary'"
        >
          {{ role.replace('ROLE_', '') }}
        </v-chip>
      </template>

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
          :disabled="item.username === 'admin' || item.id === currentUserId"
        >
          mdi-delete
        </v-icon>
      </template>
    </v-data-table>

    <UserEditDialog
      v-model="dialog"
      :edited-item="editedItem ?? {}"
      @save="saveItem"
      @close="close"
    />

    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      timeout="3000"
    >
      {{ snackbar.text }}
    </v-snackbar>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import UserEditDialog from '@/components/UserEditDialog.vue'
import UserCreateDialog from '@/components/UserCreateDialog.vue'
import { useAuthStore } from '@/stores/auth'

interface User {
  id: number
  username: string
  email: string
  firstName: string
  lastName: string
  roles: string[]
}

const authStore = useAuthStore()
const users = ref<User[]>([])
const loading = ref(true)
const dialog = ref(false)
const editedItem = ref<User | null>(null)

const currentUserId = computed(() => authStore.user?.id)

const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
})

const headers = [
  { title: 'Username', key: 'username' },
  { title: 'Email', key: 'email' },
  { title: 'First Name', key: 'firstName' },
  { title: 'Last Name', key: 'lastName' },
  { title: 'Roles', key: 'roles' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const showSnackbar = (text: string, color: string = 'success') => {
  snackbar.value = {
    show: true,
    text,
    color
  }
}

const fetchUsers = async () => {
  try {
    loading.value = true
    const response = await axios.get<User[]>('http://localhost:8080/api/users', {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    users.value = response.data
  } catch (error) {
    console.error('Error fetching users:', error)
    showSnackbar('Error fetching users', 'error')
  } finally {
    loading.value = false
  }
}

const editItem = (item: User) => {
  editedItem.value = { ...item }
  dialog.value = true
}

const deleteItem = async (item: User) => {
  if (item.username === 'admin') {
    showSnackbar('Cannot delete admin user', 'error')
    return
  }

  if (item.id === currentUserId.value) {
    showSnackbar('Cannot delete your own account', 'error')
    return
  }

  if (confirm('Are you sure you want to delete this user?')) {
    try {
      await axios.delete(`http://localhost:8080/api/users/${item.id}`, {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      })
      await fetchUsers()
      showSnackbar('User deleted successfully')
    } catch (error) {
      console.error('Error deleting user:', error)
      showSnackbar('Error deleting user', 'error')
    }
  }
}

const saveItem = async (updatedItem: User) => {
  try {
    const response = await axios.put<User>(
      `http://localhost:8080/api/users/${updatedItem.id}`,
      updatedItem,
      {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      }
    )
    const index = users.value.findIndex(u => u.id === updatedItem.id)
    if (index !== -1) {
      users.value[index] = response.data
    }
    dialog.value = false
    showSnackbar('User updated successfully')
  } catch (error) {
    console.error('Error updating user:', error)
    showSnackbar('Error updating user', 'error')
  }
}

const handleUserCreated = (newUser: User) => {
  users.value.push(newUser)
  showSnackbar('User created successfully')
}

const close = () => {
  dialog.value = false
  editedItem.value = null
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.users-view {
  padding: 20px;
}
</style> 