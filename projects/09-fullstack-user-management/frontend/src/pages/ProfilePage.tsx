import { useAuth } from '../context/AuthProvider'
import { Avatar } from '../components/ui/Avatar'
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/Card'

export default function ProfilePage() {
  const { user } = useAuth()
  return (
    <div className="max-w-2xl">
      <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50 mb-6">Hồ sơ cá nhân</h1>
      <Card>
        <CardHeader>
          <div className="flex items-center gap-4">
            <Avatar name={user?.fullName ?? 'User'} src={user?.avatarUrl ?? undefined} size="xl" />
            <div>
              <CardTitle>{user?.fullName ?? '...'}</CardTitle>
              <p className="text-sm text-neutral-500 dark:text-neutral-400">{user?.email ?? ''}</p>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-neutral-500">Trang hồ sơ — sẽ được hoàn thiện ở Step 7.</p>
        </CardContent>
      </Card>
    </div>
  )
}
