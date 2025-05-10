<template>
  <div class="container mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold mb-6">Edit Article</h1>
    <ArticleForm :article-id="articleId" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import ArticleForm from '../components/ArticleForm.vue';

const route = useRoute();
const articleId = computed(() => Number(route.params.id));
</script> 