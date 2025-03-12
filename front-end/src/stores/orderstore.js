import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import api from '@/services/apiService'; // Make sure the import path is correct

export const useOrderStore = defineStore('order', () => {
  const orders = ref([]);
  const userId = ref(1);
  const loading = ref(false);
  const error = ref(null);
  const currentPage = ref(1);
  const itemsPerPage = ref(5);

  // Function to fetch orders from an API
  async function fetchOrders() {
    loading.value = true;
    error.value = null;

    try {
      const response = await api.get(`/orders/${userId.value}`);
      console.log('API Response:', response.data);

      if (response && response.data && Array.isArray(response.data)) {
        orders.value = response.data;
        console.log('Updated orders:', orders.value);
      } else {
        throw new Error('No orders found');
      }

      loading.value = false;
      return orders.value;

    } catch (err) {
      console.error('Error fetching orders:', err);
      error.value = err.message || 'Failed to fetch orders';
      loading.value = false;
    }
  }

  const pagedOrders = computed(() => {
    const startIndex = (currentPage.value - 1) * itemsPerPage.value;
    const endIndex = startIndex + itemsPerPage.value;
    return orders.value.slice(startIndex, endIndex);
  });

  const totalPages = computed(() => {
    return Math.ceil(orders.value.length / itemsPerPage.value);
  });

  function setPage(page) {
    currentPage.value = page;
  }

  function nextPage() {
    if (currentPage.value < totalPages.value) {
      currentPage.value++;
    }
  }

  function prevPage() {
    if (currentPage.value > 1) {
      currentPage.value--;
    }
  }

  return {
    orders,
    userId,
    loading,
    error,
    currentPage,
    itemsPerPage,
    fetchOrders,
    pagedOrders,
    totalPages,
    setPage,
    nextPage,
    prevPage,
  };
});
