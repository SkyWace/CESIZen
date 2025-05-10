import axios from '@/services/axios'

const API_URL = ''

export interface Exercise {
  idExercice: number
  nom: string
  description: string
  type: string
  dureeInspiration: number
  dureeExpiration: number
  dureeApnee: number
  nombreCycles: number
  averageRating?: number
  creatorId?: number
  isOfficial?: boolean
  official?: boolean
}

export interface ExerciseStats {
  totalExercises: number
  totalMinutes: number
  currentStreak: number
  bestStreak: number
}

export interface BreathingExerciseHistory {
  id: number
  exerciseId: number
  exerciseName: string
  duration: number
  completedAt: string
}

const exerciseService = {
  async getOfficialExercises(): Promise<Exercise[]> {
    const response = await axios.get(`${API_URL}/exercises`)
    return response.data.filter((exercise: Exercise) => exercise.official)
  },

  async getPersonalExercises(): Promise<Exercise[]> {
    const response = await axios.get(`${API_URL}/exercises/user`)
    return response.data
  },

  async getExerciseStats(): Promise<ExerciseStats> {
    const response = await axios.get(`${API_URL}/exercises/stats`)
    return response.data
  },

  async getBreathingHistory(): Promise<BreathingExerciseHistory[]> {
    const response = await axios.get(`${API_URL}/breathing-history`)
    return response.data
  },

  async createExercise(exercise: Partial<Exercise>): Promise<Exercise> {
    const response = await axios.post(`${API_URL}/exercises`, exercise)
    return response.data
  },

  async updateExercise(id: number, exercise: Partial<Exercise>): Promise<Exercise> {
    const response = await axios.put(`${API_URL}/exercises/${id}`, exercise)
    return response.data
  },

  async deleteExercise(id: number): Promise<void> {
    await axios.delete(`${API_URL}/exercises/${id}`)
  }
}

export default exerciseService 