import { defineStore } from 'pinia'
import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

interface User {
  id: number
  email: string
  firstName: string
  lastName: string
  roles: string[]
}

interface AuthState {
  user: User | null
  token: string | null
  isAuthenticated: boolean
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => {
    const token = localStorage.getItem('token')
    const userStr = localStorage.getItem('user')
    let user = null

    try {
      if (userStr) {
        user = JSON.parse(userStr)
      }
    } catch (e) {
      console.error('Failed to parse stored user:', e)
    }

    return {
      user,
      token,
      isAuthenticated: !!token && !!user,
    }
  },

  getters: {
    isAdmin: (state) => {
      return state.user?.roles?.includes('ROLE_ADMIN') || state.user?.roles?.includes('ROLE_SUPER_ADMIN') || false
    },
    isSuperAdmin: (state) => {
      return state.user?.roles?.includes('ROLE_SUPER_ADMIN') || false
    },
    fullName: (state) => {
      if (!state.user) return ''
      return `${state.user.firstName} ${state.user.lastName}`
    },
  },

  actions: {
    async login(email: string, password: string) {
      try {
        const response = await axios.post(`${API_URL}/api/auth/signin`, { 
          username: email,  // Using email as username
          password 
        })

        // Extract data from response
        const { accessToken, id, email: userEmail, firstName, lastName, roles } = response.data

        // Create user object from response data
        const user = {
          id,
          email: userEmail,
          firstName,
          lastName,
          roles
        }

        this.token = accessToken
        this.user = user
        this.isAuthenticated = true

        localStorage.setItem('token', accessToken)
        localStorage.setItem('user', JSON.stringify(user))
        axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`

        return true
      } catch (error) {
        console.error('Login failed:', error)
        this.logout()
        throw error
      }
    },

    logout() {
      this.user = null
      this.token = null
      this.isAuthenticated = false
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      delete axios.defaults.headers.common['Authorization']
    },

    async checkAuth() {
      if (!this.token) {
        this.logout()
        return false
      }

      try {
        const response = await axios.get(`${API_URL}/api/users/me`, {
          headers: {
            Authorization: `Bearer ${this.token}`
          }
        })

        // Extract user data from response
        const userData = response.data

        // Create user object with expected structure
        const user = {
          id: userData.id,
          email: userData.email,
          firstName: userData.firstName,
          lastName: userData.lastName,
          roles: Array.from(userData.roles)
        }

        this.user = user
        this.isAuthenticated = true
        localStorage.setItem('user', JSON.stringify(user))
        return true
      } catch (error) {
        console.error('Auth check failed:', error)
        this.logout()
        return false
      }
    },

    // Initialize auth state and axios interceptors
    initialize() {
      // Set up axios interceptor for 401 responses
      axios.interceptors.response.use(
        (response) => response,
        (error) => {
          if (error.response?.status === 401) {
            this.logout()
            window.location.href = '/login'
          }
          return Promise.reject(error)
        }
      )

      // Set initial token if it exists
      if (this.token) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`
      }
    }
  }
}) 
