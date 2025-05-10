import axiosInstance from './axios'

interface LoginRequest {
  username: string
  password: string
}

interface RegisterRequest {
  username: string
  email: string
  password: string
  firstName: string
  lastName: string
}

interface AuthResponse {
  accessToken: string
  id: number
  username: string
  email: string
  firstName: string
  lastName: string
  roles: string[]
}

interface UpdateProfileRequest {
  username?: string
  email?: string
  firstName?: string
  lastName?: string
}

interface PasswordUpdateRequest {
  currentPassword: string
  newPassword: string
}

const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await axiosInstance.post<AuthResponse>('/auth/signin', credentials)
    if (response.data.accessToken) {
      localStorage.setItem('token', response.data.accessToken)
      localStorage.setItem('user', JSON.stringify(response.data))
    }
    return response.data
  },

  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await axiosInstance.post<AuthResponse>('/auth/signup', data)
    if (response.data.accessToken) {
      localStorage.setItem('token', response.data.accessToken)
      localStorage.setItem('user', JSON.stringify(response.data))
    }
    return response.data
  },

  async getProfile(): Promise<AuthResponse> {
    const response = await axiosInstance.get<AuthResponse>('/users/me')
    return response.data
  },

  async updateProfile(data: UpdateProfileRequest): Promise<AuthResponse> {
    const response = await axiosInstance.put<AuthResponse>('/users/me', data)
    localStorage.setItem('user', JSON.stringify(response.data))
    return response.data
  },

  async updatePassword(data: PasswordUpdateRequest): Promise<AuthResponse> {
    const response = await axiosInstance.put<AuthResponse>('/users/me/password', data)
    return response.data
  },

  logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },

  getCurrentUser() {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      return JSON.parse(userStr)
    }
    return null
  },

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token')
  }
}

export default authService 