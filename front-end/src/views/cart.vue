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
          <img :src="item.image" alt="Product Image" />
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ item.name }}</h3>
          <p class="product-price">Price: ₹{{ item.price }}</p>

          <!-- Quantity controls -->
          <div class="quantity-controls">
            <button @click="decrementQuantity(item.id)" :disabled="item.quantity <= 1">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="incrementQuantity(item.id)">+</button>
          </div>

          <button class="remove-btn" @click="removeItem(item.id)">Remove</button>
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
      <p><strong>Total Price:</strong> ₹{{ totalPrice }}</p>
      <br />
      <button class="clear-cart" @click="handleClearCart">Clear Cart</button><br />
      <!-- Move the Checkout button below the Clear Cart button -->
      <router-link to="/checkout" class="checkout-btn">Proceed to Checkout</router-link>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'pinia'
import { useCartStore } from '@/stores/cartStore'

export default {
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
    incrementQuantity(itemId) {
      const item = this.cartItems.find((item) => item.id === itemId)
      if (item) {
        this.updateQuantity(itemId, item.quantity + 1)
      }
    },

    // Decrement quantity of item
    decrementQuantity(itemId) {
      const item = this.cartItems.find((item) => item.id === itemId)
      if (item && item.quantity > 1) {
        this.updateQuantity(itemId, item.quantity - 1)
      }
    },

    // Remove item from cart
    removeItem(itemId) {
      this.removeFromCart(itemId)
    },

    // Clear the entire cart and redirect to orders page
    handleClearCart() {
      this.clearCart()
      this.$router.push('/orders')
    },
  },

  async mounted() {
    this.isLoading = true
    try {
      await this.fetchCart()
    } catch (err) {
      this.error = err.message
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
}

.cart-items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
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
  cursor: pointer;
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
  padding: 12px;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.product-name {
  font-size: 0.9rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
  color: #333;
}

.product-price {
  font-size: 1rem;
  font-weight: bold;
  color: #0095da;
  margin-bottom: 0.3rem;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}

.quantity-controls button {
  padding: 5px 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.quantity-controls button:hover {
  background-color: #0056b3;
}

.quantity-controls button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.remove-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 4px;
  margin-top: 10px;
}

.remove-btn:hover {
  background-color: #b02a37;
}

.cart-summary {
  text-align: center;
  margin-top: 20px;
  font-size: 1.1rem;
}

.clear-cart {
  background-color: #007bff;
  color: white;
  padding: 8px;
  border: none;
  cursor: pointer;
  border-radius: 4px;
}

.clear-cart:hover {
  background-color: #0056b3;
}

.checkout-btn {
  background-color: #28a745;
  color: white;
  padding: 8px;
  margin-top: 10px;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  text-decoration: none;
  display: inline-block;
}

.checkout-btn:hover {
  background-color: #218838;
}

.empty-cart {
  text-align: center;
  font-size: 18px;
  margin-top: 50px;
}

.continue-shopping {
  display: inline-block;
  margin-top: 10px;
  padding: 8px 12px;
  background-color: #28a745;
  color: white;
  text-decoration: none;
  border-radius: 5px;
}

.continue-shopping:hover {
  background-color: #218838;
}

/* Loading and error styles */
.loading-spinner {
  text-align: center;
  padding: 30px;
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
</style>
