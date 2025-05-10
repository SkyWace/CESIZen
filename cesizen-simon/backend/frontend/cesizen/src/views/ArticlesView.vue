<template>
  <div class="articles">
    <div class="d-flex align-center mb-6">
      <h1 class="text-h3 font-weight-bold text-primary">
        Articles
      </h1>
    </div>

    <v-alert
      v-if="error"
      type="error"
      variant="tonal"
      class="mb-4"
    >
      {{ error }}
    </v-alert>

    <v-progress-circular
      v-if="loading"
      indeterminate
      color="primary"
      class="d-flex mx-auto my-8"
    ></v-progress-circular>

    <template v-else>
      <v-row>
        <v-col
          v-for="article in articles"
          :key="article.id"
          cols="12"
          md="6"
          lg="4"
        >
          <v-card
            class="h-100 article-card"
            :class="{ 'on-hover': hover === article.id }"
            @mouseenter="hover = article.id"
            @mouseleave="hover = null"
          >
            <v-img
              :src="`http://localhost:8080${article.imageUrl}`"
              height="200"
              cover
              class="align-end"
            >
              <template v-slot:placeholder>
                <v-row
                  class="fill-height ma-0"
                  align="center"
                  justify="center"
                >
                  <v-progress-circular
                    indeterminate
                    color="primary"
                  ></v-progress-circular>
                </v-row>
              </template>
              <div class="gradient-overlay"></div>
              <v-card-title class="text-white text-h5 font-weight-bold article-title">
                {{ article.title }}
              </v-card-title>
            </v-img>

            <v-card-text class="pt-6">
              <p class="text-body-1 text-medium-emphasis mb-4 article-description">
                {{ article.description }}
              </p>
              <div class="d-flex align-center">
                <v-chip
                  color="primary"
                  variant="tonal"
                  class="text-none"
                >
                  <v-icon start icon="mdi-calendar" size="small"></v-icon>
                  {{ formatDate(article.createdAt) }}
                </v-chip>
              </div>
            </v-card-text>

            <v-card-actions class="pt-0">
              <v-btn
                color="primary"
                variant="text"
                :to="`/articles/${article.id}`"
                class="text-none font-weight-medium"
                block
              >
                Lire l'article
                <v-icon end icon="mdi-arrow-right" class="ml-2"></v-icon>
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>

      <div class="d-flex justify-center mt-6">
        <v-pagination
          v-model="currentPage"
          :length="totalPages"
          :total-visible="7"
          rounded="circle"
        ></v-pagination>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { publicArticleService, type Article, type ArticleResponse } from '@/services/publicArticleService'

const hover = ref<number | null>(null)
const articles = ref<Article[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const currentPage = ref(0)
const totalPages = ref(0)
const itemsPerPage = 10

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  }).format(date)
}

const loadArticles = async () => {
  try {
    loading.value = true
    error.value = null
    const response = await publicArticleService.getAll(currentPage.value, itemsPerPage)
    articles.value = response.content
    totalPages.value = response.totalPages
  } catch (err) {
    error.value = 'Erreur lors du chargement des articles'
    console.error('Erreur:', err)
  } finally {
    loading.value = false
  }
}

watch(currentPage, () => {
  loadArticles()
})

onMounted(() => {
  loadArticles()
})
</script>

<style scoped>
.articles {
  max-width: 1200px;
  margin: 0 auto;
}

.v-card {
  transition: transform 0.2s ease-in-out;
}

.v-card.on-hover {
  transform: translateY(-4px);
}

.gradient-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 70%;
  background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
}

.article-card {
  overflow: hidden;
}

.article-description {
  font-size: 1rem;
  line-height: 1.5;
  color: rgba(0, 0, 0, 0.87);
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.article-title {
  position: relative;
  z-index: 2;
  padding: 16px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
  font-size: 1.5rem !important;
  line-height: 1.4;
  margin-bottom: 8px;
}
</style> 