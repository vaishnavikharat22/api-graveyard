import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { getUnreadCount } from '../api/alerts'

export default function MainLayout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [showUserMenu, setShowUserMenu] = useState(false)

  const { data: unreadData } = useQuery({
    queryKey: ['unreadCount'],
    queryFn: getUnreadCount,
    refetchInterval: 30000, // poll every 30 seconds
  })

  const unreadCount = unreadData?.count || 0

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const navItems = [
    { to: '/dashboard', icon: '📊', label: 'Dashboard' },
    { to: '/apis', icon: '🔌', label: 'My APIs' },
    { to: '/alerts', icon: '🚨', label: 'Alerts', badge: unreadCount },
  ]

  return (
    <div className="flex h-screen bg-slate-900 overflow-hidden">

      {/* Sidebar */}
      <aside className="w-64 bg-slate-800 border-r border-slate-700 flex flex-col">

        {/* Logo */}
        <div className="p-6 border-b border-slate-700">
          <div className="flex items-center gap-3">
            <span className="text-2xl">⚰️</span>
            <div>
              <h1 className="text-white font-bold text-lg leading-tight">API Graveyard</h1>
              <p className="text-slate-400 text-xs">Health Monitor</p>
            </div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-4 space-y-1">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `flex items-center justify-between px-4 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-primary-500/20 text-primary-400 border border-primary-500/30'
                    : 'text-slate-400 hover:text-white hover:bg-slate-700'
                }`
              }
            >
              <div className="flex items-center gap-3">
                <span>{item.icon}</span>
                <span>{item.label}</span>
              </div>
              {item.badge > 0 && (
                <span className="bg-red-500 text-white text-xs font-bold px-2 py-0.5 rounded-full">
                  {item.badge}
                </span>
              )}
            </NavLink>
          ))}
        </nav>

        {/* User Section */}
        <div className="p-4 border-t border-slate-700">
          <div className="relative">
            <button
              onClick={() => setShowUserMenu(!showUserMenu)}
              className="w-full flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-slate-700 transition-colors"
            >
              <div className="w-8 h-8 bg-primary-500 rounded-full flex items-center justify-center text-white text-sm font-bold">
                {user?.fullName?.charAt(0).toUpperCase() || 'U'}
              </div>
              <div className="flex-1 text-left">
                <p className="text-white text-sm font-medium truncate">{user?.fullName}</p>
                <p className="text-slate-400 text-xs truncate">{user?.email}</p>
              </div>
              <span className="text-slate-400 text-xs">▲</span>
            </button>

            {showUserMenu && (
              <div className="absolute bottom-full left-0 right-0 mb-1 bg-slate-700 rounded-lg border border-slate-600 overflow-hidden shadow-xl">
                <button
                  onClick={handleLogout}
                  className="w-full text-left px-4 py-2.5 text-sm text-red-400 hover:bg-slate-600 transition-colors"
                >
                  🚪 Sign Out
                </button>
              </div>
            )}
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 overflow-auto">
        {/* Top Bar */}
        <div className="bg-slate-800/50 border-b border-slate-700 px-8 py-4 flex items-center justify-between sticky top-0 backdrop-blur">
          <div className="text-slate-400 text-sm">
            {new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
          </div>
          {unreadCount > 0 && (
            <div className="flex items-center gap-2 bg-red-500/10 border border-red-500/30 text-red-400 text-sm px-3 py-1.5 rounded-lg">
              <span>🔔</span>
              <span>{unreadCount} unresolved alert{unreadCount > 1 ? 's' : ''}</span>
            </div>
          )}
        </div>

        {/* Page Content */}
        <div className="p-8">
          <Outlet />
        </div>
      </main>
    </div>
  )
}