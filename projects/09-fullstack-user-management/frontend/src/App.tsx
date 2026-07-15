import { Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import { ProtectedRoute } from './components/ProtectedRoute'

function UsersPlaceholder() {
  return <div className="p-8 text-xl">Users (placeholder)</div>
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute role="ADMIN" />}>
        <Route path="/users" element={<UsersPlaceholder />} />
      </Route>
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}
