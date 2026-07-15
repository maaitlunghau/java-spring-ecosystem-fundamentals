import { forwardRef, useState } from 'react'
import { Input, type InputProps } from './Input'
import { IconEye, IconEyeOff } from '../icons'

type PasswordInputProps = Omit<InputProps, 'type' | 'rightAddon'>

export const PasswordInput = forwardRef<HTMLInputElement, PasswordInputProps>(
  (props, ref) => {
    const [show, setShow] = useState(false)
    return (
      <Input
        {...props}
        ref={ref}
        type={show ? 'text' : 'password'}
        rightAddon={
          <button
            type="button"
            onClick={() => setShow((s) => !s)}
            className="pointer-events-auto text-neutral-400 hover:text-neutral-600 dark:hover:text-neutral-300 transition-colors"
            aria-label={show ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
          >
            {show ? <IconEyeOff size={16} /> : <IconEye size={16} />}
          </button>
        }
      />
    )
  },
)
PasswordInput.displayName = 'PasswordInput'
