<template>
  <div class="search-results-page">
    <h2>Search Results</h2>

    <!-- Loading indicator -->
    <div v-if="loading" class="loading-indicator">
      <div class="spinner"></div>
      <p>Searching products...</p>
    </div>

    <!-- Error message -->
    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <button @click="retrySearch" class="retry-button">Try Again</button>
    </div>

    <!-- Display products using ProductCard -->
    <div v-else-if="searchResults.length > 0" class="products-grid">
      <ProductCard v-for="product in searchResults" :key="product.id" :product="product" />
    </div>

    <!-- Display message if no products are found -->
    <div v-else class="empty-products">
      <p>No products found matching your search.</p>
    </div>
  </div>
</template>

<script>
import { useRoute, useRouter } from 'vue-router'
import { ref, watch, computed, onMounted } from 'vue'
import { useProductStore } from '@/stores/productStore' // Import productStore
import ProductCard from '@/components/product-card.vue'

export default {
  name: 'SearchResults',
  components: {
    ProductCard,
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const searchQuery = ref(' ')

    // Use the productStore directly
    const productStore = useProductStore()

    // Computed properties from the store
    const searchResults = computed(() => productStore.allProducts)
    const loading = computed(() => productStore.loading)
    const error = computed(() => productStore.error)

    // Watch for query parameter changes in the URL
    watch(
      () => route.query.q,
      async (newQuery) => {
        if (newQuery) {
          searchQuery.value = newQuery
          await performSearch(newQuery)
        } else {
          // If no query, reset and redirect to home
          productStore.allProducts = []
          router.push('/')
        }
      },
      { immediate: true },
    )

    // Search function that calls the store's searchProducts action
    const performSearch = async (query) => {
      if (!query || query.trim() === '') {
        productStore.allProducts = []
        return
      }

      try {
        await productStore.searchProducts(query)
      } catch (err) {
        console.error('Error searching products:', err)
      }
    }

    // Retry search if there was an error
    const retrySearch = () => {
      productStore.clearError()
      performSearch(searchQuery.value)
    }

    // Format price with commas (helper function)
    const formatPrice = (price) => {
      return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }

    onMounted(() => {
      // If there's a query in the URL, perform search on mount
      if (route.query.q) {
        searchQuery.value = route.query.q
        performSearch(searchQuery.value)
      }
    })

    return {
      searchQuery,
      searchResults,
      loading,
      error,
      formatPrice,
      retrySearch,
    }
  },
}
</script>

<style scoped>
.search-results-page {
  padding: 20px;
  font-family: 'Roboto', Arial, sans-serif;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
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
