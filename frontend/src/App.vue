<template>
  <div id="app">
    <header class="app-header">
      <div class="logo">UniCampus</div>
      <nav class="main-nav" v-if="$route.path !== '/admin'">
        <router-link to="/">Courses</router-link>
        <router-link to="/library">Library</router-link>
        <router-link to="/dormitory">Dormitory</router-link>
      </nav>
      <div class="login-section" v-if="$route.path !== '/admin'">
        <div v-if="studentId" class="user-info">
          <span>Logged in: {{ studentId }}</span>
          <button @click="logout" class="btn-logout">Logout</button>
        </div>
        <div v-else class="login-form">
          <input v-model="loginInput" placeholder="Enter Student ID" class="login-input" />
          <button @click="login" class="btn-login">Login</button>
        </div>
      </div>
    </header>
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      studentId: localStorage.getItem('studentId') || '',
      loginInput: ''
    }
  },
  methods: {
    async login() {
      const inputId = this.loginInput.trim();
      if (!inputId) return;

      // Validate UUID format roughly
      const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
      if (!uuidRegex.test(inputId)) {
        alert('Please enter a valid UUID format.');
        return;
      }

      try {
        const response = await fetch(`/api/students/${inputId}`);
        if (!response.ok) {
          throw new Error('Student not found or invalid');
        }
        const data = await response.json();
        
        // Successfully found student
        this.studentId = inputId;
        localStorage.setItem('studentId', this.studentId);
        this.loginInput = '';
        alert(`Logged in as: ${data.firstName} ${data.lastName}`);
      } catch (error) {
        alert('Login failed: ' + error.message);
      }
    },
    logout() {
      this.studentId = '';
      localStorage.removeItem('studentId');
    }
  }
}
</script>

<style>
body {
  margin: 0;
  padding: 0;
  background-color: #f8f9fa;
}

.app-header {
  background-color: #343a40;
  color: white;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 24px;
  font-weight: bold;
}

.main-nav {
  display: flex;
  gap: 20px;
}

.main-nav a {
  color: white;
  text-decoration: none;
  font-size: 16px;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.main-nav a:hover, .main-nav a.router-link-active {
  background-color: #495057;
}

.login-section {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 14px;
}

.login-form {
  display: flex;
  gap: 10px;
}

.login-input {
  padding: 6px 10px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 14px;
}

.btn-login, .btn-logout {
  padding: 6px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  font-size: 14px;
  transition: background-color 0.2s;
}

.btn-login {
  background-color: #28a745;
  color: white;
}

.btn-login:hover {
  background-color: #218838;
}

.btn-logout {
  background-color: #dc3545;
  color: white;
}

.btn-logout:hover {
  background-color: #c82333;
}
</style>
