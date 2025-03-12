<template>
    <div class="product-card" @click="navigateToProduct">
      <div class="product-image">
        <img :src="product.imageUrl" :alt="product.name">
        <slot name="badge"></slot>
      </div>
      <div class="product-info">
        <h3 class="product-name">{{ product.name }}</h3>
        <div class="product-price">₹{{ formatPrice(product.price) }}</div>
        <div class="product-seller">{{ product.seller }}</div>
        <div class="product-rating">
          <div class="stars">
            <span 
              v-for="star in 5" 
              :key="star" 
              class="star" 
              :class="{ 'filled': star <= Math.round(product.rating) }"
            >★</span>
          </div>
          <span class="rating-count">({{ product.ratingCount }})</span>
        </div>
        <slot name="actions"></slot>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'ProductCard',
    
    props: {
      product: {
        type: Object,
        required: true
      }
    },
    
    methods: {
      formatPrice(price) {
        return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      },
      
      navigateToProduct() {
        // In Vue Router 4, we use different syntax for navigation with state
        this.$router.push({
          name: 'ProductDetail',
          params: { id: this.product.id },
        });
  
        // Store product data in localStorage instead
        localStorage.setItem('productData', JSON.stringify(this.product));
      }
    }
  };
  </script>
  
  <style scoped>
  .product-card {
    border-radius: 8px;
    overflow: hidden;
    background-color: white;
    transition: transform 0.2s, box-shadow 0.2s;
    display: flex;
    flex-direction: column;
    cursor: pointer;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
    border: 1px solid #f0f0f0;
    height: 100%;
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
    flex-grow: 1;
  }
  
  .product-name {
    font-size: 0.9rem;
    margin-bottom: 0.5rem;
    color: #333;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    line-height: 1.3;
    height: 2.6em;
  }
  
  .product-price {
    font-size: 1rem;
    font-weight: bold;
    color: #0095DA;
    margin-bottom: 0.3rem;
  }
  
  .product-seller {
    font-size: 0.8rem;
    color: #666;
    margin-bottom: 0.3rem;
  }
  
  .product-rating {
    display: flex;
    align-items: center;
    font-size: 0.8rem;
  }
  
  .stars {
    display: flex;
    color: #C8C8C8;
    margin-right: 5px;
  }
  
  .star {
    margin-right: 2px;
  }
  
  .star.filled {
    color: #F5A623;
  }
  
  .rating-count {
    color: #999;
  }
  </style>