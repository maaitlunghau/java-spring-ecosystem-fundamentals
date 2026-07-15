import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate } from 'react-router-dom'
import { useLogin } from '../hooks/useAuth'

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
    login.mutate(values, { onSuccess: () => navigate('/users') })

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="max-w-sm mx-auto mt-20 space-y-4">
      <h1 className="text-2xl font-bold">Đăng nhập</h1>
      <input {...register('email')} placeholder="Email"
        className="w-full border rounded px-3 py-2" />
      {errors.email && <p className="text-red-600 text-sm">{errors.email.message}</p>}
      <input {...register('password')} type="password" placeholder="Mật khẩu"
        className="w-full border rounded px-3 py-2" />
      {errors.password && <p className="text-red-600 text-sm">{errors.password.message}</p>}
      <button disabled={login.isPending}
        className="w-full bg-blue-600 text-white rounded py-2 disabled:opacity-50">
        {login.isPending ? 'Đang đăng nhập…' : 'Đăng nhập'}
      </button>
      {login.isError && <p className="text-red-600 text-sm">Sai email hoặc mật khẩu</p>}
    </form>
  )
}
