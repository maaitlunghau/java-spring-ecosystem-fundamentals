import type { ReactNode, ThHTMLAttributes, TdHTMLAttributes, HTMLAttributes } from 'react'
import { cn } from '../../lib/utils'

export function Table({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <div className={cn('w-full overflow-x-auto rounded-lg border border-neutral-200 dark:border-neutral-800', className)}>
      <table className="w-full border-collapse text-sm">{children}</table>
    </div>
  )
}

export function TableHead({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <thead className={cn('border-b border-neutral-200 dark:border-neutral-800 bg-neutral-50 dark:bg-neutral-900/50', className)}>
      {children}
    </thead>
  )
}

export function TableBody({ children, className }: { children: ReactNode; className?: string }) {
  return <tbody className={cn('divide-y divide-neutral-100 dark:divide-neutral-800/60 bg-white dark:bg-neutral-900', className)}>{children}</tbody>
}

export function TableRow({ children, className, ...props }: HTMLAttributes<HTMLTableRowElement>) {
  return (
    <tr {...props} className={cn('hover:bg-neutral-50/80 dark:hover:bg-neutral-800/40 transition-colors', className)}>
      {children}
    </tr>
  )
}

interface ThProps extends ThHTMLAttributes<HTMLTableCellElement> {
  sortable?: boolean
  sorted?: 'asc' | 'desc' | false
  onSort?: () => void
}

export function Th({ children, sortable, sorted, onSort, className, ...props }: ThProps) {
  return (
    <th
      {...props}
      className={cn(
        'px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide',
        'text-neutral-500 dark:text-neutral-400',
        sortable && 'cursor-pointer select-none hover:text-neutral-700 dark:hover:text-neutral-200',
        className,
      )}
      onClick={sortable ? onSort : undefined}
    >
      <span className="inline-flex items-center gap-1">
        {children}
        {sortable && (
          <span className="text-neutral-400">
            {sorted === 'asc' ? '↑' : sorted === 'desc' ? '↓' : '↕'}
          </span>
        )}
      </span>
    </th>
  )
}

export function Td({ children, className, ...props }: TdHTMLAttributes<HTMLTableCellElement>) {
  return (
    <td {...props} className={cn('px-4 py-3 text-neutral-700 dark:text-neutral-300', className)}>
      {children}
    </td>
  )
}
