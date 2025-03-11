<template>
    <div class="order-history-container">
      <h2>Your Orders</h2>
    
      <!-- Display orders -->
      <div v-if="orders.length > 0" class="order-items-grid">
        <div v-for="(order, index) in orders" :key="index" class="order-card">
          <div class="order-header">
            <h3>Order #{{ index + 1 }}</h3>
            <p><strong>Date:</strong> {{ order.date }}</p>
          </div>
    
          <!-- Display items within the order using the same product-card layout -->
          <div v-for="item in order.items" :key="item.id" class="product-card">
            <div class="product-image">
              <img :src="item.image" alt="Product Image" />
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ item.name }}</h3>
              <p class="product-price">Price: ₹{{ item.price }}</p>
              <p class="product-quantity">Quantity: {{ item.quantity }}</p>
            </div>
          </div>
    
          <p class="order-summary"><strong>Total Price:</strong> ₹{{ order.total }}</p>
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
  import { useCartStore } from "@/stores/cartStore";
  import { ref, onMounted } from "vue";
  import { useRouter } from "vue-router";  // Import router for navigation
  
  export default {
    setup() {
      const cartStore = useCartStore();
      const orders = ref([]);
      const router = useRouter();  // Initialize the router
  
      // Mock data for order history (you can replace this with real data from your API)
      const mockOrders = [
        {
          items: [
            {
              id: 1,
              name: "MacBook Air M2",
              price: 99990,
              quantity: 1,
              image: "https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/mbp16-spacegray-gallery1-202301?wid=400&hei=400&fmt=jpeg&qlt=90&.v=1671304678952"
            },
            {
              id: 2,
              name: "Sony WH-1000XM5 Headphones",
              price: 29990,
              quantity: 1,
              image: "https://m.media-amazon.com/images/I/61mUNgHsLHL._AC_UL480_FMwebp_QL65_.jpg"
            }
          ],
          total: 129980,
          date: "2025-03-01 12:30 PM"
        },
        {
          items: [
            {
              id: 3,
              name: "Samsung Galaxy S24 Ultra",
              price: 124999,
              quantity: 1,
              image: "https://m.media-amazon.com/images/I/81rbxujGNjL._AC_UL480_FMwebp_QL65_.jpg"
            }
          ],
          total: 124999,
          date: "2025-03-05 3:45 PM"
        }
      ];
  
      // Mock API call - replace with real API later
      const fetchOrders = async () => {
        // Example of an API call (mocked for now):
        // try {
        //   const response = await fetch('https://your-api.com/orders');
        //   if (!response.ok) throw new Error('Failed to fetch orders');
        //   const data = await response.json();
        //   orders.value = data.orders;
        // } catch (error) {
        //   console.error(error);
        // }
        // Simulate loading mock data
        orders.value = mockOrders;
      };
  
      // Go to homepage
      const goToHomePage = () => {
        router.push('/');  // Redirect user to the homepage
      };
  
      onMounted(() => {
        fetchOrders(); // Fetch orders when component mounts
      });
  
      return { orders, goToHomePage };
    }
  };
  </script>
  
  <style scoped>
  /* Reuse the card style from cart page for orders page */
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
  