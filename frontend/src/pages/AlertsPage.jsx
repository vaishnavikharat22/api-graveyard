import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getAlerts, resolveAlert } from '../api/alerts'

const SeverityBadge = ({ severity }) => {
  const styles = {
    CRITICAL: 'bg-red-500/20 text-red-400 border border-red-500/30',
    HIGH: 'bg-orange-500/20 text-orange-400 border border-orange-500/30',
    MEDIUM: 'bg-yellow-500/20 text-yellow-400 border border-yellow-500/30',
    LOW: 'bg-blue-500/20 text-blue-400 border border-blue-500/30',
  }
  return (
    <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${styles[severity] || styles.LOW}`}>
      {severity}
    </span>
  )
}

const AlertTypeIcon = ({ type }) => {
  const icons = {
    DOWN: '🔴',
    DEGRADED: '⚠️',
    STATUS_CHANGE: '🔄',
    DEPRECATION: '⚰️',
    RESPONSE_CHANGE: '📡',
  }
  return <span>{icons[type] || '🔔'}</span>
}

export default function AlertsPage() {
  const [filter, setFilter] = useState('unresolved')
  const queryClient = useQueryClient()

  const { data, isLoading } = useQuery({
    queryKey: ['alerts', filter],
    queryFn: () => {
      if (filter === 'unresolved') return getAlerts(false)
      if (filter === 'resolved') return getAlerts(true)
      return getAlerts()
    },
    refetchInterval: 30000,
  })

  const resolveMutation = useMutation({
    mutationFn: resolveAlert,
    onSuccess: () => {
      queryClient.invalidateQueries(['alerts'])
      queryClient.invalidateQueries(['unreadCount'])
      queryClient.invalidateQueries(['dashboardSummary'])
    }
  })

  const alerts = data?.content || []

  return (
    <div className="space-y-6">

      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Alerts</h1>
          <p className="text-slate-400 mt-1">
            {data?.unreadCount || 0} unresolved alert{data?.unreadCount !== 1 ? 's' : ''}
          </p>
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="flex gap-2 bg-slate-800 border border-slate-700 rounded-lg p-1 w-fit">
        {[
          { key: 'unresolved', label: '🔴 Unresolved' },
          { key: 'resolved', label: '✅ Resolved' },
          { key: 'all', label: '📋 All' },
        ].map(tab => (
          <button
            key={tab.key}
            onClick={() => setFilter(tab.key)}
            className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
              filter === tab.key
                ? 'bg-primary-500 text-white'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Alerts List */}
      {isLoading ? (
        <div className="text-slate-400 text-center py-16">Loading alerts...</div>
      ) : alerts.length === 0 ? (
        <div className="bg-slate-800 border border-slate-700 rounded-xl p-16 text-center">
          <div className="text-5xl mb-4">
            {filter === 'unresolved' ? '🎉' : '📭'}
          </div>
          <h3 className="text-white font-semibold text-lg">
            {filter === 'unresolved' ? 'All clear!' : 'No alerts found'}
          </h3>
          <p className="text-slate-400 mt-2">
            {filter === 'unresolved'
              ? 'No unresolved alerts. Your APIs are healthy!'
              : 'No alerts match this filter.'}
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {alerts.map((alert) => (
            <div
              key={alert.alertId}
              className={`bg-slate-800 border rounded-xl p-5 transition-colors ${
                alert.isResolved
                  ? 'border-slate-700 opacity-60'
                  : 'border-slate-700 hover:border-slate-600'
              }`}
            >
              <div className="flex items-start justify-between gap-4">
                <div className="flex items-start gap-3 min-w-0 flex-1">

                  {/* Icon */}
                  <div className="text-xl mt-0.5">
                    <AlertTypeIcon type={alert.alertType} />
                  </div>

                  {/* Content */}
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2 flex-wrap">
                      <h3 className="text-white font-medium">{alert.title}</h3>
                      <SeverityBadge severity={alert.severity} />
                      {alert.isResolved && (
                        <span className="text-xs bg-green-500/20 text-green-400 border border-green-500/30 px-2 py-0.5 rounded-full">
                          Resolved
                        </span>
                      )}
                    </div>

                    <p className="text-slate-400 text-sm mt-1">{alert.description}</p>

                    <div className="flex items-center gap-3 mt-2 text-xs text-slate-500">
                      <span>🔌 {alert.apiName}</span>
                      <span>•</span>
                      <span>{new Date(alert.createdAt).toLocaleString()}</span>
                      {alert.resolvedAt && (
                        <>
                          <span>•</span>
                          <span>Resolved {new Date(alert.resolvedAt).toLocaleString()}</span>
                        </>
                      )}
                    </div>
                  </div>
                </div>

                {/* Resolve Button */}
                {!alert.isResolved && (
                  <button
                    onClick={() => resolveMutation.mutate(alert.alertId)}
                    disabled={resolveMutation.isPending}
                    className="shrink-0 bg-green-500/10 hover:bg-green-500/20 text-green-400 border border-green-500/20 text-sm px-3 py-1.5 rounded-lg transition-colors disabled:opacity-50"
                  >
                    ✓ Resolve
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}