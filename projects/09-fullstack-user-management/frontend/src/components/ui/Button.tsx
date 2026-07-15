import type { ButtonHTMLAttributes, ReactNode } from 'react'
import { cn } from '../../lib/utils'
import { Spinner } from './Spinner'

export type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger' | 'link'
export type ButtonSize = 'sm' | 'md' | 'lg'

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant
  size?: ButtonSize
  loading?: boolean
  iconOnly?: boolean
  leftIcon?: ReactNode
  rightIcon?: ReactNode
}

const variantCls: Record<ButtonVariant, string> = {
  primary: [
    'bg-primary-600 text-white',
    'hover:bg-primary-700 active:bg-primary-800',
    'focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2',
    'disabled:bg-primary-300 dark:disabled:bg-primary-800',
    'dark:bg-primary-500 dark:hover:bg-primary-400',
  ].join(' '),
  secondary: [
    'bg-neutral-100 text-neutral-900 border border-neutral-200',
    'hover:bg-neutral-200 active:bg-neutral-300',
    'focus-visible:ring-2 focus-visible:ring-neutral-400 focus-visible:ring-offset-2',
    'dark:bg-neutral-800 dark:text-neutral-50 dark:border-neutral-700',
    'dark:hover:bg-neutral-700',
  ].join(' '),
  ghost: [
    'text-neutral-700 bg-transparent',
    'hover:bg-neutral-100 active:bg-neutral-200',
    'focus-visible:ring-2 focus-visible:ring-neutral-400 focus-visible:ring-offset-2',
    'dark:text-neutral-300 dark:hover:bg-neutral-800',
  ].join(' '),
  danger: [
    'bg-danger-600 text-white',
    'hover:bg-danger-700 active:bg-danger-800',
    'focus-visible:ring-2 focus-visible:ring-danger-500 focus-visible:ring-offset-2',
    'disabled:bg-danger-300',
  ].join(' '),
  link: [
    'text-primary-600 underline-offset-4 hover:underline bg-transparent p-0 h-auto',
    'focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2',
    'dark:text-primary-400',
  ].join(' '),
}

const sizeCls: Record<ButtonSize, string> = {
  sm: 'h-8 px-3 text-sm rounded-sm gap-1.5',
  md: 'h-9 px-4 text-sm rounded-md gap-2',
  lg: 'h-11 px-5 text-base rounded-md gap-2',
}

const iconOnlyCls: Record<ButtonSize, string> = {
  sm: 'h-8 w-8 p-0 rounded-sm',
  md: 'h-9 w-9 p-0 rounded-md',
  lg: 'h-11 w-11 p-0 rounded-md',
}

export function Button({
  variant = 'primary',
  size = 'md',
  loading = false,
  iconOnly = false,
  leftIcon,
  rightIcon,
  children,
  className,
  disabled,
  ...props
}: ButtonProps) {
  const isLink = variant === 'link'
  return (
    <button
      {...props}
      disabled={disabled || loading}
      className={cn(
        'inline-flex items-center justify-center font-medium',
        'transition-colors duration-150',
        'disabled:cursor-not-allowed disabled:opacity-50',
        'outline-none',
        !isLink && (iconOnly ? iconOnlyCls[size] : sizeCls[size]),
        variantCls[variant],
        className,
      )}
    >
      {loading ? <Spinner size="sm" /> : leftIcon}
      {children}
      {!loading && rightIcon}
    </button>
  )
}
