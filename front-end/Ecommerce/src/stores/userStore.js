// src/stores/userStore.js
import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    isAuthenticated: false,
    loading: false,
    error: null
  }),
  
  getters: {
    // Check if user is logged in
    loggedIn(state) {
      return state.isAuthenticated;
    },
    
    // Get current user information
    currentUser(state) {
      return state.user;
    },
    
    // Get user's name for display
    userName(state) {
      return state.user?.username || 'Guest';
    },
    
    // Check if there's an authentication error
    hasError(state) {
      return !!state.error;
    }
  },
  
  actions: {
    // Login action
    async login(email, password) {
      this.loading = true;
      this.error = null;
      
      try {
        // This would be your API call
        // const response = await api.post('/login', { email, password });
        
        // For demonstration, simulating API response:
        const mockUsers = JSON.parse(localStorage.getItem('users') || '[]');
        const user = mockUsers.find(u => u.email === email);
        
        if (!user || user.password !== password) {
          throw new Error('Invalid email or password');
        }
        
        // Remove password before storing in state
        const { password: _, ...userWithoutPassword } = user;
        
        // Update store state
        this.user = userWithoutPassword;
        this.isAuthenticated = true;
        
        // Store in localStorage for persistence
        localStorage.setItem('currentUser', JSON.stringify(userWithoutPassword));
        
        return { success: true };
      } catch (error) {
        this.error = error.message || 'Login failed';
        return { success: false, error: this.error };
      } finally {
        this.loading = false;
      }
    },
    
    // Check if email exists (for signup flow)
    checkEmailExists(email) {
      const mockUsers = JSON.parse(localStorage.getItem('users') || '[]');
      return mockUsers.some(user => user.email === email);
    },
    
    // Signup action
    async signup(username, email, password) {
      this.loading = true;
      this.error = null;
      
      try {
        // This would be your API call
        // const response = await api.post('/signup', { username, email, password });
        
        // For demonstration, simulating API storage:
        const mockUsers = JSON.parse(localStorage.getItem('users') || '[]');
        
        // Check if user already exists
        if (mockUsers.some(user => user.email === email)) {
          throw new Error('Email already in use');
        }
        
        // Create new user
        const newUser = {
          id: Date.now().toString(),
          username,
          email,
          password // In a real app, this would be hashed on the server
        };
        
        mockUsers.push(newUser); //Here also, an API Call will be required to send data to backend
        localStorage.setItem('users', JSON.stringify(mockUsers));
        
        return { success: true };
      } catch (error) {
        this.error = error.message || 'Signup failed';
        return { success: false, error: this.error };
      } finally {
        this.loading = false;
      }
    },
    
    // Logout action
    logout() {
      this.user = null;
      this.isAuthenticated = false;
      localStorage.removeItem('currentUser');
    },
    
    // Initialize user from localStorage (called on app start)
    initializeUser() {
      const storedUser = localStorage.getItem('currentUser');
      if (storedUser) {
        this.user = JSON.parse(storedUser);
        this.isAuthenticated = true;
      }
    }
  }
});