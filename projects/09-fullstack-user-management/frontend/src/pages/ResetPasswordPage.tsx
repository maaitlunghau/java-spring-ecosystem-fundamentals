import { Link } from 'react-router-dom'

export default function ResetPasswordPage() {
  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Đặt lại mật khẩu</h1>
        <p className="mt-2 text-sm text-neutral-500 dark:text-neutral-400">
          Nhập mật khẩu mới cho tài khoản của bạn.
        </p>
      </div>
      <p className="text-sm text-neutral-500">Trang đặt lại mật khẩu — sẽ được hoàn thiện ở Step 6.</p>
      <div className="mt-6">
        <Link to="/login" className="text-sm font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400">
          Quay lại đăng nhập
        </Link>
      </div>
    </div>
  )
}
