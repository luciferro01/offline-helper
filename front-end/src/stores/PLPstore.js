import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useProductStore = defineStore('product', {
  state: () => ({
    allProducts: [],  // All products data
    searchQuery: '',
    searchResults: [],
    currentPage: 1,   // Current page
    itemsPerPage: 8,  // Items per page
    loading: false,   // To handle the loading state
    error: null,      // To handle any errors from the API call
  }),

  actions: {
    async fetchProducts() {
      this.loading = true;
      this.error = null; // Reset error state

      try {
        // Simulated data, replace with actual API call later
        const mockData = [
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
                        
                  ];
            
        
        setTimeout(() => {
          this.allProducts = mockData;
          this.loading = false;
          this.filterProducts();  // Re-filter products after fetching
        }, 1000); // Simulate a delay for fetching data
      } catch (err) {
        this.error = err.message || 'Failed to fetch products';
        this.loading = false;
      }
    },

    // Filter products based on search query and paginate them
    filterProducts() {
      if (!this.searchQuery) {
        this.searchResults = [];
        return;
      }

      const query = this.searchQuery.toLowerCase();
      const filteredProducts = this.allProducts.filter(product =>
        product.name.toLowerCase().includes(query)
      );

      // Calculate pagination based on current page and items per page
      const startIndex = (this.currentPage - 1) * this.itemsPerPage;
      const paginatedProducts = filteredProducts.slice(startIndex, startIndex + this.itemsPerPage);

      this.searchResults = paginatedProducts;
    },

    updateSearchQuery(query) {
      this.searchQuery = query;
      this.currentPage = 1; // Reset to first page when search query changes
      this.filterProducts();
    },

    // Pagination: Move to the next page
    nextPage() {
      this.currentPage += 1;
      this.filterProducts();
    },

    // Pagination: Move to the previous page
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage -= 1;
        this.filterProducts();
      }
    },

    // Set current page directly
    setPage(page) {
      this.currentPage = page;
      this.filterProducts();
    },
  },
});
