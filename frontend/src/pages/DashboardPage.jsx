import { useAuth } from '../contexts/AuthContext.jsx'

// Backend timestamps arrive ISO-8601 with offset already converted to this
// browser's zone (X-Timezone header), so Date just renders them.
export const formatDateTime = (value) =>
  value ? new Date(value).toLocaleString() : '—'

export default function DashboardPage() {
  const { user } = useAuth()

  return (
    <div className="card">
      <h1>My profile</h1>
      <dl className="details">
        <dt>Name</dt>
        <dd>{user.firstName} {user.lastName}</dd>
        <dt>Email</dt>
        <dd>
          {user.email}{' '}
          {user.emailVerified
            ? <span className="badge ok">verified</span>
            : <span className="badge warn">unverified</span>}
        </dd>
        <dt>Role</dt>
        <dd>{user.role === 'ROLE_ADMIN' ? 'Administrator' : 'User'}</dd>
        <dt>Member since</dt>
        <dd>{formatDateTime(user.createdDate)}</dd>
      </dl>
    </div>
  )
}
