import type { LabelHTMLAttributes } from 'react'
import { cn } from '../../lib/utils'

interface LabelProps extends LabelHTMLAttributes<HTMLLabelElement> {
  required?: boolean
}

export function Label({ children, required, className, ...props }: LabelProps) {
  return (
    <label
      {...props}
      className={cn('block text-sm font-medium text-neutral-700 dark:text-neutral-300', className)}
    >
      {children}
      {required && <span className="ml-0.5 text-danger-500" aria-hidden>*</span>}
    </label>
  )
}
