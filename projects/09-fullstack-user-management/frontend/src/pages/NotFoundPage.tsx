import { Link } from 'react-router-dom'
import { Button } from '../components/ui/Button'

export default function NotFoundPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-neutral-50 dark:bg-neutral-950 px-4">
      <div className="text-center max-w-md">
        <h1 className="text-8xl font-bold text-neutral-200 dark:text-neutral-800 mb-4">404</h1>
        <h2 className="text-2xl font-semibold text-neutral-900 dark:text-neutral-50 mb-3">Trang không tồn tại</h2>
        <p className="text-neutral-500 dark:text-neutral-400 mb-8">
          Trang bạn đang tìm kiếm đã bị xóa, đổi tên, hoặc chưa bao giờ tồn tại.
        </p>
        <div className="flex flex-wrap items-center justify-center gap-3">
          <Link to="/dashboard">
            <Button>Về trang chính</Button>
          </Link>
          <Link to="/">
            <Button variant="secondary">Trang chủ</Button>
          </Link>
        </div>
      </div>
    </div>
  )
}
