import { apiClient } from './axiosClient.js'

export const getMe = () => apiClient.get('/users/me').then((r) => r.data.data)

export const listUsers = (page = 0, size = 20) =>
  apiClient.get('/admin/users', { params: { page, size } }).then((r) => r.data.data)

export const setUserStatus = (userId, active) =>
  apiClient.patch(`/admin/users/${userId}/status`, { active }).then((r) => r.data.data)
