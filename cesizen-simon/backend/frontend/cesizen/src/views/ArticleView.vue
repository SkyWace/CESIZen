<template>
  <div class="article-view">
    <v-container>
      <v-row justify="center">
        <v-col cols="12" md="8">
          <div v-if="loading" class="d-flex justify-center my-8">
            <v-progress-circular
              indeterminate
              color="primary"
            ></v-progress-circular>
          </div>

          <v-alert
            v-else-if="error"
            type="error"
            variant="tonal"
            class="mb-4"
          >
            {{ error }}
          </v-alert>

          <template v-else>
            <div class="mb-6">
              <v-btn
                color="primary"
                variant="text"
                prepend-icon="mdi-arrow-left"
                to="/articles"
                class="mb-4"
              >
                Retour aux articles
              </v-btn>
            </div>

            <article>
              <h1 class="text-h3 font-weight-bold mb-4">
                {{ article?.title }}
              </h1>

              <div class="d-flex align-center mb-6">
                <v-chip
                  color="primary"
                  variant="tonal"
                  class="text-none"
                >
                  <v-icon start icon="mdi-calendar" size="small"></v-icon>
                  {{ formatDate(article?.createdAt) }}
                </v-chip>
              </div>

              <v-img
                v-if="article?.imageUrl"
              :src="`${apiUrl}${article.imageUrl}`"
                height="400"
                cover
                class="rounded-lg mb-6"
              ></v-img>

              <div class="article-content text-body-1">
                {{ article?.content }}
              </div>
            </article>
          </template>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { publicArticleService, type Article } from '@/services/publicArticleService'

const route = useRoute()
const article = ref<Article | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

const apiUrl = import.meta.env.VITE_API_URL


const formatDate = (dateString?: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  }).format(date)
}

const loadArticle = async () => {
  try {
    loading.value = true
    error.value = null
    const articleId = Number(route.params.id)
    article.value = await publicArticleService.getById(articleId)
  } catch (err) {
    error.value = 'Erreur lors du chargement de l\'article'
    console.error('Erreur:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadArticle()
})
</script>

<style scoped>
.article-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 0;
}

.article-content {
  line-height: 1.8;
  color: rgba(0, 0, 0, 0.87);
  white-space: pre-wrap;
}
</style> 