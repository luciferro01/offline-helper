<template>
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
  </style>