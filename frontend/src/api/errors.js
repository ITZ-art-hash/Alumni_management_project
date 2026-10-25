// Backend errors arrive as ApiResponse: { success, message, data, timestamp }
// where data carries field-level validation messages for 400s.
export function apiErrorMessage(error, fallback = 'Something went wrong') {
  const body = error?.response?.data
  if (!body) return fallback
  if (body.data && typeof body.data === 'object') {
    return Object.values(body.data).join('. ')
  }
  return body.message || fallback
}
