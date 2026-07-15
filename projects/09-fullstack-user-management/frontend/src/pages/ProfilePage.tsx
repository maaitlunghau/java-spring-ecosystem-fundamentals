import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthProvider'
import { useUpdateUser } from '../hooks/useUsers'
import { useToast } from '../components/ui/Toast'
import { Avatar } from '../components/ui/Avatar'
import { Badge } from '../components/ui/Badge'
import { Button } from '../components/ui/Button'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../components/ui/Card'
import { FormField } from '../components/ui/FormField'
import { Input } from '../components/ui/Input'
import { IconUser, IconShield } from '../components/icons'
import { formatDateTime } from '../lib/utils'

export default function ProfilePage() {
  const { user } = useAuth()
  const updateUser = useUpdateUser()
  const { toast } = useToast()

  const [fullName, setFullName] = useState(user?.fullName ?? '')
  const [dirty, setDirty] = useState(false)

  useEffect(() => {
    if (user) setFullName(user.fullName)
  }, [user])

  const handleSave = () => {
    if (!user || !fullName.trim()) return
    updateUser.mutate(
      { id: user.id, fullName: fullName.trim() },
      {
        onSuccess: () => { toast('Hồ sơ đã được cập nhật', 'success'); setDirty(false) },
        onError: () => toast('Cập nhật thất bại', 'danger'),
      },
    )
  }

  if (!user) return null

  return (
    <div className="max-w-2xl space-y-6">
      <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Hồ sơ cá nhân</h1>

      {/* Profile card */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-4">
            <Avatar name={user.fullName} src={user.avatarUrl ?? undefined} size="xl" />
            <div>
              <CardTitle>{user.fullName}</CardTitle>
              <CardDescription>{user.email}</CardDescription>
              <div className="mt-2 flex flex-wrap gap-2">
                <Badge variant={user.role === 'ADMIN' ? 'primary' : 'default'}>
                  {user.role === 'ADMIN' ? (
                    <span className="flex items-center gap-1"><IconShield size={11} /> Admin</span>
                  ) : (
                    <span className="flex items-center gap-1"><IconUser size={11} /> User</span>
                  )}
                </Badge>
                {user.isEmailVerified
                  ? <Badge variant="success">Email đã xác thực</Badge>
                  : <Badge variant="warning">Email chưa xác thực</Badge>}
              </div>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <dl className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <dt className="text-neutral-500 dark:text-neutral-400">ID tài khoản</dt>
              <dd className="mt-0.5 font-mono text-neutral-900 dark:text-neutral-100">{user.id}</dd>
            </div>
            <div>
              <dt className="text-neutral-500 dark:text-neutral-400">Ngày tạo</dt>
              <dd className="mt-0.5 text-neutral-900 dark:text-neutral-100">{formatDateTime(user.createdAt)}</dd>
            </div>
          </dl>
        </CardContent>
      </Card>

      {/* Edit form */}
      <Card>
        <CardHeader>
          <CardTitle>Thông tin cá nhân</CardTitle>
          <CardDescription>Cập nhật họ tên hiển thị của bạn.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-5">
          <FormField label="Email" htmlFor="profileEmail" hint="Email không thể thay đổi.">
            <Input id="profileEmail" value={user.email} disabled />
          </FormField>

          <FormField label="Họ và tên" htmlFor="profileName" required>
            <Input
              id="profileName"
              value={fullName}
              onChange={(e) => { setFullName(e.target.value); setDirty(true) }}
              placeholder="Nguyễn Văn A"
            />
          </FormField>

          <div className="flex justify-end">
            <Button onClick={handleSave} loading={updateUser.isPending} disabled={!dirty || !fullName.trim()}>
              Lưu thay đổi
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
