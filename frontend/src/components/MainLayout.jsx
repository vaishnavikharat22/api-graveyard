import { Outlet } from 'react-router-dom'

export default function MainLayout() {
  return (
    <div className="flex h-screen bg-slate-900">
      <div className="w-64 bg-slate-800 p-4 text-white">Sidebar</div>
      <div className="flex-1 overflow-auto">
        <Outlet />
      </div>
    </div>
  )
}