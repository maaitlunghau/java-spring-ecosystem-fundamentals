import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Link, useNavigate } from 'react-router-dom'
import { useLogin } from '../hooks/useAuth'
import { Button } from '../components/ui/Button'
import { FormField } from '../components/ui/FormField'
import { Input } from '../components/ui/Input'
import { PasswordInput } from '../components/ui/PasswordInput'
import { Label } from '../components/ui/Label'

const schema = z.object({
  email: z.string().email('Email không hợp lệ'),
  password: z.string().min(1, 'Bắt buộc'),
})
type FormValues = z.infer<typeof schema>

export default function LoginPage() {
  const { register, handleSubmit, formState: { errors } } =
    useForm<FormValues>({ resolver: zodResolver(schema) })
  const login = useLogin()
  const navigate = useNavigate()

  const onSubmit = (values: FormValues) =>
    login.mutate(values, { onSuccess: () => navigate('/dashboard') })

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Đăng nhập</h1>
        <p className="mt-2 text-sm text-neutral-500 dark:text-neutral-400">
          Chưa có tài khoản?{' '}
          <Link to="/register" className="font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400">
            Tạo tài khoản miễn phí
          </Link>
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <FormField
          label={<Label htmlFor="email">Email</Label>}
          error={errors.email?.message}
        >
          <Input
            id="email"
            type="email"
            autoComplete="email"
            placeholder="you@example.com"
            {...register('email')}
            error={!!errors.email}
          />
        </FormField>

        <FormField
          label={
            <div className="flex items-center justify-between">
              <Label htmlFor="password">Mật khẩu</Label>
              <Link
                to="/forgot-password"
                className="text-xs font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400"
              >
                Quên mật khẩu?
              </Link>
            </div>
          }
          error={errors.password?.message}
        >
          <PasswordInput
            id="password"
            autoComplete="current-password"
            placeholder="••••••••"
            {...register('password')}
            error={!!errors.password}
          />
        </FormField>

        {login.isError && (
          <p className="rounded-lg bg-danger-50 dark:bg-danger-900/20 px-3 py-2 text-sm text-danger-700 dark:text-danger-400">
            Sai email hoặc mật khẩu. Vui lòng thử lại.
          </p>
        )}

        <Button type="submit" className="w-full" loading={login.isPending}>
          Đăng nhập
        </Button>
      </form>
    </div>
  )
}
