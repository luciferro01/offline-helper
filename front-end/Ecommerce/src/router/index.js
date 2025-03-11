import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/userStore'

// Import components
import SignIn from '@/views/SignIn.vue'
import SignUp from '@/views/SignUp.vue'
import HomePage from '@/views/HomePage.vue'
import PLP from '@/views/PLP.vue'
import orders from '@/views/orders.vue'
import cart from '@/views/cart.vue' // We'll create this
import checkout from '@/views/checkout.vue'
import ProductDetail from '@/views/ProductDetail.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage,
    // meta: { requiresAuth: true } // Require authentication for home page
  },
  {
    path: '/signin',
    name: 'SignIn',
    component: SignIn,
    meta: { guestOnly: true },
  },
  {
    path: '/signup',
    name: 'SignUp',
    component: SignUp,
    meta: { guestOnly: true },
  },
  {
    path: '/PLP',
    name: 'PLP',
    component: PLP,
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail,
    props: true,
    // meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: orders,
  },
  {
    path: '/cart',
    name: 'Cart',
    component: cart,
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: checkout,
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: {
      template: '<div><h1>Page Not Found</h1><p>The page you requested does not exist.</p></div>',
    },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Navigation guards
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // Initialize user from localStorage if not already done
  if (!userStore.user) {
    userStore.initializeUser()
  }

  // Check if route requires authentication
  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    // Redirect to login
    next({ name: 'SignIn' })
  }

  // Check if route is for guests only (like login/register)
  else if (to.meta.guestOnly && userStore.isAuthenticated) {
    // Redirect authenticated users to home
    next({ name: 'Home' })
  } else {
    // Normal navigation
    next()
  }
})

export default router
