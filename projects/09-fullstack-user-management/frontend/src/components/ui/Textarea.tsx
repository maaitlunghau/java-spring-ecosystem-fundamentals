import { forwardRef, type TextareaHTMLAttributes } from 'react'
import { cn } from '../../lib/utils'

interface TextareaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  error?: boolean
}

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(
  ({ error, className, ...props }, ref) => (
    <textarea
      ref={ref}
      {...props}
      className={cn(
        'w-full rounded-md border bg-white px-3 py-2 text-sm text-neutral-900',
        'placeholder:text-neutral-400 resize-y min-h-20',
        'transition-colors duration-150',
        'focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500',
        'disabled:cursor-not-allowed disabled:opacity-50 disabled:bg-neutral-50',
        'dark:bg-neutral-900 dark:text-neutral-50 dark:placeholder:text-neutral-500',
        error
          ? 'border-danger-500 focus:ring-danger-500'
          : 'border-neutral-300 dark:border-neutral-700',
        className,
      )}
    />
  ),
)
Textarea.displayName = 'Textarea'
