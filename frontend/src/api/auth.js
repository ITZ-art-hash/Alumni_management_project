import { apiClient } from './axiosClient.js'

export const login = (email, password) =>
  apiClient.post('/auth/login', { email, password }).then((r) => r.data.data)

export const register = (payload) =>
  apiClient.post('/auth/register', payload).then((r) => r.data.data)

export const logout = () => apiClient.post('/auth/logout')

export const verifyEmail = (token) =>
  apiClient.get('/auth/verify-email', { params: { token } }).then((r) => r.data)

export const resendVerification = (email) =>
  apiClient.post('/auth/resend-verification', null, { params: { email } }).then((r) => r.data)
