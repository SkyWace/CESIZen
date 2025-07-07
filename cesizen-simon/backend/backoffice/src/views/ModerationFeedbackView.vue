<template>
  <div class="moderation-feedback-view">
    <h1>Modération des avis d'exercice</h1>
    <div v-if="loadingFeedbacks">Chargement...</div>
    <div v-else-if="errorFeedbacks">{{ errorFeedbacks }}</div>
    <div v-else class="feedback-cards">
      <div v-for="fb in feedbacks" :key="fb.id" class="feedback-card">
        <div class="feedback-header">
          <strong>{{ fb.userName }}</strong> — <span class="feedback-rating">{{ fb.rating }}★</span>
          <span class="feedback-date">{{ new Date(fb.createdAt).toLocaleString() }}</span>
        </div>
        <div class="feedback-comment">{{ fb.comment }}</div>
        <div class="feedback-actions">
          <button @click="openExerciseModal(fb.exerciseId)" title="Voir l'exercice" class="info-btn">ℹ️</button>
          <button @click="deleteFeedback(fb)" title="Supprimer" class="delete-btn">🗑️</button>
        </div>
      </div>
    </div>
    <div v-if="showExerciseModal" class="modal-overlay" @click.self="closeExerciseModal">
      <div class="exercise-modal">
        <h2>Exercice #{{ exerciseDetails?.idExercice }}</h2>
        <div v-if="exerciseDetails">
          <p><strong>Nom :</strong> {{ exerciseDetails.nom }}</p>
          <p><strong>Description :</strong> {{ exerciseDetails.description }}</p>
          <p><strong>Type :</strong> {{ exerciseDetails.type }}</p>
          <p><strong>Inspiration :</strong> {{ exerciseDetails.dureeInspiration }}s</p>
          <p><strong>Expiration :</strong> {{ exerciseDetails.dureeExpiration }}s</p>
          <p><strong>Apnée :</strong> {{ exerciseDetails.dureeApnee }}s</p>
          <p><strong>Nombre de cycles :</strong> {{ exerciseDetails.nombreCycles }}</p>
          <p><strong>Officiel :</strong> {{ exerciseDetails.official ? 'Oui' : 'Non' }}</p>
        </div>
        <div v-else>Chargement...</div>
        <button @click="closeExerciseModal" class="close-btn">Fermer</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';

interface Feedback {
  id: number;
  exerciseId: number;
  userId: number;
  userName: string;
  comment: string;
  rating: number;
  createdAt: string;
}

interface ExerciseDetails {
  idExercice: number;
  nom: string;
  description: string;
  type: string;
  dureeInspiration: number;
  dureeExpiration: number;
  dureeApnee: number;
  nombreCycles: number;
  official?: boolean;
}

const feedbacks = ref<Feedback[]>([]);
const loadingFeedbacks = ref(false);
const errorFeedbacks = ref('');

const showExerciseModal = ref(false);
const exerciseDetails = ref<ExerciseDetails|null>(null);

async function fetchFeedbacks() {
  loadingFeedbacks.value = true;
  errorFeedbacks.value = '';
  try {
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
    const response = await axios.get(apiUrl+'/feedback', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    });
    console.log('Feedback data from API:', response.data);
    feedbacks.value = response.data.map((fb: any) => {
      console.log('Processing feedback:', fb);
      return {
        ...fb,
        exerciseId: fb.exerciseId || fb.idExercice || fb.exercise?.idExercice
      };
    });
  } catch (e) {
    errorFeedbacks.value = 'Erreur lors du chargement des avis.';
  } finally {
    loadingFeedbacks.value = false;
  }
}

async function deleteFeedback(feedback: Feedback) {
  if (!confirm('Supprimer cet avis ?')) return;
  try {
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
    await axios.delete(`${apiUrl}/exercises/${feedback.exerciseId}/feedback/${feedback.id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    });
    await fetchFeedbacks();
  } catch (e) {
    alert('Erreur lors de la suppression.');
  }
}

async function openExerciseModal(exerciseId: number) {
  if (!exerciseId) {
    alert('ID d\'exercice invalide');
    return;
  }
  showExerciseModal.value = true;
  exerciseDetails.value = null;
  try {
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
    const response = await axios.get(apiUrl+`/exercises/${exerciseId}`);
    exerciseDetails.value = response.data;
  } catch (e) {
    alert('Erreur lors du chargement des détails de l\'exercice');
    exerciseDetails.value = null;
  }
}

function closeExerciseModal() {
  showExerciseModal.value = false;
  exerciseDetails.value = null;
}

onMounted(fetchFeedbacks);
</script>

<style scoped>
.moderation-feedback-view {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem 1rem;
}
.feedback-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 1.2rem;
  margin-top: 1.5rem;
}
.feedback-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.07);
  padding: 1.1rem 1.3rem 0.9rem 1.3rem;
  min-width: 270px;
  max-width: 340px;
  flex: 1 1 270px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.feedback-header {
  font-size: 1.08em;
  margin-bottom: 0.5em;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.feedback-rating {
  color: #f7b500;
  font-weight: 600;
  margin-left: 6px;
}
.feedback-date {
  font-size: 0.92em;
  color: #888;
  margin-left: 0.5em;
}
.feedback-comment {
  margin: 0.7em 0 0.7em 0;
  color: #333;
  font-size: 1.04em;
}
.feedback-actions {
  display: flex;
  gap: 10px;
  margin-top: 0.5em;
}
.info-btn {
  background: #e3f2fd;
  color: #1976d2;
  border: none;
  border-radius: 6px;
  padding: 4px 10px;
  cursor: pointer;
  font-size: 1.1em;
}
.delete-btn {
  background: #ffebee;
  color: #d32f2f;
  border: none;
  border-radius: 6px;
  padding: 4px 10px;
  cursor: pointer;
  font-size: 1.1em;
}
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.exercise-modal {
  background: #fff;
  border-radius: 12px;
  padding: 2rem 2.2rem 1.5rem 2.2rem;
  min-width: 320px;
  max-width: 95vw;
  box-shadow: 0 4px 24px rgba(0,0,0,0.13);
  position: relative;
}
.close-btn {
  margin-top: 1.2em;
  background: #e3f2fd;
  color: #1976d2;
  border: none;
  border-radius: 6px;
  padding: 6px 18px;
  cursor: pointer;
  font-size: 1em;
}
</style> 