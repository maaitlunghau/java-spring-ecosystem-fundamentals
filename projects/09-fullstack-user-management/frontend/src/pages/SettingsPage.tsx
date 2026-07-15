import { useTheme, type Theme } from '../context/ThemeProvider'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../components/ui/Card'
import { Switch } from '../components/ui/Switch'
import { IconMoon, IconSun, IconMonitor } from '../components/icons'
import { cn } from '../lib/utils'

interface ThemeOption {
  key: Theme
  icon: typeof IconSun
  label: string
  desc: string
}

const THEME_OPTIONS: ThemeOption[] = [
  { key: 'light', icon: IconSun, label: 'Sáng', desc: 'Luôn dùng giao diện sáng' },
  { key: 'dark', icon: IconMoon, label: 'Tối', desc: 'Luôn dùng giao diện tối' },
  { key: 'system', icon: IconMonitor, label: 'Hệ thống', desc: 'Theo cài đặt thiết bị của bạn' },
]

export default function SettingsPage() {
  const { theme, setTheme } = useTheme()

  return (
    <div className="max-w-2xl space-y-6">
      <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Cài đặt</h1>

      {/* Appearance */}
      <Card>
        <CardHeader>
          <CardTitle>Giao diện</CardTitle>
          <CardDescription>Tuỳ chỉnh cách ứng dụng hiển thị.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-1">
            {THEME_OPTIONS.map(({ key, icon: Icon, label, desc }) => (
              <button
                key={key}
                onClick={() => setTheme(key)}
                className={cn(
                  'flex w-full items-center gap-3 rounded-lg px-3 py-3 text-left transition-colors',
                  theme === key
                    ? 'bg-primary-50 dark:bg-primary-900/30 text-primary-700 dark:text-primary-300'
                    : 'hover:bg-neutral-100 dark:hover:bg-neutral-800 text-neutral-700 dark:text-neutral-300',
                )}
              >
                <Icon size={20} className="shrink-0" />
                <div className="flex-1 min-w-0">
                  <p className="font-medium text-sm">{label}</p>
                  <p className="text-xs text-neutral-500 dark:text-neutral-400">{desc}</p>
                </div>
                <div className={cn(
                  'h-4 w-4 rounded-full border-2 transition-colors',
                  theme === key
                    ? 'border-primary-600 bg-primary-600'
                    : 'border-neutral-300 dark:border-neutral-600',
                )} />
              </button>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Notifications (UI-only, no backend) */}
      <Card>
        <CardHeader>
          <CardTitle>Thông báo</CardTitle>
          <CardDescription>Quản lý cách bạn nhận thông báo.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {[
              { label: 'Thông báo qua email', desc: 'Nhận email khi có hoạt động quan trọng' },
              { label: 'Thông báo bảo mật', desc: 'Cảnh báo khi phát hiện đăng nhập bất thường' },
              { label: 'Bản tin sản phẩm', desc: 'Nhận tin tức và cập nhật mới nhất' },
            ].map(({ label, desc }) => (
              <div key={label} className="flex items-center justify-between gap-4">
                <div>
                  <p className="text-sm font-medium text-neutral-900 dark:text-neutral-100">{label}</p>
                  <p className="text-xs text-neutral-500 dark:text-neutral-400">{desc}</p>
                </div>
                <Switch defaultChecked={label !== 'Bản tin sản phẩm'} aria-label={label} />
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Danger zone */}
      <Card className="border-danger-200 dark:border-danger-800">
        <CardHeader>
          <CardTitle className="text-danger-700 dark:text-danger-400">Vùng nguy hiểm</CardTitle>
          <CardDescription>Các hành động này không thể hoàn tác.</CardDescription>
        </CardHeader>
        <CardContent>
          <button
            className="text-sm text-danger-600 hover:text-danger-700 dark:text-danger-400 font-medium underline-offset-2 hover:underline transition-colors"
            onClick={() => alert('Tính năng này chưa được triển khai.')}
          >
            Xóa tài khoản vĩnh viễn
          </button>
        </CardContent>
      </Card>
    </div>
  )
}
