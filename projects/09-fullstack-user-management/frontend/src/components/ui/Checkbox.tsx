import { forwardRef, type InputHTMLAttributes } from 'react'
import { cn } from '../../lib/utils'

interface CheckboxProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'type'> {
  label?: string
}

export const Checkbox = forwardRef<HTMLInputElement, CheckboxProps>(
  ({ label, className, id, ...props }, ref) => (
    <label className="flex items-center gap-2 cursor-pointer select-none">
      <input
        ref={ref}
        id={id}
        type="checkbox"
        {...props}
        className={cn(
          'h-4 w-4 rounded-sm border-neutral-300 text-primary-600',
          'focus:ring-2 focus:ring-primary-500 focus:ring-offset-1',
          'dark:border-neutral-600 dark:bg-neutral-900',
          'disabled:cursor-not-allowed disabled:opacity-50',
          'accent-primary-600',
          className,
        )}
      />
      {label && <span className="text-sm text-neutral-700 dark:text-neutral-300">{label}</span>}
    </label>
  ),
)
Checkbox.displayName = 'Checkbox'
