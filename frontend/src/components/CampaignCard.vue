<script setup lang="ts">
import { BiPersonFill, GiCrenelCrown } from 'oh-vue-icons/icons'
import { OhVueIcon, addIcons } from 'oh-vue-icons'
import { formatDateStr } from '@/utils/dateUtil'
withDefaults(
  defineProps<{
    title: string
    description: string | null
    role: 'master' | 'player'
    createdOn: string | null
    finishedOn: string | null
  }>(),
  {
    description: null,
    createdOn: null,
  },
)

addIcons(BiPersonFill, GiCrenelCrown)
</script>

<template>
  <div
    class="w-full h-64 flex flex-col bg-white p-4 cursor-pointer rounded-xl overflow-hidden transition-colors hover:border-4 hover:border-purple-600"
  >
    <div class="justify-start grid grid-cols-5">
      <div class="col-span-4 min-w-0 flex flex-col">
        <h2 class="text-xl truncate">{{ title }}</h2>
        <h4 class="text-sm uppercase">created: {{ formatDateStr(createdOn) }}</h4>
        <h4 v-if="finishedOn" class="text-sm uppercase">
          finished: {{ formatDateStr(finishedOn) }}
        </h4>
      </div>
      <!-- ROLE ICON -->
      <div class="justify-self-end col-span-1 shrink-0">
        <OhVueIcon
          v-if="role === 'master'"
          name="gi-crenel-crown"
          class="shrink-0 text-neutral-700"
          scale="1.5"
        />
        <OhVueIcon
          v-if="role === 'player'"
          name="bi-person-fill"
          class="shrink-0 text-neutral-700"
          scale="1.5"
        />
      </div>
    </div>

    <!-- DESC -->

    <div class="mt-auto border-4 border-stone-200 rounded-xl overflow-hidden py-4 px-2">
      <p class="text-sm font-light text-black overflow-hidden line-clamp-3">
        {{ description?.trim() || 'No description' }}
      </p>
    </div>
  </div>
</template>
