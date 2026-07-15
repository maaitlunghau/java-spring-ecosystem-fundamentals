import { Link } from 'react-router-dom'

export default function RegisterPage() {
  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Tạo tài khoản</h1>
        <p className="mt-2 text-sm text-neutral-500 dark:text-neutral-400">
          Đã có tài khoản?{' '}
          <Link to="/login" className="font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400">
            Đăng nhập
          </Link>
        </p>
      </div>
      <p className="text-sm text-neutral-500">Trang đăng ký — sẽ được hoàn thiện ở Step 6.</p>
    </div>
  )
}
