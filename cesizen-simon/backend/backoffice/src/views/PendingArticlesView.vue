<template>
  <div class="container mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold mb-6">Pending Articles</h1>

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
        v-for="article in pendingArticles"
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
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              color="success"
              variant="text"
              @click="approveArticle(article.id)"
              :loading="loading"
            >
              Approve
            </v-btn>
            <v-btn
              color="error"
              variant="text"
              @click="rejectArticle(article.id)"
              :loading="loading"
            >
              Reject
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
import { onMounted, ref } from 'vue';
import { useArticleStore } from '../stores/articleStore';
import { storeToRefs } from 'pinia';

const articleStore = useArticleStore();
const { pendingArticles, loading, error } = storeToRefs(articleStore);

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
    await articleStore.fetchPendingArticles();
  } catch (err) {
    console.error('Failed to fetch pending articles:', err);
  }
});

const approveArticle = async (id: number | undefined) => {
  if (!id) return;
  try {
    await articleStore.approveArticle(id);
    showSuccess('Article approved successfully');
  } catch (err) {
    console.error('Failed to approve article:', err);
    showError('Failed to approve article');
  }
};

const rejectArticle = async (id: number | undefined) => {
  if (!id) return;
  try {
    await articleStore.rejectArticle(id);
    showSuccess('Article rejected successfully');
  } catch (err) {
    console.error('Failed to reject article:', err);
    showError('Failed to reject article');
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