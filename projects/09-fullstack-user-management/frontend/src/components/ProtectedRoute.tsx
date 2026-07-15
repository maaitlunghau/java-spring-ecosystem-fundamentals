import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthProvider'
import { Spinner } from './ui/Spinner'

export function ProtectedRoute({ role }: { role?: 'ADMIN' | 'USER' }) {
  const { user, isLoading } = useAuth()
  const location = useLocation()

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Spinner size="lg" />
      </div>
    )
  }

  if (!user) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />
  }

  if (role && user.role !== role) {
    return <Navigate to="/403" replace />
  }

  return <Outlet />
}
