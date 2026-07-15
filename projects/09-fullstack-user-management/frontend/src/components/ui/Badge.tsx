import type { ReactNode } from 'react'
import { cn } from '../../lib/utils'

type BadgeVariant = 'default' | 'primary' | 'success' | 'warning' | 'danger' | 'info'

interface BadgeProps {
  children: ReactNode
  variant?: BadgeVariant
  className?: string
}

const variantCls: Record<BadgeVariant, string> = {
  default: 'bg-neutral-100 text-neutral-700 dark:bg-neutral-800 dark:text-neutral-300',
  primary: 'bg-primary-50 text-primary-700 dark:bg-primary-950 dark:text-primary-300',
  success: 'bg-success-50 text-success-700 dark:bg-success-700/20 dark:text-success-400',
  warning: 'bg-warning-50 text-warning-700 dark:bg-warning-700/20 dark:text-warning-400',
  danger: 'bg-danger-50 text-danger-700 dark:bg-danger-700/20 dark:text-danger-400',
  info: 'bg-info-50 text-info-700 dark:bg-info-700/20 dark:text-info-400',
}

export function Badge({ children, variant = 'default', className }: BadgeProps) {
  return (
    <span
      className={cn(
        'inline-flex items-center gap-1 rounded-full px-2.5 py-0.5',
        'text-xs font-medium leading-none',
        variantCls[variant],
        className,
      )}
    >
      {children}
    </span>
  )
}

interface StatusDotProps {
  active: boolean
  label?: string
}

export function StatusDot({ active, label }: StatusDotProps) {
  return (
    <span className="inline-flex items-center gap-1.5">
      <span className={cn('h-1.5 w-1.5 rounded-full', active ? 'bg-success-500' : 'bg-neutral-400')} />
      {label && <span className="text-xs text-neutral-600 dark:text-neutral-400">{label}</span>}
    </span>
  )
}
