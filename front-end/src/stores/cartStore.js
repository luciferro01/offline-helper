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
  },

  actions: {
    async fetchCart() {
      this.loading = true
      this.error = null

      try {
        const userStore = useUserStore()

        // Check if user is logged in
        if (!userStore.loggedIn || !userStore.userId) {
          throw new Error('User not authenticated')
        }

        // Fetch cart using userId
        const response = await api.get(`/carts/user/${userStore.userId}`)

        this.cartItems = response.data.items || []

        return { success: true }
      } catch (error) {
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
        if (!userStore.loggedIn || !userStore.userId) {
          throw new Error('Please sign in to add items to cart')
        }

        // Add item to cart
        const response = await api.post('/carts/add', {
          userId: userStore.userId,
          productOfferingId,
          quantity,
        })

        // Update local cart with response
        await this.fetchCart()

        return { success: true }
      } catch (error) {
        this.error = error.message
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    async updateCartItem(itemId, quantity) {
      this.loading = true
      this.error = null

      try {
        const userStore = useUserStore()

        // Check if user is logged in
        if (!userStore.loggedIn || !userStore.userId) {
          throw new Error('User not authenticated')
        }

        // Update cart item quantity
        await api.put(`/carts/item/${itemId}`, {
          userId: userStore.userId,
          quantity,
        })

        // Refresh cart
        await this.fetchCart()

        return { success: true }
      } catch (error) {
        this.error = error.message
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    async removeCartItem(itemId) {
      this.loading = true
      this.error = null

      try {
        const userStore = useUserStore()

        // Check if user is logged in
        if (!userStore.loggedIn || !userStore.userId) {
          throw new Error('User not authenticated')
        }

        // Remove item from cart
        await api.delete(`/carts/item/${itemId}`, {
          data: { userId: userStore.userId },
        })

        // Refresh cart
        await this.fetchCart()

        return { success: true }
      } catch (error) {
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
        if (!userStore.loggedIn || !userStore.userId) {
          throw new Error('User not authenticated')
        }

        // Clear entire cart
        await api.delete(`/carts/user/${userStore.userId}`)

        // Reset local cart state
        this.cartItems = []

        return { success: true }
      } catch (error) {
        this.error = error.message
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },
  },
})
