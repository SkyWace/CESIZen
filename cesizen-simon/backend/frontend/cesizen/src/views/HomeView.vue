<template>
  <div class="home">
    <v-row class="mt-4">
      <v-col cols="12" md="6" class="d-flex flex-column justify-center">
        <h1 class="text-h2 font-weight-bold mb-4 text-primary">
          Bienvenue sur Cesizen
        </h1>
        <p class="text-h6 text-medium-emphasis mb-6">
          Votre compagnon pour une meilleure gestion du stress et de l'anxiété.
          Découvrez des exercices de respiration guidés et suivez votre progression.
        </p>
        <v-alert
          type="info"
          variant="tonal"
          class="mb-6"
          border="start"
        >
          <template v-slot:prepend>
            <v-icon icon="mdi-information" color="info"></v-icon>
          </template>
          Pour pratiquer les exercices de respiration, veuillez utiliser notre application mobile.
        </v-alert>
        <div class="d-flex gap-4">
          <v-btn
            color="primary"
            size="x-large"
            to="/exercises"
            class="text-none font-weight-medium"
            elevation="2"
          >
            <v-icon start icon="mdi-dumbbell" class="mr-2"></v-icon>
            Voir les exercices
          </v-btn>
          <v-btn
            v-if="authStore.isAuthenticated"
            color="secondary"
            size="x-large"
            to="/profile"
            class="text-none font-weight-medium"
            variant="outlined"
          >
            <v-icon start icon="mdi-account" class="mr-2"></v-icon>
            Mon profil
          </v-btn>
          <v-btn
            v-else
            color="secondary"
            size="x-large"
            to="/login"
            class="text-none font-weight-medium"
            variant="outlined"
          >
            <v-icon start icon="mdi-login" class="mr-2"></v-icon>
            Se connecter
          </v-btn>
        </div>
      </v-col>
      <v-col cols="12" md="6" class="d-flex align-center justify-center">
        <v-img
          src="https://images.unsplash.com/photo-1506126613408-eca07ce68773?q=80&w=1000"
          alt="Meditation illustration"
          class="rounded-lg elevation-4"
          height="500"
          cover
          :aspect-ratio="4/3"
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
        </v-img>
      </v-col>
    </v-row>

    <v-row class="mt-16">
      <v-col cols="12">
        <h2 class="text-h4 font-weight-bold text-center mb-8">
          Pourquoi choisir Cesizen ?
        </h2>
      </v-col>
      <v-col
        v-for="feature in features"
        :key="feature.title"
        cols="12"
        md="4"
      >
        <v-card
          class="h-100"
          variant="outlined"
          :class="{ 'on-hover': hover === feature.title }"
          @mouseenter="hover = feature.title"
          @mouseleave="hover = null"
        >
          <v-card-item>
            <template v-slot:prepend>
              <v-avatar
                color="primary"
                size="48"
                class="rounded-lg"
              >
                <v-icon
                  :icon="feature.icon"
                  color="white"
                  size="24"
                ></v-icon>
              </v-avatar>
            </template>
            <v-card-title class="text-h6 font-weight-bold">
              {{ feature.title }}
            </v-card-title>
          </v-card-item>
          <v-card-text class="text-body-1 text-medium-emphasis">
            {{ feature.description }}
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Section Articles -->
    <v-row class="mt-16">
      <v-col cols="12">
        <h2 class="text-h4 font-weight-bold text-center mb-8">
          Articles récents
        </h2>
      </v-col>

      <v-progress-circular
        v-if="loading"
        indeterminate
        color="primary"
        class="d-flex mx-auto my-8"
      ></v-progress-circular>

      <v-alert
        v-if="error"
        type="error"
        variant="tonal"
        class="mb-4"
      >
        {{ error }}
      </v-alert>

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
            :src="`${apiUrl}${article.imageUrl}`"
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
            <v-card-title class="text-white text-h5 font-weight-bold">
              {{ article.title }}
            </v-card-title>
          </v-img>

          <v-card-text class="pt-6">
            <p class="text-body-1 text-medium-emphasis mb-4">
              {{ article.description }}
            </p>
            <div class="d-flex align-center gap-2">
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { publicArticleService, type Article } from '@/services/publicArticleService'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const hover = ref<string | number | null>(null)
const articles = ref<Article[]>([])
const loading = ref(true)
const error = ref('')

const apiUrl = import.meta.env.VITE_API_URL


const features = [
  {
    title: 'Exercices guidés',
    description: 'Des exercices de respiration soigneusement conçus pour vous aider à gérer votre stress et votre anxiété.',
    icon: 'mdi-meditation'
  },
  {
    title: 'Suivi personnalisé',
    description: 'Suivez votre progression et vos statistiques pour rester motivé dans votre pratique quotidienne.',
    icon: 'mdi-chart-line'
  },
  {
    title: 'Accessible partout',
    description: 'Pratiquez vos exercices où que vous soyez grâce à notre application mobile et notre plateforme web.',
    icon: 'mdi-cellphone-link'
  }
]

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  }).format(date)
}

async function loadArticles() {
  try {
    loading.value = true
    error.value = ''
    const response = await publicArticleService.getAll(0, 3) // Récupérer les 3 premiers articles
    articles.value = response.content
  } catch (e: any) {
    error.value = e.message || 'Erreur lors du chargement des articles'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadArticles()
})
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
}

.gap-4 {
  gap: 1rem;
}

.gap-2 {
  gap: 0.5rem;
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
  height: 50%;
  background: linear-gradient(to top, rgba(0,0,0,0.7), transparent);
}

.article-card {
  overflow: hidden;
}
</style> 