import api from './axios'

export const getTrackedApis = async () => {
  const response = await api.get('/tracked-apis')
  return response.data
}

export const getTrackedApi = async (apiId) => {
  const response = await api.get(`/tracked-apis/${apiId}`)
  return response.data
}

export const createTrackedApi = async (data) => {
  const response = await api.post('/tracked-apis', data)
  return response.data
}

export const updateTrackedApi = async (apiId, data) => {
  const response = await api.put(`/tracked-apis/${apiId}`, data)
  return response.data
}

export const deleteTrackedApi = async (apiId) => {
  await api.delete(`/tracked-apis/${apiId}`)
}

export const checkApiNow = async (apiId) => {
  const response = await api.post(`/tracked-apis/${apiId}/check-now`)
  return response.data
}

export const getHealthHistory = async (apiId) => {
  const response = await api.get(`/tracked-apis/${apiId}/health-history`)
  return response.data
}