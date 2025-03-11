<template>
  <div class="product-detail-container" v-if="product">
    <div class="product-detail-wrapper">
      <!-- Product Images Section -->
      <div class="product-images">
        <div class="main-image">
          <img :src="selectedImage" :alt="product.name" />
        </div>
        <div class="image-thumbnails">
          <img
            v-for="(image, index) in product.images"
            :key="index"
            :src="image"
            :alt="`${product.name} - view ${index + 1}`"
            :class="{ active: selectedImage === image }"
            @click="selectedImage = image"
          />
        </div>
      </div>

      <!-- Product Info Section -->
      <div class="product-info">
        <h1 class="product-name">{{ product.name }}</h1>

        <div class="product-rating">
          <div class="stars">
            <span
              v-for="star in 5"
              :key="star"
              class="star"
              :class="{ filled: star <= Math.round(product.rating) }"
              >★</span
            >
          </div>
          <span class="rating-count">{{ product.rating }} ({{ product.ratingCount }} reviews)</span>
        </div>

        <div class="product-price">₹{{ formatPrice(selectedSeller.price) }}</div>

        <div class="seller-section">
          <label for="seller-select">Seller:</label>
          <select id="seller-select" v-model="selectedSellerId" @change="updateSelectedSeller">
            <option v-for="seller in product.sellers" :key="seller.id" :value="seller.id">
              {{ seller.name }} - ₹{{ formatPrice(seller.price) }}
            </option>
          </select>
        </div>

        <div class="product-description">
          <h3>Description</h3>
          <p>{{ product.description }}</p>
        </div>

        <div class="quantity-section">
          <label for="quantity">Quantity:</label>
          <div class="quantity-controls">
            <button
              class="quantity-btn"
              @click="decreaseQuantity"
              :class="{ 'delete-btn': quantity === 1 }"
            >
              <span v-if="quantity > 1">-</span>
              <span v-else class="trash-icon">🗑️</span>
            </button>
            <input
              type="number"
              id="quantity"
              v-model="quantity"
              min="1"
              @input="validateQuantity"
            />
            <button class="quantity-btn" @click="increaseQuantity">+</button>
          </div>
        </div>

        <button class="add-to-cart-btn" @click="addToCart">Add to Cart</button>

        <div class="product-details">
          <h3>Product Details</h3>
          <ul>
            <li v-for="(value, key) in product.details" :key="key">
              <strong>{{ formatKey(key) }}:</strong> {{ value }}
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Reviews Section -->
    <div class="reviews-section">
      <h2>Customer Reviews</h2>
      <div class="review-summary">
        <div class="avg-rating">
          <span class="rating-number">{{ product.rating }}</span>
          <div class="stars">
            <span
              v-for="star in 5"
              :key="star"
              class="star"
              :class="{ filled: star <= Math.round(product.rating) }"
              >★</span
            >
          </div>
          <span>{{ product.ratingCount }} reviews</span>
        </div>
        <div class="rating-bars">
          <div v-for="i in 5" :key="i" class="rating-bar-container">
            <span class="star-label">{{ 6 - i }} star</span>
            <div class="rating-bar">
              <div class="rating-bar-fill" :style="{ width: `${getPercentage(6 - i)}%` }"></div>
            </div>
            <span class="rating-percent">{{ getPercentage(6 - i) }}%</span>
          </div>
        </div>
      </div>

      <div class="reviews-list">
        <div v-for="review in product.reviews" :key="review.id" class="review-item">
          <div class="review-header">
            <div class="review-user">{{ review.userName }}</div>
            <div class="review-date">{{ formatDate(review.date) }}</div>
          </div>
          <div class="review-rating">
            <div class="stars">
              <span
                v-for="star in 5"
                :key="star"
                class="star"
                :class="{ filled: star <= review.rating }"
                >★</span
              >
            </div>
          </div>
          <div class="review-title">{{ review.title }}</div>
          <div class="review-content">{{ review.content }}</div>
          <div class="review-helpful">
            <span>Was this review helpful?</span>
            <button class="helpful-btn">Yes</button>
            <button class="helpful-btn">No</button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else-if="loading" class="loading">Loading product details...</div>
  <div v-else-if="error" class="error">
    {{ error }}
  </div>
</template>
<script>
import { mapState, mapActions } from 'pinia'
import { useProductStore } from '@/stores/productStore'
import { useCartStore } from '@/stores/cartStore'

export default {
  name: 'ProductDetail',

  props: {
    id: {
      type: [Number, String],
      required: true,
    },
  },

  data() {
    return {
      selectedImage: '',
      selectedSellerId: null,
      selectedSeller: {},
      quantity: 1,
    }
  },

  computed: {
    ...mapState(useProductStore, ['currentProduct', 'loading', 'error']),

    product() {
      return this.currentProduct
    },
  },

  methods: {
    ...mapActions(useProductStore, ['fetchProductById']),
    ...mapActions(useCartStore, ['addToCart']),

    // Initialize product details after it's loaded
    initializeProductDetails() {
      if (this.product && this.product.images && this.product.images.length > 0) {
        this.selectedImage = this.product.images[0]

        if (this.product.sellers && this.product.sellers.length > 0) {
          this.selectedSellerId = this.product.sellers[0].id
          this.selectedSeller = this.product.sellers[0]
        }
      }
    },

    formatPrice(price) {
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    },

    formatKey(key) {
      return key.charAt(0).toUpperCase() + key.slice(1).replace(/([A-Z])/g, ' $1')
    },

    formatDate(dateString) {
      const options = { year: 'numeric', month: 'long', day: 'numeric' }
      return new Date(dateString).toLocaleDateString(undefined, options)
    },

    updateSelectedSeller() {
      if (this.product && this.product.sellers) {
        this.selectedSeller = this.product.sellers.find(
          (seller) => seller.id === this.selectedSellerId,
        )
      }
    },

    increaseQuantity() {
      this.quantity++
    },

    decreaseQuantity() {
      if (this.quantity > 1) {
        this.quantity--
      }
    },

    validateQuantity() {
      // Make sure quantity is at least 1
      if (this.quantity < 1 || isNaN(this.quantity)) {
        this.quantity = 1
      }
    },

    getPercentage(rating) {
      if (!this.product || !this.product.ratingDistribution) return 0

      const count = this.product.ratingDistribution[rating] || 0
      return Math.round((count / this.product.ratingCount) * 100)
    },

    addToCart() {
      if (this.product && this.selectedSeller) {
        const cartItem = {
          id: this.product.id,
          name: this.product.name,
          price: this.selectedSeller.price,
          seller: this.selectedSeller.name,
          quantity: this.quantity,
          imageUrl: this.product.images[0],
        }
        this.addToCart(cartItem)
        // Show confirmation
        this.$router.push('/cart')
      }
    },
  },

  async mounted() {
    try {
      console.log('Fetching product with ID:', this.id)
      await this.fetchProductById(this.id)
      console.log('Product data received:', this.product)
      this.initializeProductDetails()
    } catch (error) {
      console.error('Error loading product details:', error)
    }
  },

  watch: {
    // Watch for changes in the product data
    currentProduct: {
      handler() {
        this.initializeProductDetails()
      },
      immediate: true,
    },
  },
}
</script>

<style scoped>
.product-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
  font-family: 'Roboto', Arial, sans-serif;
}

.loading {
  text-align: center;
  padding: 3rem;
  font-size: 1.2rem;
  color: #666;
}

.product-detail-wrapper {
  display: flex;
  gap: 2rem;
  margin-bottom: 3rem;
}

@media (max-width: 768px) {
  .product-detail-wrapper {
    flex-direction: column;
  }
}

/* Product Images */
.product-images {
  flex: 1;
  max-width: 500px;
}

.main-image {
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 1rem;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9f9f9;
}

.main-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.image-thumbnails {
  display: flex;
  gap: 0.5rem;
  overflow-x: auto;
}

.image-thumbnails img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid transparent;
}

.image-thumbnails img.active {
  border-color: #0095da;
}

/* Product Info */
.product-info {
  flex: 1;
}

.product-name {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  color: #333;
}

.product-rating {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.stars {
  display: flex;
  color: #c8c8c8;
  margin-right: 0.5rem;
}

.star {
  margin-right: 2px;
}

.star.filled {
  color: #f5a623;
}

.rating-count {
  color: #666;
  font-size: 0.9rem;
}

.product-price {
  font-size: 1.8rem;
  font-weight: bold;
  color: #0095da;
  margin-bottom: 1rem;
}

.seller-section {
  margin-bottom: 1.5rem;
}

.seller-section label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.seller-section select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  font-size: 0.9rem;
}

.product-description {
  margin-bottom: 1.5rem;
}

.product-description h3 {
  margin-bottom: 0.5rem;
  font-size: 1.1rem;
}

.product-description p {
  line-height: 1.6;
  color: #444;
}

.quantity-section {
  margin-bottom: 1.5rem;
}

.quantity-section label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.quantity-controls {
  display: flex;
  align-items: center;
  width: fit-content;
}

.quantity-btn {
  width: 40px;
  height: 40px;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  cursor: pointer;
}

.quantity-btn:first-child {
  border-radius: 4px 0 0 4px;
}

.quantity-btn:last-child {
  border-radius: 0 4px 4px 0;
}

.quantity-btn.delete-btn {
  background-color: #ffecec;
  color: #ff4f4f;
}

.trash-icon {
  font-size: 1rem;
}

.quantity-controls input {
  width: 60px;
  height: 40px;
  border: 1px solid #ddd;
  border-left: none;
  border-right: none;
  text-align: center;
  font-size: 1rem;
}

.add-to-cart-btn {
  width: 100%;
  padding: 1rem;
  background-color: #0095da;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  margin-bottom: 1.5rem;
  transition: background-color 0.2s;
}

.add-to-cart-btn:hover {
  background-color: #0085c5;
}

.product-details {
  border-top: 1px solid #eee;
  padding-top: 1.5rem;
}

.product-details h3 {
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

.product-details ul {
  list-style: none;
  padding: 0;
}

.product-details li {
  margin-bottom: 0.5rem;
  display: flex;
  line-height: 1.5;
}

.product-details li strong {
  min-width: 130px;
  color: #666;
}

/* Reviews Section */
.reviews-section {
  margin-top: 3rem;
  border-top: 1px solid #eee;
  padding-top: 2rem;
}

.reviews-section h2 {
  margin-bottom: 1.5rem;
}

.review-summary {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
}

@media (max-width: 768px) {
  .review-summary {
    flex-direction: column;
  }
}

.avg-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-right: 2rem;
  border-right: 1px solid #eee;
}

@media (max-width: 768px) {
  .avg-rating {
    border-right: none;
    border-bottom: 1px solid #eee;
    padding-right: 0;
    padding-bottom: 1rem;
    margin-bottom: 1rem;
  }
}

.rating-number {
  font-size: 3rem;
  font-weight: bold;
  color: #333;
  line-height: 1;
  margin-bottom: 0.5rem;
}

.avg-rating .stars {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.rating-bars {
  flex: 1;
}

.rating-bar-container {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}

.star-label {
  width: 60px;
  font-size: 0.9rem;
  color: #666;
}

.rating-bar {
  flex: 1;
  height: 10px;
  background-color: #f0f0f0;
  border-radius: 5px;
  overflow: hidden;
  margin: 0 1rem;
}

.rating-bar-fill {
  height: 100%;
  background-color: #f5a623;
}

.rating-percent {
  width: 40px;
  font-size: 0.9rem;
  color: #666;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.review-item {
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
}

.review-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.review-user {
  font-weight: 500;
}

.review-date {
  color: #666;
  font-size: 0.9rem;
}

.review-rating {
  margin-bottom: 0.5rem;
}

.review-title {
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.review-content {
  line-height: 1.6;
  color: #444;
  margin-bottom: 1rem;
}

.review-helpful {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #666;
}

.helpful-btn {
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  padding: 0.25rem 0.5rem;
  border-radius: 3px;
  cursor: pointer;
}
</style>
