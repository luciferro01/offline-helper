// src/stores/productStore.js
import { defineStore } from 'pinia';
import api from '@/services/apiService';

export const useProductStore = defineStore('product', {
  state: () => ({
    featuredProducts: [],
    allProducts: [],
    currentProduct: null,
    loading: false,
    error: null,
    statusMessage: ''
  }),
  
  getters: {
    isFetching: (state) => state.loading,
    hasError: (state) => !!state.error,
    errorMessage: (state) => state.error
  },
  
  actions: {
    // Fetch all products
    async fetchProducts() {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await api.get('/products');
        
        // The API service already validates success status and extracts data
        this.allProducts = response.data;
        this.statusMessage = response.message;
        return this.allProducts;
      } catch (error) {
        console.error('Error fetching products:', error);
        this.error = error.message || 'Failed to fetch products';
        return [];
      } finally {
        this.loading = false;
      }
    },
    
    // Fetch featured products
    async fetchFeaturedProducts() {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await api.get('/products/featured');
        
        this.featuredProducts = response.data;
        this.statusMessage = response.message;
        return this.featuredProducts;
      } catch (error) {
        console.error('Error fetching featured products:', error);
        this.error = error.message || 'Failed to fetch featured products';
        return [];
      } finally {
        this.loading = false;
      }
    },
    
    // Fetch a single product by ID
    async fetchProductById(productId) {
      this.loading = true;
      this.error = null;
      this.currentProduct = null;
      
      try {
        const response = await api.get(`/products/${productId}`);
        
        this.currentProduct = response.data;
        this.statusMessage = response.message;
        return this.currentProduct;
      } catch (error) {
        console.error(`Error fetching product ${productId}:`, error);
        this.error = error.message || 'Failed to fetch product details';
        return null;
      } finally {
        this.loading = false;
      }
    },
    
    // Set current product (used when navigating from product list to detail)
    setCurrentProduct(product) {
      this.currentProduct = product;
    },
    
    // Search products by query
    async searchProducts(query) {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await api.get('/products/search', {
          params: { q: query }
        });
        
        // Update allProducts with search results
        this.allProducts = response.data;
        this.statusMessage = response.message;
        return this.allProducts;
      } catch (error) {
        console.error('Error searching products:', error);
        this.error = error.message || 'Failed to search products';
        return [];
      } finally {
        this.loading = false;
      }
    },
    
    // Filter products by category
    async filterByCategory(categoryId) {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await api.get('/products/category/' + categoryId);
        
        this.allProducts = response.data;
        this.statusMessage = response.message;
        return this.allProducts;
      } catch (error) {
        console.error('Error filtering products:', error);
        this.error = error.message || 'Failed to filter products';
        return [];
      } finally {
        this.loading = false;
      }
    },
    
    // Handle error (useful for clearing errors or showing specific messages)
    setError(message) {
      this.error = message;
    },
    
    clearError() {
      this.error = null;
    }
  }
});