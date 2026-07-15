import { useEffect, useRef } from 'react'
import { Link, useSearchParams, useNavigate } from 'react-router-dom'
import { useVerifyEmail } from '../hooks/useAuth'
import { Spinner } from '../components/ui/Spinner'
import { Button } from '../components/ui/Button'
import { IconMail, IconCheckCircle, IconAlertCircle } from '../components/icons'

export default function VerifyEmailPage() {
  const [params] = useSearchParams()
  const token = params.get('token')
  const verify = useVerifyEmail()
  const navigate = useNavigate()
  const called = useRef(false)

  useEffect(() => {
    if (token && !called.current) {
      called.current = true
      verify.mutate(token)
    }
  }, [token, verify])

  if (!token) {
    return (
      <div className="text-center">
        <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-full bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400">
          <IconMail size={32} />
        </div>
        <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Xác thực email</h1>
        <p className="mt-3 text-sm text-neutral-500 dark:text-neutral-400">
          Chúng tôi đã gửi link xác thực đến email của bạn. Vui lòng kiểm tra hộp thư (kể cả thư mục spam).
        </p>
        <div className="mt-8">
          <Link to="/login" className="text-sm font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400">
            Quay lại đăng nhập
          </Link>
        </div>
      </div>
    )
  }

  if (verify.isPending) {
    return (
      <div className="flex flex-col items-center gap-4">
        <Spinner size="lg" />
        <p className="text-sm text-neutral-500 dark:text-neutral-400">Đang xác thực email…</p>
      </div>
    )
  }

  if (verify.isError) {
    return (
      <div className="text-center">
        <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-full bg-danger-50 dark:bg-danger-900/30 text-danger-500 dark:text-danger-400">
          <IconAlertCircle size={32} />
        </div>
        <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Xác thực thất bại</h1>
        <p className="mt-3 text-sm text-neutral-500 dark:text-neutral-400">
          Link xác thực không hợp lệ hoặc đã hết hạn. Vui lòng đăng ký lại.
        </p>
        <div className="mt-8 flex items-center justify-center gap-4">
          <Link to="/register"><Button variant="secondary">Đăng ký lại</Button></Link>
          <Link to="/login"><Button>Đăng nhập</Button></Link>
        </div>
      </div>
    )
  }

  if (verify.isSuccess) {
    return (
      <div className="text-center">
        <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-full bg-success-50 dark:bg-success-900/30 text-success-600 dark:text-success-400">
          <IconCheckCircle size={32} />
        </div>
        <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Email đã xác thực!</h1>
        <p className="mt-3 text-sm text-neutral-500 dark:text-neutral-400">
          Tài khoản của bạn đã được kích hoạt thành công.
        </p>
        <div className="mt-8">
          <Button onClick={() => navigate('/login')}>Đăng nhập ngay</Button>
        </div>
      </div>
    )
  }

  return null
}
