import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useUser, useUpdateUser, useDeleteUser } from '../hooks/useUsers'
import { useToast } from '../components/ui/Toast'
import { Avatar } from '../components/ui/Avatar'
import { Badge } from '../components/ui/Badge'
import { Button } from '../components/ui/Button'
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/Card'
import { FormField } from '../components/ui/FormField'
import { Input } from '../components/ui/Input'
import { Modal } from '../components/ui/Modal'
import { Skeleton } from '../components/ui/Skeleton'
import { ErrorState } from '../components/ui/ErrorState'
import { IconArrowLeft, IconEdit, IconTrash } from '../components/icons'
import { formatDateTime } from '../lib/utils'

export default function UserDetailPage() {
  const { id } = useParams<{ id: string }>()
  const userId = Number(id)
  const navigate = useNavigate()
  const { toast } = useToast()

  const { data: user, isLoading, isError, refetch } = useUser(userId)
  const updateUser = useUpdateUser()
  const deleteUser = useDeleteUser()

  const [editing, setEditing] = useState(false)
  const [editName, setEditName] = useState('')
  const [confirmDelete, setConfirmDelete] = useState(false)

  const openEdit = () => {
    setEditName(user?.fullName ?? '')
    setEditing(true)
  }

  const handleUpdate = () => {
    if (!editName.trim()) return
    updateUser.mutate(
      { id: userId, fullName: editName.trim() },
      {
        onSuccess: () => {
          toast('Cập nhật thành công', 'success')
          setEditing(false)
        },
        onError: () => toast('Cập nhật thất bại', 'danger'),
      },
    )
  }

  const handleDelete = () => {
    deleteUser.mutate(userId, {
      onSuccess: () => {
        toast('Đã xóa người dùng', 'success')
        navigate('/users')
      },
      onError: () => toast('Xóa thất bại', 'danger'),
    })
  }

  return (
    <div className="max-w-2xl space-y-6">
      <div className="flex items-center gap-3">
        <Link to="/users">
          <Button variant="ghost" size="sm" leftIcon={<IconArrowLeft size={16} />}>Quay lại</Button>
        </Link>
      </div>

      {isLoading ? (
        <Card>
          <CardContent className="space-y-4 pt-6">
            <div className="flex items-center gap-4">
              <Skeleton className="h-16 w-16 rounded-full" />
              <div className="space-y-2 flex-1">
                <Skeleton className="h-5 w-40" />
                <Skeleton className="h-4 w-56" />
              </div>
            </div>
          </CardContent>
        </Card>
      ) : isError ? (
        <ErrorState
          title="Không thể tải thông tin người dùng"
          description="Vui lòng thử lại."
          onRetry={() => refetch()}
        />
      ) : user ? (
        <>
          <Card>
            <CardHeader>
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-center gap-4">
                  <Avatar name={user.fullName} src={user.avatarUrl ?? undefined} size="xl" />
                  <div>
                    <CardTitle>{user.fullName}</CardTitle>
                    <p className="text-sm text-neutral-500 dark:text-neutral-400">{user.email}</p>
                    <div className="mt-2 flex flex-wrap gap-2">
                      <Badge variant={user.role === 'ADMIN' ? 'primary' : 'default'}>
                        {user.role === 'ADMIN' ? 'Admin' : 'User'}
                      </Badge>
                      {!user.isEnabled && <Badge variant="default">Vô hiệu</Badge>}
                      {!user.isEmailVerified && <Badge variant="warning">Chưa xác thực email</Badge>}
                      {user.isEnabled && user.isEmailVerified && <Badge variant="success">Hoạt động</Badge>}
                    </div>
                  </div>
                </div>
                <div className="flex gap-2 shrink-0">
                  <Button variant="secondary" size="sm" leftIcon={<IconEdit size={14} />} onClick={openEdit}>
                    Chỉnh sửa
                  </Button>
                  <Button variant="ghost" size="sm" leftIcon={<IconTrash size={14} />}
                    className="text-danger-600 hover:bg-danger-50 dark:text-danger-400 dark:hover:bg-danger-900/20"
                    onClick={() => setConfirmDelete(true)}>
                    Xóa
                  </Button>
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <dl className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <dt className="text-neutral-500 dark:text-neutral-400">ID</dt>
                  <dd className="mt-0.5 font-medium text-neutral-900 dark:text-neutral-100 tabular-nums">{user.id}</dd>
                </div>
                <div>
                  <dt className="text-neutral-500 dark:text-neutral-400">Ngày tạo</dt>
                  <dd className="mt-0.5 font-medium text-neutral-900 dark:text-neutral-100">{formatDateTime(user.createdAt)}</dd>
                </div>
              </dl>
            </CardContent>
          </Card>

          {/* Edit modal */}
          <Modal open={editing} onClose={() => setEditing(false)} title="Chỉnh sửa người dùng" size="sm">
            <div className="space-y-4">
              <FormField label="Họ và tên" htmlFor="editName" required>
                <Input
                  id="editName"
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  autoFocus
                />
              </FormField>
              <div className="flex justify-end gap-3">
                <Button variant="secondary" onClick={() => setEditing(false)}>Hủy</Button>
                <Button onClick={handleUpdate} loading={updateUser.isPending}>Lưu</Button>
              </div>
            </div>
          </Modal>

          {/* Delete confirm modal */}
          <Modal open={confirmDelete} onClose={() => setConfirmDelete(false)} title="Xác nhận xóa" size="sm">
            <div className="space-y-4">
              <p className="text-sm text-neutral-600 dark:text-neutral-400">
                Bạn có chắc muốn xóa người dùng <strong>{user.fullName}</strong>? Hành động này không thể hoàn tác.
              </p>
              <div className="flex justify-end gap-3">
                <Button variant="secondary" onClick={() => setConfirmDelete(false)}>Hủy</Button>
                <Button variant="danger" onClick={handleDelete} loading={deleteUser.isPending}>Xóa</Button>
              </div>
            </div>
          </Modal>
        </>
      ) : null}
    </div>
  )
}
