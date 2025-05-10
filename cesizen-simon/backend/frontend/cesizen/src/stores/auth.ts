import { defineStore } from 'pinia'
import authService from '@/services/authService'

interface User {
  id: number
  username: string
  email: string
  firstName: string
  lastName: string
  roles: string[]
}

interface AuthState {
  user: User | null
  token: string | null
}

interface RegisterRequest {
  username: string
  email: string
  password: string
  firstName: string
  lastName: string
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    token: localStorage.getItem('token')
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    currentUser: (state) => state.user
  },

  actions: {
    async login(username: string, password: string) {
      try {
        const response = await authService.login({ username, password })
        this.token = response.accessToken
        this.user = response
        localStorage.setItem('user', JSON.stringify(response))
        localStorage.setItem('token', response.accessToken)
        return response
      } catch (error) {
        throw error
      }
    },

    async register(data: RegisterRequest) {
      try {
        await authService.register(data)
        // On ne stocke pas le token ni l'utilisateur car l'inscription ne renvoie pas de token
        return true
      } catch (error) {
        throw error
      }
    },

    async fetchProfile() {
      const response = await authService.getProfile()
      this.user = response
      localStorage.setItem('user', JSON.stringify(response))
    },

    async updateProfile(data: Partial<User>) {
      const response = await authService.updateProfile(data)
      this.user = response
      localStorage.setItem('user', JSON.stringify(response))
    },

    async updatePassword(currentPassword: string, newPassword: string) {
      await authService.updatePassword({ currentPassword, newPassword })
    },

    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    }
  }
}) 