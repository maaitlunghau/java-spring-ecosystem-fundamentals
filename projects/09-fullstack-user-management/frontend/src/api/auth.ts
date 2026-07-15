import { api } from './axios'
import type { ApiResponse, UserResponse } from '../types/api'

export const authApi = {
  me: () => api.get<ApiResponse<UserResponse>>('/api/me').then((r) => r.data.data),
  login: (body: { email: string; password: string }) =>
    api.post('/api/auth/login', body),
  logout: () => api.post('/api/auth/logout'),
}
