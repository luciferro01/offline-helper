// src/services/apiService.js
import axios from 'axios'

// Create base API instance with correct base URL
const api = axios.create({
  baseURL: 'http://localhost:8084',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Important for CORS with credentials
})

// Function to refresh token
// Defined separately to avoid circular dependency issues
const refreshAuthToken = async () => {
  try {
    const refreshToken = localStorage.getItem('refreshToken')

    if (!refreshToken) {
      throw new Error('No refresh token available')
    }

    // Call the refresh token endpoint
    const response = await axios.post(
      'http://localhost:8084/auth/refresh-token',
      { refreshToken },
      { withCredentials: true },
    )

    // Check if tokens are in headers
    const newAccessToken = response.headers['access-token'] || response.headers['Access-Token']
    const newRefreshToken = response.headers['refresh-token'] || response.headers['Refresh-Token']

    if (!newAccessToken) {
      throw new Error('No new access token received')
    }

    localStorage.setItem('authToken', newAccessToken)
    if (newRefreshToken) {
      localStorage.setItem('refreshToken', newRefreshToken)
    }

    return newAccessToken
  } catch (error) {
    // Clear tokens if refresh fails
    localStorage.removeItem('authToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('currentUser')

    // Force redirect to login
    window.location.href = '/signin'

    throw error
  }
}

// Response interceptor
api.interceptors.response.use(
  (response) => {
    // Check if response has data
    if (!response.data) {
      return response
    }

    const commonResponse = response.data

    // Validate the CommonResponse format
    if (commonResponse === null || typeof commonResponse !== 'object') {
      return Promise.reject(new Error('Invalid response format'))
    }

    // Check if request was successful based on the success flag
    if (commonResponse.success === false) {
      return Promise.reject(new Error(commonResponse.message || 'Request failed'))
    }

    // If statusCode indicates an error
    if (commonResponse.statusCode >= 400) {
      return Promise.reject(
        new Error(commonResponse.message || `HTTP Error: ${commonResponse.statusCode}`),
      )
    }

    return response
  },

  async (error) => {
    const originalRequest = error.config

    // Handle token expiration (typically 401 Unauthorized)
    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry &&
      localStorage.getItem('refreshToken')
    ) {
      originalRequest._retry = true

      try {
        // Try to refresh the token
        const newToken = await refreshAuthToken()

        // Update the failed request with new token and retry
        originalRequest.headers.Authorization = `Bearer ${newToken}`
        return axios(originalRequest)
      } catch (refreshError) {
        // If refresh fails, reject with the refresh error
        return Promise.reject(refreshError)
      }
    }

    // Handle other API errors
    if (error.response) {
      // The server responded with a status code outside the 2xx range
      return Promise.reject(
        new Error(error.response.data?.message || `Server error: ${error.response.status}`),
      )
    } else if (error.request) {
      // The request was made but no response was received
      return Promise.reject(new Error('No response from server. Please check your connection.'))
    } else {
      // Something else happened while setting up the request
      return Promise.reject(error)
    }
  },
)

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

export default api
