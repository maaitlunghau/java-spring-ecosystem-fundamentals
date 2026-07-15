import { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { Spinner } from '../components/ui/Spinner'

export default function OAuthCallbackPage() {
  const navigate = useNavigate()
  const [params] = useSearchParams()

  useEffect(() => {
    const error = params.get('error')
    if (error) {
      navigate('/login?error=' + encodeURIComponent(error), { replace: true })
    } else {
      navigate('/dashboard', { replace: true })
    }
  }, [navigate, params])

  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="flex flex-col items-center gap-3 text-neutral-500">
        <Spinner size="lg" />
        <p className="text-sm">Đang xử lý đăng nhập…</p>
      </div>
    </div>
  )
}
