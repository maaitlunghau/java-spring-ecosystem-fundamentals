import { useState, useEffect, useCallback } from 'react'
import { Outlet, Link, NavLink, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthProvider'
import { useTheme, type Theme } from '../../context/ThemeProvider'
import { useToast } from '../ui/Toast'
import { Avatar } from '../ui/Avatar'
import { Breadcrumb } from '../ui/Breadcrumb'
import { DropdownMenu, DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuLabel } from '../ui/DropdownMenu'
import {
  IconLayoutDashboard, IconUsers, IconSettings, IconLogOut, IconBell,
  IconSearch, IconMoon, IconSun, IconMonitor, IconUser, IconMenu, IconX,
  IconPanelLeft, IconChevronDown, IconShield,
} from '../icons'
import { cn } from '../../lib/utils'
import { useLogout } from '../../hooks/useAuth'

const SIDEBAR_KEY = 'sidebar_collapsed'

interface NavItem {
  to: string
  icon: typeof IconLayoutDashboard
  label: string
  adminOnly?: boolean
}

const NAV_ITEMS: NavItem[] = [
  { to: '/dashboard', icon: IconLayoutDashboard, label: 'Dashboard' },
  { to: '/users', icon: IconUsers, label: 'Người dùng', adminOnly: true },
  { to: '/profile', icon: IconUser, label: 'Hồ sơ' },
  { to: '/settings', icon: IconSettings, label: 'Cài đặt' },
]

function ThemeToggle() {
  const { theme, setTheme } = useTheme()
  const themes: { key: Theme; icon: typeof IconSun; label: string }[] = [
    { key: 'light', icon: IconSun, label: 'Sáng' },
    { key: 'dark', icon: IconMoon, label: 'Tối' },
    { key: 'system', icon: IconMonitor, label: 'Hệ thống' },
  ]
  return (
    <div className="flex items-center rounded-full border border-neutral-200 dark:border-neutral-700 bg-neutral-100 dark:bg-neutral-800 p-0.5 gap-0.5">
      {themes.map(({ key, icon: Icon, label }) => (
        <button
          key={key}
          onClick={() => setTheme(key)}
          aria-label={label}
          className={cn(
            'flex h-7 w-7 items-center justify-center rounded-full transition-colors',
            theme === key
              ? 'bg-white dark:bg-neutral-700 text-neutral-900 dark:text-neutral-100 shadow-sm'
              : 'text-neutral-500 hover:text-neutral-700 dark:text-neutral-400 dark:hover:text-neutral-200',
          )}
        >
          <Icon size={14} />
        </button>
      ))}
    </div>
  )
}

interface SidebarNavProps {
  collapsed: boolean
  isAdmin: boolean
  onNavClick?: () => void
}

function SidebarNav({ collapsed, isAdmin, onNavClick }: SidebarNavProps) {
  const items = NAV_ITEMS.filter((i) => !i.adminOnly || isAdmin)
  return (
    <nav className="flex-1 px-2 py-4 space-y-0.5 overflow-y-auto">
      {items.map(({ to, icon: Icon, label }) => (
        <NavLink
          key={to}
          to={to}
          onClick={onNavClick}
          className={({ isActive }) =>
            cn(
              'flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors',
              'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary-500',
              isActive
                ? 'bg-primary-50 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300'
                : 'text-neutral-600 hover:bg-neutral-100 hover:text-neutral-900 dark:text-neutral-400 dark:hover:bg-neutral-800 dark:hover:text-neutral-100',
              collapsed && 'justify-center px-2',
            )
          }
        >
          <Icon size={18} className="shrink-0" />
          {!collapsed && <span className="truncate">{label}</span>}
        </NavLink>
      ))}
    </nav>
  )
}

function useBreadcrumb() {
  const location = useLocation()
  const segments = location.pathname.split('/').filter(Boolean)
  const crumbs: { label: string; href?: string }[] = [{ label: 'Trang chủ', href: '/dashboard' }]
  const labelMap: Record<string, string> = {
    dashboard: 'Dashboard',
    users: 'Người dùng',
    profile: 'Hồ sơ',
    settings: 'Cài đặt',
  }
  segments.forEach((seg, i) => {
    const href = '/' + segments.slice(0, i + 1).join('/')
    crumbs.push({ label: labelMap[seg] ?? seg, href: i < segments.length - 1 ? href : undefined })
  })
  return crumbs.length > 1 ? crumbs : []
}

export function AppLayout() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const { toast } = useToast()
  const breadcrumbs = useBreadcrumb()
  const isAdmin = user?.role === 'ADMIN'

  const [collapsed, setCollapsed] = useState(() => {
    try { return localStorage.getItem(SIDEBAR_KEY) === 'true' } catch { return false }
  })
  const [mobileOpen, setMobileOpen] = useState(false)
  const [loggingOut, setLoggingOut] = useState(false)

  const toggleCollapse = useCallback(() => {
    setCollapsed((prev) => {
      const next = !prev
      try { localStorage.setItem(SIDEBAR_KEY, String(next)) } catch {}
      return next
    })
  }, [])

  // Close mobile drawer on route change
  const location = useLocation()
  useEffect(() => { setMobileOpen(false) }, [location.pathname])

  // Lock body scroll when mobile drawer is open
  useEffect(() => {
    document.body.style.overflow = mobileOpen ? 'hidden' : ''
    return () => { document.body.style.overflow = '' }
  }, [mobileOpen])

  const logoutMutation = useLogout()

  const handleLogout = async () => {
    setLoggingOut(true)
    logoutMutation.mutate(undefined, {
      onSettled: () => {
        setLoggingOut(false)
        toast('Đã đăng xuất', 'success')
        navigate('/login')
      }
    })
  }

  const sidebarContent = (isMobile = false) => (
    <div className={cn('flex h-full flex-col', isMobile ? 'w-64' : collapsed ? 'w-[60px]' : 'w-60')}>
      {/* Logo */}
      <div className={cn('flex h-14 items-center border-b border-neutral-200 dark:border-neutral-800 shrink-0', collapsed && !isMobile ? 'justify-center px-2' : 'px-4 gap-2')}>
        <Link to="/dashboard" className="flex items-center gap-2">
          <span className="flex h-7 w-7 items-center justify-center rounded-lg bg-primary-600 text-white text-xs font-bold shrink-0">U</span>
          {(!collapsed || isMobile) && <span className="font-semibold text-neutral-900 dark:text-neutral-50 text-sm">UserMgmt</span>}
        </Link>
        {isMobile && (
          <button
            onClick={() => setMobileOpen(false)}
            className="ml-auto text-neutral-500 hover:text-neutral-900 dark:hover:text-neutral-100"
            aria-label="Đóng menu"
          >
            <IconX size={18} />
          </button>
        )}
      </div>

      <SidebarNav collapsed={collapsed && !isMobile} isAdmin={isAdmin} onNavClick={isMobile ? () => setMobileOpen(false) : undefined} />

      {/* Collapse toggle — desktop only */}
      {!isMobile && (
        <div className="shrink-0 border-t border-neutral-200 dark:border-neutral-800 p-2">
          <button
            onClick={toggleCollapse}
            aria-label={collapsed ? 'Mở rộng sidebar' : 'Thu gọn sidebar'}
            className={cn(
              'flex w-full items-center gap-3 rounded-lg px-3 py-2 text-sm text-neutral-500 hover:bg-neutral-100 hover:text-neutral-700 dark:hover:bg-neutral-800 dark:hover:text-neutral-300 transition-colors',
              collapsed && 'justify-center px-2',
            )}
          >
            <IconPanelLeft size={18} className={cn('shrink-0 transition-transform', collapsed && 'rotate-180')} />
            {!collapsed && <span>Thu gọn</span>}
          </button>
        </div>
      )}

      {/* User card at bottom */}
      {!collapsed || isMobile ? (
        <div className="shrink-0 border-t border-neutral-200 dark:border-neutral-800 p-3">
          <div className="flex items-center gap-3">
            <Avatar name={user?.fullName ?? 'User'} src={user?.avatarUrl ?? undefined} size="sm" />
            <div className="min-w-0 flex-1">
              <p className="truncate text-sm font-medium text-neutral-900 dark:text-neutral-100">{user?.fullName ?? 'Người dùng'}</p>
              <p className="truncate text-xs text-neutral-500 dark:text-neutral-400">{user?.email ?? ''}</p>
            </div>
            {isAdmin && <IconShield size={14} className="shrink-0 text-primary-500" />}
          </div>
        </div>
      ) : (
        <div className="shrink-0 border-t border-neutral-200 dark:border-neutral-800 p-2 flex justify-center">
          <Avatar name={user?.fullName ?? 'User'} src={user?.avatarUrl ?? undefined} size="sm" />
        </div>
      )}
    </div>
  )

  return (
    <div className="flex h-screen overflow-hidden bg-neutral-50 dark:bg-neutral-950">
      {/* Desktop sidebar */}
      <aside className={cn(
        'hidden md:flex flex-col shrink-0 border-r border-neutral-200 dark:border-neutral-800 bg-white dark:bg-neutral-900 transition-all duration-200 overflow-hidden',
        collapsed ? 'w-[60px]' : 'w-60',
      )}>
        {sidebarContent(false)}
      </aside>

      {/* Mobile drawer backdrop */}
      {mobileOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/40 md:hidden"
          onClick={() => setMobileOpen(false)}
          aria-hidden="true"
        />
      )}

      {/* Mobile drawer */}
      <aside className={cn(
        'fixed inset-y-0 left-0 z-50 md:hidden flex flex-col border-r border-neutral-200 dark:border-neutral-800',
        'bg-white dark:bg-neutral-900 transition-transform duration-200',
        mobileOpen ? 'translate-x-0' : '-translate-x-full',
      )}>
        {sidebarContent(true)}
      </aside>

      {/* Main area */}
      <div className="flex flex-1 flex-col min-w-0 overflow-hidden">
        {/* Topbar */}
        <header className="flex h-14 shrink-0 items-center gap-3 border-b border-neutral-200 dark:border-neutral-800 bg-white dark:bg-neutral-900 px-4">
          {/* Mobile menu button */}
          <button
            onClick={() => setMobileOpen(true)}
            className="md:hidden text-neutral-500 hover:text-neutral-900 dark:hover:text-neutral-100"
            aria-label="Mở menu"
          >
            <IconMenu size={20} />
          </button>

          {/* Breadcrumb */}
          <div className="flex-1 min-w-0">
            {breadcrumbs.length > 0 && <Breadcrumb items={breadcrumbs} />}
          </div>

          {/* Right actions */}
          <div className="flex items-center gap-2 shrink-0">
            {/* Search button — mobile */}
            <button
              className="flex h-8 w-8 items-center justify-center rounded-lg text-neutral-500 hover:bg-neutral-100 hover:text-neutral-700 dark:hover:bg-neutral-800 dark:hover:text-neutral-300 transition-colors"
              aria-label="Tìm kiếm"
            >
              <IconSearch size={16} />
            </button>

            <ThemeToggle />

            {/* Notifications */}
            <button
              className="relative flex h-8 w-8 items-center justify-center rounded-lg text-neutral-500 hover:bg-neutral-100 hover:text-neutral-700 dark:hover:bg-neutral-800 dark:hover:text-neutral-300 transition-colors"
              aria-label="Thông báo"
            >
              <IconBell size={16} />
              <span className="absolute right-1.5 top-1.5 h-1.5 w-1.5 rounded-full bg-danger-500" />
            </button>

            {/* User menu */}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <button className="flex items-center gap-2 rounded-lg px-2 py-1 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors">
                  <Avatar name={user?.fullName ?? 'User'} src={user?.avatarUrl ?? undefined} size="sm" />
                  <span className="hidden sm:block text-sm font-medium text-neutral-700 dark:text-neutral-300 max-w-[120px] truncate">
                    {user?.fullName ?? 'Người dùng'}
                  </span>
                  <IconChevronDown size={14} className="text-neutral-400" />
                </button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="right">
                <DropdownMenuLabel>{user?.email ?? ''}</DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={() => navigate('/profile')} icon={<IconUser size={15} />}>
                  Hồ sơ cá nhân
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => navigate('/settings')} icon={<IconSettings size={15} />}>
                  Cài đặt
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem danger onClick={handleLogout} disabled={loggingOut} icon={<IconLogOut size={15} />}>
                  {loggingOut ? 'Đang đăng xuất…' : 'Đăng xuất'}
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
