import api from './api'

export interface Article {
  id: number
  title: string
  excerpt: string
  content: string
  date: string
  readTime: number
  image: string
}

export const articleService = {
  async getAll(): Promise<Article[]> {
    const response = await api.get('/articles')
    return response.data
  },

  async getById(id: number): Promise<Article> {
    const response = await api.get(`/articles/${id}`)
    return response.data
  },

  async search(query: string): Promise<Article[]> {
    const response = await api.get('/articles/search', {
      params: { q: query }
    })
    return response.data
  }
} 