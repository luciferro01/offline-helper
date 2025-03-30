// src/main.js
import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';

// Create Pinia (store)
const pinia = createPinia();

// Create Vue application
const app = createApp(App);

// Use plugins
app.use(pinia);
app.use(router);

// Mount the app
app.mount('#app');