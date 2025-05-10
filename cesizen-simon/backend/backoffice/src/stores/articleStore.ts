import { defineStore } from 'pinia';
import axios from 'axios';

interface Article {
  id?: number;
  title: string;
  description: string;
  content: string;
  imageUrl?: string;
  createdAt?: string;
  updatedAt?: string;
  approvalStatus?: 'PENDING' | 'APPROVED' | 'REJECTED';
}

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export const useArticleStore = defineStore('article', {
  state: () => ({
    articles: [] as Article[],
    pendingArticles: [] as Article[],
    currentArticle: null as Article | null,
    loading: false,
    error: null as string | null,
  }),

  actions: {
    async fetchArticles() {
      this.loading = true;
      this.error = null;
      try {
        console.log('Fetching articles from:', `${API_URL}/api/articles`);
        const response = await axios.get(`${API_URL}/api/articles`);
        console.log('Articles response:', response.data);
        this.articles = response.data.content;
      } catch (err: any) {
        console.error('Error fetching articles:', err);
        this.error = err.response?.data?.message || 'Failed to fetch articles';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async fetchPendingArticles() {
      this.loading = true;
      this.error = null;
      try {
        const response = await axios.get(`${API_URL}/api/articles/pending`);
        this.pendingArticles = response.data;
      } catch (err: any) {
        console.error('Error fetching pending articles:', err);
        this.error = err.response?.data?.message || 'Failed to fetch pending articles';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async fetchArticle(id: number) {
      this.loading = true;
      this.error = null;
      try {
        const response = await axios.get(`${API_URL}/api/articles/${id}`);
        this.currentArticle = response.data;
        return response.data;
      } catch (err: any) {
        console.error('Error fetching article:', err);
        this.error = err.response?.data?.message || 'Failed to fetch article';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async createArticle(article: Article, image: File | null) {
      this.loading = true;
      this.error = null;
      try {
        const formData = new FormData();
        formData.append('article', new Blob([JSON.stringify(article)], {
          type: 'application/json'
        }));
        if (image) {
          formData.append('image', image);
        }

        const response = await axios.post(`${API_URL}/api/articles`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        this.articles.push(response.data);
        return response.data;
      } catch (err: any) {
        console.error('Error creating article:', err);
        this.error = err.response?.data?.message || 'Failed to create article';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async approveArticle(id: number) {
      this.loading = true;
      this.error = null;
      try {
        const response = await axios.post(`${API_URL}/api/articles/${id}/approve`);
        const index = this.articles.findIndex(a => a.id === id);
        if (index !== -1) {
          this.articles[index] = response.data;
        }
        this.pendingArticles = this.pendingArticles.filter(a => a.id !== id);
        return response.data;
      } catch (err: any) {
        console.error('Error approving article:', err);
        this.error = err.response?.data?.message || 'Failed to approve article';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async rejectArticle(id: number) {
      this.loading = true;
      this.error = null;
      try {
        const response = await axios.post(`${API_URL}/api/articles/${id}/reject`);
        const index = this.articles.findIndex(a => a.id === id);
        if (index !== -1) {
          this.articles[index] = response.data;
        }
        this.pendingArticles = this.pendingArticles.filter(a => a.id !== id);
        return response.data;
      } catch (err: any) {
        console.error('Error rejecting article:', err);
        this.error = err.response?.data?.message || 'Failed to reject article';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async updateArticle(id: number, article: Article, image: File | null) {
      this.loading = true;
      this.error = null;
      try {
        const formData = new FormData();
        formData.append('article', new Blob([JSON.stringify(article)], {
          type: 'application/json'
        }));
        if (image) {
          formData.append('image', image);
        }

        const response = await axios.put(`${API_URL}/api/articles/${id}`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        const index = this.articles.findIndex(a => a.id === id);
        if (index !== -1) {
          this.articles[index] = response.data;
        }
        return response.data;
      } catch (err: any) {
        console.error('Error updating article:', err);
        this.error = err.response?.data?.message || 'Failed to update article';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async deleteArticle(id: number) {
      this.loading = true;
      this.error = null;
      try {
        await axios.delete(`${API_URL}/api/articles/${id}`);
        this.articles = this.articles.filter(a => a.id !== id);
        this.pendingArticles = this.pendingArticles.filter(a => a.id !== id);
      } catch (err: any) {
        console.error('Error deleting article:', err);
        this.error = err.response?.data?.message || 'Failed to delete article';
        throw err;
      } finally {
        this.loading = false;
      }
    }
  },
}); 