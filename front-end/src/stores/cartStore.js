// src/stores/cartStore.js
import { defineStore } from 'pinia'
import api from '@/services/apiService'
import { useUserStore } from './userStore'

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartItems: [],
    loading: false,
    error: null,
  }),

  getters: {
    totalAmount() {
      return this.cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
    },
    totalItems() {
      return this.cartItems.reduce((total, item) => total + item.quantity, 0)
    },
    isEmpty() {
      return this.cartItems.length === 0
    },
    // Add getters to match with Cart.vue
    items() {
      return this.cartItems
    },
    totalPrice() {
      return this.totalAmount
    },
  },

  actions: {
    async fetchCart() {
      this.loading = true
      this.error = null

      try {
        const userStore = useUserStore()

        // Check if user is logged in
        if (!userStore.loggedIn) {
          throw new Error('User not authenticated')
        }

        // Token will be automatically added by the api interceptor
        const response = await api.get('/carts/getCart')

        // Handle the data structure based on your API response
        this.cartItems = response.data.data || []

        return { success: true }
      } catch (error) {
        console.error('Error fetching cart:', error)
        this.error = error.message
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    async addToCart(productOfferingId, quantity = 1) {
      this.loading = true
      this.error = null

      try {
        const userStore = useUserStore()

        // Check if user is logged in
        if (!userStore.loggedIn) {
          throw new Error('Please sign in to add items to cart')
        }

        // The JWT token will be added in the header by the interceptor
        await api.post('/carts/items', {
          productOfferingId,
          quantity,
        })

        // Update local cart with response
        await this.fetchCart()

        return { success: true }
      } catch (error) {
        console.error('Error adding to cart:', error)
        this.error = error.message
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    async updateQuantity(itemId, quantity) {
      this.loading = true
      this.error = null

      try {
        const userStore = useUserStore()

        // Check if user is logged in
        if (!userStore.loggedIn) {
          throw new Error('User not authenticated')
        }

        // The JWT token will be added in the header by the interceptor
        await api.put(`/carts/items/${itemId}`, {
          quantity,
        })

        // Refresh cart
        await this.fetchCart()

        return { success: true }
      } catch (error) {
        console.error('Error updating quantity:', error)
        this.error = error.message
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    async removeFromCart(itemId) {
      this.loading = true
      this.error = null

      try {
        const userStore = useUserStore()

        // Check if user is logged in
        if (!userStore.loggedIn) {
          throw new Error('User not authenticated')
        }

        // The JWT token will be added in the header by the interceptor
        await api.delete(`/carts/items/${itemId}`)

        // Refresh cart
        await this.fetchCart()

        return { success: true }
      } catch (error) {
        console.error('Error removing from cart:', error)
        this.error = error.message
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    async clearCart() {
      this.loading = true
      this.error = null

      try {
        const userStore = useUserStore()

        // Check if user is logged in
        if (!userStore.loggedIn) {
          throw new Error('User not authenticated')
        }

        // The JWT token will be added in the header by the interceptor
        await api.delete('/carts')

        // Reset local cart state
        this.cartItems = []

        return { success: true }
      } catch (error) {
        console.error('Error clearing cart:', error)
        this.error = error.message
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },
  },
})
