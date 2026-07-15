import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { useResetPassword } from '../hooks/useAuth'
import { useToast } from '../components/ui/Toast'
import { Button } from '../components/ui/Button'
import { FormField } from '../components/ui/FormField'
import { PasswordInput } from '../components/ui/PasswordInput'
import { IconKey } from '../components/icons'

const schema = z.object({
  newPassword: z.string().min(6, 'Mật khẩu tối thiểu 6 ký tự'),
  confirmPassword: z.string(),
}).refine((d) => d.newPassword === d.confirmPassword, {
  message: 'Mật khẩu xác nhận không khớp',
  path: ['confirmPassword'],
})
type FormValues = z.infer<typeof schema>

export default function ResetPasswordPage() {
  const { register, handleSubmit, formState: { errors } } =
    useForm<FormValues>({ resolver: zodResolver(schema) })
  const reset = useResetPassword()
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const { toast } = useToast()
  const token = params.get('token') ?? ''

  useEffect(() => {
    if (!token) navigate('/forgot-password', { replace: true })
  }, [token, navigate])

  const onSubmit = ({ newPassword }: FormValues) =>
    reset.mutate({ token, newPassword }, {
      onSuccess: () => {
        toast('Đặt lại mật khẩu thành công. Vui lòng đăng nhập.', 'success')
        navigate('/login')
      },
    })

  return (
    <div>
      <div className="mb-8">
        <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-xl bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400">
          <IconKey size={24} />
        </div>
        <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Đặt lại mật khẩu</h1>
        <p className="mt-2 text-sm text-neutral-500 dark:text-neutral-400">
          Nhập mật khẩu mới cho tài khoản của bạn.
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <FormField label="Mật khẩu mới" htmlFor="newPassword" error={errors.newPassword?.message} hint="Tối thiểu 6 ký tự">
          <PasswordInput
            id="newPassword"
            autoComplete="new-password"
            placeholder="••••••••"
            {...register('newPassword')}
            error={!!errors.newPassword}
          />
        </FormField>

        <FormField label="Xác nhận mật khẩu" htmlFor="confirmPassword" error={errors.confirmPassword?.message}>
          <PasswordInput
            id="confirmPassword"
            autoComplete="new-password"
            placeholder="••••••••"
            {...register('confirmPassword')}
            error={!!errors.confirmPassword}
          />
        </FormField>

        {reset.isError && (
          <p className="rounded-lg bg-danger-50 dark:bg-danger-900/20 px-3 py-2 text-sm text-danger-700 dark:text-danger-400">
            Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.
          </p>
        )}

        <Button type="submit" className="w-full" loading={reset.isPending}>
          Đặt lại mật khẩu
        </Button>
      </form>

      <div className="mt-6 text-center">
        <Link to="/login" className="text-sm font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400">
          Quay lại đăng nhập
        </Link>
      </div>
    </div>
  )
}
