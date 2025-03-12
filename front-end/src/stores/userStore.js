// src/stores/userStore.js
import { defineStore } from 'pinia'
import api from '@/services/apiService'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    userId: null,
    isAuthenticated: false,
    loading: false,
    error: null,
    accessToken: null,
    refreshToken: null,
  }),

  getters: {
    loggedIn(state) {
      return state.isAuthenticated
    },
    currentUser(state) {
      return state.user
    },
    userName(state) {
      return state.user?.name || 'Guest'
    },
    hasError(state) {
      return !!state.error
    },
  },

  actions: {
    async login(email, password) {
      this.loading = true
      this.error = null

      try {
        const response = await api.post(
          '/auth/login',
          { email, password },
          // {
          //   withCredentials: true, // Important for CORS with credentials
          // },
        )

        console.log('Full response:', response)

        // Getting Access Token
        const accessToken = response.headers['access-token'] || response.headers['Access-Token']
        const refreshToken = response.headers['refresh-token'] || response.headers['Refresh-Token']

        console.log('Access token from headers:', accessToken)
        console.log('Refresh token from headers:', refreshToken)
        console.log('All headers:', response.headers)
        //From Commonresponse we are extracting data, commmonresponse also has statuscode and success
        const userData = response.data.data // Assuming your CommonResponse has a data field
        // console.log('User data:', userData)
        // console.log('Username from response:', response.data.data.name)

        const { name, email: userEmail, id } = userData
        // console.log('Username object:', name)
        // console.log('UserEmail object:', userEmail)

        // Store user info
        this.user = { name, email: userEmail }
        // console.log('Username below:', name)
        // console.log('UserEmail below:', userEmail)
        this.userId = id
        this.isAuthenticated = true

        this.accessToken = accessToken
        this.refreshToken = refreshToken

        // Store in localStorage for persistence
        localStorage.setItem('authToken', this.accessToken)
        if (this.refreshToken) {
          localStorage.setItem('refreshToken', this.refreshToken)
        }

        localStorage.setItem(
          'currentUser',
          JSON.stringify({
            name,
            email: userEmail,
            userId: id,
          }),
        )

        return { success: true }
      } catch (error) {
        console.error('Login error:', error)
        this.error = error.response?.data?.message || error.message || 'Login failed'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    async signup(name, email, password, confirmPassword) {
      this.loading = true
      this.error = null

      try {
        await api.post('/auth/register', {
          name,
          email,
          password,
          confirmPassword,
        })

        return { success: true }
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Signup failed'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    logout() {
      // Once we logout, we need to reset the user and authentication fields to null
      this.user = null
      this.userId = null
      this.isAuthenticated = false
      this.accessToken = null
      this.refreshToken = null

      // After we log out, we also need to remove from localStorage
      localStorage.removeItem('currentUser')
      localStorage.removeItem('authToken')
      localStorage.removeItem('refreshToken')
    },

    initializeUser() {
      const storedUser = localStorage.getItem('currentUser')
      const authToken = localStorage.getItem('authToken')

      if (storedUser && authToken) {
        this.user = JSON.parse(storedUser)
        this.userId = this.user.userId
        this.accessToken = authToken
        this.refreshToken = localStorage.getItem('refreshToken') || null
        this.isAuthenticated = true
      }
    },
  },
})
