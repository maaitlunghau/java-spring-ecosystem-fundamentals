import { Link } from 'react-router-dom'
import { cn } from '../../lib/utils'
import { IconChevronRight } from '../icons'

interface BreadcrumbItem {
  label: string
  href?: string
}

interface BreadcrumbProps {
  items: BreadcrumbItem[]
  className?: string
}

export function Breadcrumb({ items, className }: BreadcrumbProps) {
  return (
    <nav aria-label="Breadcrumb" className={cn('flex items-center', className)}>
      <ol className="flex items-center gap-1 text-sm text-neutral-500 dark:text-neutral-400">
        {items.map((item, i) => {
          const isLast = i === items.length - 1
          return (
            <li key={i} className="flex items-center gap-1">
              {i > 0 && <IconChevronRight size={14} className="text-neutral-400" />}
              {isLast || !item.href ? (
                <span
                  className={cn(
                    isLast
                      ? 'font-medium text-neutral-900 dark:text-neutral-100'
                      : 'hover:text-neutral-700 dark:hover:text-neutral-300',
                  )}
                  aria-current={isLast ? 'page' : undefined}
                >
                  {item.label}
                </span>
              ) : (
                <Link
                  to={item.href}
                  className="hover:text-neutral-700 dark:hover:text-neutral-300 transition-colors"
                >
                  {item.label}
                </Link>
              )}
            </li>
          )
        })}
      </ol>
    </nav>
  )
}
