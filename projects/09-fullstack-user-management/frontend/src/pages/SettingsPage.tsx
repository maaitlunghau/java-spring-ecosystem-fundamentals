import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/Card'

export default function SettingsPage() {
  return (
    <div className="max-w-2xl">
      <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50 mb-6">Cài đặt</h1>
      <Card>
        <CardHeader>
          <CardTitle>Tùy chọn</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-neutral-500">Trang cài đặt — sẽ được hoàn thiện ở Step 8.</p>
        </CardContent>
      </Card>
    </div>
  )
}
