import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Link, useNavigate } from 'react-router-dom'
import { useRegister } from '../hooks/useAuth'
import { Button } from '../components/ui/Button'
import { FormField } from '../components/ui/FormField'
import { Input } from '../components/ui/Input'
import { PasswordInput } from '../components/ui/PasswordInput'

const schema = z.object({
  fullName: z.string().min(1, 'Họ tên là bắt buộc'),
  email: z.string().email('Email không hợp lệ'),
  password: z.string().min(6, 'Mật khẩu tối thiểu 6 ký tự'),
})
type FormValues = z.infer<typeof schema>

export default function RegisterPage() {
  const { register, handleSubmit, formState: { errors } } =
    useForm<FormValues>({ resolver: zodResolver(schema) })
  const doRegister = useRegister()
  const navigate = useNavigate()

  const onSubmit = (values: FormValues) =>
    doRegister.mutate(values, {
      onSuccess: () => navigate('/verify-email'),
    })

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

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <FormField label="Họ và tên" htmlFor="fullName" error={errors.fullName?.message}>
          <Input
            id="fullName"
            autoComplete="name"
            placeholder="Nguyễn Văn A"
            {...register('fullName')}
            error={!!errors.fullName}
          />
        </FormField>

        <FormField label="Email" htmlFor="email" error={errors.email?.message}>
          <Input
            id="email"
            type="email"
            autoComplete="email"
            placeholder="you@example.com"
            {...register('email')}
            error={!!errors.email}
          />
        </FormField>

        <FormField label="Mật khẩu" htmlFor="password" error={errors.password?.message} hint="Tối thiểu 6 ký tự">
          <PasswordInput
            id="password"
            autoComplete="new-password"
            placeholder="••••••••"
            {...register('password')}
            error={!!errors.password}
          />
        </FormField>

        {doRegister.isError && (
          <p className="rounded-lg bg-danger-50 dark:bg-danger-900/20 px-3 py-2 text-sm text-danger-700 dark:text-danger-400">
            Đăng ký thất bại. Email có thể đã được sử dụng.
          </p>
        )}

        <Button type="submit" className="w-full" loading={doRegister.isPending}>
          Tạo tài khoản
        </Button>
      </form>
    </div>
  )
}
