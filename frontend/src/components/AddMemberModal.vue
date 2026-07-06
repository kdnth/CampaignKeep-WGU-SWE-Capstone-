<script setup lang="ts">
import { ref } from 'vue'
import { isAxiosError } from 'axios'
import { useCampaignStore } from '@/stores/campaign'
import BaseButton from '@/components/BaseButton.vue'
import BaseInput from '@/components/BaseInput.vue'

const props = defineProps<{
  campaignId: number
}>()

const emit = defineEmits<{
  close: []
}>()

const campaignStore = useCampaignStore()

const identifier = ref('')
const role = ref<'master' | 'player'>('player')
const errorMessage = ref<string | null>(null)
const isLoading = ref(false)

async function handleSubmit() {
  errorMessage.value = null
  isLoading.value = true

  try {
    await campaignStore.addMember(props.campaignId, {
      identifier: identifier.value,
      role: role.value,
    })
    emit('close')
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to add member')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <Teleport to="body">
    <div
      class="fixed inset-0 bg-black/70 flex items-center justify-center z-50"
      @click.self="emit('close')"
    >
      <div class="bg-white rounded-xl p-6 w-full max-w-sm flex flex-col gap-4">
        <h3 class="text-xl">Add Member</h3>

        <form @submit.prevent="handleSubmit" class="flex flex-col gap-4">
          <div class="flex flex-col gap-1">
            <label for="identifier">Username or email</label>
            <BaseInput id="identifier" v-model="identifier" required />
          </div>

          <div class="flex flex-col gap-1">
            <label for="role">Role</label>
            <select id="role" v-model="role" class="rounded-lg px-2 py-2">
              <option value="player">Player</option>
              <option value="master">Master</option>
            </select>
          </div>

          <p v-if="errorMessage" class="text-red-600 text-sm">{{ errorMessage }}</p>

          <div class="flex justify-end gap-2">
            <BaseButton type="button" variant="danger" @click="emit('close')">Cancel</BaseButton>
            <BaseButton type="submit" variant="primary" :loading="isLoading">
              {{ isLoading ? 'Adding...' : 'Add Member' }}
            </BaseButton>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>
