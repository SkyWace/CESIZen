<template>
  <v-form @submit.prevent="handleSubmit" v-model="isFormValid" class="max-w-3xl mx-auto">
    <v-card class="pa-6">
      <v-card-title class="text-h5 mb-4">
        {{ props.articleId ? 'Edit Article' : 'Create New Article' }}
      </v-card-title>

      <v-card-text>
        <v-text-field
          v-model="form.title"
          label="Title"
          :rules="[v => !!v || 'Title is required']"
          required
          variant="outlined"
          class="mb-4"
        ></v-text-field>

        <v-textarea
          v-model="form.description"
          label="Description"
          :rules="[v => !!v || 'Description is required']"
          required
          variant="outlined"
          rows="3"
          class="mb-4"
        ></v-textarea>

        <v-textarea
          v-model="form.content"
          label="Content"
          :rules="[v => !!v || 'Content is required']"
          required
          variant="outlined"
          rows="10"
          class="mb-4"
        ></v-textarea>

        <v-file-input
          v-model="selectedImage"
          label="Article Image"
          accept="image/*"
          variant="outlined"
          prepend-icon="mdi-camera"
          :show-size="1000"
          @change="handleImageChange"
          class="mb-4"
        >
          <template v-slot:selection="{ fileNames }">
            <template v-for="(fileName) in fileNames" :key="fileName">
              <v-chip
                size="small"
                label
                color="primary"
                class="me-2"
              >
                {{ fileName }}
              </v-chip>
            </template>
          </template>
        </v-file-input>

        <div v-if="previewUrl || form.imageUrl" class="mb-4">
          <p class="text-subtitle-2 mb-2">Image Preview</p>
          <v-img
            :src="previewUrl || getImageUrl(form.imageUrl)"
            max-height="200"
            cover
            class="rounded-lg"
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
        </div>

        <v-chip
          v-if="form.approvalStatus"
          :color="getStatusColor(form.approvalStatus)"
          class="mt-2"
        >
          {{ form.approvalStatus }}
        </v-chip>
      </v-card-text>

      <v-card-actions class="pt-4">
        <v-spacer></v-spacer>
        <v-btn
          color="grey"
          variant="text"
          @click="$router.back()"
          :disabled="loading"
        >
          Cancel
        </v-btn>
        <v-btn
          color="primary"
          type="submit"
          :loading="loading"
          :disabled="!isFormValid"
        >
          {{ loading ? 'Saving...' : 'Save Article' }}
        </v-btn>
      </v-card-actions>
    </v-card>

    <v-snackbar
      v-model="showSnackbar"
      :color="snackbarColor"
      timeout="3000"
    >
      {{ snackbarText }}
    </v-snackbar>
  </v-form>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useArticleStore } from '../stores/articleStore';
import { useAuthStore } from '../stores/auth';

type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

interface Article {
  id?: number;
  title: string;
  description: string;
  content: string;
  imageUrl?: string;
  createdAt?: string;
  updatedAt?: string;
  approvalStatus: ApprovalStatus;
}

const props = defineProps<{
  articleId?: number;
}>();

const router = useRouter();
const articleStore = useArticleStore();
const authStore = useAuthStore();
const loading = ref(false);
const selectedImage = ref<File | null>(null);
const previewUrl = ref<string | null>(null);
const isFormValid = ref(false);
const showSnackbar = ref(false);
const snackbarText = ref('');
const snackbarColor = ref('success');

const isSuperAdmin = computed(() => authStore.isSuperAdmin);

const statusOptions = [
  { title: 'Pending', value: 'PENDING' as ApprovalStatus },
  { title: 'Approved', value: 'APPROVED' as ApprovalStatus },
  { title: 'Rejected', value: 'REJECTED' as ApprovalStatus }
];

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const form = reactive<Article>({
  title: '',
  description: '',
  content: '',
  approvalStatus: 'PENDING'
});

const getStatusColor = (status: ApprovalStatus | undefined) => {
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
  if (props.articleId) {
    try {
      const article = await articleStore.fetchArticle(props.articleId);
      Object.assign(form, {
        title: article.title,
        description: article.description,
        content: article.content,
        imageUrl: article.imageUrl,
        approvalStatus: article.approvalStatus || 'PENDING'
      });
      if (article.imageUrl) {
        previewUrl.value = getImageUrl(article.imageUrl);
      }
    } catch (err) {
      console.error('Failed to fetch article:', err);
      showError('Failed to load article data');
    }
  }
});

const handleImageChange = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    selectedImage.value = input.files[0];
    previewUrl.value = URL.createObjectURL(input.files[0]);
  } else {
    // If no new image is selected, keep the existing image
    previewUrl.value = form.imageUrl ? getImageUrl(form.imageUrl) : null;
  }
};

const getImageUrl = (imageUrl: string | undefined) => {
  if (!imageUrl) return '';
  if (imageUrl.startsWith('http')) {
    return imageUrl;
  }
  return `${API_URL}${imageUrl}`;
};

const handleSubmit = async () => {
  if (!isFormValid.value) return;
  
  loading.value = true;
  try {
    if (props.articleId) {
      // Set status to PENDING when editing
      form.approvalStatus = 'PENDING';
      // Only send image if a new one was selected
      const imageToSend = selectedImage.value || null;
      await articleStore.updateArticle(props.articleId, form, imageToSend);
      showSuccess('Article updated successfully and sent for approval');
    } else {
      await articleStore.createArticle(form, selectedImage.value);
      showSuccess('Article created successfully');
    }
    router.push('/articles');
  } catch (err) {
    console.error('Failed to save article:', err);
    showError('Failed to save article');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.v-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.v-text-field,
.v-textarea {
  margin-bottom: 16px;
}

.v-file-input {
  margin-bottom: 16px;
}
</style> 