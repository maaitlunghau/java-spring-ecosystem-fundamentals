import type { ReactNode } from 'react'
import { cn } from '../../lib/utils'

interface TooltipProps {
  content: string
  children: ReactNode
  side?: 'top' | 'bottom' | 'left' | 'right'
  className?: string
}

const sideCls: Record<string, string> = {
  top: 'bottom-full left-1/2 -translate-x-1/2 mb-2',
  bottom: 'top-full left-1/2 -translate-x-1/2 mt-2',
  left: 'right-full top-1/2 -translate-y-1/2 mr-2',
  right: 'left-full top-1/2 -translate-y-1/2 ml-2',
}

export function Tooltip({ content, children, side = 'top', className }: TooltipProps) {
  return (
    <div className={cn('group relative inline-flex', className)}>
      {children}
      <div
        role="tooltip"
        className={cn(
          'pointer-events-none absolute z-50 whitespace-nowrap rounded-md',
          'bg-neutral-900 px-2 py-1 text-xs text-white shadow-md',
          'opacity-0 group-hover:opacity-100 transition-opacity duration-150',
          'dark:bg-neutral-700',
          sideCls[side],
        )}
      >
        {content}
      </div>
    </div>
  )
}
