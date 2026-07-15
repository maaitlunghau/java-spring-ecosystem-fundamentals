export interface StatCard {
  label: string
  value: string
  delta: string
  trend: 'up' | 'down' | 'neutral'
}

export interface ActivityItem {
  id: number
  user: string
  action: string
  timestamp: string
}

export const STAT_CARDS: StatCard[] = [
  { label: 'Tổng người dùng', value: '1,284', delta: '+12%', trend: 'up' },
  { label: 'Hoạt động hôm nay', value: '342', delta: '+5%', trend: 'up' },
  { label: 'Chờ xác thực email', value: '47', delta: '-3%', trend: 'down' },
  { label: 'Tài khoản bị khóa', value: '8', delta: '0%', trend: 'neutral' },
]

export const RECENT_ACTIVITY: ActivityItem[] = [
  { id: 1, user: 'Nguyễn Văn A', action: 'Đăng ký tài khoản mới', timestamp: '2026-07-15T10:30:00Z' },
  { id: 2, user: 'Trần Thị B', action: 'Đặt lại mật khẩu thành công', timestamp: '2026-07-15T09:15:00Z' },
  { id: 3, user: 'Lê Văn C', action: 'Xác thực email thành công', timestamp: '2026-07-15T08:45:00Z' },
  { id: 4, user: 'Phạm Thị D', action: 'Đăng nhập từ thiết bị mới', timestamp: '2026-07-15T08:00:00Z' },
  { id: 5, user: 'Hoàng Văn E', action: 'Cập nhật thông tin cá nhân', timestamp: '2026-07-14T23:30:00Z' },
]
