import { useAuth } from '../context/AuthProvider'
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/Card'
import { Avatar } from '../components/ui/Avatar'
import { STAT_CARDS, RECENT_ACTIVITY } from '../mocks/dashboard'
import { IconTrendingUp, IconActivity, IconUsers, IconShield } from '../components/icons'
import { cn, formatDateTime } from '../lib/utils'

const TREND_ICONS = {
  up: IconTrendingUp,
  down: IconTrendingUp,
  neutral: IconActivity,
}

const ICON_BY_INDEX = [IconUsers, IconActivity, IconShield, IconShield]

export default function DashboardPage() {
  const { user } = useAuth()

  return (
    <div className="space-y-6">
      {/* Greeting */}
      <div>
        <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">
          Xin chào, {user?.fullName ?? 'bạn'}!
        </h1>
        <p className="mt-1 text-sm text-neutral-500 dark:text-neutral-400">
          Đây là tổng quan hệ thống hôm nay.
        </p>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {STAT_CARDS.map((stat, i) => {
          const TrendIcon = TREND_ICONS[stat.trend]
          const StatIcon = ICON_BY_INDEX[i]
          return (
            <Card key={stat.label}>
              <CardContent className="pt-5">
                <div className="flex items-start justify-between">
                  <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400">
                    <StatIcon size={20} />
                  </div>
                  <span className={cn(
                    'flex items-center gap-1 text-xs font-medium',
                    stat.trend === 'up' ? 'text-success-600 dark:text-success-400' :
                    stat.trend === 'down' ? 'text-danger-600 dark:text-danger-400' :
                    'text-neutral-500',
                  )}>
                    <TrendIcon size={12} className={stat.trend === 'down' ? 'rotate-180' : ''} />
                    {stat.delta}
                  </span>
                </div>
                <div className="mt-3">
                  <p className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">{stat.value}</p>
                  <p className="mt-0.5 text-sm text-neutral-500 dark:text-neutral-400">{stat.label}</p>
                </div>
              </CardContent>
            </Card>
          )
        })}
      </div>

      {/* Recent activity */}
      <Card>
        <CardHeader>
          <CardTitle>Hoạt động gần đây</CardTitle>
        </CardHeader>
        <CardContent>
          <ul className="space-y-4">
            {RECENT_ACTIVITY.map((item, i) => (
              <li key={item.id} className="flex items-start gap-3">
                <Avatar name={item.user} size="sm" />
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-neutral-900 dark:text-neutral-100 truncate">{item.user}</p>
                  <p className="text-sm text-neutral-500 dark:text-neutral-400">{item.action}</p>
                </div>
                <time className="shrink-0 text-xs text-neutral-400 tabular-nums">
                  {formatDateTime(item.timestamp)}
                </time>
                {i < RECENT_ACTIVITY.length - 1 && (
                  <div className="absolute left-[3.25rem] top-full h-4 w-px bg-neutral-200 dark:bg-neutral-700" />
                )}
              </li>
            ))}
          </ul>
        </CardContent>
      </Card>
    </div>
  )
}
