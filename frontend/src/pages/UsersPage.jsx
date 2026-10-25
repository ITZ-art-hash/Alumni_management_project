import { useCallback, useEffect, useState } from 'react'
import { listUsers, setUserStatus } from '../api/users.js'
import { apiErrorMessage } from '../api/errors.js'
import { useAuth } from '../contexts/AuthContext.jsx'
import { formatDateTime } from './DashboardPage.jsx'

export default function UsersPage() {
  const { user: me } = useAuth()
  const [page, setPage] = useState(0)
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)
  const [busyId, setBusyId] = useState(null)

  const load = useCallback(() => {
    listUsers(page)
      .then(setData)
      .catch((err) => setError(apiErrorMessage(err, 'Failed to load users')))
  }, [page])

  useEffect(load, [load])

  const toggleStatus = async (u) => {
    setError(null)
    setBusyId(u.id)
    try {
      await setUserStatus(u.id, !u.active)
      load()
    } catch (err) {
      setError(apiErrorMessage(err, 'Failed to update user'))
    } finally {
      setBusyId(null)
    }
  }

  if (!data && !error) return <p className="muted center">Loading…</p>

  return (
    <div className="card">
      <h1>Users</h1>
      {error && <p className="error">{error}</p>}
      {data && (
        <>
          <table>
            <thead>
              <tr>
                <th>ID</th><th>Name</th><th>Email</th><th>Role</th>
                <th>Verified</th><th>Status</th><th>Created</th><th></th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((u) => (
                <tr key={u.id} className={u.active ? '' : 'inactive'}>
                  <td>{u.id}</td>
                  <td>{u.firstName} {u.lastName}</td>
                  <td>{u.email}</td>
                  <td>{u.role.replace('ROLE_', '')}</td>
                  <td>{u.emailVerified ? 'Yes' : 'No'}</td>
                  <td>
                    {u.active
                      ? <span className="badge ok">active</span>
                      : <span className="badge warn">deactivated</span>}
                  </td>
                  <td>{formatDateTime(u.createdDate)}</td>
                  <td>
                    {u.id !== me.id && (
                      <button
                        className={u.active ? 'danger' : ''}
                        disabled={busyId === u.id}
                        onClick={() => toggleStatus(u)}
                      >
                        {u.active ? 'Deactivate' : 'Activate'}
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="pagination">
            <button disabled={data.page === 0} onClick={() => setPage(page - 1)}>Previous</button>
            <span className="muted">
              Page {data.page + 1} of {Math.max(data.totalPages, 1)} ({data.totalElements} users)
            </span>
            <button disabled={data.last} onClick={() => setPage(page + 1)}>Next</button>
          </div>
        </>
      )}
    </div>
  )
}
