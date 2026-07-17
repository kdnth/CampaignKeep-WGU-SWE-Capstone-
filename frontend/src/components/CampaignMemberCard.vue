<script setup lang="ts">
import { GiCrenelCrown } from 'oh-vue-icons/icons'
import { BiPersonFill } from 'oh-vue-icons/icons'
import { OhVueIcon, addIcons } from 'oh-vue-icons'
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useCampaignStore, type CampaignMemberResponse } from '@/stores/campaign'
import { storeToRefs } from 'pinia'

addIcons(GiCrenelCrown, BiPersonFill)

const props = defineProps<{
  member: CampaignMemberResponse
}>()

const emit = defineEmits<{
  remove: [userId: number]
  changeRole: [userId: number, role: 'master' | 'player']
}>()

const authStore = useAuthStore()
const campaignStore = useCampaignStore()
const { members } = storeToRefs(campaignStore)

const viewerMembership = computed(() => members.value.find((m) => m.userId === authStore.userId))

const isSelf = computed(() => props.member.userId === authStore.userId)

const masterCount = computed(() => members.value.filter((m) => m.role === 'master').length)

const isLastMaster = computed(() => {
  if (props.member.role !== 'master') return false
  return masterCount.value === 1
})

const canRemove = computed(() => {
  if (isSelf.value) return !isLastMaster.value
  return viewerMembership.value?.role === 'master' && !isLastMaster.value
})

const canChangeRole = computed(() => {
  if (isSelf.value) {
    return props.member.role === 'master' && masterCount.value > 1
  }
  if (viewerMembership.value?.role !== 'master') return false
  if (props.member.role === 'master') return masterCount.value > 1
  return true
})
</script>

<template>
  <div class="flex items-center justify-between bg-surface rounded-lg px-4 py-3">
    <div class="flex items-center gap-3">
      <OhVueIcon
        :name="member.role === 'master' ? 'gi-crenel-crown' : 'bi-person-fill'"
        class="shrink-0 text-fg-muted"
        scale="1.2"
      />
      <span class="text-fg">{{ member.username }}</span>
      <span class="text-xs uppercase text-fg-subtle">{{ member.role }}</span>
    </div>

    <div class="flex gap-2">
      <button
        v-if="canChangeRole"
        class="text-sm text-accent hover:underline"
        @click="emit('changeRole', member.userId, member.role === 'master' ? 'player' : 'master')"
      >
        {{ member.role === 'master' ? 'Demote' : 'Promote' }}
      </button>
      <button
        v-if="canRemove"
        class="text-sm text-danger hover:underline"
        @click="emit('remove', member.userId)"
      >
        {{ isSelf ? 'Leave' : 'Remove' }}
      </button>
    </div>
  </div>
</template>
