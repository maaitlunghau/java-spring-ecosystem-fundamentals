import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthProvider'
import { Spinner } from './ui/Spinner'

/** Chỉ cho phép user CHƯA đăng nhập. Đã đăng nhập → redirect /dashboard. */
export function GuestRoute() {
  const { user, isLoading } = useAuth()

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Spinner size="lg" />
      </div>
    )
  }

  if (user) return <Navigate to="/dashboard" replace />

  return <Outlet />
}
