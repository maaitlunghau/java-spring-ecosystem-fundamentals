import type { HTMLAttributes, ReactNode } from 'react'
import { cn } from '../../lib/utils'

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  children: ReactNode
}

export function Card({ children, className, ...props }: CardProps) {
  return (
    <div
      {...props}
      className={cn(
        'rounded-lg border border-neutral-200 bg-white shadow-sm',
        'dark:border-neutral-800 dark:bg-neutral-900',
        className,
      )}
    >
      {children}
    </div>
  )
}

export function CardHeader({ children, className, ...props }: CardProps) {
  return (
    <div {...props} className={cn('flex flex-col gap-1 p-5 pb-3', className)}>
      {children}
    </div>
  )
}

export function CardTitle({ children, className, ...props }: CardProps) {
  return (
    <h3 {...props} className={cn('text-base font-semibold text-neutral-900 dark:text-neutral-50', className)}>
      {children}
    </h3>
  )
}

export function CardDescription({ children, className, ...props }: CardProps) {
  return (
    <p {...props} className={cn('text-sm text-neutral-500 dark:text-neutral-400', className)}>
      {children}
    </p>
  )
}

export function CardContent({ children, className, ...props }: CardProps) {
  return (
    <div {...props} className={cn('p-5 pt-0', className)}>
      {children}
    </div>
  )
}

export function CardFooter({ children, className, ...props }: CardProps) {
  return (
    <div {...props} className={cn('flex items-center p-5 pt-3 border-t border-neutral-100 dark:border-neutral-800', className)}>
      {children}
    </div>
  )
}
