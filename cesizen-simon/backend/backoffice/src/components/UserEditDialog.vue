<template>
  <v-dialog v-model="dialog" max-width="500px">
    <v-card>
      <v-card-title>
        <span class="text-h5">Edit User</span>
      </v-card-title>

      <v-card-text>
        <v-form ref="form" v-model="valid" @submit.prevent="save">
          <v-text-field
            v-model="editedItem.username"
            label="Username"
            :rules="[v => !!v || 'Username is required']"
            required
            :disabled="editedItem.username === 'admin'"
          ></v-text-field>

          <v-text-field
            v-model="editedItem.email"
            label="Email"
            :rules="[
              v => !!v || 'Email is required',
              v => /.+@.+\..+/.test(v) || 'Email must be valid'
            ]"
            required
          ></v-text-field>

          <v-text-field
            v-model="editedItem.firstName"
            label="First Name"
            :rules="[v => !!v || 'First name is required']"
            required
          ></v-text-field>

          <v-text-field
            v-model="editedItem.lastName"
            label="Last Name"
            :rules="[v => !!v || 'Last name is required']"
            required
          ></v-text-field>

          <v-select
            v-model="editedItem.roles"
            :items="roleOptions"
            label="Roles"
            multiple
            chips
            :rules="[v => v.length > 0 || 'At least one role is required']"
            required
            :disabled="editedItem.username === 'admin'"
          ></v-select>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="error" variant="text" @click="close">Cancel</v-btn>
        <v-btn color="primary" variant="text" @click="save" :disabled="!valid">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: Boolean,
  editedItem: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update:modelValue', 'save', 'close'])

const dialog = ref(props.modelValue)
const valid = ref(false)
const form = ref()

const roleOptions = [
  { title: 'Super Admin', value: 'ROLE_SUPER_ADMIN' },
  { title: 'Admin', value: 'ROLE_ADMIN' },
  { title: 'User', value: 'ROLE_USER' }
]

watch(() => props.modelValue, (newValue) => {
  dialog.value = newValue
})

watch(dialog, (newValue) => {
  emit('update:modelValue', newValue)
})

const close = () => {
  emit('close')
}

const save = () => {
  if (form.value.validate()) {
    emit('save', props.editedItem)
  }
}
</script> 