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
}
