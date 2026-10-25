import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext.jsx'

export default function Navbar() {
  const { user, isAdmin, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <Link to="/" className="brand">TechnoBoost</Link>
      {user && (
        <div className="nav-links">
          <NavLink to="/">Dashboard</NavLink>
          {isAdmin && <NavLink to="/users">Users</NavLink>}
          <span className="muted">{user.firstName} {user.lastName}</span>
          <button onClick={handleLogout}>Logout</button>
        </div>
      )}
    </nav>
  )
}
