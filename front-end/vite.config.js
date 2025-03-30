// import { fileURLToPath, URL } from 'node:url'

// import { defineConfig } from 'vite'
// import vue from '@vitejs/plugin-vue'
// import vueDevTools from 'vite-plugin-vue-devtools'

// // https://vite.dev/config/
// export default defineConfig({
//   server: {
//      port: 5173,
//      proxy: {
//      '/api': {
//       target: 'http://localhost:8084',
//       changeOrigin: true,
//       rewrite: (path) => path.replace(/^\/api/,''),
//      }
//     }
//   },
//   plugins: [vue(), vueDevTools()],
//   resolve: {
//     alias: {
//       '@': fileURLToPath(new URL('./src', import.meta.url)),
//     },
//   },
// })

// vite.config.js
import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  server: {
    port: 5173, // The frontend port (Vue.js)
    proxy: {
      '/api': {
        target: 'http://localhost:8084', // The backend port (Express API)
        changeOrigin: true,              // Make the request appear as if it’s from the backend domain
        rewrite: (path) => path.replace(/^\/api/, ''), // Remove `/api` prefix when forwarding
        secure: false, // If you're running the backend without HTTPS, set this to false
      },
    },
  },
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})

