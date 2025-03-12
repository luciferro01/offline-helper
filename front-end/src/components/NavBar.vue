<template>
  <nav class="navbar">
    <!-- Company Logo (Always visible) -->
    <router-link to="/" class="logo">
      <div class="logo-text"><img src="@/assets/blibli.png" width="90" /></div>
    </router-link>

    <!-- Show full navbar only for logged-in users -->
    <template v-if="loggedIn">
      <div class="navbar-content">
        <!-- Search Bar -->
        <div class="search-container">
          <input
            type="text"
            class="search-input"
            placeholder="Search products..."
            v-model="searchQuery"
            @keyup.enter="handleSearch"
          />
          <button class="search-button" @click="handleSearch">Search</button>
        </div>

        <div class="navbar-right">
          <!-- Account Section -->
          <div class="nav-item">
            <div class="nav-link">
              <div class="nav-text">
                <span class="nav-greeting">Hello, {{ userName }}</span>
                <span class="nav-link-text">Account</span>
              </div>
            </div>

            <!-- Dropdown for account actions -->
            <div class="account-dropdown">
              <button class="signout-button" @click="handleSignOut">Sign Out</button>
            </div>
          </div>

          <!-- Orders -->
          <div class="nav-item">
            <router-link to="/orders" class="nav-link">
              <div class="nav-text">
                <span class="nav-greeting">Returns</span>
                <span class="nav-link-text">& Orders</span>
              </div>
            </router-link>
          </div>

          <!-- Cart -->
          <div class="nav-item cart">
            <router-link to="/cart" class="nav-link">
              <span class="cart-icon">
                Cart
                <span class="cart-count">{{ totalItems }}</span>
              </span>
            </router-link>
          </div>
        </div>
      </div>
    </template>

    <!-- Show just Sign In link for non-logged-in users -->
    <template v-else>
      <div class="navbar-content">
        <!-- Search Bar -->
        <div class="search-container">
          <input
            type="text"
            class="search-input"
            placeholder="Search products..."
            v-model="searchQuery"
            @keyup.enter="handleSearch"
          />
          <button class="search-button" @click="handleSearch">Search</button>
        </div>

        <div class="navbar-right">
          <!-- Account Section -->
          <div class="nav-item">
            <div class="nav-link">
              <div class="nav-text">
                <span class="nav-greeting">Hello, {{ userName }}</span>
                <!-- <span class="nav-link-text">Account</span> -->
              </div>
            </div>

            <!-- //Dropdown for account actions -->
            <!-- <div class="account-dropdown">
              <router-link to="/signin" class="signin-button">Sign In</router-link>
            </div> -->
          </div>

          <!-- Cart -->
          <div class="nav-item cart">
            <router-link to="/cart" class="nav-link">
              <span class="cart-icon">
                Cart
                <span class="cart-count">{{ totalItems }}</span>
              </span>
            </router-link>
          </div>
        </div>
      </div>

      <div class="signin-button-container">
        <router-link to="/signin" class="signin-button">Sign In</router-link>
      </div>
    </template>
  </nav>
</template>

<script>
import { mapState, mapActions } from 'pinia'
import { useUserStore } from '@/stores/userStore'
import { useCartStore } from '@/stores/cartStore'

export default {
  name: 'NavBar',

  data() {
    return {
      searchQuery: '',
    }
  },

  computed: {
    ...mapState(useUserStore, {
      loggedIn: 'loggedIn',
      userName: 'userName',
    }),
    ...mapState(useCartStore, ['totalItems']),
  },

  methods: {
    ...mapActions(useUserStore, ['logout', 'initializeUser']),

    handleSignOut() {
      this.logout()
      this.$router.push('/signin')
    },
  },

  handleSearch() {
    if (this.searchQuery.trim()) {
      this.$router.push({ name: 'PLP', query: { q: this.searchQuery } })
    }
  },

  watch: {
    searchQuery(newQuery) {
      if (newQuery.trim()) {
        this.$router.replace({ name: 'PLP', query: { q: newQuery } })
      }
    },
  },

  mounted() {
    // Initialize user from localStorage when component mounts
    this.initializeUser()
  },
}
</script>

<style scoped>
.navbar {
  display: flex;
  background-color: lightblue;
  color: white;
  padding: 0.5rem 1rem;
  height: 60px;
  align-items: center;
  position: relative; /* Make it a positioning context */
}

.navbar-content {
  display: flex;
  flex: 1;
}

.logo {
  padding: 0.5rem;
  margin-right: 1rem;
  text-decoration: none;
}

.logo-text {
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
}

.signin-button-container {
  margin-left: auto;
}

.signin-button:hover {
  background-color: skyblue;
}

.search-container {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  width: 800px; /* Fixed width */
  z-index: 1;
}

.search-input {
  flex: 1;
  padding: 0.5rem;
  border: none;
  border-radius: 4px 0 0 4px;
  font-size: 0.9rem;
}

.search-button {
  background-color: #2789f2;
  border: none;
  padding: 0 0.75rem;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

.search-button:hover {
  background-color: skyblue;
}

.navbar-right {
  display: flex;
  align-items: center;
  margin-left: auto;
}

.nav-item {
  position: relative;
  padding: 0 0.75rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  display: flex;
  flex-direction: column;
}

.nav-text {
  display: flex;
  flex-direction: column;
}

.nav-greeting {
  font-size: 0.75rem;
  color: blue;
}

.nav-link-text {
  font-size: 0.9rem;
  font-weight: bold;
}

.cart {
  display: flex;
  align-items: center;
}

.cart-icon {
  position: relative;
  font-size: 1rem;
  margin-right: 0.25rem;
}

.cart-count {
  background-color: #2789f2;
  color: black;
  font-size: 0.75rem;
  font-weight: bold;
  padding: 0.1rem 0.3rem;
  border-radius: 50%;
  margin-left: 5px;
}

.account-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 0.5rem;
  z-index: 10;
  display: none;
  /*min-width: 120px; Add this to ensure adequate width */
}

.nav-item:hover .account-dropdown {
  display: block;
}

.signout-button {
  color: white;
  background-color: #2789f2;
  border: 1px solid lightblue;
  border-radius: 3px;
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
  cursor: pointer;
  width: 100%;
}

.signout-button:hover {
  background-color: lightblue;
}
</style>
