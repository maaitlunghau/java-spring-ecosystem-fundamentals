import { useEffect, useRef } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { authApi } from '../api/auth'
import { Spinner } from '../components/ui/Spinner'

export default function OAuthCallbackPage() {
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const qc = useQueryClient()
  const called = useRef(false)

  useEffect(() => {
    const code = params.get('code')
    const error = params.get('error')

    if (error) {
      navigate('/login?error=' + encodeURIComponent(error), { replace: true })
      return
    }

    if (!code) {
      navigate('/login', { replace: true })
      return
    }

    if (called.current) return
    called.current = true

    authApi.oauth2Exchange(code)
      .then(() => {
        qc.invalidateQueries({ queryKey: ['me'] })
        navigate('/dashboard', { replace: true })
      })
      .catch(() => {
        navigate('/login?error=oauth_failed', { replace: true })
      })
  }, [navigate, params, qc])

  return (
    <div className="flex min-h-screen items-center justify-center bg-neutral-50 dark:bg-neutral-950">
      <div className="flex flex-col items-center gap-3 text-neutral-500 dark:text-neutral-400">
        <Spinner size="lg" />
        <p className="text-sm">Đang hoàn tất đăng nhập…</p>
      </div>
    </div>
  )
}
