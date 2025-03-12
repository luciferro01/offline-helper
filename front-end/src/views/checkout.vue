   <template>
    <div class="checkout-container">
      <div class="checkout-form">
        <h2>Checkout</h2><br>
  
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
              <p class="product-price">Price: ₹{{ (item.price) }}</p>
              <p>Quantity: {{ item.quantity }}</p>
            </div>
          </div>
        </div><br>
  
        <!-- Place Order Button -->
        <button
          v-if="cartItems.length > 0 && !orderPlaced && !loading"
          class="place-order-button"
          @click="emailPopup"
        >
          Place the order
        </button>
        <!-- <button v-else-if="loading" disabled>Placing Order...</button> -->
  
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
  
        <!-- Order Placed Message -->
        <!-- <div v-if="orderPlaced" class="order-placed-message">
          <h2>Order Placed</h2>
          <p>Thank you! Your order has been successfully placed.</p>
        </div> -->
  
        <div class="order-summary" v-if="!orderPlaced">
          <p><strong>Total Items:</strong> {{ totalItems }}</p>
          <p><strong>Total Price:</strong> ₹{{ (totalPrice) }}</p>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { computed, ref } from 'vue';
  import { useCartStore } from '@/stores/cartStore';
  import { useUserStore } from '@/stores/userStore';
  import { useRouter } from 'vue-router';
  import { useRoute } from 'vue-router';
  import { onMounted } from 'vue';
  import Popup from '@/components/Popup.vue';
  import axios from 'axios';
  import api from '@/services/apiService';

  
  export default {
    name: 'Checkout',
    components: {
      Popup,
    },
  
    setup() {
      const showEmailPopup = ref(false);
      const email = ref('');
      const errorMessage = ref('');
      const successMessage = ref('');
      const loading = ref(false);
      const orderPlaced = ref(false);
      const user_id = ref('');
  
      const cartStore = useCartStore();
      const userStore = useUserStore();
      const router = useRouter();
      const route = useRoute();
  
      const totalItems = computed(() => cartStore.totalItems);
      const totalPrice = computed(() => cartStore.totalPrice);
      const cartItems = computed(() => cartStore.items);
  
      const emailPopup = () => {
        showEmailPopup.value = true;
      };
  
   //    const submitEmail = async () => {
   //      if (!email.value) {
   //        errorMessage.value = 'Please enter a valid email.';
   //        return;
   //      }
  
   // //      loading.value = true;
   // //      errorMessage.value = '';
  
   // //      try {
   // //        const response = await axios.post('/api/place-order', {
   // //          email: email.value,
   // //          cartItems: cartItems.value,
   // //          totalPrice: totalPrice.value,
   // //        });
  
   // //        if (response.data.success) {
   //          successMessage.value = 'Order placed successfully! Thank you!';
   //          showEmailPopup.value = false;
   //          orderPlaced.value = true;
   //          cartStore.clearCart();
   // //        } else {
   // //          errorMessage.value = response.data.message || 'Failed to place order.';
   // //        }
   // //      } catch (error) {
   // //        console.error('Error submitting email:', error);
   // //        errorMessage.value = 'An error occurred. Please try again.';
   // //      } finally {
   // //        loading.value = false;
   // //      }
   //    };
//     const submitEmail = async () => {
//   if (!email.value) {
//     errorMessage.value = 'Please enter a valid email.';
//     return;
//   }

//   loading.value = true;
//   errorMessage.value = '';
 
//   try {
//     const cartData = {
//       items: cartItems.value.map(item => ({
//         productId: item.id,
//         quantity: item.quantity,
//         price: item.price,
//       })),
//       totalPrice: totalPrice.value,
//     };

//     const response = await axios.post(`http://localhost:8085/orders/create/${userStore.userId}`, cartData);

//     if (response.data.success) {
//       successMessage.value = 'Order placed successfully! Thank you!';
//       showEmailPopup.value = false;
//       orderPlaced.value = true;
//       cartStore.clearCart();
//       console.log(cartStore);
//     } else {
//       errorMessage.value = response.data.message || 'Failed to place order.';
//     }
//   } catch (error) {
//     console.error('Error submitting email:', error);
//     errorMessage.value = 'An error occurred. Please try again.';
//   } finally {
//     loading.value = false;
//   }
// };

// const submitEmail = async () => {
//  if (!email.value) {
//    errorMessage.value = 'Please enter a valid email.';
//    return;
//  }

//  loading.value = true;
//  errorMessage.value = '';

//  try {
//    const cartData = {
//      items: cartItems.value.map(item => ({
//        productId: item.id,
//        quantity: item.quantity,
//        price: item.price,
//      })),
//      totalPrice: totalPrice.value,
//      email: email.value,
//    };

//    console.log('Sending cart data:', cartData);
   

//    const response = await api.post(`/carts/checkout/1`, cartData);

//    if (response.success) {
//      successMessage.value = 'Order placed successfully! Thank you!';
//      showEmailPopup.value = false;
//      orderPlaced.value = true;
//      cartStore.clearCart();
//      console.log('Order placed successfully:', response);
//    } else {
//      errorMessage.value = response.message || 'Failed to place order.';
//    }
//  } catch (error) {
//    console.error('Error submitting email:', error);
//    errorMessage.value = error.response?.data?.message || 'An error occurred. Please try again.';
//  } finally {
//    loading.value = false;
//  }
// };

onMounted(() => {
  console.log("hii");
  user_id.value = route.params.user_id; 
  console.log('User ID:', user_id.value);
});




// const submitEmail = async () => {
//   if (!email.value) {
//     errorMessage.value = 'Please enter a valid email.';
//     return;
//   }

//   loading.value = true;
//   errorMessage.value = '';
//   successMessage.value = '';

//   try {
//     // Check if userId is available (ensure it's defined and valid)
//     if (!userId.value) {
//       errorMessage.value = 'User ID is required.';
//       return;
//     }

//     // Prepare the email data with both userId and email
//     const emailData = {
//       userId: user_id.value,  // Add userId to the data being sent
//       email: email.value,
//     };

//     console.log('Sending email data:', emailData);

//     // Send the data to the server
//     const response = await api.post(`/carts/1/submit-email`, emailData);  // Adjust endpoint as necessary

//     if (response.status === 200 || response.status === 201) {
//       if (response.data.success) {
//         successMessage.value = response.data.message || 'Email submitted successfully!';
//         showEmailPopup.value = false;
//       } else {
//         errorMessage.value = response.data.message || 'Failed to submit email.';
//       }
//     } else {
//       console.error("Unexpected status code:", response.status);
//       errorMessage.value = `Failed to submit email. Server returned status: ${response.status}`;
//     }

//   } catch (error) {
//     console.error('Error submitting email:', error);

//     if (error.response) {
//       console.error("Server responded with:", error.response.data);
//       errorMessage.value = error.response.data.message || 'An error occurred. Please try again.';
//     } else if (error.request) {
//       console.error("No response received from server:", error.request);
//       errorMessage.value = 'No response from server. Please check your connection.';
//     } else {
//       console.error('Error setting up the request:', error.message);
//       errorMessage.value = 'An error occurred. Please try again.';
//     }

//   } finally {
//     loading.value = false;
//   }
// };

const submitEmail = async () => {
      if (!email.value) {
        errorMessage.value = 'Please enter a valid email.';
        return;
      }

      loading.value = true;
      errorMessage.value = '';
      successMessage.value = '';

      try {
        if (!user_id.value) {
          errorMessage.value = 'User ID is required. Please login.';
          return;  // Stop if no user ID
        }

        const emailData = {
          userId: user_id.value,
          email: email.value,
        };

        console.log('Sending email data:', emailData);

        const response = await api.post(`/carts/1/submit-email`, emailData);

        if (response.status === 200 || response.status === 201) {
          if (response.data.success) {
            successMessage.value = response.data.message || 'Email submitted successfully!';
            showEmailPopup.value = false;
            // Maybe redirect to a thank you page here, or show a confirmation message
          } else {
            // Server indicates a failure, even with a 2xx status
            errorMessage.value = response.data.message || 'Failed to submit email. Please try again.';
          }
        } else {
          // Unexpected status code
          console.error("Unexpected status code:", response.status);
          errorMessage.value = `Failed to submit email. Server returned status: ${response.status}.`;
        }

      } catch (error) {
        console.error('Error submitting email:', error);

        if (error.response) {
          console.error("Server responded with:", error.response.data);

          //More specific handling for server errors
          if (error.response.status === 400) {
               errorMessage.value = error.response.data.message || "Invalid email format. Please check your email address.";
          } else if (error.response.status === 404) {
              errorMessage.value = error.response.data.message || "User not found. Please try again."
          }
          else {
            errorMessage.value = error.response.data.message || 'An error occurred. Please try again.';
          }
        } else if (error.request) {
          console.error("No response received from server:", error.request);
          errorMessage.value = 'No response from server. Please check your connection.';
        } else {
          console.error('Error setting up the request:', error.message);
          errorMessage.value = 'An error occurred. Please try again.';
        }

      } finally {
        loading.value = false;
      }
    };

  
      const onEmailPopupClose = () => {
        showEmailPopup.value = false;
      };
  
      const formatPrice = (price) => {
        return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
      };
  
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
        formatPrice,
        emailPopup,
      };
    },
  };
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
  </style>
  