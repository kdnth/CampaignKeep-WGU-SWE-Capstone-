<script setup lang="ts">
import { GiCrenelCrown, BiPersonFill } from 'oh-vue-icons/icons'
import { OhVueIcon, addIcons } from 'oh-vue-icons'
import { formatDateStr } from '@/utils/dateUtil'
import type { CampaignResponse } from '@/stores/campaign'

defineProps<{
  campaign: CampaignResponse
}>()

addIcons(GiCrenelCrown, BiPersonFill)
</script>

<template>
  <section class="relative rounded-2xl border-2 border-white bg-neutral-900 p-6 text-white">
    <div class="flex flex-wrap items-start justify-between gap-4">
      <div class="min-w-0 flex-1">
        <div class="mb-2 flex items-center gap-2">
          <OhVueIcon
            :name="campaign.callerRole === 'master' ? 'gi-crenel-crown' : 'bi-person-fill'"
            class="shrink-0 text-purple-400"
            scale="1.2"
          />
          <span class="text-sm uppercase tracking-wide text-purple-400">
            {{ campaign.callerRole === 'master' ? 'Game Master' : 'Player' }}
          </span>
        </div>
        <h1 class="text-3xl font-semibold">{{ campaign.title }}</h1>
        <p class="mt-1 text-sm text-neutral-400">
          Created {{ formatDateStr(campaign.createdOn) }}
          <span v-if="campaign.finishedOn">
            · Finished {{ formatDateStr(campaign.finishedOn) }}
          </span>
        </p>
        <p v-if="campaign.description" class="mt-4 text-neutral-300">
          {{ campaign.description }}
        </p>
      </div>
    </div>

    <p
      v-if="campaign.finishedOn"
      class="pointer-events-none absolute inset-0 flex items-center justify-center text-6xl font-extralight text-neutral-500/40"
    >
      FINISHED
    </p>
  </section>
</template>
