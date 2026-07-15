import { useEffect, useRef, type ReactNode } from 'react'
import { createPortal } from 'react-dom'
import { cn } from '../../lib/utils'
import { IconX } from '../icons'
import { Button } from './Button'

interface ModalProps {
  open: boolean
  onClose: () => void
  title?: string
  description?: string
  children?: ReactNode
  footer?: ReactNode
  size?: 'sm' | 'md' | 'lg'
  className?: string
}

const sizeMap = { sm: 'max-w-sm', md: 'max-w-lg', lg: 'max-w-2xl' }

export function Modal({ open, onClose, title, description, children, footer, size = 'md', className }: ModalProps) {
  const dialogRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (!open) return
    const prev = document.body.style.overflow
    document.body.style.overflow = 'hidden'

    const handleKey = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose() }
    document.addEventListener('keydown', handleKey)

    // focus trap
    const focusable = dialogRef.current?.querySelectorAll<HTMLElement>(
      'a, button, input, textarea, select, [tabindex]:not([tabindex="-1"])',
    )
    focusable?.[0]?.focus()

    return () => {
      document.body.style.overflow = prev
      document.removeEventListener('keydown', handleKey)
    }
  }, [open, onClose])

  if (!open) return null

  return createPortal(
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby={title ? 'modal-title' : undefined}
      aria-describedby={description ? 'modal-desc' : undefined}
      className="fixed inset-0 z-50 flex items-center justify-center p-4"
    >
      {/* backdrop */}
      <div
        className="absolute inset-0 bg-black/50 backdrop-blur-sm animate-in fade-in duration-150"
        onClick={onClose}
        aria-hidden
      />
      {/* dialog */}
      <div
        ref={dialogRef}
        className={cn(
          'relative z-10 w-full rounded-xl border border-neutral-200 bg-white shadow-lg',
          'dark:border-neutral-800 dark:bg-neutral-900',
          'animate-in fade-in zoom-in-95 duration-150',
          sizeMap[size],
          className,
        )}
      >
        {(title || description) && (
          <div className="flex items-start justify-between gap-4 p-5 pb-3 border-b border-neutral-100 dark:border-neutral-800">
            <div>
              {title && <h2 id="modal-title" className="text-base font-semibold text-neutral-900 dark:text-neutral-50">{title}</h2>}
              {description && <p id="modal-desc" className="mt-0.5 text-sm text-neutral-500 dark:text-neutral-400">{description}</p>}
            </div>
            <Button variant="ghost" size="sm" iconOnly onClick={onClose} aria-label="Đóng">
              <IconX size={16} />
            </Button>
          </div>
        )}
        {children && <div className="p-5">{children}</div>}
        {footer && (
          <div className="flex items-center justify-end gap-2 p-5 pt-3 border-t border-neutral-100 dark:border-neutral-800">
            {footer}
          </div>
        )}
      </div>
    </div>,
    document.body,
  )
}
