<template>
  <div class="checkout-container">
    <div class="checkout-form">
      <!-- Order Placed Success Message -->
      <div v-if="orderPlaced" class="order-placed-message">
        <div class="success-icon">✓</div>
        <h2>Order Placed Successfully!</h2>
        <p>Thank you for your purchase. Your order has been confirmed.</p>
        <p>
          A confirmation email has been sent to <strong>{{ email }}</strong>
        </p>
        <p class="redirect-message">You will be redirected to the homepage in a few seconds...</p>
      </div>

      <!-- Normal Checkout Flow -->
      <div v-else>
        <h2>Checkout</h2>
        <br />

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
        </div>

        <!-- Display Cart Items -->
        <div v-if="cartItems.length > 0" class="cart-items-grid">
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
        <br />

        <!-- Place Order Button -->
        <button
          v-if="cartItems.length > 0 && !loading"
          class="place-order-button"
          @click="emailPopup"
        >
          Place the order
        </button>

        <!-- Order Summary -->
        <div class="order-summary" v-if="cartItems.length > 0">
          <p><strong>Total Items:</strong> {{ totalItems }}</p>
          <p><strong>Total Price:</strong> ₹{{ totalPrice }}</p>
        </div>
      </div>
    </div>
  </div>

  <!-- Email Popup for Email input -->
  <Popup :visible="showEmailPopup" @close="onEmailPopupClose">
    <form @submit.prevent="submitEmail">
      <input
        type="email"
        v-model="email"
        placeholder="Enter email to place the order"
        class="email-input"
        required
      />
      <button type="submit" class="submit-email-button" :disabled="loading">
        {{ loading ? 'Submitting...' : 'Submit' }}
      </button>
    </form>
  </Popup>
</template>

<script>
import { computed, ref } from 'vue'
import { useCartStore } from '@/stores/cartStore'
import { useUserStore } from '@/stores/userStore'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'
import { onMounted } from 'vue'
import Popup from '@/components/Popup.vue'
import api from '@/services/apiService'

export default {
  name: 'Checkout',
  components: {
    Popup,
  },

  setup() {
    const showEmailPopup = ref(false)
    const email = ref('')
    const errorMessage = ref('')
    const successMessage = ref('')
    const loading = ref(false)
    const orderPlaced = ref(false)
    const user_id = ref('')

    const cartStore = useCartStore()
    const userStore = useUserStore()
    const router = useRouter()

    const totalItems = computed(() => cartStore.totalItems)
    const totalPrice = computed(() => cartStore.totalPrice)
    const cartItems = computed(() => cartStore.items)

    const emailPopup = () => {
      showEmailPopup.value = true
    }

    // Check if cart is empty and redirect if it is
    onMounted(() => {
      if (cartItems.value.length === 0) {
        router.push('/')
      }

      user_id.value = 1 // For demo; in production, get from userStore
      console.log('User ID:', user_id.value)
    })

    const submitEmail = async () => {
      if (!email.value) {
        errorMessage.value = 'Please enter a valid email.'
        return
      }

      loading.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        if (!user_id.value) {
          errorMessage.value = 'User ID is required. Please login.'
          return // Stop if no user ID
        }

        // const emailData = {
        //   email: email.value,
        // }

        // console.log('Sending email data:', emailData)

        const response = await api.get(`/carts/checkout?email=${email.value}`)
        console.log(response)

        if (response.status === 200 || response.status === 201) {
          // Close the popup
          showEmailPopup.value = false

          // Mark order as placed
          orderPlaced.value = true

          // Show success message
          successMessage.value = 'Order placed successfully! Thank you for your purchase.'

          // Clear the cart
          await cartStore.clearCart()

          // Redirect to homepage after 3 seconds
          setTimeout(() => {
            router.push('/')
          }, 5000)
        } else {
          // Unexpected status code
          console.error('Unexpected status code:', response.status)
          errorMessage.value = `Failed to submit email. Server returned status: ${response.status}.`
        }
      } catch (error) {
        console.error('Error submitting email:', error)

        if (error.response) {
          console.error('Server responded with:', error.response.data)

          // More specific handling for server errors
          if (error.response.status === 400) {
            errorMessage.value =
              error.response.data.message ||
              'Invalid email format. Please check your email address.'
          } else if (error.response.status === 404) {
            errorMessage.value = error.response.data.message || 'User not found. Please try again.'
          } else {
            errorMessage.value =
              error.response.data.message || 'An error occurred. Please try again.'
          }
        } else if (error.request) {
          console.error('No response received from server:', error.request)
          errorMessage.value = 'No response from server. Please check your connection.'
        } else {
          console.error('Error setting up the request:', error.message)
          errorMessage.value = 'An error occurred. Please try again.'
        }
      } finally {
        loading.value = false
      }
    }

    const onEmailPopupClose = () => {
      showEmailPopup.value = false
    }

    return {
      email,
      errorMessage,
      successMessage,
      loading,
      totalItems,
      totalPrice,
      cartItems,
      showEmailPopup,
      orderPlaced,
      submitEmail,
      onEmailPopupClose,
      emailPopup,
    }
  },
}
</script>

<style scoped>
.checkout-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding-top: 20px;
}

.checkout-form {
  width: 100%;
  max-width: 600px;
  padding: 2rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  max-height: 80vh;
  overflow-y: auto;
}

.place-order-button {
  background-color: #4caf50;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: block;
  font-size: 16px;
  cursor: pointer;
  border: none;
  border-radius: 5px;
  margin: 0 auto;
}

.place-order-button:hover {
  background-color: #218838;
}

.error-message {
  color: #c40000;
  background-color: #fff1f1;
  padding: 0.75rem;
  margin-bottom: 1rem;
  border: 1px solid #c40000;
  border-radius: 4px;
  width: 100%;
}

.success-message {
  color: #28a745;
  background-color: #f0fff0;
  padding: 0.75rem;
  margin-bottom: 1rem;
  border: 1px solid #28a745;
  border-radius: 4px;
  width: 100%;
}

.order-summary {
  text-align: center;
  margin-top: 20px;
  width: 100%;
}

.cart-items-grid {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
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

/* Popup styling */
.email-input {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1rem;
}

.submit-email-button {
  background-color: #4caf50;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
}

.submit-email-button:hover {
  background-color: #218838;
}

/* Order Placed Success Message */
.order-placed-message {
  text-align: center;
  padding: 2rem;
  margin: 1rem 0;
  background-color: #f0fff0;
  border: 1px solid #28a745;
  border-radius: 8px;
  animation: fadeIn 0.5s ease-in-out;
  width: 100%;
}

.success-icon {
  font-size: 4rem;
  color: #28a745;
  margin-bottom: 1rem;
  background-color: #f0fff0;
  border-radius: 50%;
  width: 100px;
  height: 100px;
  line-height: 100px;
  display: inline-block;
  animation: bounceIn 0.8s ease;
}

.redirect-message {
  margin-top: 2rem;
  font-size: 0.9rem;
  color: #666;
  animation: pulse 1.5s infinite;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes bounceIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.2);
  }
  70% {
    transform: scale(0.9);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes pulse {
  0% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.6;
  }
}
</style>
