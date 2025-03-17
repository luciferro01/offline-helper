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
      <div v-for="(order, index) in orderStore.orders" :key="order.id || index" class="order-card">
        <div class="order-header">
          <h3>Order #{{ order.id || index + 1 }}</h3>
          <p><strong>Date:</strong> {{ formatDate(order.createdAt) }}</p>
        </div>

        <!-- Display product details directly from the order object -->
        <div class="product-card">
          <div class="product-image">
            <img
              :src=order.productImageUrl 
              alt="Product Image"
            />
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ order.productOfferingName || 'Product Name' }}</h3>
            <p class="product-price">Price: ₹{{ formatPrice(order.price || 0) }}</p>
            <p class="product-quantity">Quantity: {{ order.quantity || 1 }}</p>
            <br />
            <router-link
              v-if="order.productOfferingId && !hasReviewed(order.productOfferingId)"
              :to="`/review/${order.productOfferingId}`"
              class="review-btn"
            >
              Add review
            </router-link>
            <button
              v-else
              class="review-btn"
              :disabled="true"
            >
              Review Done
            </button>
          </div>
        </div>

        <p class="order-summary">
          <strong>Total Price:</strong> ₹{{
            formatPrice((order.price || 0) * (order.quantity || 1))
          }}
        </p>
      </div>
    </div>

    <!-- Empty state for order history -->
    <div v-else class="empty-order-history">
      <p>No orders placed yet.</p>
      <router-link to="/" class="continue-shopping">Continue Shopping</router-link>
    </div>

    <!-- Action button: Go to homepage -->
    <button class="go-home" @click="goToHomePage">Go to Homepage</button>

    <!-- Debug information section -->
    
  </div>
</template>

<!-- <script>
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
</script> -->
<script>
import { useOrderStore } from '@/stores/orderstore'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/apiService'

export default {
  setup() {
    const orderStore = useOrderStore()
    const router = useRouter()
    const showDebug = ref(false)
    const reviewedOrders = ref([])

    // Fetch orders when the component mounts
    onMounted(async () => {
      try {
        await orderStore.fetchOrders()
        console.log('Orders fetched:', orderStore.orders)
        await fetchReviewsForOrders()
      } catch (error) {
        console.error('Error in component while fetching orders:', error)
      }
    })

    // Fetch reviews and store them for checking
//     const fetchReviewsForOrders = async () => {
//   try {
//     const promises = orderStore.orders.map(async (order) => {
//       // console.log(order.productOfferingId)
//       // const response = await fetch(`/reviews/${order.productOfferingId}`);
//       axios.get(`/reviews/${order.productOfferingId}`)
//   .then(response => {
//     // response.data is already parsed as a JavaScript object
//     if (response.data.success) {
//       const userReviewed = response.data.data.some(review => review.userId === orderStore.user.id);
//       if (userReviewed) {
//         reviewedOrders.value.push(order.productOfferingId);
//       }
//     }
//   })
//   .catch(error => {
//     console.error("Error fetching reviews:", error);
//   });
      
//       // Check if the response is OK (status code 200-299)
//       if (!response.ok) {
//         throw new Error(`Failed to fetch reviews for product offering ${order.productOfferingId}: ${response.statusText}`);
//       }

//       // Parse the JSON response
//       const data = await response.json();
//       if (data.success) {
//         const userReviewed = data.data.some((review) => review.userId === orderStore.user.id);
//         if (userReviewed) {
//           reviewedOrders.value.push(order.productOfferingId);
//         }
//       }
//     });

//     await Promise.all(promises);
//   } catch (error) {
//     console.error('Error fetching reviews:', error);
//     // Log the response in case of error for debugging
//     if (error.response) {
//       console.error('Error response:', error.response);
//     }
//   }
// }
// const fetchReviewsForOrders = async () => {
//   try {
//     const promises = orderStore.orders.map(async (order) => {
//       try {
//         const response = await api.get(`/reviews/${order.productOfferingId}`);

//         if (response.data.success) {
//           // Check if *any* reviews exist for this offering
//           const hasAnyReviews = response.data.data && response.data.data.length > 0;

//           if (hasAnyReviews) {
//             reviewedOrders.value.push(order.productOfferingId); // Mark it as reviewed if ANY reviews exist
//           }
//         }
//       } catch (error) {
//         console.error("Error fetching reviews:", error);
//       }
//     });

//     await Promise.all(promises);
//   } catch (error) {
//     console.error('Error fetching reviews:', error);
//   }
// }
const fetchReviewsForOrders = async () => {
  try {
    const promises = orderStore.orders.map(async (order) => {
      try {
        const response = await api.get(`/reviews/${order.productOfferingId}`);

        if (response.data.success) {
          // Check if ANY reviews exist for this offering
          const reviews = response.data.data || [];  // Ensure `reviews` is always an array
          console.log(reviews)
          console.log(order)
          
          const userHasReviewed = reviews.some(review => review.productOfferingId === order.productOfferingId);

          if (userHasReviewed) {
            reviewedOrders.value.push(order.productOfferingId); // Mark it as reviewed if the user reviewed
          }
        }
      } catch (error) {
        console.error("Error fetching reviews:", error);
      }
    });

    await Promise.all(promises);
  } catch (error) {
    console.error('Error fetching reviews:', error);
  }
}

const hasReviewed = (offeringId) => {
  return reviewedOrders.value.includes(offeringId);
};
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
      hasReviewed,
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

@media (max-width: 768px) {
  .order-items-grid {
    grid-template-columns: 1fr;
  }
}
</style>
