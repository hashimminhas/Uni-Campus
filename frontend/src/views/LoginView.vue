<template>
  <div class="login-container">
    <div class="login-card">
      <h1>UniCampus Login</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">Username:</label>
          <input 
            v-model="credentials.username" 
            type="text" 
            id="username" 
            placeholder="Enter username"
            required
          />
        </div>
        <div class="form-group">
          <label for="password">Password:</label>
          <input 
            v-model="credentials.password" 
            type="password" 
            id="password" 
            placeholder="Enter password"
            required
          />
        </div>
        <button type="submit" class="login-btn">Login</button>
        <p v-if="error" class="error-message">{{ error }}</p>
      </form>
      
      <div class="demo-users">
        <h3>Demo Users:</h3>
        <p><strong>Admin:</strong> admin / admin123</p>
        <p><strong>Student:</strong> student / student123</p>
        <p><strong>Teacher:</strong> teacher / teacher123</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginView',
  data() {
    return {
      credentials: {
        username: '',
        password: ''
      },
      error: ''
    }
  },
  methods: {
    async handleLogin() {
      this.error = '';
      try {
        const response = await fetch('http://localhost:8084/auth/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.credentials)
        });

        if (!response.ok) {
          throw new Error('Invalid credentials');
        }

        const data = await response.json();
        
        // Store JWT token and user info
        localStorage.setItem('token', data.token);
        localStorage.setItem('username', data.username);
        localStorage.setItem('role', data.role);

        // Redirect to home
        this.$router.push('/');
      } catch (err) {
        this.error = err.message || 'Login failed. Please try again.';
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}

.login-btn:hover {
  background: #5568d3;
}

.error-message {
  color: #e74c3c;
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
}

.demo-users {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #eee;
  background: #f9f9f9;
  padding: 20px;
  border-radius: 4px;
}

.demo-users h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 14px;
}

.demo-users p {
  margin: 8px 0;
  color: #666;
  font-size: 13px;
}

.demo-users strong {
  color: #667eea;
}
</style>
