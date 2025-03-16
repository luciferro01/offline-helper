<!-- <template>
  <div class="order-history-container">
    <h2>Your Orders</h2>

    <div v-if="orderStore.loading" class="loading-state">
      <p>Loading your orders...</p>
    </div>

    <div v-else-if="orderStore.error" class="error-state">
      <p>{{ orderStore.error }}</p>
      <button @click="retryFetch" class="retry-btn">Retry</button>
    </div>

    <div v-else-if="orderStore.orders && orderStore.orders.length > 0" class="order-items-grid">
      <div v-for="(order, index) in orderStore.orders" :key="order.id || index" class="order-card">
        <div class="order-header">
          <h3>Order #{{ order.id || index + 1 }}</h3>
          <p><strong>Date:</strong> {{ formatDate(order.createdAt) }}</p>
        </div>

        <div class="product-card">
          <div class="product-image">
            <img
              :src="order.productImageUrl = 'https://via.placeholder.com/150'"
              alt="Product Image"
            />
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ order.productOfferingName || 'Product Name' }}</h3>
            <p class="product-price">Price: ₹{{ formatPrice(order.price || 0) }}</p>
            <p class="product-quantity">Quantity: {{ order.quantity || 1 }}</p>
            <br />
            <router-link
              v-if="order.productOfferingId"
              :to="`/review/${order.productOfferingId}`"
              class="review-btn"
            >
              Add review
            </router-link>
          </div>
        </div>

        <p class="order-summary">
          <strong>Total Price:</strong> ₹{{
            formatPrice((order.price || 0) * (order.quantity || 1))
          }}
        </p>
      </div>
    </div>

    <div v-else class="empty-order-history">
      <p>No orders placed yet.</p>
      <router-link to="/" class="continue-shopping">Continue Shopping</router-link>
    </div>

    <button class="go-home" @click="goToHomePage">Go to Homepage</button>

    
  </div>
</template>

<script>
import { useOrderStore } from '@/stores/orderstore'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

export default {
  setup() {
    const orderStore = useOrderStore()
    const router = useRouter()
    const showDebug = ref(false)

    onMounted(async () => {
      try {
        await orderStore.fetchOrders()
        console.log('Orders fetched:', orderStore.orders)
      } catch (error) {
        console.error('Error in component while fetching orders:', error)
      }
    })

    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'

      try {
        const date = new Date(dateString)
        return date.toLocaleDateString()
      } catch (e) {
        return dateString
      }
    }

    const formatPrice = (price) => {
      if (price === null || price === undefined) return '0'
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }

    const goToHomePage = () => {
      router.push('/')
    }

    const retryFetch = () => {
      orderStore.fetchOrders()
    }

    const toggleDebug = () => {
      showDebug.value = !showDebug.value
    }

    return {
      orderStore,
      goToHomePage,
      formatPrice,
      formatDate,
      retryFetch,
      showDebug,
      toggleDebug,
    }
  },
}
</script>

<style scoped>
.review-btn {
  padding: 12px 12px;
  background-color: #0095da;
  color: white;
  font-size: 12px;
  font-weight: bold;
  border-radius: 4px;
  text-decoration: none;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.3s ease;
  display: inline-block;
}

.review-btn:hover {
  background-color: #0077be;
}

.order-history-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.order-items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.order-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}

.order-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.order-header {
  margin-bottom: 15px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.product-card {
  display: flex;
  border-radius: 8px;
  overflow: hidden;
  background-color: white;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
}

.product-image {
  flex: 0 0 100px;
  height: 100px;
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
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
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

.product-quantity {
  font-size: 0.9rem;
  color: #555;
}

.order-summary {
  text-align: right;
  font-size: 1.1rem;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.go-home {
  background-color: #198754;
  color: white;
  padding: 10px;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  width: 100%;
  margin-top: 20px;
  font-weight: bold;
  transition: background-color 0.3s;
}

.go-home:hover {
  background-color: #146c43;
}

.empty-order-history {
  text-align: center;
  font-size: 18px;
  margin-top: 50px;
  padding: 30px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.continue-shopping {
  display: inline-block;
  margin-top: 10px;
  padding: 8px 12px;
  background-color: #198754;
  color: white;
  text-decoration: none;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.continue-shopping:hover {
  background-color: #146c43;
}

/* New styles */
.loading-state,
.error-state {
  text-align: center;
  padding: 40px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin: 20px 0;
}

.retry-btn {
  background-color: #0095da;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
}

.debug-info {
  margin-top: 30px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.debug-info pre {
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
  .order-items-grid {
    grid-template-columns: 1fr;
  }
}
</style> -->

<template>
  <div class="order-history-container">
    <h2>Your Orders</h2>

    <!-- Loading state -->
    <div v-if="orderStore.loading" class="loading-state">
      <p>Loading your orders...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="orderStore.error" class="error-state">
      <p>{{ orderStore.error }}</p>
      <button @click="retryFetch" class="retry-btn">Retry</button>
    </div>

    <!-- Display orders when available -->
    <div v-else-if="orderStore.orders && orderStore.orders.length > 0" class="order-items-grid">
      <div v-for="(order, index) in orderStore.pagedOrders" :key="order.id || index" class="order-card">
        <div class="order-header">
          <h3>Order #{{ order.id || index + 1 }}</h3>
          <p><strong>Date:</strong> {{ formatDate(order.createdAt) }}</p>
        </div>

        <!-- Display product details directly from the order object -->
        <div class="product-card">
         
          <div class="product-image">
          <img :src="order.productImageUrl || 'https://via.placeholder.com/150'" alt="Product Image" />
        </div>
          <div class="product-info">
            <h3 class="product-name">{{ order.productOfferingName || 'Product Name' }}</h3>
            <p class="product-price">Price: ₹{{ formatPrice(order.price || 0) }}</p>
            <p class="product-quantity">Quantity: {{ order.quantity || 1 }}</p>
            <br />
            <router-link
              v-if="order.productOfferingId"
              :to="`/review/${order.productOfferingId}`"
              class="review-btn"
            >
              Add review
            </router-link>
          </div>
        </div>

        <p class="order-summary">
          <strong>Total Price:</strong> ₹{{
            formatPrice((order.price || 0) * (order.quantity || 1))
          }}
        </p>
      </div>

      <!-- Pagination Controls -->
      <div v-if="orderStore.totalPages > 1" class="pagination">
        <button
          class="pagination-btn"
          :disabled="orderStore.currentPage === 1"
          @click="orderStore.prevPage"
        >
          Previous
        </button>
        <span>{{ orderStore.currentPage }} / {{ orderStore.totalPages }}</span>
        <button
          class="pagination-btn"
          :disabled="orderStore.currentPage === orderStore.totalPages"
          @click="orderStore.nextPage"
        >
          Next
        </button>
      </div>
    </div>

    <!-- Empty state for order history -->
    <div v-else class="empty-order-history">
      <p>No orders placed yet.</p>
      <router-link to="/" class="continue-shopping">Continue Shopping</router-link>
    </div>

    <!-- Action button: Go to homepage -->
    <button class="go-home" @click="goToHomePage">Go to Homepage</button>
  </div>
</template>

<script>
import { useOrderStore } from '@/stores/orderstore'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

export default {
  setup() {
    const orderStore = useOrderStore()
    const router = useRouter()
    const showDebug = ref(false)

    // Fetch orders when the component mounts
    onMounted(async () => {
      try {
        await orderStore.fetchOrders()
        console.log('Orders fetched:', orderStore.orders)
      } catch (error) {
        console.error('Error in component while fetching orders:', error)
      }
    })

    // Format date
    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'

      try {
        const date = new Date(dateString)
        return date.toLocaleDateString()
      } catch (e) {
        return dateString
      }
    }

    // Format price with commas
    const formatPrice = (price) => {
      if (price === null || price === undefined) return '0'
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }

    // Go to homepage
    const goToHomePage = () => {
      router.push('/')
    }

    // Retry fetching orders
    const retryFetch = () => {
      orderStore.fetchOrders()
    }

    // Toggle debug information
    const toggleDebug = () => {
      showDebug.value = !showDebug.value
    }

    return {
      orderStore,
      goToHomePage,
      formatPrice,
      formatDate,
      retryFetch,
      showDebug,
      toggleDebug,
    }
  },
}
</script>

<style scoped>
/* Your existing styles */
.review-btn {
  padding: 12px 12px;
  background-color: #0095da;
  color: white;
  font-size: 12px;
  font-weight: bold;
  border-radius: 4px;
  text-decoration: none;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.3s ease;
  display: inline-block;
}

.review-btn:hover {
  background-color: #0077be;
}

.order-history-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.order-items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.order-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}

.order-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.order-header {
  margin-bottom: 15px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.product-card {
  display: flex;
  border-radius: 8px;
  overflow: hidden;
  background-color: white;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
}

.product-image {
  flex: 0 0 100px;
  height: 100px;
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
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
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

.product-quantity {
  font-size: 0.9rem;
  color: #555;
}

.order-summary {
  text-align: right;
  font-size: 1.1rem;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.go-home {
  background-color: #198754;
  color: white;
  padding: 10px;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  width: 100%;
  margin-top: 20px;
  font-weight: bold;
  transition: background-color 0.3s;
}

.go-home:hover {
  background-color: #146c43;
}

.empty-order-history {
  text-align: center;
  font-size: 18px;
  margin-top: 50px;
  padding: 30px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.continue-shopping {
  display: inline-block;
  margin-top: 10px;
  padding: 8px 12px;
  background-color: #198754;
  color: white;
  text-decoration: none;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.continue-shopping:hover {
  background-color: #146c43;
}

/* New styles */
.loading-state,
.error-state {
  text-align: center;
  padding: 40px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin: 20px 0;
}

.retry-btn {
  background-color: #0095da;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
}

.debug-info {
  margin-top: 30px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.debug-info pre {
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

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination-btn {
  background-color: #0095da;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin: 0 10px;
  font-size: 1rem;
  transition: background-color 0.3s;
}

.pagination-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #0077be;
}

@media (max-width: 768px) {
  .order-items-grid {
    grid-template-columns: 1fr;
  }
}
</style>

