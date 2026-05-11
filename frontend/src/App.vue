<template>
  <div id="app">
    <header class="app-header">
      <div class="logo">UniCampus</div>
      <nav class="main-nav" v-if="$route.path !== '/admin'">
        <router-link to="/">Courses</router-link>
        <router-link to="/library">Library</router-link>
      </nav>
    </header>
    <router-view />
  </div>
</template>

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
</style>
