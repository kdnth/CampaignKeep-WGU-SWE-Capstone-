<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import BaseButton from '@/components/BaseButton.vue'
import AddCampaignCharacterModal from '@/components/AddCampaignCharacterModal.vue'

const props = defineProps<{
  campaignId: number
  characterType: 'pc' | 'npc'
}>()

const emit = defineEmits<{
  added: []
}>()

const router = useRouter()
const showAddModal = ref(false)

const typeLabel = computed(() => (props.characterType === 'pc' ? 'Character' : 'NPC'))
const createRouteName = computed(() =>
  props.characterType === 'pc' ? 'createPlayableCharacter' : 'createNonplayableCharacter',
)

function openCreate() {
  router.push({
    name: createRouteName.value,
    query: { attachCampaign: String(props.campaignId) },
  })
}

function handleAdded() {
  emit('added')
}
</script>

<template>
  <div class="flex flex-wrap gap-2">
    <BaseButton variant="primary" @click="showAddModal = true">
      Add Existing {{ typeLabel }}
    </BaseButton>
    <BaseButton variant="secondary" @click="openCreate">
      Create New {{ typeLabel }}
    </BaseButton>
  </div>

  <AddCampaignCharacterModal
    v-if="showAddModal"
    :campaign-id="campaignId"
    :character-type="characterType"
    @close="showAddModal = false"
    @added="handleAdded"
  />
</template>
