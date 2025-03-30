<template>
  <div class="cart-container">
    <h2>Shopping Cart</h2>

    <!-- Loading and Error states -->
    <div v-if="isLoading" class="loading-spinner">Loading cart...</div>

    <div v-if="error" class="error-message">
      {{ error }}
    </div>

    <!-- Display cart items -->
    <div v-if="cartItems.length > 0" class="cart-items-grid">
      <div v-for="item in cartItems" :key="item.id" class="product-card">
        <div class="product-image">
          <img :src="item.image || 'https://via.placeholder.com/150'" alt="Product Image" />
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ item.productName || 'Product' }}</h3>
          <p class="product-price">Price: ₹{{ formatPrice(item.price) }}</p>

          <!-- Quantity controls -->
          <div class="quantity-controls">
            <button
              @click="decrementQuantity(item)"
              :disabled="isUpdating || item.quantity <= 1"
              class="quantity-btn"
            >
              -
            </button>
            <span class="quantity-value">{{ item.quantity }}</span>
            <button @click="incrementQuantity(item)" :disabled="isUpdating" class="quantity-btn">
              +
            </button>
          </div>

          <button class="remove-btn" @click="removeItem(item)" :disabled="isUpdating">
            Remove
          </button>
        </div>
      </div>
    </div>

    <!-- Empty cart message -->
    <div v-else-if="!isLoading" class="empty-cart">
      <p>Your cart is empty.</p>
      <router-link to="/" class="continue-shopping">Continue Shopping</router-link>
    </div>

    <div v-if="cartItems.length > 0" class="cart-summary">
      <p><strong>Total Items:</strong> {{ totalItems }}</p>
      <p><strong>Total Price:</strong> ₹{{ formatPrice(totalPrice) }}</p>
      <br />
      <button class="clear-cart" @click="handleClearCart" :disabled="isUpdating">Clear Cart</button
      ><br />
      <!-- Move the Checkout button below the Clear Cart button -->
      <router-link to="/checkout" class="checkout-btn">Proceed to Checkout</router-link>
    </div>

    <!-- Debug panel - remove in production -->
    
  </div>
</template>

<script>
import { mapState, mapActions } from 'pinia'
import { useCartStore } from '@/stores/cartStore'
import { ref } from 'vue'

export default {
  setup() {
    const isUpdating = ref(false)
    const showDebug = ref(false)

    const toggleDebug = () => {
      showDebug.value = !showDebug.value
    }

    // Format price with commas for thousands
    const formatPrice = (price) => {
      if (!price) return '0'
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }

    return {
      isUpdating,
      showDebug,
      toggleDebug,
      formatPrice,
    }
  },

  data() {
    return {
      isLoading: false,
      error: null,
    }
  },

  computed: {
    ...mapState(useCartStore, ['items', 'totalItems', 'totalPrice']),

    cartItems() {
      return this.items
    },
  },

  methods: {
    ...mapActions(useCartStore, ['fetchCart', 'updateQuantity', 'removeFromCart', 'clearCart']),

    // Increment quantity of item
    async incrementQuantity(item) {
      try {
        this.isUpdating = true
        console.log(`Incrementing item ID: ${item.id}, Current quantity: ${item.quantity}`)

        // Make sure we're using the cart item's ID, not the product offering ID
        await this.updateQuantity(item.id, item.quantity + 1)
      } catch (error) {
        console.error('Error incrementing quantity:', error)
        this.error = error.message || 'Failed to update quantity'
      } finally {
        this.isUpdating = false
      }
    },

    // Decrement quantity of item
    async decrementQuantity(item) {
      if (item.quantity <= 1) return

      try {
        this.isUpdating = true
        console.log(`Decrementing item ID: ${item.id}, Current quantity: ${item.quantity}`)

        // Make sure we're using the cart item's ID, not the product offering ID
        await this.updateQuantity(item.id, item.quantity - 1)
      } catch (error) {
        console.error('Error decrementing quantity:', error)
        this.error = error.message || 'Failed to update quantity'
      } finally {
        this.isUpdating = false
      }
    },

    // Remove item from cart
    async removeItem(item) {
      try {
        this.isUpdating = true
        console.log(`Removing item ID: ${item.id}`)

        // Make sure we're using the cart item's ID, not the product offering ID
        await this.removeFromCart(item.id)
      } catch (error) {
        console.error('Error removing item:', error)
        this.error = error.message || 'Failed to remove item'
      } finally {
        this.isUpdating = false
      }
    },

    // Clear the entire cart and redirect to orders page
    async handleClearCart() {
      try {
        this.isUpdating = true
        await this.clearCart()
        this.$router.push('/orders')
      } catch (error) {
        console.error('Error clearing cart:', error)
        this.error = error.message || 'Failed to clear cart'
      } finally {
        this.isUpdating = false
      }
    },
  },

  async mounted() {
    this.isLoading = true
    try {
      await this.fetchCart()
      console.log('Cart fetched successfully:', this.cartItems)
    } catch (err) {
      console.error('Error fetching cart:', err)
      this.error = err.message || 'Failed to load cart'
    } finally {
      this.isLoading = false
    }
  },
}
</script>

<style scoped>
/* Cart styling */
.cart-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Roboto', Arial, sans-serif;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.cart-items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 20px;
  overflow-y: auto;
  max-height: 600px;
}

.product-card {
  border-radius: 8px;
  overflow: hidden;
  background-color: white;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.product-image {
  position: relative;
  height: 150px;
  overflow: hidden;
  background-color: #f9f9f9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10%;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.product-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.product-name {
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 0.75rem;
  color: #333;
}

.product-price {
  font-size: 1.1rem;
  font-weight: bold;
  color: #0095da;
  margin-bottom: 1rem;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 15px 0;
}

.quantity-btn {
  width: 32px;
  height: 32px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
}

.quantity-btn:hover:not(:disabled) {
  background-color: #0056b3;
}

.quantity-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.quantity-value {
  min-width: 30px;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
}

.remove-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-top: 10px;
  transition: background-color 0.2s;
  font-weight: bold;
}

.remove-btn:hover:not(:disabled) {
  background-color: #b02a37;
}

.remove-btn:disabled {
  background-color: #e9a8af;
  cursor: not-allowed;
}

.cart-summary {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  margin-top: 30px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.cart-summary p {
  font-size: 1.2rem;
  margin: 10px 0;
}

.clear-cart {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  font-weight: bold;
  transition: background-color 0.2s;
}

.clear-cart:hover:not(:disabled) {
  background-color: #0056b3;
}

.clear-cart:disabled {
  background-color: #a6c5e9;
  cursor: not-allowed;
}

.checkout-btn {
  background-color: #28a745;
  color: white;
  padding: 10px 20px;
  margin-top: 15px;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  text-decoration: none;
  display: inline-block;
  font-weight: bold;
  transition: background-color 0.2s;
}

.checkout-btn:hover {
  background-color: #218838;
}

.empty-cart {
  text-align: center;
  font-size: 18px;
  margin-top: 50px;
  background-color: #f8f9fa;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.continue-shopping {
  display: inline-block;
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #28a745;
  color: white;
  text-decoration: none;
  border-radius: 5px;
  font-weight: bold;
  transition: background-color 0.2s;
}

.continue-shopping:hover {
  background-color: #218838;
}

/* Loading and error styles */
.loading-spinner {
  text-align: center;
  padding: 40px;
  font-size: 18px;
  color: #007bff;
}

.error-message {
  background-color: #f8d7da;
  color: #721c24;
  padding: 15px;
  border-radius: 5px;
  margin: 20px 0;
  text-align: center;
}

/* Debug panel */
.debug-panel {
  margin-top: 30px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.debug-panel pre {
  background-color: #eee;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  max-height: 300px;
}

.debug-toggle {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .cart-items-grid {
    grid-template-columns: 1fr;
  }
}
</style>
