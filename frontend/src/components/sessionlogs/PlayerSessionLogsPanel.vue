<script setup lang="ts">
import SessionLogDetailModal from '@/components/sessionlogs/SessionLogDetailModal.vue'
import { useSessionLogStore } from '@/stores/sessionLog'
import { formatDateStr } from '@/utils/dateUtil'
import { isAxiosError } from 'axios'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref } from 'vue'

const props = defineProps<{
  campaignId: number
}>()

const sessionLogStore = useSessionLogStore()
const { sessionLogs, activeSessionLog } = storeToRefs(sessionLogStore)

const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

const searchQuery = ref('')
const sortOrder = ref<'newest' | 'oldest'>('newest')

const detailOpen = ref(false)
const isLoadingDetail = ref(false)

const filteredLogs = computed(() => {
  let result = sessionLogs.value

  if (searchQuery.value.trim() !== '') {
    const query = searchQuery.value.trim().toLowerCase()
    result = result.filter((log) => log.title.toLowerCase().includes(query))
  }

  result = [...result].sort((a, b) => {
    const dateA = new Date(a.createdOn).getTime()
    const dateB = new Date(b.createdOn).getTime()
    return sortOrder.value === 'newest' ? dateB - dateA : dateA - dateB
  })

  return result
})

async function loadSessionLogs() {
  isLoading.value = true
  errorMessage.value = null
  try {
    await sessionLogStore.fetchSessionLogs(props.campaignId)
  } catch (err: unknown) {
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load session logs')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
}

async function openDetailModal(logId: number) {
  detailOpen.value = true
  isLoadingDetail.value = true
  errorMessage.value = null
  try {
    await sessionLogStore.fetchSessionLog(props.campaignId, logId)
  } catch (err: unknown) {
    detailOpen.value = false
    errorMessage.value = isAxiosError(err)
      ? (err.response?.data?.message ?? 'Failed to load session log')
      : 'An unexpected error occurred. Please try again.'
  } finally {
    isLoadingDetail.value = false
  }
}

function closeDetail() {
  detailOpen.value = false
}

onMounted(loadSessionLogs)
</script>

<template>
  <div class="flex flex-col gap-4">
    <div class="flex flex-wrap items-center gap-4">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Search by title..."
        class="rounded-lg border-2 border-white bg-neutral-400 p-2 placeholder:text-neutral-700"
      />
      <select v-model="sortOrder" class="rounded-lg border-2 border-white bg-neutral-400 p-2">
        <option value="newest">Newest first</option>
        <option value="oldest">Oldest first</option>
      </select>
    </div>

    <p v-if="isLoading" class="text-neutral-400">Loading session logs...</p>
    <p v-else-if="errorMessage" class="text-red-500">{{ errorMessage }}</p>
    <p v-else-if="sessionLogs.length === 0" class="text-neutral-400">No session logs yet.</p>
    <p v-else-if="filteredLogs.length === 0" class="text-neutral-400">
      No session logs match your search.
    </p>

    <div v-else class="overflow-x-auto rounded-xl bg-white">
      <table class="w-full min-w-[28rem] text-left">
        <thead>
          <tr class="border-b border-neutral-200 text-sm text-neutral-600">
            <th class="px-4 py-3 font-medium">Session Title</th>
            <th class="px-4 py-3 font-medium">Date</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="log in filteredLogs"
            :key="log.id"
            class="border-b border-neutral-100 last:border-b-0"
          >
            <td class="px-4 py-3">
              <button
                type="button"
                class="text-left font-medium text-black hover:text-purple-700"
                @click="openDetailModal(log.id)"
              >
                {{ log.title.trim() || 'Untitled Session' }}
              </button>
            </td>
            <td class="px-4 py-3 text-neutral-700">{{ formatDateStr(log.createdOn) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <SessionLogDetailModal
      v-if="detailOpen && activeSessionLog && !isLoadingDetail"
      :log="activeSessionLog"
      @close="closeDetail"
    />

    <p v-if="detailOpen && isLoadingDetail" class="text-neutral-400">Loading session log...</p>
  </div>
</template>
