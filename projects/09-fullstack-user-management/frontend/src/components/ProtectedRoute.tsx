import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthProvider'

export function ProtectedRoute({ role }: { role?: 'ADMIN' | 'USER' }) {
  const { user, isLoading } = useAuth()
  if (isLoading) return <div className="p-8">Đang tải…</div>
  if (!user) return <Navigate to="/login" replace />
  if (role && user.role !== role) return <Navigate to="/" replace />
  return <Outlet />
}
