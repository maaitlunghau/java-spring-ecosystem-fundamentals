import {
  createContext,
  useContext,
  useEffect,
  useRef,
  useState,
  type ReactNode,
  type KeyboardEvent,
} from 'react'
import { cn } from '../../lib/utils'

interface DropdownContextValue {
  open: boolean
  setOpen: (v: boolean) => void
}

const DropdownCtx = createContext<DropdownContextValue>({ open: false, setOpen: () => {} })

interface DropdownMenuProps {
  children: ReactNode
  align?: 'left' | 'right'
}

export function DropdownMenu({ children }: DropdownMenuProps) {
  const [open, setOpen] = useState(false)
  const ref = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (!open) return
    const handler = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false)
    }
    document.addEventListener('mousedown', handler)
    return () => document.removeEventListener('mousedown', handler)
  }, [open])

  return (
    <DropdownCtx.Provider value={{ open, setOpen }}>
      <div ref={ref} className="relative inline-block">
        {children}
      </div>
    </DropdownCtx.Provider>
  )
}

export function DropdownMenuTrigger({ children, asChild }: { children: ReactNode; asChild?: boolean }) {
  const { open, setOpen } = useContext(DropdownCtx)
  if (asChild) {
    return (
      <span
        onClick={() => setOpen(!open)}
        onKeyDown={(e: KeyboardEvent<HTMLSpanElement>) => {
          if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); setOpen(!open) }
        }}
        role="button"
        tabIndex={0}
        aria-expanded={open}
        aria-haspopup="menu"
      >
        {children}
      </span>
    )
  }
  return (
    <button
      type="button"
      onClick={() => setOpen(!open)}
      aria-expanded={open}
      aria-haspopup="menu"
    >
      {children}
    </button>
  )
}

interface DropdownMenuContentProps {
  children: ReactNode
  align?: 'left' | 'right'
  className?: string
}

export function DropdownMenuContent({ children, align = 'right', className }: DropdownMenuContentProps) {
  const { open, setOpen } = useContext(DropdownCtx)
  if (!open) return null

  const handleKey = (e: KeyboardEvent<HTMLDivElement>) => {
    if (e.key === 'Escape') { setOpen(false) }
    if (e.key === 'ArrowDown') {
      const items = (e.currentTarget as HTMLElement).querySelectorAll<HTMLElement>('[role="menuitem"]')
      const idx = Array.from(items).findIndex((el) => el === document.activeElement)
      items[(idx + 1) % items.length]?.focus()
      e.preventDefault()
    }
    if (e.key === 'ArrowUp') {
      const items = (e.currentTarget as HTMLElement).querySelectorAll<HTMLElement>('[role="menuitem"]')
      const idx = Array.from(items).findIndex((el) => el === document.activeElement)
      items[(idx - 1 + items.length) % items.length]?.focus()
      e.preventDefault()
    }
  }

  return (
    <div
      role="menu"
      onKeyDown={handleKey}
      className={cn(
        'absolute z-40 mt-1 min-w-44 rounded-lg border border-neutral-200 bg-white py-1 shadow-lg',
        'dark:border-neutral-700 dark:bg-neutral-800',
        'animate-in fade-in zoom-in-95 duration-150 origin-top',
        align === 'right' ? 'right-0' : 'left-0',
        className,
      )}
    >
      {children}
    </div>
  )
}

interface DropdownMenuItemProps {
  children: ReactNode
  onClick?: () => void
  className?: string
  danger?: boolean
  disabled?: boolean
  icon?: ReactNode
}

export function DropdownMenuItem({ children, onClick, className, danger, disabled, icon }: DropdownMenuItemProps) {
  const { setOpen } = useContext(DropdownCtx)
  return (
    <button
      role="menuitem"
      tabIndex={0}
      disabled={disabled}
      onClick={() => { if (!disabled) { onClick?.(); setOpen(false) } }}
      className={cn(
        'flex w-full items-center gap-2 px-3 py-2 text-left text-sm transition-colors',
        'focus:outline-none focus-visible:bg-neutral-100 dark:focus-visible:bg-neutral-700',
        danger
          ? 'text-danger-600 hover:bg-danger-50 dark:text-danger-400 dark:hover:bg-danger-700/20'
          : 'text-neutral-700 hover:bg-neutral-100 dark:text-neutral-200 dark:hover:bg-neutral-700',
        'disabled:cursor-not-allowed disabled:opacity-50',
        className,
      )}
    >
      {icon && <span className="flex-shrink-0 text-neutral-400">{icon}</span>}
      {children}
    </button>
  )
}

export function DropdownMenuSeparator() {
  return <div className="my-1 h-px bg-neutral-100 dark:bg-neutral-700" />
}

export function DropdownMenuLabel({ children }: { children: ReactNode }) {
  return (
    <div className="px-3 py-1.5 text-xs font-semibold text-neutral-500 dark:text-neutral-400 uppercase tracking-wide">
      {children}
    </div>
  )
}
