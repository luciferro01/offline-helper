// src/stores/cartStore.js
import { defineStore } from 'pinia'
import api from '@/services/apiService'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    cartId: null,
    userId: 3, // For testing - ideally this would come from a user store
    loading: false,
    error: null,
  }),

  getters: {
    totalItems(state) {
      return state.items.reduce((total, item) => total + item.quantity, 0)
    },

    totalPrice(state) {
      return state.items.reduce((total, item) => total + item.price * item.quantity, 0).toFixed(2)
    },

    isEmpty(state) {
      return state.items.length === 0
    },
  },

  actions: {
    // Fetch cart from server
    async fetchCart() {
      this.loading = true
      this.error = null

      try {
        // Using hardcoded userId for testing (adjust as needed)
        const response = await api.get(`/carts/${this.userId}`)

        if (response.data) {
          // Store cart ID from response
          this.cartId = response.data.id

          // Store items from response
          // this.items = response.data.items || []
          // Transform the data to match expected format
          this.items = (response.data.items || []).map((item) => ({
            id: item.id,
            productId: item.productOfferingId,
            quantity: item.quantity,
            name: item.productName, // Map productName to name
            price: item.price,
            image: item.imageUrl, // Map imageUrl to image
          }))

          this.saveCart() // Backup to localStorage
        }

        return this.items
      } catch (error) {
        console.error('Error fetching cart:', error)
        this.error = error.message
        this.loadCart() // Fallback to local storage if API fails
        return this.items
      } finally {
        this.loading = false
      }
    },

    // Add item to cart
    async addToCart(product) {
      this.loading = true
      this.error = null

      try {
        const itemDto = {
          productOfferingId: product.id, // Make sure this matches your DTO
          quantity: 1,
          productName: product.name,
          price: product.price,
          imageUrl: product.imageUrl || product.image,
        }

        // Using correct endpoint to add item to cart
        const response = await api.post(`/carts/${this.userId}/items`, itemDto)

        if (response.success) {
          // Refresh the cart to get the updated state from server
          await this.fetchCart()
        }

        return true
      } catch (error) {
        console.error('Error adding to cart:', error)
        this.error = error.message
        return false
      } finally {
        this.loading = false
      }
    },

    // Remove item from cart
    async removeFromCart(itemId) {
      this.loading = true
      this.error = null

      try {
        // Using correct endpoint to remove item from cart
        const response = await api.delete(`/carts/${this.userId}/items/${itemId}`)

        if (response.success) {
          // Remove from local state
          const index = this.items.findIndex((item) => item.id === itemId)
          if (index !== -1) {
            this.items.splice(index, 1)
            this.saveCart()
          }
        }

        return true
      } catch (error) {
        console.error('Error removing from cart:', error)
        this.error = error.message
        return false
      } finally {
        this.loading = false
      }
    },

    // Update item quantity
    async updateQuantity(itemId, quantity) {
      this.loading = true
      this.error = null

      try {
        if (quantity <= 0) {
          return this.removeFromCart(itemId)
        }

        const itemDto = {
          quantity: quantity,
        }

        // Using correct endpoint to update item in cart
        const response = await api.put(`/carts/${this.userId}/items/${itemId}`, itemDto)

        if (response.success) {
          // Update local state
          const item = this.items.find((item) => item.id === itemId)
          if (item) {
            item.quantity = quantity
            this.saveCart()
          }
        }

        return true
      } catch (error) {
        console.error('Error updating cart:', error)
        this.error = error.message
        return false
      } finally {
        this.loading = false
      }
    },

    // Clear the entire cart
    async clearCart() {
      this.loading = true
      this.error = null

      try {
        // Using correct endpoint to clear cart
        const response = await api.delete(`/carts/${this.userId}`)

        if (response.success) {
          this.items = []
          this.saveCart()
        }

        return true
      } catch (error) {
        console.error('Error clearing cart:', error)
        this.error = error.message
        return false
      } finally {
        this.loading = false
      }
    },

    // Local storage methods (for offline support)
    saveCart() {
      localStorage.setItem('cart', JSON.stringify(this.items))
      localStorage.setItem('cartId', this.cartId)
    },

    loadCart() {
      const savedCart = localStorage.getItem('cart')
      const savedCartId = localStorage.getItem('cartId')

      if (savedCart) {
        this.items = JSON.parse(savedCart)
      }

      if (savedCartId) {
        this.cartId = savedCartId
      }
    },
  },
})
