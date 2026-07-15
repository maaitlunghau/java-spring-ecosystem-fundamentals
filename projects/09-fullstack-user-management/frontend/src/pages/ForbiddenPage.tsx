import { Link } from 'react-router-dom'
import { Button } from '../components/ui/Button'
import { IconShield } from '../components/icons'

export default function ForbiddenPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-neutral-50 dark:bg-neutral-950 px-4">
      <div className="text-center max-w-md">
        <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-warning-100 dark:bg-warning-900/30 text-warning-600 dark:text-warning-400">
          <IconShield size={40} />
        </div>
        <h1 className="text-6xl font-bold text-neutral-900 dark:text-neutral-50 mb-3">403</h1>
        <h2 className="text-xl font-semibold text-neutral-700 dark:text-neutral-300 mb-3">Không có quyền truy cập</h2>
        <p className="text-neutral-500 dark:text-neutral-400 mb-8">
          Bạn không có quyền xem trang này. Vui lòng liên hệ quản trị viên nếu cần hỗ trợ.
        </p>
        <Link to="/dashboard">
          <Button>Về trang chính</Button>
        </Link>
      </div>
    </div>
  )
}
