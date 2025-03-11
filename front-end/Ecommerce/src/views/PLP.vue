<!-- <template>
    <div class="search-results-page">
      <h2>Search Results</h2>
      <div v-if="searchResults.length > 0" class="products-grid">
        <div v-for="product in searchResults" :key="product.id" class="product-card">
          <div class="product-image">
            <img :src="product.imageUrl" :alt="product.name">
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <div class="product-price">₹{{ formatPrice(product.price) }}</div>
            <div class="product-seller">{{ product.seller }}</div>
            <div class="product-rating">
              <div class="stars">
                <span v-for="star in 5" :key="star" class="star" :class="{ 'filled': star <= Math.round(product.rating) }">★</span>
              </div>
              <span class="rating-count">({{ product.ratingCount }})</span>
            </div>
          </div>
        </div>
      </div>
      <div v-else>
        No products found.
      </div>
    </div>
  </template>
  
  <script>
  import { useRoute, useRouter } from 'vue-router';
  import { ref, watch } from 'vue';
  
  export default {
    name: 'PLP',
    setup() {
      const route = useRoute();
      const router = useRouter();
      const searchQuery = ref('');
      const searchResults = ref([]);
  
      const allProducts = [
        { id: 1, name: 'Laptop', category: 'Electronics', price: 50000, seller: 'ABC Store', rating: 4.5, ratingCount: 120, imageUrl: 'https://m.media-amazon.com/images/I/61Dw5Z8LzJL._AC_SL1500_.jpg' },
        { id: 2, name: 'Smartphone', category: 'Electronics', price: 20000, seller: 'XYZ Store', rating: 4.2, ratingCount: 98, imageUrl: 'https://m.media-amazon.com/images/I/71geVdy6-OS._AC_SL1500_.jpg' },
        { id: 3, name: 'Headphones', category: 'Accessories', price: 3000, seller: 'Music Hub', rating: 4.0, ratingCount: 45, imageUrl: 'https://m.media-amazon.com/images/I/61+U9qQH1YL._AC_SL1500_.jpg' },
        { id: 4, name: 'Shoes', category: 'Fashion', price: 2000, seller: 'Footwear Mart', rating: 4.3, ratingCount: 70, imageUrl: 'https://m.media-amazon.com/images/I/71KrJjbXfiL._AC_UL1500_.jpg' },
        { id: 5, name: 'Watch', category: 'Accessories', price: 5000, seller: 'Time Center', rating: 4.6, ratingCount: 88, imageUrl: 'https://m.media-amazon.com/images/I/71qxOQlZGLL._AC_UL1500_.jpg' }
      ];
  
      const filterProducts = () => {
        if (!searchQuery.value) {
          searchResults.value = [];
          return;
        }
  
        const query = searchQuery.value.toLowerCase();
        searchResults.value = allProducts.filter(product =>
          product.name.toLowerCase().includes(query)
        );
      };
  
      watch(searchQuery, (newQuery) => {
        filterProducts();
  
        // if (newQuery === '') {
        //   router.go(-1);
        // }
        if (newQuery === '') {
        router.push('/'); 
      }
      });
  
      watch(
        () => route.query.q,
        (newQuery) => {
          searchQuery.value = newQuery || '';
          filterProducts();
        },
        { immediate: true }
      );
  
      const formatPrice = (price) => {
        return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      };
  
      return {
        searchQuery,
        searchResults,
        formatPrice
      };
    }
  };
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
  
  .product-card {
    border-radius: 8px;
    overflow: hidden;
    background-color: white;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    display: flex;
    flex-direction: column;
    cursor: pointer;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
    border: 1px solid #f0f0f0;
  }
  
  .product-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  }
  
  .product-image {
    position: relative;
    height: 150px;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f9f9f9;
    padding: 10%;
  }
  
  .product-image img {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
  }
  
  .product-info {
    padding: 15px;
    display: flex;
    flex-direction: column;
  }
  
  .product-name {
    font-size: 1rem;
    font-weight: bold;
    margin-bottom: 5px;
    color: #333;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    overflow: hidden;
    line-height: 1.3;
    height: 2.6em;
  }
  
  .product-price {
    font-size: 0.9rem;
    color: #0095DA;
    margin-bottom: 8px;
  }
  
  .product-seller {
    font-size: 0.8rem;
    color: #666;
    margin-bottom: 8px;
  }
  
  .product-rating {
    display: flex;
    align-items: center;
    font-size: 0.8rem;
    color: #999;
  }
  
  .stars {
    display: inline-flex;
    margin-right: 5px;
  }
  
  .star {
    color: #C8C8C8;
  }
  
  .star.filled {
    color: #F5A623;
  }
  
  .rating-count {
    margin-left: 5px;
    color: #999;
  }
  </style>
   -->

   <template>
    <div class="search-results-page">
      <h2>Search Results</h2>
  
      <!-- Display products using ProductCard -->
      <div v-if="searchResults.length > 0" class="products-grid">
        <ProductCard
          v-for="product in searchResults"
          :key="product.id"
          :product="product"
        />
      </div>
  
      <!-- Display message if no products are found -->
      <div v-else>
        No products found.
      </div>
    </div>
  </template>
  
  <script>
  import { useRoute, useRouter } from 'vue-router';
  import { ref, watch } from 'vue';
  import ProductCard from '@/components/product-card.vue';  // Import the ProductCard component
  
  export default {
    name: 'SearchResults',
    components: {
      ProductCard, // Register ProductCard component
    },
    setup() {
      const route = useRoute();
      const router = useRouter();
      const searchQuery = ref('');
      const searchResults = ref([]);
  
      const allProducts = [
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
    ];
  
      // Filter products based on search query
      const filterProducts = () => {
        if (!searchQuery.value) {
          searchResults.value = [];
          return;
        }
  
        const query = searchQuery.value.toLowerCase();
        searchResults.value = allProducts.filter(product =>
          product.name.toLowerCase().includes(query)
        );
      };
  
      // Watch for changes in searchQuery and update results accordingly
      watch(searchQuery, (newQuery) => {
        filterProducts();
  
        if (newQuery === '') {
          router.push('/'); 
        }
      });
  
      // Watch for changes in query parameter in the URL
      watch(
        () => route.query.q,
        (newQuery) => {
          searchQuery.value = newQuery || '';
          filterProducts();
        },
        { immediate: true }
      );
  
      // Format price with commas
      const formatPrice = (price) => {
        return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      };
  
      return {
        searchQuery,
        searchResults,
        formatPrice
      };
    }
  };
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
  
  .product-card {
    border-radius: 8px;
    overflow: hidden;
    background-color: white;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    display: flex;
    flex-direction: column;
    cursor: pointer;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
    border: 1px solid #f0f0f0;
  }
  
  .product-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  }
  
  .product-image {
    position: relative;
    height: 150px;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f9f9f9;
    padding: 10%;
  }
  
  .product-image img {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
  }
  
  .product-info {
    padding: 15px;
    display: flex;
    flex-direction: column;
  }
  
  .product-name {
    font-size: 1rem;
    font-weight: bold;
    margin-bottom: 5px;
    color: #333;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    overflow: hidden;
    line-height: 1.3;
    height: 2.6em;
  }
  
  .product-price {
    font-size: 0.9rem;
    color: #0095DA;
    margin-bottom: 8px;
  }
  
  .product-seller {
    font-size: 0.8rem;
    color: #666;
    margin-bottom: 8px;
  }
  
  .product-rating {
    display: flex;
    align-items: center;
    font-size: 0.8rem;
    color: #999;
  }
  
  .stars {
    display: inline-flex;
    margin-right: 5px;
  }
  
  .star {
    color: #C8C8C8;
  }
  
  .star.filled {
    color: #F5A623;
  }
  
  .rating-count {
    margin-left: 5px;
    color: #999;
  }
  </style>
  