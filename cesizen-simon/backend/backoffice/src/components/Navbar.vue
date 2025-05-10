<template>
  <v-navigation-drawer app color="primary" class="sidebar-nav" permanent>
    <v-list dense>
      <v-list-item
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        router
        exact
        active-class="v-list-item--active"
      >
        <v-icon :icon="item.icon" class="mr-2" />
        <span>{{ item.title }}</span>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { VNavigationDrawer, VList, VListItem, VListItemTitle, VIcon } from 'vuetify/components'

const menuItems = ref([
  { 
    title: 'Dashboard', 
    path: '/', 
    icon: 'mdi-view-dashboard'
  },
  { 
    title: 'Users', 
    path: '/users', 
    icon: 'mdi-account-group'
  },
  { 
    title: 'Breathing Exercises', 
    path: '/breathing-exercises', 
    icon: 'mdi-lungs'
  },
  { 
    title: 'Articles', 
    path: '/articles', 
    icon: 'mdi-newspaper'
  },
  {
    title: 'Modération avis',
    path: '/moderation-feedback',
    icon: 'mdi-comment-alert'
  }
])
</script>

<style scoped>
.sidebar-nav {
  width: 220px;
  min-width: 180px;
  max-width: 260px;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
}
.v-list-item--active {
  background-color: rgba(255, 255, 255, 0.12);
}
.v-list-item {
  text-transform: none;
  letter-spacing: 0.5px;
}
a {
  text-decoration: none;
}
</style> 