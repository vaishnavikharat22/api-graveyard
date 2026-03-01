import api from './axios'

export const getAlerts = async (resolved) => {
  const params = resolved !== undefined ? { resolved } : {}
  const response = await api.get('/alerts', { params })
  return response.data
}

export const resolveAlert = async (alertId) => {
  const response = await api.patch(`/alerts/${alertId}/resolve`)
  return response.data
}

export const getUnreadCount = async () => {
  const response = await api.get('/alerts/unread-count')
  return response.data
}