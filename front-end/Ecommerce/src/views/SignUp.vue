<template>
    <div class="signup-container">
      <div class="signup-form">
        <h2>Create Account</h2>
        
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        
        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
        </div>
        
        <form @submit.prevent="handleSignUp">
          <div class="form-group">
            <label for="username">Your Name</label>
            <input 
              type="text" 
              id="username" 
              v-model="username" 
              required
              placeholder="Enter your name"
            />
          </div>
          
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
              placeholder="At least 6 characters"
              minlength="6"
            />
            <small>Passwords must be at least 6 characters.</small>
          </div>
          
          <div class="form-group">
            <label for="confirmPassword">Re-enter Password</label>
            <input 
              type="password" 
              id="confirmPassword" 
              v-model="confirmPassword" 
              required
              placeholder="Re-enter password"
            />
          </div>
          
          <button 
            type="submit" 
            class="signup-button" 
            :disabled="loading || isSuccess"
          >
            {{ loading ? 'Creating account...' : 'Create your account' }}
          </button>
        </form>
        
        <div class="signin-redirect">
          Already have an account? 
          <router-link to="/signin">Sign In</router-link>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { mapState, mapActions } from 'pinia';
  import { useUserStore } from '@/stores/userStore';
  
  export default {
    name: 'SignUp',
    
    data() {
      return {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        errorMessage: '',
        successMessage: '',
        isSuccess: false
      };
    },
    
    computed: {
      ...mapState(useUserStore, ['loading'])
    },
    
    methods: {
      ...mapActions(useUserStore, ['signup', 'checkEmailExists']),
      
      async handleSignUp() {
        this.errorMessage = '';
        
        // Validate passwords match
        if (this.password !== this.confirmPassword) {
          this.errorMessage = 'Passwords do not match';
          return;
        }
        
        // Check if email already exists
        if (this.checkEmailExists(this.email)) {
          this.errorMessage = 'Email already in use. Please sign in instead.';
          return;
        }
        
        const result = await this.signup(this.username, this.email, this.password);
        
        if (result.success) {
          this.successMessage = 'Account created successfully! Redirecting to sign in...';
          this.isSuccess = true;
          
          // Clear form
          this.username = '';
          this.email = '';
          this.password = '';
          this.confirmPassword = '';
          
          // Redirect to sign in page after a delay
          setTimeout(() => {
            this.$router.push('/signin');
          }, 2000);
        } else {
          this.errorMessage = result.error;
        }
      }
    }
  };
  </script>
  
  <style scoped>
  .signup-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 80vh;
  }
  
  .signup-form {
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
  
  small {
    display: block;
    color: #555;
    margin-top: 0.25rem;
  }
  
  .signup-button {
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
  
  .signup-button:hover {
    background-color: gray;
  }
  
  .signup-button:disabled {
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
  
  .success-message {
    color: #007600;
    background-color: #f0f8f0;
    padding: 0.75rem;
    margin-bottom: 1rem;
    border: 1px solid #007600;
    border-radius: 4px;
  }
  
  .signin-redirect {
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