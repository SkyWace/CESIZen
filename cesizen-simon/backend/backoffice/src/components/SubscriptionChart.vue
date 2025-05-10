<template>
  <div class="subscription-chart">
    <Line
      :data="chartData"
      :options="chartOptions"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
)

interface SubscriptionData {
  date: string
  count: number
}

const authStore = useAuthStore()
const subscriptionData = ref<SubscriptionData[]>([])

const chartData = ref({
  labels: [] as string[],
  datasets: [
    {
      label: 'New Subscriptions',
      data: [] as number[],
      borderColor: '#1976D2',
      backgroundColor: 'rgba(25, 118, 210, 0.1)',
      tension: 0.4,
      fill: true
    }
  ]
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top' as const
    },
    title: {
      display: true,
      text: 'User Subscriptions Over Time'
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        stepSize: 1
      }
    }
  }
}

const fetchSubscriptionData = async () => {
  try {
    const response = await axios.get<SubscriptionData[]>('http://localhost:8080/api/users/subscriptions/timeline', {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    subscriptionData.value = response.data

    // Update chart data
    chartData.value.labels = subscriptionData.value.map(item => {
      const date = new Date(item.date)
      return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
    })
    chartData.value.datasets[0].data = subscriptionData.value.map(item => item.count)
  } catch (error) {
    console.error('Error fetching subscription data:', error)
  }
}

onMounted(() => {
  fetchSubscriptionData()
})
</script>

<style scoped>
.subscription-chart {
  height: 300px;
  width: 100%;
}
</style> 