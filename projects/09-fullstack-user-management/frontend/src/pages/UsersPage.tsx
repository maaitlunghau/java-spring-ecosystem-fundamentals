import { useState } from 'react'
import { useUsers } from '../hooks/useUsers'
import { useLogout } from '../hooks/useAuth'
import { useNavigate } from 'react-router-dom'

export default function UsersPage() {
  const [page, setPage] = useState(0)
  const [keyword, setKeyword] = useState('')
  const { data, isLoading, isError } = useUsers({ page, size: 20, sort: 'createdAt,desc', keyword })
  const logout = useLogout()
  const navigate = useNavigate()

  return (
    <div className="max-w-4xl mx-auto p-8">
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">Quản lý User</h1>
        <button onClick={() => logout.mutate(undefined, { onSuccess: () => navigate('/login') })}
          className="text-sm text-red-600">Đăng xuất</button>
      </div>

      <input value={keyword} onChange={(e) => { setPage(0); setKeyword(e.target.value) }}
        placeholder="Tìm theo tên/email…" className="border rounded px-3 py-2 mb-4 w-full" />

      {isLoading && <p>Đang tải…</p>}
      {isError && <p className="text-red-600">Không tải được danh sách</p>}

      {data && (
        <>
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b">
                <th className="py-2">ID</th><th>Email</th><th>Họ tên</th><th>Role</th><th>Enabled</th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((u) => (
                <tr key={u.id} className="border-b">
                  <td className="py-2">{u.id}</td>
                  <td>{u.email}</td>
                  <td>{u.fullName}</td>
                  <td>{u.role}</td>
                  <td>{u.isEnabled ? '✓' : '✗'}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="flex items-center gap-4 mt-4">
            <button disabled={page === 0} onClick={() => setPage((p) => p - 1)}
              className="px-3 py-1 border rounded disabled:opacity-50">Trước</button>
            <span>Trang {data.number + 1} / {data.totalPages}</span>
            <button disabled={page + 1 >= data.totalPages} onClick={() => setPage((p) => p + 1)}
              className="px-3 py-1 border rounded disabled:opacity-50">Sau</button>
          </div>
        </>
      )}
    </div>
  )
}
