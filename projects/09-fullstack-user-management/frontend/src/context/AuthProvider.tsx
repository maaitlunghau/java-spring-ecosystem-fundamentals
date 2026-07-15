import { createContext, useContext, type ReactNode } from 'react'
import { useMe } from '../hooks/useAuth'
import type { UserResponse } from '../types/api'

interface AuthState {
  user: UserResponse | null | undefined
  isLoading: boolean
}

const AuthContext = createContext<AuthState>({ user: undefined, isLoading: true })

export function AuthProvider({ children }: { children: ReactNode }) {
  const { data, isLoading } = useMe()
  return (
    <AuthContext.Provider value={{ user: data ?? null, isLoading }}>
      {children}
    </AuthContext.Provider>
  )
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  return useContext(AuthContext)
}
