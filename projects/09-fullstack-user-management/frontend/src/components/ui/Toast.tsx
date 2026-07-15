import { createContext, useCallback, useContext, useReducer, type ReactNode } from 'react'
import { createPortal } from 'react-dom'
import { cn } from '../../lib/utils'
import { IconX, IconCheckCircle, IconAlertCircle, IconInfo } from '../icons'

export type ToastVariant = 'success' | 'danger' | 'warning' | 'info'

export interface Toast {
  id: string
  message: string
  variant?: ToastVariant
  duration?: number
}

type Action =
  | { type: 'ADD'; toast: Toast }
  | { type: 'REMOVE'; id: string }

function reducer(state: Toast[], action: Action): Toast[] {
  if (action.type === 'ADD') return [...state, action.toast]
  if (action.type === 'REMOVE') return state.filter((t) => t.id !== action.id)
  return state
}

interface ToastContextValue {
  toast: (msg: string, variant?: ToastVariant, duration?: number) => void
}

const ToastContext = createContext<ToastContextValue>({ toast: () => {} })

let _counter = 0

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, dispatch] = useReducer(reducer, [])

  const toast = useCallback((message: string, variant: ToastVariant = 'info', duration = 4000) => {
    const id = String(++_counter)
    dispatch({ type: 'ADD', toast: { id, message, variant, duration } })
    setTimeout(() => dispatch({ type: 'REMOVE', id }), duration)
  }, [])

  const remove = useCallback((id: string) => dispatch({ type: 'REMOVE', id }), [])

  return (
    <ToastContext.Provider value={{ toast }}>
      {children}
      {createPortal(
        <div
          aria-live="polite"
          className="fixed bottom-4 right-4 z-50 flex flex-col gap-2 max-w-sm w-full"
        >
          {toasts.map((t) => (
            <ToastItem key={t.id} toast={t} onClose={() => remove(t.id)} />
          ))}
        </div>,
        document.body,
      )}
    </ToastContext.Provider>
  )
}

const variantCls: Record<ToastVariant, string> = {
  success: 'border-success-200 bg-success-50 text-success-800 dark:bg-success-700/20 dark:border-success-700 dark:text-success-300',
  danger: 'border-danger-200 bg-danger-50 text-danger-800 dark:bg-danger-700/20 dark:border-danger-700 dark:text-danger-300',
  warning: 'border-warning-200 bg-warning-50 text-warning-800 dark:bg-warning-700/20 dark:border-warning-700 dark:text-warning-300',
  info: 'border-info-200 bg-info-50 text-info-800 dark:bg-info-700/20 dark:border-info-700 dark:text-info-300',
}

const variantIcon: Record<ToastVariant, ReactNode> = {
  success: <IconCheckCircle size={16} />,
  danger: <IconAlertCircle size={16} />,
  warning: <IconAlertCircle size={16} />,
  info: <IconInfo size={16} />,
}

function ToastItem({ toast, onClose }: { toast: Toast; onClose: () => void }) {
  const variant = toast.variant ?? 'info'
  return (
    <div
      role="alert"
      className={cn(
        'flex items-start gap-3 rounded-lg border p-4 shadow-md',
        'animate-in fade-in slide-in-from-right-4',
        variantCls[variant],
      )}
    >
      <span className="mt-0.5 flex-shrink-0">{variantIcon[variant]}</span>
      <p className="text-sm flex-1">{toast.message}</p>
      <button
        onClick={onClose}
        aria-label="Đóng thông báo"
        className="flex-shrink-0 opacity-60 hover:opacity-100 transition-opacity"
      >
        <IconX size={14} />
      </button>
    </div>
  )
}

export function useToast() {
  return useContext(ToastContext)
}
