import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Link } from 'react-router-dom'
import { useForgotPassword } from '../hooks/useAuth'
import { Button } from '../components/ui/Button'
import { FormField } from '../components/ui/FormField'
import { Input } from '../components/ui/Input'
import { IconMail, IconCheckCircle } from '../components/icons'

const schema = z.object({ email: z.string().email('Email không hợp lệ') })
type FormValues = z.infer<typeof schema>

export default function ForgotPasswordPage() {
  const { register, handleSubmit, formState: { errors } } =
    useForm<FormValues>({ resolver: zodResolver(schema) })
  const forgot = useForgotPassword()
  const [submitted, setSubmitted] = useState(false)

  const onSubmit = (values: FormValues) =>
    forgot.mutate(values.email, { onSuccess: () => setSubmitted(true) })

  if (submitted) {
    return (
      <div className="text-center">
        <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-full bg-success-50 dark:bg-success-900/30 text-success-600 dark:text-success-400">
          <IconCheckCircle size={32} />
        </div>
        <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Kiểm tra hộp thư</h1>
        <p className="mt-3 text-sm text-neutral-500 dark:text-neutral-400">
          Nếu email tồn tại trong hệ thống, chúng tôi đã gửi hướng dẫn đặt lại mật khẩu.
        </p>
        <div className="mt-8">
          <Link to="/login" className="text-sm font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400">
            Quay lại đăng nhập
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div>
      <div className="mb-8">
        <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-xl bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400">
          <IconMail size={24} />
        </div>
        <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Quên mật khẩu?</h1>
        <p className="mt-2 text-sm text-neutral-500 dark:text-neutral-400">
          Nhập email để nhận link đặt lại mật khẩu.
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
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

        <Button type="submit" className="w-full" loading={forgot.isPending}>
          Gửi link đặt lại
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
