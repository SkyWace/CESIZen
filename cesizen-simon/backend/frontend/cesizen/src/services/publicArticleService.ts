import axios from 'axios'

export interface Article {
  id: number
  title: string
  description: string
  content: string
  imageUrl: string
  createdAt: string
  updatedAt: string
  approvalStatus: string
}

export interface ArticleResponse {
  content: Article[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
  numberOfElements: number
}

const publicApi = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

export const publicArticleService = {
  async getAll(page: number = 0, size: number = 10): Promise<ArticleResponse> {
    const response = await publicApi.get('/articles', {
      params: { page, size }
    })
    return response.data
  },

  async getById(id: number): Promise<Article> {
    const response = await publicApi.get(`/articles/${id}`)
    return response.data
  },

  async search(query: string, page: number = 0, size: number = 10): Promise<ArticleResponse> {
    const response = await publicApi.get('/articles/search', {
      params: { q: query, page, size }
    })
    return response.data
  }
} 