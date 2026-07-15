import type { ReactNode } from 'react'
import { cn } from '../../lib/utils'
import { IconAlertCircle } from '../icons'
import { Button } from './Button'

interface ErrorStateProps {
  title?: string
  description?: string
  onRetry?: () => void
  className?: string
  action?: ReactNode
}

export function ErrorState({
  title = 'Đã xảy ra lỗi',
  description = 'Không thể tải dữ liệu. Vui lòng thử lại.',
  onRetry,
  action,
  className,
}: ErrorStateProps) {
  return (
    <div className={cn('flex flex-col items-center justify-center py-12 text-center', className)}>
      <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-danger-50 text-danger-500 dark:bg-danger-700/20">
        <IconAlertCircle size={24} />
      </div>
      <h3 className="text-sm font-semibold text-neutral-900 dark:text-neutral-50">{title}</h3>
      <p className="mt-1 text-sm text-neutral-500 dark:text-neutral-400 max-w-xs">{description}</p>
      <div className="mt-4">
        {action ?? (onRetry && (
          <Button variant="secondary" size="sm" onClick={onRetry}>
            Thử lại
          </Button>
        ))}
      </div>
    </div>
  )
}
