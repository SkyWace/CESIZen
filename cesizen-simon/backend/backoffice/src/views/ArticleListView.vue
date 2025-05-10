<template>
  <div class="container mx-auto px-4 py-8">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold">Articles</h1>
      <div class="flex gap-4">
        <v-btn
          v-if="isSuperAdmin"
          color="warning"
          @click="navigateToPending"
          prepend-icon="mdi-clock-outline"
        >
          Pending Articles
        </v-btn>
        <v-btn
          color="primary"
          @click="navigateToCreate"
          prepend-icon="mdi-plus"
        >
          Create New Article
        </v-btn>
      </div>
    </div>

    <v-progress-circular
      v-if="loading"
      indeterminate
      color="primary"
      class="mx-auto d-block my-4"
    ></v-progress-circular>

    <v-alert
      v-else-if="error"
      type="error"
      class="mb-4"
    >
      {{ error }}
    </v-alert>

    <v-row v-else>
      <v-col
        v-for="article in articles"
        :key="article.id"
        cols="12"
        sm="6"
        md="4"
      >
        <v-card>
          <v-img
            v-if="article.imageUrl"
            :src="getImageUrl(article.imageUrl)"
            :alt="article.title"
            height="200"
            cover
            class="bg-grey-lighten-2"
          >
            <template v-slot:placeholder>
              <v-row
                class="fill-height ma-0"
                align="center"
                justify="center"
              >
                <v-progress-circular
                  indeterminate
                  color="grey-lighten-4"
                ></v-progress-circular>
              </v-row>
            </template>
          </v-img>
          <v-card-title>{{ article.title }}</v-card-title>
          <v-card-text>
            <p class="text-truncate">{{ article.description }}</p>
            <p class="text-caption text-grey">
              {{ article.createdAt ? new Date(article.createdAt).toLocaleDateString() : '' }}
            </p>
            <v-chip
              :color="getStatusColor(article.approvalStatus)"
              size="small"
              class="mt-2"
            >
              {{ article.approvalStatus }}
            </v-chip>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              color="primary"
              variant="text"
              @click="navigateToEdit(article.id)"
              :disabled="!isSuperAdmin"
            >
              Edit
            </v-btn>
            <v-btn
              color="error"
              variant="text"
              @click="deleteArticle(article.id)"
              :disabled="!isSuperAdmin"
            >
              Delete
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <v-snackbar
      v-model="showSnackbar"
      :color="snackbarColor"
      timeout="3000"
    >
      {{ snackbarText }}
    </v-snackbar>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useArticleStore } from '../stores/articleStore';
import { useAuthStore } from '../stores/auth';
import { storeToRefs } from 'pinia';

const router = useRouter();
const articleStore = useArticleStore();
const authStore = useAuthStore();
const { articles, loading, error } = storeToRefs(articleStore);

const isSuperAdmin = computed(() => authStore.isSuperAdmin);

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const showSnackbar = ref(false);
const snackbarText = ref('');
const snackbarColor = ref('success');

const getImageUrl = (imageUrl: string) => {
  if (imageUrl.startsWith('http')) {
    return imageUrl;
  }
  return `${API_URL}${imageUrl}`;
};

const getStatusColor = (status: string | undefined) => {
  switch (status) {
    case 'APPROVED':
      return 'success';
    case 'PENDING':
      return 'warning';
    case 'REJECTED':
      return 'error';
    default:
      return 'grey';
  }
};

const showSuccess = (message: string) => {
  snackbarText.value = message;
  snackbarColor.value = 'success';
  showSnackbar.value = true;
};

const showError = (message: string) => {
  snackbarText.value = message;
  snackbarColor.value = 'error';
  showSnackbar.value = true;
};

onMounted(async () => {
  try {
    await articleStore.fetchArticles();
  } catch (err) {
    console.error('Failed to fetch articles:', err);
    showError('Failed to fetch articles');
  }
});

const navigateToCreate = () => {
  router.push('/articles/create');
};

const navigateToEdit = (id: number | undefined) => {
  if (id) {
    router.push(`/articles/edit/${id}`);
  }
};

const navigateToPending = () => {
  router.push('/articles/pending');
};

const deleteArticle = async (id: number | undefined) => {
  if (id && confirm('Are you sure you want to delete this article?')) {
    try {
      await articleStore.deleteArticle(id);
      showSuccess('Article deleted successfully');
    } catch (err) {
      console.error('Failed to delete article:', err);
      showError('Failed to delete article');
    }
  }
};
</script>

<style scoped>
.v-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.v-card-text {
  flex-grow: 1;
}

.v-img {
  background-color: #f5f5f5;
}
</style> 