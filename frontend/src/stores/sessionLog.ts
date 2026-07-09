import { defineStore } from 'pinia'
import { ref } from 'vue'
import apiClient from '@/api/axios'

export interface SessionLogSummaryResponse {
  id: number
  title: string
  createdOn: string
}

export interface SessionLogDetailResponse {
  id: number
  title: string
  body: string
  createdOn: string
}

export interface CreateSessionLogRequest {
  title: string
  body?: string
}

export const useSessionLogStore = defineStore('sessionLog', () => {
  const sessionLogs = ref<SessionLogSummaryResponse[]>([])
  const activeSessionLog = ref<SessionLogDetailResponse | null>(null)

  function clearSessionLogs() {
    sessionLogs.value = []
    activeSessionLog.value = null
  }

  async function fetchSessionLogs(campaignId: number) {
    const response = await apiClient.get<SessionLogSummaryResponse[]>(
      `/campaigns/${campaignId}/session-logs`,
    )
    sessionLogs.value = response.data
    return response.data
  }

  async function fetchSessionLog(campaignId: number, logId: number) {
    const response = await apiClient.get<SessionLogDetailResponse>(
      `/campaigns/${campaignId}/session-logs/${logId}`,
    )
    activeSessionLog.value = response.data
    return response.data
  }

  async function createSessionLog(campaignId: number, request: CreateSessionLogRequest) {
    const response = await apiClient.post<SessionLogDetailResponse>(
      `/campaigns/${campaignId}/session-logs`,
      request,
    )
    sessionLogs.value.unshift({
      id: response.data.id,
      title: response.data.title,
      createdOn: response.data.createdOn,
    })
    return response.data
  }

  async function deleteSessionLog(campaignId: number, logId: number) {
    await apiClient.delete(`/campaigns/${campaignId}/session-logs/${logId}`)
    sessionLogs.value = sessionLogs.value.filter((log) => log.id !== logId)
    if (activeSessionLog.value?.id === logId) {
      activeSessionLog.value = null
    }
  }

  return {
    sessionLogs,
    activeSessionLog,
    clearSessionLogs,
    fetchSessionLogs,
    fetchSessionLog,
    createSessionLog,
    deleteSessionLog,
  }
})
