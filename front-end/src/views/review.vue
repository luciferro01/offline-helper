<!-- <template>
    <div class="review-container">
      <h2>Write a Review</h2>
  
      <div class="star-rating">
        <span
          v-for="n in 5"
          :key="n"
          class="star"
          :class="{ filled: n <= rating }"
          @click="setRating(n)"
        >
          ★
        </span>
      </div>
  
      <textarea v-model="reviewText" placeholder="Write your review here..."></textarea>
  
      <button @click="submitReview">Submit Review</button>
    </div>
  </template>
  
  <script>
  import { ref, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router'; 
  import api from '@/services/apiService';
  
  export default {
    setup() {
      const route = useRoute(); // Get access to the route
      const router = useRouter();
  
      const product_offering_id = ref(null);
      const user_id = ref(null);
      const rating = ref(0);
      const reviewText = ref('');
  
      onMounted(() => {
        // Access the route parameters
        product_offering_id.value = route.params.product_offering_id;
        user_id.value = route.params.user_id;
  
        console.log('Product ID:', product_offering_id.value);
        console.log('User ID:', user_id.value);
      });
  
      const setRating = (newRating) => {
        rating.value = newRating;
      };
  

const submitReview = async () => {
      try {
        const reviewData = {
          productOfferingId: product_offering_id.value, 
          userId: user_id.value, 
          rating: rating.value,
          reviewText: reviewText.value,
          createdAt: new Date().toISOString() 
        };

        console.log("Submitting Review Data:", reviewData);  

        const response = await api.post('/reviews', reviewData); 

        console.log('Review submission successful:', response);
        router.push('/'); // Redirect

      } catch (error) {
        console.error('Error submitting review:', error);
        alert(error.message); // Display error from apiService.
      }
    };
  
      return {
        product_offering_id,
        user_id,
        rating,
        reviewText,
        setRating,
        submitReview
      };
    }
  };
  </script>
  
  <style scoped>
  .review-container {
    padding: 20px;
  }
  
  .star-rating {
    display: flex;
    font-size: 2em;
    margin-bottom: 10px;
  }
  
  .star {
    cursor: pointer;
    color: #ccc; 
  }
  
  .star.filled {
    color: gold; 
  }
  
  textarea {
    width: 100%;
    min-height: 100px;
    margin-bottom: 10px;
    padding: 8px;
    border: 1px solid #ccc;
  }
  
  button {
    background-color: #4CAF50;
    color: white;
    padding: 10px 15px;
    border: none;
    cursor: pointer;
  }
  </style> -->

<template>
  <div class="review-container">
    <h2>Write a Review</h2>

    <div v-if="loading" class="loading">Loading...</div>

    <div v-else>
      <div class="product-info" v-if="productName">
        <h3>{{ productName }}</h3>
      </div>

      <div class="star-rating">
        <span
          v-for="n in 5"
          :key="n"
          class="star"
          :class="{ filled: n <= rating }"
          @click="setRating(n)"
        >
          ★
        </span>
        <span class="rating-text" v-if="rating > 0">{{ getRatingText() }}</span>
      </div>

      <textarea
        v-model="reviewText"
        placeholder="Write your review here..."
        :class="{ 'error-border': showValidationErrors && !reviewText }"
      ></textarea>

      <div class="error-message" v-if="errorMessage">
        {{ errorMessage }}
      </div>

      <div class="validation-message" v-if="showValidationErrors && !rating">
        Please select a rating
      </div>

      <div class="validation-message" v-if="showValidationErrors && !reviewText">
        Please write a review
      </div>

      <div class="buttons">
        <button @click="goBack" class="back-button">Cancel</button>
        <button @click="submitReview" :disabled="loading" class="submit-button">
          Submit Review
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/apiService'

export default {
  setup() {
    const route = useRoute()
    const router = useRouter()
    const productOfferingId = ref(null)
    const userId = ref(null)
    const rating = ref(0)
    const reviewText = ref('')
    const loading = ref(false)
    const errorMessage = ref('')
    const showValidationErrors = ref(false)
    const productName = ref('')

    // Get a fixed user ID if it's missing from the route
    const getDefaultUserId = () => {
      // Try to get from localStorage if you store it there
      const storedUserId = localStorage.getItem('userId')
      if (storedUserId) return storedUserId

      // Or use a default value if you have a way to get the current user
      return '1' // Default to user ID 1 if none is provided
    }

    onMounted(async () => {
      // Access the route parameters
      productOfferingId.value = route.params.product_offering_id

      // Fix for undefined userId - use the one from route or a default
      userId.value = route.params.user_id || getDefaultUserId()

      console.log('Product ID:', productOfferingId.value)
      console.log('User ID:', userId.value)

      // Optional: Fetch product details to display name
      try {
        loading.value = true
        // Uncomment and adjust this if you want to fetch product details
        // const response = await api.get(`/products/${productOfferingId.value}`);
        // productName.value = response.data.name || 'Product';
        loading.value = false
      } catch (error) {
        console.error('Error fetching product details:', error)
        loading.value = false
      }
    })

    const setRating = (newRating) => {
      rating.value = newRating
    }

    const getRatingText = () => {
      const texts = ['', 'Poor', 'Fair', 'Good', 'Very Good', 'Excellent']
      return texts[rating.value] || ''
    }

    const validateForm = () => {
      showValidationErrors.value = true

      if (!rating.value) {
        return false
      }

      if (!reviewText.value.trim()) {
        return false
      }

      return true
    }

    const submitReview = async () => {
      if (!validateForm()) {
        return
      }

      try {
        loading.value = true
        errorMessage.value = ''

        // Ensure userId is not undefined
        if (!userId.value) {
          userId.value = getDefaultUserId()
        }

        const reviewData = {
          productOfferingId: productOfferingId.value,
          userId: userId.value,
          rating: rating.value,
          reviewText: reviewText.value,
          createdAt: new Date().toISOString(),
        }

        console.log('Submitting Review Data:', reviewData)

        const response = await api.post(
          `/reviews?=productOfferingId=${productOfferingId.value}`,
          reviewData,
        )
        console.log('Review submission successful:', response)

        // Show success message
        alert('Thank you for your review!')

        // Redirect
        router.push('/')
      } catch (error) {
        console.error('Error submitting review:', error)
        errorMessage.value =
          error.response?.data?.message ||
          error.message ||
          'Failed to submit review. Please try again.'
        loading.value = false
      }
    }

    const goBack = () => {
      router.go(-1) // Go back to previous page
    }

    return {
      productOfferingId,
      userId,
      rating,
      reviewText,
      loading,
      errorMessage,
      showValidationErrors,
      productName,
      setRating,
      getRatingText,
      submitReview,
      goBack,
    }
  },
}
</script>

<style scoped>
.review-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.product-info {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.star-rating {
  display: flex;
  align-items: center;
  font-size: 2em;
  margin-bottom: 20px;
}

.star {
  cursor: pointer;
  color: #ddd;
  transition: color 0.2s;
  padding: 0 2px;
}

.star:hover {
  color: #ffcc00;
}

.star.filled {
  color: #ffcc00;
}

.rating-text {
  margin-left: 10px;
  font-size: 16px;
  color: #666;
}

textarea {
  width: 100%;
  min-height: 120px;
  margin-bottom: 20px;
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-family: inherit;
  font-size: 16px;
  transition: border-color 0.2s;
}

textarea:focus {
  outline: none;
  border-color: #0095da;
}

.error-border {
  border-color: #dc3545;
}

.buttons {
  display: flex;
  justify-content: space-between;
}

button {
  padding: 12px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s;
}

.back-button {
  background-color: #6c757d;
  color: white;
}

.back-button:hover {
  background-color: #5a6268;
}

.submit-button {
  background-color: #0095da;
  color: white;
}

.submit-button:hover {
  background-color: #0077be;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading {
  text-align: center;
  padding: 20px;
}

.error-message {
  padding: 10px;
  margin-bottom: 15px;
  color: #dc3545;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}

.validation-message {
  color: #dc3545;
  margin-bottom: 10px;
  font-size: 14px;
}
</style>
