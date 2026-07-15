import { api } from './axios'
import type { ApiResponse, Page, UserResponse } from '../types/api'

export interface UsersParams {
  page?: number
  size?: number
  sort?: string
  keyword?: string
  role?: string
  enabled?: boolean
}

export const usersApi = {
  list: (params: UsersParams) =>
    api.get<ApiResponse<Page<UserResponse>>>('/api/users', { params }).then((r) => r.data.data),
  getById: (id: number) =>
    api.get<ApiResponse<UserResponse>>(`/api/users/${id}`).then((r) => r.data.data),
  update: (id: number, body: { fullName: string; avatarUrl?: string | null }) =>
    api.put<ApiResponse<UserResponse>>(`/api/users/${id}`, body).then((r) => r.data.data),
  delete: (id: number) =>
    api.delete(`/api/users/${id}`),
}
