import { forwardRef, type InputHTMLAttributes, type ReactNode } from 'react'
import { cn } from '../../lib/utils'

export interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  leftAddon?: ReactNode
  rightAddon?: ReactNode
  error?: boolean
  inputClassName?: string
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ leftAddon, rightAddon, error, className, inputClassName, ...props }, ref) => {
    const hasLeft = Boolean(leftAddon)
    const hasRight = Boolean(rightAddon)
    return (
      <div className={cn('relative flex items-center', className)}>
        {hasLeft && (
          <span className="pointer-events-none absolute left-3 flex items-center text-neutral-400">
            {leftAddon}
          </span>
        )}
        <input
          ref={ref}
          {...props}
          className={cn(
            'w-full rounded-md border bg-white text-sm text-neutral-900',
            'placeholder:text-neutral-400',
            'transition-colors duration-150',
            'focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500',
            'disabled:cursor-not-allowed disabled:opacity-50 disabled:bg-neutral-50',
            'dark:bg-neutral-900 dark:text-neutral-50 dark:placeholder:text-neutral-500',
            'dark:focus:ring-primary-400',
            error
              ? 'border-danger-500 focus:ring-danger-500 focus:border-danger-500'
              : 'border-neutral-300 dark:border-neutral-700',
            hasLeft ? 'pl-9' : 'px-3',
            hasRight ? 'pr-9' : '',
            'h-9 py-2',
            inputClassName,
          )}
        />
        {hasRight && (
          <span className="absolute right-3 flex items-center text-neutral-400">
            {rightAddon}
          </span>
        )}
      </div>
    )
  },
)
Input.displayName = 'Input'
