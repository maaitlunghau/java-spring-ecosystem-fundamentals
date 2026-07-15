import { useAuth } from '../context/AuthProvider'

export default function DashboardPage() {
  const { user } = useAuth()
  return (
    <div>
      <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">
        Xin chào, {user?.fullName ?? 'bạn'}!
      </h1>
      <p className="mt-2 text-neutral-500 dark:text-neutral-400">
        Dashboard — sẽ được hoàn thiện ở Step 8.
      </p>
    </div>
  )
}
