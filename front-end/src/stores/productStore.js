// // src/stores/productStore.js
// import { defineStore } from 'pinia'
// import api from '@/services/apiService'

// export const useProductStore = defineStore('product', {
//   state: () => ({
//     featuredProducts: [],
//     allProducts: [],
//     currentProduct: null,
//     loading: false,
//     error: null,
//     search: '',
//     statusMessage: '',
//   }),

//   getters: {
//     isFetching: (state) => state.loading,
//     hasError: (state) => !!state.error,
//     errorMessage: (state) => state.error,
//   },

//   actions: {
//     // Fetch all products
//     async fetchProducts() {
//       this.loading = true
//       this.error = null

//       try {
//         const response = await api.get(`/search?${this.search}`)

//         // The API service already validates success status and extracts data
//         this.allProducts = response.data
//         this.statusMessage = response.message
//         return this.allProducts
//       } catch (error) {
//         console.error('Error fetching products:', error)
//         this.error = error.message || 'Failed to fetch products'
//         return []
//       } finally {
//         this.loading = false
//       }
//     },

//     // Fetch featured products
//     async fetchFeaturedProducts() {
//       this.loading = true
//       this.error = null

//       try {
//         const response = await api.get('/search?=" "')

//         this.featuredProducts = response.data
//         this.statusMessage = response.message
//         return this.featuredProducts
//       } catch (error) {
//         console.error('Error fetching featured products:', error)
//         this.error = error.message || 'Failed to fetch featured products'
//         return []
//       } finally {
//         this.loading = false
//       }
//     },

//     // Fetch a single product by ID
//     async fetchProductById(productId) {
//       this.loading = true
//       this.error = null
//       this.currentProduct = null

//       try {
//         const response = await api.get(`/products/${productId}`)

//         this.currentProduct = response.data
//         this.statusMessage = response.message
//         return this.currentProduct
//       } catch (error) {
//         console.error(`Error fetching product ${productId}:`, error)
//         this.error = error.message || 'Failed to fetch product details'
//         return null
//       } finally {
//         this.loading = false
//       }
//     },

//     // Set current product (used when navigating from product list to detail)
//     setCurrentProduct(product) {
//       this.currentProduct = product
//     },

//     // Search products by query
//     async searchProducts(query) {
//       this.loading = true
//       this.error = null

//       try {
//         const response = await api.get('/search', {
//           params: { q: query },
//         })

//         // Update allProducts with search results
//         this.allProducts = response.data
//         this.statusMessage = response.message
//         return this.allProducts
//       } catch (error) {
//         console.error('Error searching products:', error)
//         this.error = error.message || 'Failed to search products'
//         return []
//       } finally {
//         this.loading = false
//       }
//     },

//     // Filter products by category
//     async filterByCategory(categoryId) {
//       this.loading = true
//       this.error = null

//       try {
//         const response = await api.get('/products/category/' + categoryId)

//         this.allProducts = response.data
//         this.statusMessage = response.message
//         return this.allProducts
//       } catch (error) {
//         console.error('Error filtering products:', error)
//         this.error = error.message || 'Failed to filter products'
//         return []
//       } finally {
//         this.loading = false
//       }
//     },

//     // Handle error (useful for clearing errors or showing specific messages)
//     setError(message) {
//       this.error = message
//     },

//     clearError() {
//       this.error = null
//     },
//   },
// })

//MODIFIED CODE WITH MOCK DATA
// src/stores/productStore.js
import { defineStore } from 'pinia'
import api from '@/services/apiService'

// Mock data for development
// Use these reliable image URLs for your mock products

const mockProducts = [
  {
    id: 1,
    name: 'Smart 4K TV 55"',
    price: 49999,
    imageUrl:
      'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&h=200&q=80',
    promoTag: 'Best Seller',
  },
  {
    id: 2,
    name: 'Wireless Noise Cancelling Headphones',
    price: 12999,
    imageUrl:
      'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&h=200&q=80',
    promoTag: 'New Arrival',
  },
  {
    id: 3,
    name: 'Smartphone Pro Max',
    price: 74999,
    imageUrl:
      'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&h=200&q=80',
    promoTag: 'Hot Deal',
  },
  {
    id: 4,
    name: 'Robot Vacuum Cleaner',
    price: 29999,
    imageUrl:
      'https://images.unsplash.com/photo-1563898319827-9a8570a46e96?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&h=200&q=80',
    promoTag: 'Limited Stock',
  },
  {
    id: 5,
    name: 'Gaming Laptop 15"',
    price: 89999,
    imageUrl:
      'https://images.unsplash.com/photo-1593642702821-c8da6771f0c6?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&h=200&q=80',
    promoTag: 'Top Rated',
  },
  {
    id: 6,
    name: 'Coffee Maker Deluxe',
    price: 8499,
    imageUrl:
      'https://images.unsplash.com/photo-1523362289600-a70b4a0e09aa?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&h=200&q=80',
  },
  {
    id: 7,
    name: 'Fitness Smartwatch',
    price: 15999,
    imageUrl:
      'https://images.unsplash.com/photo-1523275335684-37898b6baf30?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&h=200&q=80',
  },
  {
    id: 8,
    name: 'Wireless Bluetooth Speaker',
    price: 5999,
    imageUrl:
      'https://images.unsplash.com/photo-1589003077984-894e133dabab?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&h=200&q=80',
  },
  {
    id: 9,
    name: 'Digital Camera DSLR',
    price: 45999,
    imageUrl:
      'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&h=200&q=80',
  },
  {
    id: 10,
    name: 'Microwave Oven',
    price: 9999,
    imageUrl:
      'https://images.unsplash.com/photo-1584269600454-e947a4c6dbcc?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&h=200&q=80',
  },
]

// Featured products are a subset of all products
const mockFeaturedProducts = mockProducts.slice(0, 5)

export const useProductStore = defineStore('product', {
  state: () => ({
    featuredProducts: [],
    allProducts: [],
    currentProduct: null,
    loading: false,
    error: null,
    search: '',
    statusMessage: '',

    // Flag to toggle between mock data and API calls
    useMockData: true, // Set to false when ready to use real API
  }),

  getters: {
    isFetching: (state) => state.loading,
    hasError: (state) => !!state.error,
    errorMessage: (state) => state.error,
  },

  actions: {
    // Fetch all products
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
        const response = await api.get(`/search?${this.search}`)
        this.allProducts = response.data
        this.statusMessage = response.message
        return this.allProducts
      } catch (error) {
        console.error('Error fetching products:', error)
        this.error = error.message || 'Failed to fetch products'
        return []
      } finally {
        this.loading = false
      }
    },

    // Fetch featured products
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
        const response = await api.get('/search?=" "')
        this.featuredProducts = response.data
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

    // Fetch a single product by ID
    async fetchProductById(productId) {
      this.loading = true
      this.error = null
      this.currentProduct = null

      // Use mock data if flag is set
      if (this.useMockData) {
        // Simulate API delay
        await new Promise((resolve) => setTimeout(resolve, 300))
        const product = mockProducts.find((p) => p.id === productId)

        if (product) {
          this.currentProduct = product
          //this.statusMessage = 'Product loaded from mock data'
        } else {
          this.error = 'Product not found'
        }

        this.loading = false
        return this.currentProduct
      }

      // Original API code
      try {
        const response = await api.get(`/products/${productId}`)
        this.currentProduct = response.data
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

    // Set current product (used when navigating from product list to detail)
    setCurrentProduct(product) {
      this.currentProduct = product
    },

    // Search products by query
    async searchProducts(query) {
      this.loading = true
      this.error = null

      // Use mock data if flag is set
      if (this.useMockData) {
        // Simulate API delay
        await new Promise((resolve) => setTimeout(resolve, 300))

        // Simple search implementation on mock data
        const searchResults = query
          ? mockProducts.filter((p) => p.name.toLowerCase().includes(query.toLowerCase()))
          : mockProducts

        this.allProducts = searchResults
        this.statusMessage = `Found ${searchResults.length} products matching "${query}"`
        this.loading = false
        return this.allProducts
      }

      // Original API code
      try {
        const response = await api.get('/products/search', {
          params: { q: query },
        })
        this.allProducts = response.data
        this.statusMessage = response.message
        return this.allProducts
      } catch (error) {
        console.error('Error searching products:', error)
        this.error = error.message || 'Failed to search products'
        return []
      } finally {
        this.loading = false
      }
    },

    // Filter products by category
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
        this.allProducts = response.data
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

    // Handle error (useful for clearing errors or showing specific messages)
    setError(message) {
      this.error = message
    },

    clearError() {
      this.error = null
    },
  },
})
