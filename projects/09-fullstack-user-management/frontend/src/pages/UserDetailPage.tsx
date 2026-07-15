import { useParams, Link } from 'react-router-dom'
import { Button } from '../components/ui/Button'
import { IconArrowLeft } from '../components/icons'

export default function UserDetailPage() {
  const { id } = useParams<{ id: string }>()
  return (
    <div>
      <div className="mb-6 flex items-center gap-3">
        <Link to="/users">
          <Button variant="ghost" size="sm" leftIcon={<IconArrowLeft size={16} />}>Quay lại</Button>
        </Link>
      </div>
      <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Chi tiết người dùng #{id}</h1>
      <p className="mt-2 text-neutral-500 dark:text-neutral-400">Trang chi tiết — sẽ được hoàn thiện ở Step 7.</p>
    </div>
  )
}
