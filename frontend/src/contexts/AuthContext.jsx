import { createContext, useContext, useEffect, useState } from 'react'
import * as authApi from '../api/auth.js'
import { getMe } from '../api/users.js'
import { getTokens, setTokens, clearTokens } from '../api/axiosClient.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!getTokens()?.accessToken) {
      setLoading(false)
      return
    }
    getMe()
      .then(setUser)
      .catch(() => clearTokens())
      .finally(() => setLoading(false))
  }, [])

  const login = async (email, password) => {
    const tokens = await authApi.login(email, password)
    setTokens(tokens)
    const me = await getMe()
    setUser(me)
    return me
  }

  const register = (payload) => authApi.register(payload)

  const logout = async () => {
    try {
      await authApi.logout()
    } catch {
      // best effort — clear locally regardless
    }
    clearTokens()
    setUser(null)
  }

  const value = {
    user,
    loading,
    isAdmin: user?.role === 'ROLE_ADMIN',
    login,
    register,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
