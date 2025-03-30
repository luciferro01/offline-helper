// src/stores/productStore.js
import { defineStore } from 'pinia'
import api from '@/services/apiService'

// Keep your existing mock data
const mockProducts = [
    
    { id: 101, name: 'Wireless Earbuds', price: 2499, seller: 'AudioTech', rating: 4.5, ratingCount: 120, imageUrl: 'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 102, name: 'Smart TV 43 inch', price: 29999, seller: 'ElectronicWorld', rating: 4.7, ratingCount: 85, imageUrl: 'https://images.unsplash.com/photo-1593784991095-a205069470b6?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 103, name: 'Digital Camera', price: 24999, seller: 'CameraShop', rating: 4.2, ratingCount: 67, imageUrl: 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 104, name: 'Gaming Console', price: 37999, seller: 'GameStation', rating: 4.8, ratingCount: 214, imageUrl: 'https://images.unsplash.com/photo-1486572788966-cfd3df1f5b42?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 105, name: 'Tablet 10 inch', price: 19999, seller: 'TabletZone', rating: 4.3, ratingCount: 92, imageUrl: 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 106, name: 'Smart Speaker', price: 3999, seller: 'SoundExpress', rating: 4.0, ratingCount: 45, imageUrl: 'https://images.unsplash.com/photo-1512446816042-444d641267d4?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 107, name: 'External Hard Drive 1TB', price: 4299, seller: 'DataStore', rating: 4.6, ratingCount: 73, imageUrl: 'https://images.unsplash.com/photo-1531492053556-74887922072e?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 108, name: 'Mesh Wifi Router', price: 3999, seller: 'NetworkPro', rating: 4.4, ratingCount: 58, imageUrl: 'https://images.unsplash.com/photo-1544281679-e015f1398a03?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 109, name: 'Men\'s Casual T-Shirt', price: 799, seller: 'FashionHub', rating: 4.1, ratingCount: 127, imageUrl: 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 110, name: 'Women\'s Jeans', price: 1499, seller: 'StyleCentral', rating: 4.3, ratingCount: 89, imageUrl: 'https://images.unsplash.com/photo-1560343776-97e7d202ff0e?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 111, name: 'Running Shoes', price: 3499, seller: 'SportWorld', rating: 4.7, ratingCount: 201, imageUrl: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 112, name: 'Leather Wallet', price: 1299, seller: 'AccessoryShop', rating: 4.2, ratingCount: 63, imageUrl: 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 113, name: 'Classic Wristwatch', price: 2999, seller: 'TimePiece', rating: 4.5, ratingCount: 112, imageUrl: 'https://images.unsplash.com/photo-1523170335258-f5ed11844a49?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 114, name: 'Designer Sunglasses', price: 1699, seller: 'OpticsFashion', rating: 4.0, ratingCount: 47, imageUrl: 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 115, name: 'Hiking Backpack', price: 2699, seller: 'OutdoorGear', rating: 4.6, ratingCount: 83, imageUrl: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 116, name: 'Winter Jacket', price: 3999, seller: 'SeasonalWear', rating: 4.4, ratingCount: 76, imageUrl: 'https://images.unsplash.com/photo-1544923246-77307dd654cb?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 117, name: 'Coffee Maker', price: 3499, seller: 'KitchenPlus', rating: 4.3, ratingCount: 58, imageUrl: 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 118, name: 'High-Speed Blender', price: 2499, seller: 'HomeAppliance', rating: 4.1, ratingCount: 42, imageUrl: 'https://images.unsplash.com/photo-1522838573142-454fa2536ba1?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 119, name: 'Non-stick Cookware Set', price: 4999, seller: 'KitchenEssentials', rating: 4.8, ratingCount: 94, imageUrl: 'https://images.unsplash.com/photo-1584990347449-a5d9e1d1a07c?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 101, name: 'Wireless Earbuds', price: 2499, seller: 'AudioTech', rating: 4.5, ratingCount: 120, imageUrl: 'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 102, name: 'Smart TV 43 inch', price: 29999, seller: 'ElectronicWorld', rating: 4.7, ratingCount: 85, imageUrl: 'https://images.unsplash.com/photo-1593784991095-a205069470b6?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 103, name: 'Digital Camera', price: 24999, seller: 'CameraShop', rating: 4.2, ratingCount: 67, imageUrl: 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 104, name: 'Gaming Console', price: 37999, seller: 'GameStation', rating: 4.8, ratingCount: 214, imageUrl: 'https://images.unsplash.com/photo-1486572788966-cfd3df1f5b42?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 105, name: 'Tablet 10 inch', price: 19999, seller: 'TabletZone', rating: 4.3, ratingCount: 92, imageUrl: 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 106, name: 'Smart Speaker', price: 3999, seller: 'SoundExpress', rating: 4.0, ratingCount: 45, imageUrl: 'https://images.unsplash.com/photo-1512446816042-444d641267d4?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 107, name: 'External Hard Drive 1TB', price: 4299, seller: 'DataStore', rating: 4.6, ratingCount: 73, imageUrl: 'https://images.unsplash.com/photo-1531492053556-74887922072e?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 108, name: 'Mesh Wifi Router', price: 3999, seller: 'NetworkPro', rating: 4.4, ratingCount: 58, imageUrl: 'https://images.unsplash.com/photo-1544281679-e015f1398a03?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 109, name: 'Men\'s Casual T-Shirt', price: 799, seller: 'FashionHub', rating: 4.1, ratingCount: 127, imageUrl: 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 110, name: 'Women\'s Jeans', price: 1499, seller: 'StyleCentral', rating: 4.3, ratingCount: 89, imageUrl: 'https://images.unsplash.com/photo-1560343776-97e7d202ff0e?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 111, name: 'Running Shoes', price: 3499, seller: 'SportWorld', rating: 4.7, ratingCount: 201, imageUrl: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 112, name: 'Leather Wallet', price: 1299, seller: 'AccessoryShop', rating: 4.2, ratingCount: 63, imageUrl: 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 113, name: 'Classic Wristwatch', price: 2999, seller: 'TimePiece', rating: 4.5, ratingCount: 112, imageUrl: 'https://images.unsplash.com/photo-1523170335258-f5ed11844a49?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 114, name: 'Designer Sunglasses', price: 1699, seller: 'OpticsFashion', rating: 4.0, ratingCount: 47, imageUrl: 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 115, name: 'Hiking Backpack', price: 2699, seller: 'OutdoorGear', rating: 4.6, ratingCount: 83, imageUrl: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 116, name: 'Winter Jacket', price: 3999, seller: 'SeasonalWear', rating: 4.4, ratingCount: 76, imageUrl: 'https://images.unsplash.com/photo-1544923246-77307dd654cb?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 117, name: 'Coffee Maker', price: 3499, seller: 'KitchenPlus', rating: 4.3, ratingCount: 58, imageUrl: 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    { id: 118, name: 'High-Speed Blender', price: 2499, seller: 'HomeAppliance', rating: 4.1, ratingCount: 42, imageUrl: 'https://images.unsplash.com/photo-1522838573142-454fa2536ba1?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80' },
    
      
  // Your existing mock products array
];

// Mock featured products (keep your existing implementation)
const mockFeaturedProducts = mockProducts

export const useProductStore = defineStore('product', {
  state: () => ({
    featuredProducts: [],
    allProducts: [],
    currentProduct: null,
    loading: false,
    error: null,
    search: ' ',
    statusMessage: '',
    currentProductOffering: null,

    // Pagination state
    currentPage: 1,
    itemsPerPage: 8,
    totalItems: 0,
    totalPages: 1,

    // Flag to toggle between mock data and API calls
    //useMockData: false, // Set to false when ready to use real API
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
    //   if (this.useMockData) {
    //     // Simulate API delay
    //     await new Promise((resolve) => setTimeout(resolve, 500))
    //     this.allProducts = mockProducts
    //     // this.statusMessage = 'Products loaded from mock data'
    //     this.loading = false
    //     return this.allProducts
    //   }

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
    //   if (this.useMockData) {
    //     // Simulate API delay
    //     await new Promise((resolve) => setTimeout(resolve, 500))
        this.featuredProducts = mockFeaturedProducts
        // this.statusMessage = 'Featured products loaded from mock data'
        // this.loading = false
        return this.featuredProducts
      //}

      // Original API code
    //   try {
    //     const response = await api.get(`/search?query=${this.search}`)
    //     this.featuredProducts = response.data.data
    //     this.statusMessage = response.message
    //     return this.featuredProducts
    //   } catch (error) {
    //     console.error('Error fetching featured products:', error)
    //     this.error = error.message || 'Failed to fetch featured products'
    //     return []
    //   } finally {
    //     this.loading = false
    //   }
    },

    // Keep your existing fetchProductById implementation

    // Update your fetchProductById method in productStore.js

    //TODO: Old Code
    // async fetchProductById(productId) {
    //   this.loading = true
    //   this.error = null
    //   this.currentProduct = null

    //   try {
    //     // 1. Fetch basic product info
    //     const productResponse = await api.get(`/products/${productId}`)
    //     const productData = productResponse.data.data
    //     console.log('Product Data:', productData)
    //     // 2. Fetch sellers for this product
    //     const sellersResponse = await api.get(
    //       `/seller/sellersForProduct?productId=${Number(productId)}`,
    //     )
    //     const sellers = sellersResponse.data.data || []

    //     // Start building our complete product object
    //     const product = {
    //       ...productData,
    //       sellers: sellers,
    //       images: productData.imagesUrl ? [productData.imagesUrl] : [],
    //       rating: 0,
    //       ratingCount: 0,
    //       reviews: [],
    //     }

    //     // 3. If sellers exist, get the first offering's details and reviews
    //     if (sellers.length > 0) {
    //       const firstOfferingId = sellers[0].id

    //       // Get offering details
    //       const offeringResponse = await api.get(`/seller/${firstOfferingId}`)
    //       const offeringData = offeringResponse.data.data

    //       // Get reviews
    //       const reviewsResponse = await api.get(`/reviews/${firstOfferingId}`)
    //       const reviews = reviewsResponse.data.data || []

    //       // Complete the product object
    //       product.offering = offeringData
    //       product.reviews = reviews || []
    //       product.rating = offeringData.rating || 0
    //       product.ratingCount = reviews.length
    //       product.details = {
    //         category: productData.categoryName,
    //         // Add other details as needed
    //       }
    //     }

    //     this.currentProduct = product
    //     return product
    //   } catch (error) {
    //     console.error(`Error fetching product ${productId}:`, error)
    //     this.error = error.message || 'Failed to fetch product details'
    //     return null
    //   } finally {
    //     this.loading = false
    //   }
    // },

    async fetchProductById(productId) {
      this.loading = true
      this.error = null
      this.currentProduct = null

      try {
        // 1. Fetch basic product info
        const productResponse = await api.get(`/products/${productId}`)
        const productData = productResponse.data.data
        console.log('Product Data:', productData)

        // 2. Fetch sellers for this product
        const sellersResponse = await api.get(
          `/seller/sellersForProduct?productId=${Number(productId)}`,
        )
        const sellers = sellersResponse.data.data || []
        console.log('Sellers:', sellers)

        // Start building our complete product object
        const product = {
          ...productData,
          sellers: sellers,
          images: productData.imagesUrl ? [productData.imagesUrl] : [],
          rating: 0,
          ratingCount: 0,
          reviews: [],
        }

        // 3. If sellers exist, get the correct offering details for this product
        let offeringData = null
        let reviews = []

        if (sellers.length > 0) {
          for (let seller of sellers) {
            // Fetch offerings for the seller
            const offeringsResponse = await api.get(`/seller/${seller.id}/offerings`)
            const offerings = offeringsResponse.data.data || []

            // Log the entire offerings array to inspect its structure
            console.log('Offerings Array:', offerings)

            // Check if offerings array is populated
            if (!offerings || offerings.length === 0) {
              console.error(`No offerings found for seller ${seller.id}`)
              continue // Skip to next seller
            }

            // Log each offering to ensure productId is available
            offerings.forEach((offering) => console.log('Offering:', offering))

            // Find the correct offering for the current productId
            offeringData = offerings.find(
              (offering) => Number(offering.productId) === Number(productId),
            )

            // Log the result of the find operation
            console.log('OfferingData from find:', offeringData)

            // If we found the correct offering, break the loop
            if (offeringData) {
              console.log('Found offering:', offeringData)
              break // Exit loop after finding the offering
            }
          }
        }

        // If we found a valid offering, fetch reviews for it
        if (offeringData) {
          // Fetch reviews for the offering
          try {
            const reviewsResponse = await api.get(`/reviews/${offeringData.id}`)
            reviews = reviewsResponse.data.data || []
          } catch (reviewError) {
            console.warn(`No reviews found for offering ID: ${offeringData.id}`)
          }

          console.log("reviews: ")
          console.log(reviews)

          // Complete the product object with offering and reviews
          product.offering = offeringData
          product.reviews = reviews || []
          product.rating = offeringData.rating || 0
          product.ratingCount = reviews.length

          // Add product details (you can add other details as necessary)
          product.details = {
            category: productData.categoryName,
          }
        } else {
          console.warn(`No valid offering found for product ID: ${productId}`)
        }

        // Assign the complete product to currentProduct
        
        this.currentProduct = product
        return product
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
        const response = await api.get(`/search/all`, {
          params: {
            query: query,
            page: page - 1, // Adjust for 0-based indexing in Spring Boot
            size: itemsPerPage,
          },
        })

        // Process the response
        // Extract data from the CommonResponse wrapper
        const commonResponse = response.data || {}

        // Extract the PageImpl from the data field of CommonResponse
        const pageData = commonResponse.data || {}

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