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

        // Get the user ID - Make sure you have this in your userStore
        const userId = userStore.user?.id || userStore.userId
        if (!userId) {
          throw new Error('User ID not found')
        }

        // Token will be automatically added by the api interceptor
        // Add userId as a query parameter
        const response = await api.get(`/carts/getCart?userId=${userId}`)
        console.log('Response:', response)

        // Extract the items array from the nested structure
        const cartData = response.data.data

        // Check if cartData contains the items array
        if (cartData && Array.isArray(cartData.items)) {
          // Set the cartItems state property (not the getter)
          this.cartItems = cartData.items.map((item) => ({
            ...item,
            // Fix the image URL if needed
            image: item.productImageUrl ? item.productImageUrl.replace(/"/g, '') : '',
          }))

          console.log('Cart Items set:', this.cartItems)
        } else {
          console.error('No items array found in cart data')
          this.cartItems = [] // Update cartItems state, not the items getter
        }

        return { success: true }
      } catch (error) {
        console.error('Error fetching cart:', error)
        this.error = error.message
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    async addToCart(productOfferingId, quantity) {
      this.loading = true
      this.error = null

      try {
        const userStore = useUserStore()

        // Check if user is logged in
        if (!userStore.loggedIn) {
          throw new Error('Please sign in to add items to cart')
        }

        // Get the user ID
        const userId = userStore.user?.id || userStore.userId
        if (!userId) {
          throw new Error('User ID not found')
        }

        // The JWT token will be added in the header by the interceptor
        // Changed to match the backend endpoint and parameters
        await api.post(
          `/carts/addToCart?userId=${userId}&productOfferingId=${productOfferingId}&quantity=${quantity}`,
        )

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
        // Changed to match the backend endpoint and parameters
        await api.put(`/carts/updateCartItem?itemId=${itemId}`, {
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
        // Changed to match the backend endpoint
        await api.delete(`/carts/removeCartItem?itemId=${itemId}`)

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

        // Get the user ID
        const userId = userStore.user?.id || userStore.userId
        if (!userId) {
          throw new Error('User ID not found')
        }

        // The JWT token will be added in the header by the interceptor
        // Changed to match the backend endpoint
        await api.delete(`/carts/removeCart?userId=${userId}`)

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

    // Add a checkout method to match backend
    async checkout(email) {
      this.loading = true
      this.error = null

      try {
        const userStore = useUserStore()

        // Check if user is logged in
        if (!userStore.loggedIn) {
          throw new Error('User not authenticated')
        }

        // Get the user ID
        const userId = userStore.user?.id || userStore.userId
        if (!userId) {
          throw new Error('User ID not found')
        }

        // The JWT token will be added in the header by the interceptor
        await api.get(`/carts/checkout?email=${email}&userId=${userId}`)

        // Reset local cart state after checkout
        this.cartItems = []

        return { success: true }
      } catch (error) {
        console.error('Error checking out:', error)
        this.error = error.message
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },
  },
})
