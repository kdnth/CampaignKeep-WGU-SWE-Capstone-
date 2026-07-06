<script setup lang="ts">
import BaseButton from '@/components/BaseButton.vue'
import { useCampaignStore } from '@/stores/campaign'
import { isAxiosError } from 'axios'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const campaignStore = useCampaignStore()

const router = useRouter()

const title = ref('')
const description = ref('')
const errorMessage = ref<string | null>(null)
const isLoading = ref(false)

const maxTitleChar = 75
const maxDescChar = 3000 // magic numbers! eventually will try to find a way around this with campaignStore

async function handleCreate() {
  errorMessage.value = null
  isLoading.value = true

  try {
    const response = await campaignStore.createCampaign({
      title: title.value,
      description: description.value,
    })

    router.push({
      name: 'campaignDetail',
      params: {
        id: response.id,
      },
    })
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      errorMessage.value = err.response?.data?.message ?? 'Invalid username or password'
    } else {
      errorMessage.value = 'An unexpected error occurred. Please try again.'
    }
  } finally {
    isLoading.value = false
  }
}
</script>
<template>
  <div class="p-20 flex flex-col items-center">
    <h2 class="text-4xl text-white self-start">Create new campaign</h2>
    <div class="border border-white rounded-xl px-8 py-4 my-4 w-full">
      <form @submit.prevent="handleCreate">
        <div class="flex flex-col max-w-3/4 pt-4">
          <label class="text-white" for="title">Title</label>
          <div class="relative block w-full">
            <input
              class="rounded-lg w-full pb-4 pt-2"
              id="title"
              v-model="title"
              type="text"
              placeholder="Campaign title"
              required
            />
            <span class="absolute bottom-1 right-1 text-sm text-neutral-600">
              {{ title.length }}/{{ maxTitleChar }}
            </span>
          </div>
        </div>
        <div class="flex flex-col pt-4">
          <label class="text-white" for="description">Description</label>
          <div class="relative block w-full">
            <textarea
              class="rounded-lg w-full min-h-48 pb-4 pt-2"
              id="description"
              v-model="description"
              type="text"
              placeholder="Campaign description"
            />
            <span class="absolute bottom-2 right-2 text-sm text-neutral-600">
              {{ description.length }}/{{ maxDescChar }}
            </span>
          </div>
        </div>

        <p v-if="errorMessage" class="text-red-600 mx-0.5 pt-2">{{ errorMessage }}</p>

        <div class="flex justify-end gap-2">
          <BaseButton variant="danger" @click="router.push({ name: 'campaigns' })"
            >Cancel</BaseButton
          >
          <BaseButton variant="primary" type="submit" :loading="isLoading">
            {{ isLoading ? 'Creating campaign...' : 'Create Campaign' }}
          </BaseButton>
        </div>
      </form>
    </div>
  </div>
</template>
