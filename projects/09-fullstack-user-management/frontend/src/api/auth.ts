import { api } from './axios'
import type { ApiResponse, UserResponse } from '../types/api'

export const authApi = {
  me: () => api.get<ApiResponse<UserResponse>>('/api/me').then((r) => r.data.data),
  login: (body: { email: string; password: string }) =>
    api.post('/api/auth/login', body),
  logout: () => api.post('/api/auth/logout'),
  register: (body: { email: string; password: string; fullName: string }) =>
    api.post('/api/auth/register', body),
  verifyEmail: (token: string) =>
    api.get('/api/auth/verify-email', { params: { token } }),
  forgotPassword: (email: string) =>
    api.post('/api/auth/forgot-password', { email }),
  resetPassword: (body: { token: string; newPassword: string }) =>
    api.post('/api/auth/reset-password', body),
  oauth2Exchange: (code: string) =>
    api.post('/api/auth/oauth2/exchange', null, { params: { code } }),
}
