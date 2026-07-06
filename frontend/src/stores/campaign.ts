import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import apiClient from '@/api/axios'
import { useAuthStore } from './auth'

export interface CampaignResponse {
  id: number
  title: string
  description: string | null
  createdOn: string
  updatedOn: string
  finishedOn: string | null
  callerRole: 'master' | 'player'
}

export interface CampaignMemberResponse {
  userId: number
  username: string
  role: 'master' | 'player'
}

export interface CreateCampaignRequest {
  title: string
  description?: string
}

export interface UpdateCampaignRequest {
  title: string
  description?: string
}

export interface AddMemberRequest {
  identifier: string
  role: 'master' | 'player'
}

export interface ChangeRoleRequest {
  role: 'master' | 'player'
}

export const useCampaignStore = defineStore('campaign', () => {
  const campaigns = ref<CampaignResponse[]>([])
  const activeCampaign = ref<CampaignResponse | null>(null)
  const members = ref<CampaignMemberResponse[]>([])

  const currentUserMembership = computed<CampaignMemberResponse | undefined>(() => {
    const authStore = useAuthStore()
    return members.value.find((m) => m.userId === authStore.userId)
  })

  const isOnlyMaster = computed<boolean>(() => {
    const membership = currentUserMembership.value
    if (!membership || membership.role !== 'master') return false
    return members.value.filter((m) => m.role === 'master').length === 1
  })

  function clearCampaigns() {
    campaigns.value = []
    activeCampaign.value = null
    members.value = []
  }

  async function fetchCampaigns() {
    const response = await apiClient.get<CampaignResponse[]>('/campaigns')
    campaigns.value = response.data
  }

  async function fetchCampaign(campaignId: number) {
    const response = await apiClient.get<CampaignResponse>(`/campaigns/${campaignId}`)
    activeCampaign.value = response.data
  }

  async function createCampaign(request: CreateCampaignRequest) {
    const response = await apiClient.post<CampaignResponse>('/campaigns', request)
    campaigns.value.push(response.data)
    return response.data
  }

  async function updateCampaign(campaignId: number, request: UpdateCampaignRequest) {
    const response = await apiClient.put<CampaignResponse>(`/campaigns/${campaignId}`, request)
    activeCampaign.value = response.data
    const index = campaigns.value.findIndex((c) => c.id === campaignId)
    if (index !== -1) campaigns.value[index] = response.data
    return response.data
  }

  async function finishCampaign(campaignId: number) {
    await apiClient.post(`/campaigns/${campaignId}/finish`)
    if (activeCampaign.value?.id === campaignId) {
      await fetchCampaign(campaignId)
    }
  }

  async function deleteCampaign(campaignId: number) {
    await apiClient.delete(`/campaigns/${campaignId}`)
    campaigns.value = campaigns.value.filter((c) => c.id !== campaignId)
    if (activeCampaign.value?.id === campaignId) activeCampaign.value = null
  }

  async function fetchMembers(campaignId: number) {
    const response = await apiClient.get<CampaignMemberResponse[]>(
      `/campaigns/${campaignId}/members`,
    )
    members.value = response.data
  }

  async function addMember(campaignId: number, request: AddMemberRequest) {
    const response = await apiClient.post<CampaignMemberResponse>(
      `/campaigns/${campaignId}/members`,
      request,
    )
    members.value.push(response.data)
    return response.data
  }

  async function removeMember(campaignId: number, targetUserId: number) {
    await apiClient.delete(`/campaigns/${campaignId}/members/${targetUserId}`)
    members.value = members.value.filter((m) => m.userId !== targetUserId)
  }

  async function changeRole(campaignId: number, targetUserId: number, request: ChangeRoleRequest) {
    const response = await apiClient.put<CampaignMemberResponse>(
      `/campaigns/${campaignId}/members/${targetUserId}/role`,
      request,
    )
    const index = members.value.findIndex((m) => m.userId === targetUserId)
    if (index !== -1) members.value[index] = response.data
    return response.data
  }

  return {
    campaigns,
    activeCampaign,
    members,
    clearCampaigns,
    currentUserMembership,
    isOnlyMaster,
    fetchCampaigns,
    fetchCampaign,
    createCampaign,
    updateCampaign,
    finishCampaign,
    deleteCampaign,
    fetchMembers,
    addMember,
    removeMember,
    changeRole,
  }
})
