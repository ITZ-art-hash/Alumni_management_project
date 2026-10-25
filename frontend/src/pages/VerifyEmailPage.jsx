import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { verifyEmail } from '../api/auth.js'
import { apiErrorMessage } from '../api/errors.js'

export default function VerifyEmailPage() {
  const [params] = useSearchParams()
  const token = params.get('token')
  const [status, setStatus] = useState(token ? 'verifying' : 'missing')
  const [message, setMessage] = useState(null)

  useEffect(() => {
    if (!token) return
    verifyEmail(token)
      .then(() => setStatus('ok'))
      .catch((err) => {
        setStatus('failed')
        setMessage(apiErrorMessage(err, 'Verification failed'))
      })
  }, [token])

  return (
    <div className="card narrow center">
      <h1>Email verification</h1>
      {status === 'verifying' && <p className="muted">Verifying…</p>}
      {status === 'missing' && <p className="error">No verification token in the link.</p>}
      {status === 'failed' && <p className="error">{message}</p>}
      {status === 'ok' && <p className="success">Your email has been verified.</p>}
      <p><Link to="/login">Go to sign in</Link></p>
    </div>
  )
}
