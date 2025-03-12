<template>
  <div class="order-history-container">
    <h2>Your Orders</h2>
 <!-- Display orders -->
 <div v-if="orderStore.orders.length > 0" class="order-items-grid">
    <div v-for="(order, index) in orderStore.orders" :key="index" class="order-card">
      <div class="order-header">
        <h3>Order #{{ index + 1 }}</h3>
        <p><strong>Date:</strong> {{ order.createdAt }}</p> <!-- Use createdAt as date -->
      </div>

      <!-- Display product details directly from the order object -->
      <div class="product-card">
        <div class="product-image">
          <img :src="order.image" alt="Product Image" />
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ order.name || 'Product Name' }}</h3> <!-- Handle null names -->
          <p class="product-price">Price: ₹{{ formatPrice(order.price) }}</p>
          <p class="product-quantity">Quantity: {{ order.quantity }}</p><br>
          <router-link :to="`/review/${order.productOfferingId}/${order.userId}`" class="review-btn">Add review</router-link>
        </div>
      </div>

      <p class="order-summary"><strong>Total Price:</strong> ₹{{ formatPrice(order.price * order.quantity) }}</p> <!-- Calculate total price -->
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
import { useOrderStore } from '@/stores/orderstore';  // Import the order store
import { onMounted } from 'vue';
import { useRouter } from 'vue-router'; // Import router for navigation

export default {
  setup() {
    const orderStore = useOrderStore();  // Access Pinia store for orders
    const router = useRouter();  // Initialize the router

    // Fetch orders when the component mounts
    onMounted(() => {
      orderStore.fetchOrders(); 
       // Fetch the orders from the store
    });

    // Go to homepage
    const goToHomePage = () => {
      router.push('/');  // Redirect user to the homepage
    };

    const formatPrice = (price) => {
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    };
  //   const goToReview = (product.id) => {
  //     router.push('/review')
  //   }

    return { orderStore, goToHomePage, formatPrice};  // Return the store to the template
  }
};
</script>

<style scoped>
/* Reuse the card style from cart page for orders page */
.review-btn {
padding: 12px 12px; /* Reduced padding for a smaller button */
background-color: #0095DA; /* Button background color */
color: white;
font-size: 12px; /* Smaller font size */
font-weight: bold;
border-radius: 4px;
text-decoration: none; /* Remove underline */
text-align: center;
cursor: pointer;
transition: background-color 0.3s ease;
}
.order-history-container {
  padding: 20px;
}

h2 {
  text-align: center;
}

.order-items-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* Adjusted for side-by-side layout */
  gap: 20px;
  margin-top: 20px;
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
  transition: transform 0.2s, box-shadow 0.2s;
}

.review {
  background-color: #537867;
  color: white;
}

/* .product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
} */

.product-image {
  position: relative;
  height: 150px;
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
  color: #0095DA;
  margin-bottom: 0.3rem;
}

.product-quantity {
  font-size: 0.9rem;
  color: #555;
}

.order-summary {
  text-align: center;
  font-size: 1.1rem;
  margin-top: 10px;
}

.go-home {
  background-color: #198754;
  color: white;
  padding: 10px;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  width: 100%;
  margin-top: 10px;
}

.go-home:hover {
  background-color: #146c43;
}

.empty-order-history {
  text-align: center;
  font-size: 18px;
  margin-top: 50px;
}

.continue-shopping {
  display: inline-block;
  margin-top: 10px;
  padding: 8px 12px;
  background-color: green;
  color: white;
  text-decoration: none;
  border-radius: 5px;
}

.continue-shopping:hover {
  background-color: #218838;
}
</style>
