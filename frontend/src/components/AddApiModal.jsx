import { useState } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createTrackedApi } from '../api/trackedApis'

export default function AddApiModal({ onClose }) {
  const [form, setForm] = useState({
    apiName: '',
    baseUrl: '',
    healthCheckUrl: '',
    documentationUrl: '',
    httpMethod: 'GET',
    expectedStatus: 200,
    checkInterval: 3600,
  })
  const [error, setError] = useState('')
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: createTrackedApi,
    onSuccess: () => {
      queryClient.invalidateQueries(['trackedApis'])
      queryClient.invalidateQueries(['dashboardSummary'])
      onClose()
    },
    onError: (err) => {
      setError(err.response?.data?.message || 'Failed to add API')
    }
  })

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    setError('')
    mutation.mutate({
      ...form,
      expectedStatus: parseInt(form.expectedStatus),
      checkInterval: parseInt(form.checkInterval),
    })
  }

  // Prevent closing when clicking inside modal
  const handleModalClick = (e) => {
    e.stopPropagation()
  }

  return (
    <div
      className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4"
      onClick={onClose}
    >
      <div
        className="bg-slate-800 border border-slate-700 rounded-2xl w-full max-w-lg shadow-2xl max-h-[90vh] overflow-y-auto"
        onClick={handleModalClick}
      >
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-slate-700">
          <h2 className="text-white font-semibold text-lg">Add New API</h2>
          <button
            type="button"
            onClick={onClose}
            className="text-slate-400 hover:text-white text-xl leading-none"
          >
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="bg-red-500/10 border border-red-500/50 text-red-400 rounded-lg p-3 text-sm">
              {error}
            </div>
          )}

          {/* API Name */}
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">
              API Name *
            </label>
            <input
              type="text"
              name="apiName"
              required
              autoComplete="off"
              placeholder="Stripe Payment API"
              value={form.apiName}
              onChange={handleChange}
              className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-primary-500 placeholder-slate-400 text-sm"
            />
          </div>

          {/* Base URL */}
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">
              Base URL *
            </label>
            <input
              type="text"
              name="baseUrl"
              required
              autoComplete="off"
              placeholder="https://api.stripe.com"
              value={form.baseUrl}
              onChange={handleChange}
              className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-primary-500 placeholder-slate-400 text-sm"
            />
          </div>

          {/* Health Check URL */}
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">
              Health Check URL
              <span className="text-slate-500 font-normal ml-1">(defaults to Base URL)</span>
            </label>
            <input
              type="text"
              name="healthCheckUrl"
              autoComplete="off"
              placeholder="https://api.stripe.com/health"
              value={form.healthCheckUrl}
              onChange={handleChange}
              className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-primary-500 placeholder-slate-400 text-sm"
            />
          </div>

          {/* Documentation URL */}
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">
              Documentation URL
            </label>
            <input
              type="text"
              name="documentationUrl"
              autoComplete="off"
              placeholder="https://stripe.com/docs"
              value={form.documentationUrl}
              onChange={handleChange}
              className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-primary-500 placeholder-slate-400 text-sm"
            />
          </div>

          {/* HTTP Method + Expected Status */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">
                HTTP Method
              </label>
              <select
                name="httpMethod"
                value={form.httpMethod}
                onChange={handleChange}
                className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-primary-500 text-sm"
              >
                <option value="GET">GET</option>
                <option value="POST">POST</option>
                <option value="PUT">PUT</option>
                <option value="DELETE">DELETE</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">
                Expected Status
              </label>
              <input
                type="number"
                name="expectedStatus"
                value={form.expectedStatus}
                onChange={handleChange}
                className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-primary-500 text-sm"
              />
            </div>
          </div>

          {/* Check Interval */}
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">
              Check Interval
            </label>
            <select
              name="checkInterval"
              value={form.checkInterval}
              onChange={handleChange}
              className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-primary-500 text-sm"
            >
              <option value={300}>Every 5 minutes</option>
              <option value={900}>Every 15 minutes</option>
              <option value={1800}>Every 30 minutes</option>
              <option value={3600}>Every hour</option>
              <option value={86400}>Every day</option>
            </select>
          </div>

          {/* Buttons */}
          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 bg-slate-700 hover:bg-slate-600 text-white rounded-lg py-2.5 text-sm font-medium transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={mutation.isPending}
              className="flex-1 bg-primary-500 hover:bg-primary-600 disabled:opacity-50 text-white rounded-lg py-2.5 text-sm font-semibold transition-colors"
            >
              {mutation.isPending ? 'Adding...' : 'Add API'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}