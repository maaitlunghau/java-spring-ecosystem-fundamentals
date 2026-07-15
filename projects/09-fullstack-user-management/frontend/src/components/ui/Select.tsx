import { forwardRef, type SelectHTMLAttributes } from 'react'
import { cn } from '../../lib/utils'

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  error?: boolean
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ error, className, children, ...props }, ref) => (
    <select
      ref={ref}
      {...props}
      className={cn(
        'w-full rounded-md border bg-white px-3 py-2 text-sm text-neutral-900',
        'appearance-none cursor-pointer',
        'focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500',
        'disabled:cursor-not-allowed disabled:opacity-50',
        'dark:bg-neutral-900 dark:text-neutral-50',
        'h-9',
        error
          ? 'border-danger-500 focus:ring-danger-500'
          : 'border-neutral-300 dark:border-neutral-700',
        'bg-[url("data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'12\' height=\'12\' viewBox=\'0 0 24 24\' fill=\'none\' stroke=\'%2371717a\' stroke-width=\'2\' stroke-linecap=\'round\' stroke-linejoin=\'round\'%3E%3Cpath d=\'m6 9 6 6 6-6\'/%3E%3C/svg%3E")] bg-no-repeat bg-[right_0.75rem_center] pr-8',
        className,
      )}
    >
      {children}
    </select>
  ),
)
Select.displayName = 'Select'
