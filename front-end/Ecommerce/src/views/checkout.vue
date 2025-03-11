<template>
  <div class="checkout-container">
    <div class="checkout-form">
      <h2>Checkout</h2>

      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <div v-if="successMessage" class="success-message">
        {{ successMessage }}
      </div>

      <!-- Display Cart Items -->
      <div v-if="cartItems.length > 0 && !orderPlaced" class="cart-items-grid">
        <div v-for="item in cartItems" :key="item.id" class="product-card">
          <div class="product-image">
            <img :src="item.image" alt="Product Image" />
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ item.name }}</h3>
            <p class="product-price">Price: ₹{{ item.price }}</p>
            <p>Quantity: {{ item.quantity }}</p>
          </div>
        </div>
      </div>

      <!-- Place Order Button -->
      <!-- <button 
            v-if="cartItems.length > 0 && !orderPlaced"
            @click="placeOrder" 
            class="place-order-button" 
            :disabled="loading"
        >
          {{ loading ? 'Placing Order...' : 'Place Order' }}
        </button>
   -->
      <button
        v-if="cartItems.length > 0 && !orderPlaced"
        @click="showPopup = true"
        class="place-order-button"
      >
        Place the order
      </button>
      <Popup :visible="showPopup" @close="showPopup = false">
        <h2>Order placed</h2>
        <p>Thank you, You have successfully placed the order.</p>
      </Popup>
      <div class="order-summary">
        <p><strong>Total Items:</strong> {{ totalItems }}</p>
        <p><strong>Total Price:</strong> ₹{{ totalPrice }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref } from 'vue'
import { useCartStore } from '@/stores/cartStore'
import { useUserStore } from '@/stores/userStore'
import { useRouter } from 'vue-router'
import Popup from '@/components/Popup.vue'

export default {
  name: 'checkout',
  components: {
    Popup,
  },

  setup() {
    const showPopup = ref(false)
    const cartStore = useCartStore()
    const userStore = useUserStore()
    const router = useRouter()

    const errorMessage = ref('')
    const successMessage = ref('')
    const loading = computed(() => userStore.loading)
    const orderPlaced = ref(false)

    const totalItems = computed(() => cartStore.totalItems)
    const totalPrice = computed(() => cartStore.totalPrice)
    const cartItems = computed(() => cartStore.items)

    const placeOrder = async () => {
      try {
        await new Promise((resolve) => setTimeout(resolve, 1500)) // Simulate a delay.
        cartStore.clearCart() // Clear the cart
        orderPlaced.value = true // Set flag to indicate order placed
        successMessage.value = 'Order placed successfully! Thank you!'

        router.push('/confirmation')
      } catch (error) {
        console.error('Error placing order:', error)
        errorMessage.value = 'Failed to place order. Please try again.'
      }
    }

    return {
      errorMessage,
      successMessage,
      loading,
      totalItems,
      totalPrice,
      cartItems,
      showPopup,
      placeOrder,
      orderPlaced,
    }
  },
}
</script>

<style scoped>
.checkout-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
}

.checkout-form {
  width: 100%;
  max-width: 600px; /* Increased max width */
  padding: 2rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.place-order-button {
  background-color: #4caf50; /* Green */
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  cursor: pointer;
  border: none;
  border-radius: 5px;
}

.checkout-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #1052ec;
  border: 1px solid lightblue;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 1rem;
}

.checkout-button:hover {
  background-color: gray;
}

.checkout-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.place-order-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #28a745; /* Green for "Place Order" */
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 1rem;
}

.place-order-button:hover {
  background-color: #218838;
}

.place-order-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error-message {
  color: #c40000;
  background-color: #fff1f1;
  padding: 0.75rem;
  margin-bottom: 1rem;
  border: 1px solid #c40000;
  border-radius: 4px;
}

.success-message {
  color: #28a745;
  background-color: #f0fff0;
  padding: 0.75rem;
  margin-bottom: 1rem;
  border: 1px solid #28a745;
  border-radius: 4px;
}

.order-summary {
  text-align: center;
  margin-top: 20px;
}

/* Cart Items Display */
.cart-items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); /* Responsive columns */
  gap: 15px;
  margin-top: 20px;
}

.product-card {
  border-radius: 8px;
  overflow: hidden;
  background-color: white;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
  padding: 1rem;
}

.product-image {
  position: relative;
  height: 120px;
  overflow: hidden;
  background-color: #f9f9f9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.product-info {
  margin-top: 0.5rem;
}

.product-name {
  font-size: 0.9rem;
  font-weight: bold;
  margin-bottom: 0.3rem;
  color: #333;
}

.product-price {
  font-size: 0.9rem;
  color: #0095da;
}
</style>
