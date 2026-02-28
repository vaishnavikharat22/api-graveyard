import { useQuery } from '@tanstack/react-query'
import { getDashboardSummary } from '../api/dashboard'
import { getTrackedApis } from '../api/trackedApis'
import { getAlerts } from '../api/alerts'
import { useAuth } from '../context/AuthContext'

const StatCard = ({ icon, label, value, color }) => (
  <div className={`bg-slate-800 border ${color} rounded-xl p-6`}>
    <div className="flex items-center justify-between">
      <div>
        <p className="text-slate-400 text-sm font-medium">{label}</p>
        <p className="text-3xl font-bold text-white mt-1">{value ?? '—'}</p>
      </div>
      <div className="text-3xl">{icon}</div>
    </div>
  </div>
)

const StatusBadge = ({ status }) => {
  const styles = {
    ACTIVE: 'bg-green-500/20 text-green-400 border-green-500/30',
    DEGRADED: 'bg-yellow-500/20 text-yellow-400 border-yellow-500/30',
    DOWN: 'bg-red-500/20 text-red-400 border-red-500/30',
    UNKNOWN: 'bg-slate-500/20 text-slate-400 border-slate-500/30',
    DEPRECATED: 'bg-orange-500/20 text-orange-400 border-orange-500/30',
  }
  return (
    <span className={`text-xs font-medium px-2 py-1 rounded-full border ${styles[status] || styles.UNKNOWN}`}>
      {status}
    </span>
  )
}

export default function DashboardPage() {
  const { user } = useAuth()

  const { data: summary, isLoading: summaryLoading } = useQuery({
    queryKey: ['dashboardSummary'],
    queryFn: getDashboardSummary,
    refetchInterval: 60000,
  })

  const { data: apis, isLoading: apisLoading } = useQuery({
    queryKey: ['trackedApis'],
    queryFn: getTrackedApis,
    refetchInterval: 60000,
  })

  const { data: alertsData } = useQuery({
    queryKey: ['alerts'],
    queryFn: () => getAlerts(false),
    refetchInterval: 30000,
  })

  const recentAlerts = alertsData?.content?.slice(0, 3) || []

  return (
    <div className="space-y-8">

      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-white">
          Welcome back, {user?.fullName?.split(' ')[0]} 👋
        </h1>
        <p className="text-slate-400 mt-1">Here's your API health overview</p>
      </div>

      {/* Stat Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          icon="🔌"
          label="Total APIs"
          value={summary?.totalApis}
          color="border-slate-700"
        />
        <StatCard
          icon="✅"
          label="Active"
          value={summary?.activeApis}
          color="border-green-500/30"
        />
        <StatCard
          icon="⚠️"
          label="Degraded"
          value={summary?.degradedApis}
          color="border-yellow-500/30"
        />
        <StatCard
          icon="🔴"
          label="Down"
          value={summary?.downApis}
          color="border-red-500/30"
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

        {/* Recent APIs */}
        <div className="bg-slate-800 border border-slate-700 rounded-xl p-6">
          <h2 className="text-white font-semibold mb-4 flex items-center gap-2">
            🔌 <span>Tracked APIs</span>
            <span className="ml-auto text-slate-400 text-sm font-normal">
              {apis?.length || 0} total
            </span>
          </h2>

          {apisLoading ? (
            <div className="text-slate-400 text-sm">Loading...</div>
          ) : apis?.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-slate-400 text-sm">No APIs tracked yet</p>
              <a href="/apis" className="text-primary-400 text-sm mt-1 block hover:underline">
                Add your first API →
              </a>
            </div>
          ) : (
            <div className="space-y-3">
              {apis?.slice(0, 5).map((api) => (
                <div key={api.apiId} className="flex items-center justify-between p-3 bg-slate-700/50 rounded-lg">
                  <div className="min-w-0 flex-1">
                    <p className="text-white text-sm font-medium truncate">{api.apiName}</p>
                    <p className="text-slate-400 text-xs truncate">{api.baseUrl}</p>
                  </div>
                  <StatusBadge status={api.currentStatus} />
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Recent Alerts */}
        <div className="bg-slate-800 border border-slate-700 rounded-xl p-6">
          <h2 className="text-white font-semibold mb-4 flex items-center gap-2">
            🚨 <span>Open Alerts</span>
            <span className="ml-auto text-slate-400 text-sm font-normal">
              {summary?.openAlerts || 0} open
            </span>
          </h2>

          {recentAlerts.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-2xl mb-2">🎉</p>
              <p className="text-green-400 text-sm font-medium">All clear!</p>
              <p className="text-slate-400 text-xs mt-1">No open alerts right now</p>
            </div>
          ) : (
            <div className="space-y-3">
              {recentAlerts.map((alert) => (
                <div key={alert.alertId} className="p-3 bg-slate-700/50 rounded-lg border-l-4 border-red-500">
                  <p className="text-white text-sm font-medium">{alert.title}</p>
                  <p className="text-slate-400 text-xs mt-1">{alert.apiName}</p>
                  <div className="flex items-center gap-2 mt-2">
                    <span className={`text-xs px-2 py-0.5 rounded-full ${
                      alert.severity === 'CRITICAL' ? 'bg-red-500/20 text-red-400' :
                      alert.severity === 'HIGH' ? 'bg-orange-500/20 text-orange-400' :
                      'bg-yellow-500/20 text-yellow-400'
                    }`}>
                      {alert.severity}
                    </span>
                    <span className="text-slate-500 text-xs">
                      {new Date(alert.createdAt).toLocaleTimeString()}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}