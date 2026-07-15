import { Link } from 'react-router-dom'
import { IconMail } from '../components/icons'

export default function VerifyEmailPage() {
  return (
    <div className="text-center">
      <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-full bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400">
        <IconMail size={32} />
      </div>
      <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Xác thực email</h1>
      <p className="mt-3 text-sm text-neutral-500 dark:text-neutral-400">
        Chúng tôi đã gửi email xác thực. Vui lòng kiểm tra hộp thư.
      </p>
      <div className="mt-8">
        <Link to="/login" className="text-sm font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400">
          Quay lại đăng nhập
        </Link>
      </div>
    </div>
  )
}
