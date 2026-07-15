import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useUsers } from '../hooks/useUsers'
import { Avatar } from '../components/ui/Avatar'
import { Badge } from '../components/ui/Badge'
import { Input } from '../components/ui/Input'
import { Pagination } from '../components/ui/Pagination'
import { Skeleton, SkeletonRow } from '../components/ui/Skeleton'
import { ErrorState } from '../components/ui/ErrorState'
import { EmptyState } from '../components/ui/EmptyState'
import { Table, TableHead, TableBody, TableRow, Th, Td } from '../components/ui/Table'
import { IconSearch, IconUsers } from '../components/icons'
import { formatDate } from '../lib/utils'
import type { UserResponse } from '../types/api'

const PAGE_SIZE = 20

function UserStatusBadge({ user }: { user: UserResponse }) {
  if (!user.isEnabled) return <Badge variant="default">Vô hiệu</Badge>
  if (!user.isEmailVerified) return <Badge variant="warning">Chưa xác thực</Badge>
  return <Badge variant="success">Hoạt động</Badge>
}

export default function UsersPage() {
  const [page, setPage] = useState(0)
  const [keyword, setKeyword] = useState('')
  const { data, isLoading, isError, refetch } = useUsers({
    page,
    size: PAGE_SIZE,
    sort: 'createdAt,desc',
    keyword,
  })

  const handleSearch = (val: string) => {
    setPage(0)
    setKeyword(val)
  }

  const totalElements = data?.totalElements ?? 0
  const totalPages = data?.totalPages ?? 0

  return (
    <div className="space-y-5">
      {/* Header */}
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Người dùng</h1>
          {data && (
            <p className="mt-0.5 text-sm text-neutral-500 dark:text-neutral-400">
              Tổng cộng {totalElements} người dùng
            </p>
          )}
        </div>
      </div>

      {/* Search */}
      <div className="flex items-center gap-3">
        <div className="relative max-w-xs w-full">
          <span className="pointer-events-none absolute inset-y-0 left-3 flex items-center text-neutral-400">
            <IconSearch size={15} />
          </span>
          <Input
            className="pl-9"
            placeholder="Tìm theo tên hoặc email…"
            value={keyword}
            onChange={(e) => handleSearch(e.target.value)}
          />
        </div>
      </div>

      {/* Table */}
      {isError ? (
        <ErrorState
          title="Không tải được danh sách"
          description="Đã có lỗi xảy ra khi lấy dữ liệu. Vui lòng thử lại."
          onRetry={() => refetch()}
        />
      ) : (
        <Table>
          <TableHead>
            <TableRow>
              <Th>Người dùng</Th>
              <Th>Email</Th>
              <Th>Vai trò</Th>
              <Th>Trạng thái</Th>
              <Th>Ngày tạo</Th>
            </TableRow>
          </TableHead>
          <TableBody>
            {isLoading ? (
              Array.from({ length: 8 }).map((_, i) => (
                <TableRow key={i}>
                  <Td><SkeletonRow /></Td>
                  <Td><Skeleton className="h-4 w-32" /></Td>
                  <Td><Skeleton className="h-5 w-14 rounded-full" /></Td>
                  <Td><Skeleton className="h-5 w-20 rounded-full" /></Td>
                  <Td><Skeleton className="h-4 w-24" /></Td>
                </TableRow>
              ))
            ) : data?.content.length === 0 ? (
              <TableRow>
                <Td colSpan={5}>
                  <EmptyState
                    icon={<IconUsers size={32} />}
                    title="Không tìm thấy người dùng"
                    description={keyword ? `Không có kết quả cho "${keyword}".` : 'Chưa có người dùng nào.'}
                    className="py-12"
                  />
                </Td>
              </TableRow>
            ) : (
              data?.content.map((u) => (
                <TableRow key={u.id}>
                  <Td>
                    <Link to={`/users/${u.id}`} className="flex items-center gap-3 group">
                      <Avatar name={u.fullName} src={u.avatarUrl ?? undefined} size="sm" />
                      <span className="font-medium text-neutral-900 dark:text-neutral-100 group-hover:text-primary-600 dark:group-hover:text-primary-400 transition-colors">
                        {u.fullName}
                      </span>
                    </Link>
                  </Td>
                  <Td className="text-neutral-500 dark:text-neutral-400">{u.email}</Td>
                  <Td>
                    <Badge variant={u.role === 'ADMIN' ? 'primary' : 'default'}>
                      {u.role === 'ADMIN' ? 'Admin' : 'User'}
                    </Badge>
                  </Td>
                  <Td><UserStatusBadge user={u} /></Td>
                  <Td className="text-neutral-500 dark:text-neutral-400 tabular-nums text-xs">
                    {formatDate(u.createdAt)}
                  </Td>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <Pagination
          page={page}
          totalPages={totalPages}
          totalElements={totalElements}
          size={PAGE_SIZE}
          onPageChange={setPage}
        />
      )}
    </div>
  )
}
