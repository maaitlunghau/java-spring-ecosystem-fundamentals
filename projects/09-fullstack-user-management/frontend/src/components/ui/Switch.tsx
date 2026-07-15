import { forwardRef, type InputHTMLAttributes } from 'react'
import { cn } from '../../lib/utils'

interface SwitchProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'type'> {
  label?: string
}

export const Switch = forwardRef<HTMLInputElement, SwitchProps>(
  ({ label, className, checked, ...props }, ref) => (
    <label className="flex items-center gap-3 cursor-pointer select-none">
      <div className="relative inline-block">
        <input ref={ref} type="checkbox" checked={checked} {...props} className="sr-only peer" />
        <div
          className={cn(
            'h-5 w-9 rounded-full border-2 border-transparent',
            'bg-neutral-200 dark:bg-neutral-700',
            'peer-checked:bg-primary-600 dark:peer-checked:bg-primary-500',
            'peer-focus-visible:ring-2 peer-focus-visible:ring-primary-500 peer-focus-visible:ring-offset-2',
            'peer-disabled:cursor-not-allowed peer-disabled:opacity-50',
            'transition-colors duration-150',
          )}
        />
        <div
          className={cn(
            'absolute top-0.5 left-0.5 h-4 w-4 rounded-full bg-white shadow-sm',
            'transition-transform duration-150',
            checked ? 'translate-x-4' : 'translate-x-0',
          )}
        />
      </div>
      {label && <span className="text-sm text-neutral-700 dark:text-neutral-300">{label}</span>}
    </label>
  ),
)
Switch.displayName = 'Switch'
