export interface ApiResponse<T> {
  status: number
  message: string
  data: T
  timestamp: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface UserResponse {
  id: number
  email: string
  fullName: string
  avatarUrl: string | null
  isEmailVerified: boolean
  isEnabled: boolean
  role: 'ADMIN' | 'USER'
  createdAt: string
}
