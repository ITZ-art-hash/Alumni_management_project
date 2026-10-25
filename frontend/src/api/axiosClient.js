import axios from 'axios'

const TOKENS_KEY = 'auth_tokens'

export const getTokens = () => {
  try {
    return JSON.parse(localStorage.getItem(TOKENS_KEY))
  } catch {
    return null
  }
}

export const setTokens = (tokens) => {
  localStorage.setItem(TOKENS_KEY, JSON.stringify(tokens))
}

export const clearTokens = () => {
  localStorage.removeItem(TOKENS_KEY)
}

// The user's IANA zone, e.g. "Asia/Kolkata" — read once, attached to every
// request so the backend renders all date-times in the caller's local time.
const BROWSER_TIMEZONE = Intl.DateTimeFormat().resolvedOptions().timeZone

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
})

apiClient.interceptors.request.use((config) => {
  config.headers['X-Timezone'] = BROWSER_TIMEZONE
  const tokens = getTokens()
  if (tokens?.accessToken && !config.headers.Authorization) {
    config.headers.Authorization = `Bearer ${tokens.accessToken}`
  }
  return config
})

// Single-flight refresh: when several requests 401 at once, only one
// refresh call goes out and the rest wait on the same promise.
let refreshPromise = null

const refreshTokens = () => {
  if (!refreshPromise) {
    const tokens = getTokens()
    refreshPromise = apiClient
      .post('/auth/refresh-token', { refreshToken: tokens?.refreshToken })
      .then((res) => {
        setTokens(res.data.data)
        return res.data.data
      })
      .finally(() => {
        refreshPromise = null
      })
  }
  return refreshPromise
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const { config, response } = error
    const isAuthEndpoint = config?.url?.startsWith('/auth/')

    if (response?.status === 401 && !isAuthEndpoint && !config._retried && getTokens()?.refreshToken) {
      config._retried = true
      try {
        const tokens = await refreshTokens()
        config.headers.Authorization = `Bearer ${tokens.accessToken}`
        return apiClient(config)
      } catch {
        clearTokens()
        window.location.assign('/login')
      }
    }
    return Promise.reject(error)
  }
)
