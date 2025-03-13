// src/stores/productStore.js
import { defineStore } from 'pinia'
import api from '@/services/apiService'

// Keep your existing mock data
const mockProducts = [
  // Your existing mock products array
]

// Mock featured products (keep your existing implementation)
const mockFeaturedProducts = mockProducts.slice(0, 5)

export const useProductStore = defineStore('product', {
  state: () => ({
    featuredProducts: [],
    allProducts: [],
    currentProduct: null,
    loading: false,
    error: null,
    search: ' ',
    statusMessage: '',

    // Pagination state
    currentPage: 1,
    itemsPerPage: 8,
    totalItems: 0,
    totalPages: 1,

    // Flag to toggle between mock data and API calls
    useMockData: true, // Set to false when ready to use real API
  }),

  getters: {
    isFetching: (state) => state.loading,
    hasError: (state) => !!state.error,
    errorMessage: (state) => state.error,
    paginatedProducts: (state) => {
      if (state.useMockData) {
        const startIndex = (state.currentPage - 1) * state.itemsPerPage
        return state.allProducts.slice(startIndex, startIndex + state.itemsPerPage)
      }
      return state.allProducts
    },
  },

  actions: {
    // Keep your existing fetchProducts implementation untouched
    async fetchProducts() {
      this.loading = true
      this.error = null

      // Use mock data if flag is set
      if (this.useMockData) {
        // Simulate API delay
        await new Promise((resolve) => setTimeout(resolve, 500))
        this.allProducts = mockProducts
        // this.statusMessage = 'Products loaded from mock data'
        this.loading = false
        return this.allProducts
      }

      // Original API code
      try {
        const response = await api.get(`/search?query=${this.search}`)
        this.allProducts = response.data.data
        this.statusMessage = response.message
        console.log(response.data.data)
        return this.allProducts
      } catch (error) {
        console.error('Error fetching products:', error)
        this.error = error.message || 'Failed to fetch products'
        return []
      } finally {
        this.loading = false
      }
    },

    // Keep your existing fetchFeaturedProducts implementation untouched
    async fetchFeaturedProducts() {
      this.loading = true
      this.error = null

      // Use mock data if flag is set
      if (this.useMockData) {
        // Simulate API delay
        await new Promise((resolve) => setTimeout(resolve, 500))
        this.featuredProducts = mockFeaturedProducts
        // this.statusMessage = 'Featured products loaded from mock data'
        this.loading = false
        return this.featuredProducts
      }

      // Original API code
      try {
        const response = await api.get('/search?query=${this.search}')
        this.featuredProducts = response.data.data
        this.statusMessage = response.message
        return this.featuredProducts
      } catch (error) {
        console.error('Error fetching featured products:', error)
        this.error = error.message || 'Failed to fetch featured products'
        return []
      } finally {
        this.loading = false
      }
    },

    // Keep your existing fetchProductById implementation
    async fetchProductById(productId) {
      this.loading = true
      this.error = null
      this.currentProduct = null

      // Original API code
      try {
        const response = await api.get(`/products/${productId}`)
        this.currentProduct = response.data.data
        this.statusMessage = response.message
        return this.currentProduct
      } catch (error) {
        console.error(`Error fetching product ${productId}:`, error)
        this.error = error.message || 'Failed to fetch product details'
        return null
      } finally {
        this.loading = false
      }
    },

    // Keep your existing setCurrentProduct implementation
    setCurrentProduct(product) {
      this.currentProduct = product
    },

    // Main search method - now only calls searchByAll
    async searchProducts(query, page = 1, itemsPerPage = 8) {
      return this.searchByAll(query, page, itemsPerPage)
    },

    // Search products by all fields with pagination
    async searchByAll(query, page = 1, itemsPerPage = 8) {
      this.loading = true
      this.error = null
      this.currentPage = page
      this.itemsPerPage = itemsPerPage
      this.search = query // Store the search query

      // Use mock data if flag is set
      if (this.useMockData) {
        // Simulate API delay
        await new Promise((resolve) => setTimeout(resolve, 300))

        // Simple search implementation on mock data
        const searchResults = query
          ? mockProducts.filter((p) => {
              const searchLower = query.toLowerCase()
              return (
                p.name.toLowerCase().includes(searchLower) ||
                (p.seller && p.seller.toLowerCase().includes(searchLower))
              )
            })
          : mockProducts

        this.totalItems = searchResults.length
        this.totalPages = Math.ceil(this.totalItems / this.itemsPerPage)

        // Store all filtered results
        this.allProducts = searchResults

        this.statusMessage = `Found ${searchResults.length} products matching "${query}"`
        this.loading = false
        return this.paginatedProducts
      }

      // API call to the backend
      try {
        const response = await api.get(`/all`, {
          params: {
            query: query,
            page: page - 1, // Adjust for 0-based indexing in Spring Boot
            size: itemsPerPage,
          },
        })

        // Process the response
        // Extract data from the CommonResponse wrapper
        const commonResponse = response.data.data || {}

        // Extract the PageImpl from the data field of CommonResponse
        const pageData = commonResponse.data.data || {}

        this.allProducts = pageData.content || []
        this.totalItems = pageData.totalElements || this.allProducts.length
        this.totalPages = pageData.totalPages || Math.ceil(this.totalItems / this.itemsPerPage)
        this.currentPage = pageData.number !== undefined ? pageData.number + 1 : this.currentPage

        this.statusMessage =
          commonResponse.message || `Found ${this.totalItems} products matching "${query}"`
        this.loading = false
        return this.paginatedProducts
      } catch (error) {
        console.error('Error searching products:', error)
        this.error = error.message || 'Failed to search products'
        this.loading = false
        return []
      }
    },

    // Navigation for pagination
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.setPage(this.currentPage + 1)
      }
    },

    prevPage() {
      if (this.currentPage > 1) {
        this.setPage(this.currentPage - 1)
      }
    },

    setPage(page) {
      // Only fetch if the page is different
      if (page !== this.currentPage) {
        this.currentPage = page
        // Re-fetch with current search query and new page
        this.searchByAll(this.search, page, this.itemsPerPage)
      }
    },

    // Keep your existing filterByCategory implementation
    async filterByCategory(categoryId) {
      this.loading = true
      this.error = null

      // Use mock data if flag is set
      if (this.useMockData) {
        // Simulate API delay
        await new Promise((resolve) => setTimeout(resolve, 300))

        // For mock data, just return all products (no categories implemented)
        this.allProducts = mockProducts
        this.statusMessage = 'Category filter applied (mock data)'
        this.loading = false
        return this.allProducts
      }

      // Original API code
      try {
        const response = await api.get('/products/category/' + categoryId)
        this.allProducts = response.data.data
        this.statusMessage = response.message
        return this.allProducts
      } catch (error) {
        console.error('Error filtering products:', error)
        this.error = error.message || 'Failed to filter products'
        return []
      } finally {
        this.loading = false
      }
    },

    // Keep your existing error handling methods
    setError(message) {
      this.error = message
    },

    clearError() {
      this.error = null
    },
  },
})
