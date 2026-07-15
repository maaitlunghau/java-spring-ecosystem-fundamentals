import { Outlet, Link } from 'react-router-dom'
import { useTheme, type Theme } from '../../context/ThemeProvider'
import { IconMoon, IconSun, IconMonitor, IconZap } from '../icons'
import { cn } from '../../lib/utils'

function ThemeToggle() {
  const { theme, setTheme } = useTheme()
  const themes: { key: Theme; icon: typeof IconSun; label: string }[] = [
    { key: 'light', icon: IconSun, label: 'Sáng' },
    { key: 'dark', icon: IconMoon, label: 'Tối' },
    { key: 'system', icon: IconMonitor, label: 'Hệ thống' },
  ]
  return (
    <div className="flex items-center rounded-full border border-neutral-200 dark:border-neutral-700 bg-neutral-100 dark:bg-neutral-800 p-0.5 gap-0.5">
      {themes.map(({ key, icon: Icon, label }) => (
        <button
          key={key}
          onClick={() => setTheme(key)}
          aria-label={label}
          className={cn(
            'flex h-7 w-7 items-center justify-center rounded-full transition-colors',
            theme === key
              ? 'bg-white dark:bg-neutral-700 text-neutral-900 dark:text-neutral-100 shadow-sm'
              : 'text-neutral-500 hover:text-neutral-700 dark:text-neutral-400 dark:hover:text-neutral-200',
          )}
        >
          <Icon size={14} />
        </button>
      ))}
    </div>
  )
}

const FEATURES = [
  'Quản lý người dùng toàn diện',
  'Phân quyền vai trò chi tiết',
  'Xác thực JWT an toàn',
  'Kiểm soát truy cập real-time',
]

export function AuthLayout() {
  return (
    <div className="min-h-screen grid lg:grid-cols-2">
      {/* Left branding panel — hidden on mobile */}
      <div className="hidden lg:flex flex-col bg-primary-600 text-white p-12 relative overflow-hidden">
        {/* decorative blobs */}
        <div className="absolute -top-24 -right-24 h-80 w-80 rounded-full bg-white/5" />
        <div className="absolute -bottom-32 -left-16 h-96 w-96 rounded-full bg-white/5" />

        {/* Logo */}
        <Link to="/" className="flex items-center gap-2 relative z-10">
          <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-white/20 font-bold text-sm">U</span>
          <span className="text-lg font-semibold">UserMgmt</span>
        </Link>

        <div className="flex-1 flex flex-col justify-center relative z-10">
          <blockquote className="mt-8">
            <p className="text-2xl font-medium leading-snug">
              "Nền tảng quản lý người dùng dành cho các đội ngũ hiện đại."
            </p>
            <footer className="mt-6 flex items-center gap-3">
              <span className="flex h-10 w-10 items-center justify-center rounded-full bg-white/20 text-sm font-semibold">A</span>
              <div>
                <p className="font-medium text-sm">Admin Team</p>
                <p className="text-white/60 text-xs">UserMgmt Platform</p>
              </div>
            </footer>
          </blockquote>

          <ul className="mt-10 space-y-3">
            {FEATURES.map((f) => (
              <li key={f} className="flex items-center gap-3 text-sm text-white/80">
                <IconZap size={16} className="text-white/60 shrink-0" />
                {f}
              </li>
            ))}
          </ul>
        </div>

        <p className="text-xs text-white/40 relative z-10">© 2026 UserMgmt. Bảo lưu mọi quyền.</p>
      </div>

      {/* Right form panel */}
      <div className="flex flex-col bg-white dark:bg-neutral-950">
        {/* Top bar */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-neutral-100 dark:border-neutral-800 lg:border-none">
          {/* Mobile logo */}
          <Link to="/" className="flex items-center gap-2 lg:invisible">
            <span className="flex h-7 w-7 items-center justify-center rounded-lg bg-primary-600 text-white text-xs font-bold">U</span>
            <span className="font-semibold text-neutral-900 dark:text-neutral-50 text-sm">UserMgmt</span>
          </Link>
          <ThemeToggle />
        </div>

        {/* Form area */}
        <div className="flex flex-1 items-center justify-center px-6 py-12">
          <div className="w-full max-w-sm">
            <Outlet />
          </div>
        </div>
      </div>
    </div>
  )
}
