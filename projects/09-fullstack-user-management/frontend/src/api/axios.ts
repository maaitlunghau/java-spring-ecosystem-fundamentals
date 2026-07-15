import axios from 'axios'
import Cookies from 'js-cookie'
import { API_URL } from '../lib/env'

declare module 'axios' {
  export interface InternalAxiosRequestConfig {
    _retry?: boolean
  }
}

export const api = axios.create({
  baseURL: API_URL,
  withCredentials: true,
})

api.interceptors.request.use((config) => {
  const method = config.method?.toUpperCase()
  if (method && ['POST', 'PUT', 'PATCH', 'DELETE'].includes(method)) {
    const csrf = Cookies.get('XSRF-TOKEN')
    if (csrf) config.headers['X-XSRF-TOKEN'] = csrf
  }
  return config
})

let isRefreshing = false
let waiters: Array<() => void> = []

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config
    const status = error.response?.status
    const isAuthCall = original?.url?.includes('/api/auth/')

    if (status !== 401 || isAuthCall || original?._retry) {
      return Promise.reject(error)
    }
    original._retry = true

    if (isRefreshing) {
      await new Promise<void>((resolve) => waiters.push(resolve))
      return api(original)
    }

    isRefreshing = true
    try {
      await api.post('/api/auth/refresh-token')
      waiters.forEach((w) => w())
      waiters = []
      return api(original)
    } catch (refreshErr) {
      waiters = []
      window.location.href = '/login'
      return Promise.reject(refreshErr)
    } finally {
      isRefreshing = false
    }
  }
)
