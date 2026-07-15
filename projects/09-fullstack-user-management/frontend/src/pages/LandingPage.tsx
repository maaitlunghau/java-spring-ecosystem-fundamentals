import { Link } from 'react-router-dom'
import { Button } from '../components/ui/Button'
import { IconZap, IconUsers, IconShield, IconActivity } from '../components/icons'

const FEATURES = [
  { icon: IconUsers, title: 'Quản lý người dùng', desc: 'Thêm, sửa, xóa và phân quyền người dùng một cách dễ dàng.' },
  { icon: IconShield, title: 'Bảo mật cao', desc: 'JWT httpOnly cookie, CSRF protection, và xác thực email tích hợp sẵn.' },
  { icon: IconZap, title: 'Hiệu suất cao', desc: 'Spring Boot backend tối ưu với TanStack Query caching thông minh.' },
  { icon: IconActivity, title: 'Giám sát real-time', desc: 'Theo dõi hoạt động người dùng và trạng thái hệ thống liên tục.' },
]

export default function LandingPage() {
  return (
    <div>
      {/* Hero */}
      <section className="mx-auto max-w-6xl px-4 py-24 text-center">
        <span className="inline-flex items-center gap-1.5 rounded-full bg-primary-50 dark:bg-primary-900/30 px-3 py-1 text-xs font-medium text-primary-700 dark:text-primary-300 mb-6">
          <IconZap size={12} /> Phiên bản 1.0 — Sẵn sàng sử dụng
        </span>
        <h1 className="text-4xl font-bold tracking-tight text-neutral-900 dark:text-neutral-50 sm:text-5xl lg:text-6xl">
          Quản lý người dùng <br />
          <span className="text-primary-600">chưa bao giờ dễ hơn</span>
        </h1>
        <p className="mt-6 max-w-2xl mx-auto text-lg text-neutral-600 dark:text-neutral-400">
          Nền tảng quản lý người dùng toàn diện xây dựng trên Spring Boot & React — bảo mật, nhanh, và dễ tích hợp.
        </p>
        <div className="mt-10 flex flex-wrap items-center justify-center gap-4">
          <Link to="/register"><Button size="lg">Bắt đầu miễn phí</Button></Link>
          <Link to="/login"><Button variant="secondary" size="lg">Đăng nhập</Button></Link>
        </div>
      </section>

      {/* Features */}
      <section id="features" className="border-t border-neutral-200 dark:border-neutral-800 bg-neutral-50 dark:bg-neutral-900/50 py-20">
        <div className="mx-auto max-w-6xl px-4">
          <h2 className="text-center text-2xl font-bold text-neutral-900 dark:text-neutral-50 mb-12">Tính năng nổi bật</h2>
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {FEATURES.map(({ icon: Icon, title, desc }) => (
              <div key={title} className="rounded-xl border border-neutral-200 dark:border-neutral-800 bg-white dark:bg-neutral-900 p-6">
                <div className="mb-4 flex h-10 w-10 items-center justify-center rounded-lg bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400">
                  <Icon size={20} />
                </div>
                <h3 className="font-semibold text-neutral-900 dark:text-neutral-100 mb-2">{title}</h3>
                <p className="text-sm text-neutral-500 dark:text-neutral-400">{desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-20">
        <div className="mx-auto max-w-2xl px-4 text-center">
          <h2 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50 mb-4">Sẵn sàng bắt đầu?</h2>
          <p className="text-neutral-600 dark:text-neutral-400 mb-8">Tạo tài khoản miễn phí và khám phá toàn bộ tính năng ngay hôm nay.</p>
          <Link to="/register"><Button size="lg">Tạo tài khoản ngay</Button></Link>
        </div>
      </section>
    </div>
  )
}
