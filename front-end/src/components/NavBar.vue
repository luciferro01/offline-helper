<template>
  <nav class="navbar">
    <!-- Company Logo -->
    <router-link to="/" class="logo">
      <div class="logo-text"><img src="@/assets/blibli.png" width="90" /></div>
    </router-link>

    <!-- Show full navbar for logged-in users -->
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
              </div>
            </div>
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
      searchQuery: ' ',
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
  min-height: 60px;
  align-items: center;
  flex-wrap: nowrap;
  justify-content: space-between;
}

.logo {
  flex: 0 0 auto; /* Don't grow or shrink, stay at natural size */
  padding: 0.5rem;
  text-decoration: none;
  z-index: 2;
}

.logo:hover {
  transform: scale(1.2);
}

.logo-text {
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
}

.navbar-content {
  display: flex;
  flex: 1;
  justify-content: center; /* Center the content */
  align-items: center;
  position: relative;
  padding: 0 1rem;
}

.search-container {
  width: 60%; /* Take 60% of the navbar-content space */
  max-width: 600px;
  display: flex;
  margin: 0 auto; /* Center within parent */
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
  color: white;
}

.search-button:hover {
  background-color: skyblue;
}

.navbar-right {
  display: flex;
  align-items: center;
  flex: 0 0 auto; /* Don't grow or shrink */
  z-index: 2;
}

.signin-button-container {
  flex: 0 0 auto;
  z-index: 2;
}

.signin-button {
  display: inline-block;
  color: white;
  background-color: #2789f2;
  padding: 0.5rem 1rem;
  border-radius: 3px;
  text-decoration: none;
}

.signin-button:hover {
  background-color: skyblue;
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
  min-width: 120px;
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

/* Media queries for responsive design */
@media (max-width: 992px) {
  .navbar {
    flex-wrap: wrap;
  }

  .logo {
    flex-basis: 30%;
    text-align: left;
  }

  .navbar-content {
    order: 3;
    flex-basis: 100%;
    margin-top: 0.5rem;
  }

  .search-container {
    width: 100%;
    max-width: 100%;
  }

  .navbar-right,
  .signin-button-container {
    flex-basis: auto;
  }
}

@media (max-width: 576px) {
  .navbar {
    flex-direction: column;
    align-items: stretch;
  }

  .logo {
    text-align: center;
    margin-bottom: 0.5rem;
  }

  .navbar-right {
    justify-content: center;
    margin-top: 0.5rem;
  }

  .signin-button-container {
    text-align: center;
    margin-top: 0.5rem;
  }

  .nav-item {
    padding: 0 0.5rem;
  }

  .cart-icon,
  .nav-greeting,
  .nav-link-text {
    font-size: 0.8rem;
  }
}
</style>
