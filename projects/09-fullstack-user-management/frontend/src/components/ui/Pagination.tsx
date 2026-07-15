import { cn } from '../../lib/utils'
import { IconChevronLeft, IconChevronRight } from '../icons'

interface PaginationProps {
  page: number
  totalPages: number
  totalElements: number
  size: number
  onPageChange: (page: number) => void
  className?: string
}

export function Pagination({ page, totalPages, totalElements, size, onPageChange, className }: PaginationProps) {
  const from = totalElements === 0 ? 0 : page * size + 1
  const to = Math.min((page + 1) * size, totalElements)
  const isFirst = page === 0
  const isLast = page + 1 >= totalPages

  const btnBase = cn(
    'flex h-8 w-8 items-center justify-center rounded-md text-sm transition-colors',
    'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary-500',
  )

  return (
    <div className={cn('flex flex-wrap items-center justify-between gap-3 text-sm text-neutral-600 dark:text-neutral-400', className)}>
      <span>
        {totalElements === 0
          ? 'Không có kết quả'
          : `${from}–${to} trên tổng ${totalElements.toLocaleString('vi-VN')} mục`}
      </span>
      <div className="flex items-center gap-1">
        <button
          disabled={isFirst}
          onClick={() => onPageChange(page - 1)}
          aria-label="Trang trước"
          className={cn(btnBase, 'border border-neutral-200 dark:border-neutral-700',
            isFirst
              ? 'opacity-40 cursor-not-allowed'
              : 'hover:bg-neutral-100 dark:hover:bg-neutral-800')}
        >
          <IconChevronLeft size={16} />
        </button>
        <span className="px-2 text-neutral-700 dark:text-neutral-300">
          {page + 1} / {totalPages || 1}
        </span>
        <button
          disabled={isLast}
          onClick={() => onPageChange(page + 1)}
          aria-label="Trang sau"
          className={cn(btnBase, 'border border-neutral-200 dark:border-neutral-700',
            isLast
              ? 'opacity-40 cursor-not-allowed'
              : 'hover:bg-neutral-100 dark:hover:bg-neutral-800')}
        >
          <IconChevronRight size={16} />
        </button>
      </div>
    </div>
  )
}
