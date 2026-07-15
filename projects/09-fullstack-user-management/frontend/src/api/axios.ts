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

type Waiter = { resolve: () => void; reject: (err: unknown) => void }
let isRefreshing = false
let waiters: Waiter[] = []

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config
    const status = error.response?.status
    // /api/me 401 = "not logged in" — let AuthProvider handle it, don't trigger refresh
    const isAuthCall = original?.url?.includes('/api/auth/') || original?.url?.endsWith('/api/me')

    if (status !== 401 || isAuthCall || original?._retry) {
      return Promise.reject(error)
    }
    original._retry = true

    if (isRefreshing) {
      await new Promise<void>((resolve, reject) => waiters.push({ resolve, reject }))
      return api(original)
    }

    isRefreshing = true
    try {
      await api.post('/api/auth/refresh-token')
      const queued = waiters
      waiters = []
      queued.forEach((w) => w.resolve())
      return api(original)
    } catch (refreshErr) {
      const queued = waiters
      waiters = []
      queued.forEach((w) => w.reject(refreshErr))
      // Redirect to SPA login page (not backend URL)
      window.location.replace(window.location.origin + '/login')
      return Promise.reject(refreshErr)
    } finally {
      isRefreshing = false
    }
  }
)
