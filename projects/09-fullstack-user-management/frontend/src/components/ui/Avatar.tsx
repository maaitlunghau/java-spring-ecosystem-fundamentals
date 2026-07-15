import { cn, initials } from '../../lib/utils'

type AvatarSize = 'xs' | 'sm' | 'md' | 'lg' | 'xl'

interface AvatarProps {
  name: string
  src?: string | null
  size?: AvatarSize
  className?: string
}

const sizeCls: Record<AvatarSize, string> = {
  xs: 'h-6 w-6 text-xs',
  sm: 'h-8 w-8 text-xs',
  md: 'h-9 w-9 text-sm',
  lg: 'h-11 w-11 text-base',
  xl: 'h-16 w-16 text-xl',
}

const colorSeeds = [
  'bg-blue-500', 'bg-violet-500', 'bg-emerald-500',
  'bg-amber-500', 'bg-rose-500', 'bg-sky-500',
  'bg-pink-500', 'bg-teal-500',
]

function colorForName(name: string) {
  let hash = 0
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash)
  return colorSeeds[Math.abs(hash) % colorSeeds.length]
}

export function Avatar({ name, src, size = 'md', className }: AvatarProps) {
  const fallback = initials(name)
  const color = colorForName(name)

  return (
    <span
      className={cn(
        'relative inline-flex items-center justify-center rounded-full overflow-hidden flex-shrink-0',
        sizeCls[size],
        !src && `${color} text-white`,
        className,
      )}
      title={name}
    >
      {src ? (
        <img src={src} alt={name} className="h-full w-full object-cover" />
      ) : (
        <span className="font-semibold leading-none">{fallback}</span>
      )}
    </span>
  )
}
