<template>
  <div class="search-results-page">
    <h2 class="search-title">
      {{ searchQuery ? `Search results for "${searchQuery}"` : 'All Products' }}
    </h2>

    <div v-if="productStore.loading">
      <div class="loading-indicator">
        <div class="spinner"></div>
        <p>Searching products...</p>
      </div>
    </div>

    <div v-else-if="productStore.error" class="error-message">
      <p>{{ productStore.error }}</p>
      <button @click="retrySearch" class="retry-button">Try Again</button>
    </div>

    <div v-else-if="paginatedProducts.length > 0" class="products-grid">
  <ProductCard
    v-for="product in paginatedProducts"
    :key="product.id"
    :product="product"
    
  />
</div>

    <div v-else class="empty-products">
      <p>No products found matching your search.</p>
      <p class="suggestion">Try different search terms.</p>
    </div>

    <!-- Pagination Controls -->
    <div v-if="productStore.totalItems > 0" class="pagination">
      <button
        :disabled="productStore.currentPage === 1"
        @click="productStore.prevPage()"
        class="pagination-button"
      >
        Previous
      </button>

      <!-- Display page numbers -->
      <button
        v-for="page in displayedPageNumbers"
        :key="page"
        @click="typeof page === 'number' ? productStore.setPage(page) : null"
        :class="{ active: page === productStore.currentPage, ellipsis: page === '...' }"
        class="pagination-button"
        :disabled="page === '...'"
      >
        {{ page }}
      </button>

      <button
        :disabled="productStore.currentPage === productStore.totalPages"
        @click="productStore.nextPage()"
        class="pagination-button"
      >
        Next
      </button>
    </div>

    <!-- Search Metadata -->
    <div v-if="paginatedProducts.length > 0" class="search-metadata">
      <p>Showing {{ paginationInfo }}</p>
    </div>
  </div>
</template>

<script>
import { useRoute, useRouter } from 'vue-router'
import { ref, onMounted, watch, computed } from 'vue'
import ProductCard from '@/components/product-card.vue'
import { useProductStore } from '@/stores/productStore' // Import ProductStore

export default {
  name: 'SearchResults',
  components: {
    ProductCard,
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const productStore = useProductStore()

    const searchQuery = ref('')

    // Initialize from URL parameters
    onMounted(() => {
      if (route.query.q) {
        searchQuery.value = route.query.q
        performSearch()
      } else {
        // If no search query, you can choose to show all products or redirect
        productStore.fetchProducts()
      }
    })

    // Watch for query parameter changes in the URL
    watch(
      () => route.query.q,
      (newQuery) => {
        if (newQuery !== undefined) {
          searchQuery.value = newQuery || ''
          performSearch()
        }
        if (newQuery === '') {
          router.push('/') // Redirect to homepage if search query is empty
        }
      },
    )

    // Function to perform search with current page and items per page
    const performSearch = async () => {
      if (!searchQuery.value || searchQuery.value.trim() === '') {
        return
      }

      try {
        await productStore.searchProducts(
          searchQuery.value,
          productStore.currentPage,
          productStore.itemsPerPage,
        )
      } catch (err) {
        console.error('Error searching products:', err)
      }
    }

    // Retry search if there was an error
    const retrySearch = () => {
      productStore.clearError()
      performSearch()
    }

    

    // Get paginated products from store
    const paginatedProducts = computed(() => {
      return productStore.paginatedProducts
    })

    // Pagination info
    const paginationInfo = computed(() => {
      const start = (productStore.currentPage - 1) * productStore.itemsPerPage + 1
      const end = Math.min(start + productStore.itemsPerPage - 1, productStore.totalItems)
      return `${start}-${end} of ${productStore.totalItems} products`
    })

    // Compute a limited set of page numbers to display for better UI
    const displayedPageNumbers = computed(() => {
      const totalPages = productStore.totalPages
      const currentPage = productStore.currentPage

      if (totalPages <= 7) {
        // If fewer than 7 pages, show all
        return Array.from({ length: totalPages }, (_, i) => i + 1)
      }

      if (currentPage <= 4) {
        // If near the beginning, show first 5 pages + ellipsis + last page
        return [1, 2, 3, 4, 5, '...', totalPages]
      }

      if (currentPage >= totalPages - 3) {
        // If near the end, show first page + ellipsis + last 5 pages
        return [
          1,
          '...',
          totalPages - 4,
          totalPages - 3,
          totalPages - 2,
          totalPages - 1,
          totalPages,
        ]
      }

      // If in the middle, show first page + ellipsis + 3 pages around current + ellipsis + last page
      return [1, '...', currentPage - 1, currentPage, currentPage + 1, '...', totalPages]
    })

    return {
      router,
      productStore,
      searchQuery,
      paginatedProducts,
      displayedPageNumbers,
      paginationInfo,
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

.search-title {
  margin-bottom: 20px;
  color: #333;
  font-size: 1.5rem;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
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

.suggestion {
  font-size: 0.9rem;
  margin-top: 10px;
  color: #888;
}

.search-metadata {
  text-align: center;
  color: #666;
  font-size: 0.9rem;
  margin-top: 20px;
}

/* Pagination Styles */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination-button {
  padding: 8px 12px;
  margin: 0 5px;
  border: 1px solid #ddd;
  background-color: #fff;
  color: #333;
  cursor: pointer;
  border-radius: 4px;
  transition:
    background-color 0.3s ease,
    color 0.3s ease;
}

.pagination-button:hover:enabled {
  background-color: #f0f0f0;
}

.pagination-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-button.active {
  background-color: #0095da;
  color: white;
  border-color: #0095da;
}

.pagination-button.ellipsis {
  border: none;
  padding: 8px 6px;
  background: transparent;
}
</style>
