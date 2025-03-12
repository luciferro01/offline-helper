<template>
  <div class="home-page">
    <!-- Loading and Error States -->
    <div v-if="loading" class="loading-indicator">
      <div class="spinner"></div>
      <p>Loading products...</p>
    </div>

    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <button @click="retryLoading" class="retry-button">Try Again</button>
    </div>

    <div v-else-if="statusMessage" class="status-message">
      {{ statusMessage }}
    </div>

    <!-- Main Content (only shown when not loading and no errors) -->
    <template v-if="!loading && !error">
      <!-- Product Carousel -->
      <div v-if="featuredProducts.length > 0" class="product-carousel">
        <button class="carousel-arrow left" @click="prevSlide">
          <span>‹</span>
        </button>

        <div class="carousel-container">
          <div
            class="carousel-slides"
            :style="{ transform: `translateX(-${currentSlide * 100}%)` }"
          >
            <div
              v-for="(product, index) in featuredProducts"
              :key="product.id"
              class="carousel-slide"
            >
              <div class="carousel-product">
                <img :src="product.imageUrl" :alt="product.name" />
                <div class="carousel-product-info">
                  <div class="promo-tag">{{ product.promoTag || 'Featured' }}</div>
                  <h2>{{ product.name }}</h2>
                  <p class="price">₹{{ formatPrice(product.price) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <button class="carousel-arrow right" @click="nextSlide">
          <span>›</span>
        </button>

        <div class="carousel-dots">
          <button
            v-for="(_, index) in featuredProducts"
            :key="index"
            class="carousel-dot"
            :class="{ active: index === currentSlide }"
            @click="goToSlide(index)"
          ></button>
        </div>
      </div>

      <!-- All Products Section -->
      <div class="all-products-section">
        <h2 class="section-title">Our Products</h2>

        <!-- Empty state for products -->
        <div v-if="allProducts.length === 0" class="empty-products">
          <p>No products found. Check back soon for new items!</p>
        </div>

        <div v-else class="products-grid">
          <ProductCard v-for="product in allProducts" :key="product.id" :product="product">
            <!-- <template v-slot:badge v-if="product.isNew">
                <div class="new-badge">New</div>
              </template> -->
          </ProductCard>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { mapState, mapActions } from 'pinia'
import { useProductStore } from '@/stores/productStore'
import ProductCard from '@/components/ProductCard.vue'

export default {
  name: 'HomePage',

  components: {
    ProductCard,
  },

  data() {
    return {
      currentSlide: 0,
      autoplayInterval: null,
    }
  },

  computed: {
    ...mapState(useProductStore, [
      'featuredProducts',
      'allProducts',
      'loading',
      'error',
      'statusMessage',
    ]),
  },

  methods: {
    ...mapActions(useProductStore, ['fetchProducts', 'fetchFeaturedProducts', 'clearError']),

    prevSlide() {
      if (this.featuredProducts.length === 0) return
      this.currentSlide =
        (this.currentSlide - 1 + this.featuredProducts.length) % this.featuredProducts.length
      this.resetAutoplay()
    },

    nextSlide() {
      if (this.featuredProducts.length === 0) return
      this.currentSlide = (this.currentSlide + 1) % this.featuredProducts.length
      this.resetAutoplay()
    },

    goToSlide(index) {
      this.currentSlide = index
      this.resetAutoplay()
    },

    startAutoplay() {
      if (this.featuredProducts.length <= 1) return // Don't autoplay if only one slide
      this.autoplayInterval = setInterval(() => {
        this.nextSlide()
      }, 5000) // Change slide every 5 seconds
    },

    resetAutoplay() {
      clearInterval(this.autoplayInterval)
      this.startAutoplay()
    },

    formatPrice(price) {
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    },

    // Retry loading products if there was an error
    async retryLoading() {
      this.clearError() // Clear the previous error
      try {
        await Promise.all([this.fetchProducts(), this.fetchFeaturedProducts()])

        // If products loaded successfully, start carousel
        if (this.featuredProducts.length > 0) {
          this.startAutoplay()
        }
      } catch (error) {
        console.error('Error retrying product load:', error)
      }
    },
  },

  async mounted() {
    try {
      // Fetch products from API
      await Promise.all([this.fetchProducts(), this.fetchFeaturedProducts()])

      // Start carousel if we have featured products
      if (this.featuredProducts.length > 0) {
        this.startAutoplay()
      }
    } catch (error) {
      console.error('Error loading products:', error)
    }
  },

  beforeUnmount() {
    // Clear interval when component is unmounted
    clearInterval(this.autoplayInterval)
  },
}
</script>

<style scoped>
.home-page {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  font-family: 'Roboto', Arial, sans-serif;
}

/* Carousel Styles */
.product-carousel {
  position: relative;
  width: 100%;
  margin-bottom: 2rem;
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.carousel-container {
  width: 100%;
  overflow: hidden;
}

.carousel-slides {
  display: flex;
  transition: transform 0.5s ease;
}

.carousel-slide {
  min-width: 100%;
  height: 350px;
}

.carousel-product {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  background: #f5f5f5;
}

.carousel-product img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-product-info {
  position: absolute;
  left: 40px;
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-radius: 8px;
  max-width: 300px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.carousel-product-info h2 {
  margin: 0.5rem 0;
  font-size: 1.4rem;
  color: #333;
}

.carousel-product-info .price {
  color: #0095da;
  font-weight: bold;
  font-size: 1.2rem;
  margin-top: 0.5rem;
}

.promo-tag {
  display: inline-block;
  background-color: #ff4f4f;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.carousel-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  font-size: 24px;
  z-index: 2;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  color: #0095da;
}

.carousel-arrow.left {
  left: 15px;
}

.carousel-arrow.right {
  right: 15px;
}

.carousel-dots {
  position: absolute;
  bottom: 15px;
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 8px;
}

.carousel-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.carousel-dot.active {
  width: 12px;
  height: 12px;
  background: white;
}

/* All Products Section */
.all-products-section {
  margin-bottom: 3rem;
}

.section-title {
  margin-bottom: 1rem;
  font-size: 1.5rem;
  color: #0095da;
  position: relative;
  padding-bottom: 0.5rem;
}

.section-title::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 50px;
  height: 3px;
  background-color: #0095da;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
}

@media (max-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 576px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.new-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #0095da;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: bold;
}

.loading-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  text-align: center;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #0095da;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.error-message {
  background-color: #fff1f1;
  border: 1px solid #ff4f4f;
  color: #ff4f4f;
  padding: 1.5rem;
  border-radius: 8px;
  text-align: center;
  margin: 2rem 0;
}

.status-message {
  background-color: #e6f7ff;
  border: 1px solid #0095da;
  color: #0095da;
  padding: 1rem;
  border-radius: 8px;
  text-align: center;
  margin-bottom: 1.5rem;
}

.retry-button {
  background-color: #0095da;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-weight: bold;
  margin-top: 1rem;
  cursor: pointer;
}

.retry-button:hover {
  background-color: #0085c5;
}

.empty-products {
  text-align: center;
  padding: 3rem;
  background-color: #f9f9f9;
  border-radius: 8px;
  color: #666;
}
</style>
