<template>
    <div class="signin-container">
      <div class="signin-form">
        <h2>Sign In</h2>
        
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        
        <form @submit.prevent="handleSignIn">
          <div class="form-group">
            <label for="email">Email</label>
            <input 
              type="email" 
              id="email" 
              v-model="email" 
              required
              placeholder="Enter your email"
            />
          </div>
          
          <div class="form-group">
            <label for="password">Password</label>
            <input 
              type="password" 
              id="password" 
              v-model="password" 
              required
              placeholder="Enter your password"
            />
          </div>
          
          <button 
            type="submit" 
            class="signin-button" 
            :disabled="loading"
          >
            {{ loading ? 'Signing in...' : 'Sign In' }}
          </button>
        </form>
        
        <div class="signup-redirect">
          Don't have an account? 
          <router-link to="/signup">Sign Up</router-link>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { mapState, mapActions } from 'pinia';
  import { useUserStore } from '@/stores/userStore';
  
  export default {
    name: 'SignIn',
    
    data() {
      return {
        email: '',
        password: '',
        errorMessage: ''
      };
    },
    
    computed: {
      ...mapState(useUserStore, ['loading'])
    },
    
    methods: {
      ...mapActions(useUserStore, ['login', 'checkEmailExists']),
      
      async handleSignIn() {
        this.errorMessage = '';
        
        const result = await this.login(this.email, this.password);
        
        if (result.success) {
          // Redirect to home page or last visited page
          this.$router.push('/');
        } else {
          this.errorMessage = result.error;
          
          // Check if email exists and suggest signup
          const emailExists = this.checkEmailExists(this.email);
          if (!emailExists) {
            this.errorMessage = 'Email not found. Please sign up first.';
          }
        }
      }
    }
  };
  </script>
  
  <style scoped>
  .signin-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 80vh;
  }
  
  .signin-form {
    width: 100%;
    max-width: 400px;
    padding: 2rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  h2 {
    text-align: center;
    margin-bottom: 1.5rem;
  }
  
  .form-group {
    margin-bottom: 1rem;
  }
  
  label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
  }
  
  input {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
  }
  
  .signin-button {
    color: white;
    width: 100%;
    padding: 0.75rem;
    background-color: #1052ec;
    border: 1px solid lightblue;
    border-radius: 4px;
    font-size: 1rem;
    cursor: pointer;
    margin-top: 1rem;
  }
  
  .signin-button:hover {
    background-color: gray;
  }
  
  .signin-button:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
  
  .error-message {
    color: #c40000;
    background-color: #fff1f1;
    padding: 0.75rem;
    margin-bottom: 1rem;
    border: 1px solid #c40000;
    border-radius: 4px;
  }
  
  .signup-redirect {
    margin-top: 1.5rem;
    text-align: center;
  }
  
  a {
    color: #0066c0;
    text-decoration: none;
  }
  
  a:hover {
    text-decoration: underline;
    color: #c45500;
  }
  </style>