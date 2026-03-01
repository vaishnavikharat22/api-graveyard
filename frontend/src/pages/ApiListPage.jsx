import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getTrackedApis, deleteTrackedApi, checkApiNow } from '../api/trackedApis'
import AddApiModal from '../components/AddApiModal'

const StatusBadge = ({ status }) => {
  const styles = {
    ACTIVE: 'bg-green-500/20 text-green-400 border border-green-500/30',
    DEGRADED: 'bg-yellow-500/20 text-yellow-400 border border-yellow-500/30',
    DOWN: 'bg-red-500/20 text-red-400 border border-red-500/30',
    UNKNOWN: 'bg-slate-500/20 text-slate-400 border border-slate-500/30',
    DEPRECATED: 'bg-orange-500/20 text-orange-400 border border-orange-500/30',
  }
  const dots = {
    ACTIVE: 'bg-green-400',
    DEGRADED: 'bg-yellow-400',
    DOWN: 'bg-red-400',
    UNKNOWN: 'bg-slate-400',
    DEPRECATED: 'bg-orange-400',
  }
  return (
    <span className={`inline-flex items-center gap-1.5 text-xs font-medium px-2.5 py-1 rounded-full ${styles[status] || styles.UNKNOWN}`}>
      <span className={`w-1.5 h-1.5 rounded-full ${dots[status] || dots.UNKNOWN}`}></span>
      {status}
    </span>
  )
}

export default function ApiListPage() {
  const [showModal, setShowModal] = useState(false)
  const [checkingId, setCheckingId] = useState(null)
  const queryClient = useQueryClient()

  const { data: apis, isLoading } = useQuery({
    queryKey: ['trackedApis'],
    queryFn: getTrackedApis,
    refetchInterval: 60000,
  })

  const deleteMutation = useMutation({
    mutationFn: deleteTrackedApi,
    onSuccess: () => {
      queryClient.invalidateQueries(['trackedApis'])
      queryClient.invalidateQueries(['dashboardSummary'])
    }
  })

  const handleCheckNow = async (apiId) => {
    setCheckingId(apiId)
    try {
      await checkApiNow(apiId)
      queryClient.invalidateQueries(['trackedApis'])
    } finally {
      setCheckingId(null)
    }
  }

  const handleDelete = (apiId, apiName) => {
    if (window.confirm(`Remove "${apiName}" from monitoring?`)) {
      deleteMutation.mutate(apiId)
    }
  }

  return (
    <div className="space-y-6">

      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">My APIs</h1>
          <p className="text-slate-400 mt-1">
            {apis?.length || 0} API{apis?.length !== 1 ? 's' : ''} being monitored
          </p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="bg-primary-500 hover:bg-primary-600 text-white font-semibold px-5 py-2.5 rounded-lg transition-colors flex items-center gap-2"
        >
          <span>+</span> Add API
        </button>
      </div>

      {/* API List */}
      {isLoading ? (
        <div className="text-slate-400 text-center py-16">Loading your APIs...</div>
      ) : apis?.length === 0 ? (
        <div className="bg-slate-800 border border-slate-700 rounded-xl p-16 text-center">
          <div className="text-5xl mb-4">🔌</div>
          <h3 className="text-white font-semibold text-lg">No APIs tracked yet</h3>
          <p className="text-slate-400 mt-2 mb-6">Add your first API to start monitoring</p>
          <button
            onClick={() => setShowModal(true)}
            className="bg-primary-500 hover:bg-primary-600 text-white font-semibold px-6 py-2.5 rounded-lg transition-colors"
          >
            + Add Your First API
          </button>
        </div>
      ) : (
        <div className="grid gap-4">
          {(apis || []).map((api) => (
            <div
              key={api.apiId}
              className="bg-slate-800 border border-slate-700 rounded-xl p-6 hover:border-slate-600 transition-colors"
            >
              <div className="flex items-start justify-between gap-4">
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-3 flex-wrap">
                    <h3 className="text-white font-semibold">{api.apiName}</h3>
                    <StatusBadge status={api.currentStatus} />
                  </div>
                  <p className="text-slate-400 text-sm mt-1 truncate">{api.baseUrl}</p>
                  <div className="flex items-center gap-4 mt-3 text-xs text-slate-500 flex-wrap">
                    <span>Method: <span className="text-slate-300">{api.httpMethod}</span></span>
                    <span>Expects: <span className="text-slate-300">{api.expectedStatus}</span></span>
                    <span>Interval: <span className="text-slate-300">
                      {api.checkInterval >= 3600
                        ? `${api.checkInterval / 3600}h`
                        : `${api.checkInterval / 60}m`}
                    </span></span>
                    {api.lastChecked && (
                      <span>Last checked: <span className="text-slate-300">
                        {new Date(api.lastChecked).toLocaleTimeString()}
                      </span></span>
                    )}
                  </div>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                  <button
                    onClick={() => handleCheckNow(api.apiId)}
                    disabled={checkingId === api.apiId}
                    className="bg-slate-700 hover:bg-slate-600 disabled:opacity-50 text-white text-sm px-3 py-1.5 rounded-lg transition-colors"
                  >
                    {checkingId === api.apiId ? '⏳' : '⚡'} Check Now
                  </button>
                  <button
                    onClick={() => handleDelete(api.apiId, api.apiName)}
                    className="bg-red-500/10 hover:bg-red-500/20 text-red-400 text-sm px-3 py-1.5 rounded-lg transition-colors border border-red-500/20"
                  >
                    🗑️
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal - rendered outside the list */}
      {showModal && (
        <AddApiModal onClose={() => setShowModal(false)} />
      )}
    </div>
  )
}