<template>
  <div id="app" :class="{ dark: darkMode }" @click="closeDropdown">
    <header class="navbar" v-if="$route.path !== '/admin'">
      <div class="navbar-inner">

        <!-- Logo -->
        <router-link to="/" class="nav-logo">
          <span class="logo-mark"></span>
          <span class="logo-word">UniCampus</span>
        </router-link>

        <!-- Links -->
        <nav class="nav-links">
          <router-link to="/students" active-class="nav-active">Students</router-link>
          <router-link to="/courses"  active-class="nav-active">Courses</router-link>
          <router-link to="/library"  active-class="nav-active">Library</router-link>
        </nav>

        <!-- Right side -->
        <div class="nav-right">

          <!-- Dark mode toggle -->
          <button class="icon-btn" @click.stop="darkMode = !darkMode" :title="darkMode ? 'Light mode' : 'Dark mode'">
            <svg v-if="!darkMode" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
            <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>
          </button>

          <!-- Bell -->
          <div v-if="studentId" class="bell-wrap" @click.stop="toggleDropdown">
            <button class="icon-btn" title="Notifications">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
              <span v-if="notifications.length > 0" class="notif-dot"></span>
            </button>

            <!-- Dropdown -->
            <div v-if="dropdownOpen" class="notif-panel">
              <div class="notif-panel-head">
                <div>
                  <div class="notif-panel-title">Notifications</div>
                  <div class="notif-panel-count">{{ notifications.length }} unread</div>
                </div>
                <button class="icon-btn" @click.stop="clearNotifications" title="Clear all">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/></svg>
                </button>
              </div>
              <div class="notif-list">
                <div v-if="notifications.length === 0" class="notif-empty">No notifications</div>
                <div v-for="n in notifications" :key="n.notificationId" class="notif-item">
                  <span class="notif-bullet"></span>
                  <div>
                    <div class="notif-subject">{{ n.subject }}</div>
                    <div class="notif-body">{{ n.body }}</div>
                    <div class="notif-time">{{ formatTime(n.sentAt) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Auth -->
          <div v-if="studentId" class="user-chip">
            <span class="user-avatar">{{ initials }}</span>
            <span class="user-name">{{ shortId }}</span>
            <button class="logout-btn" @click="logout">Logout</button>
          </div>
          <div v-else class="login-form">
            <input v-model="loginInput" placeholder="Student UUID" class="login-input" @keyup.enter="login" />
            <button @click="login" class="login-btn">Log in</button>
          </div>

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
      studentName: localStorage.getItem('studentName') || '',
      token: localStorage.getItem('token') || '',
      loginInput: '',
      notifications: [],
      dropdownOpen: false,
      darkMode: false
    }
  },
  computed: {
    initials() {
      if (!this.studentName) return '?'
      return this.studentName.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase()
    },
    shortId() {
      return this.studentName || this.studentId.substring(0, 8) + '…'
    }
  },
  mounted() {
    if (this.studentId) this.fetchNotifications()
  },
  methods: {
    async login() {
      const inputId = this.loginInput.trim()
      if (!inputId) return
      const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i
      if (!uuidRegex.test(inputId)) { alert('Please enter a valid UUID.'); return }
      try {
        const res = await fetch('/api/students/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ studentId: inputId })
        })
        if (!res.ok) throw new Error('Student not found')
        const data = await res.json()
        this.studentId = data.studentId
        this.studentName = `${data.firstName} ${data.lastName}`
        this.token = data.token
        localStorage.setItem('studentId', data.studentId)
        localStorage.setItem('studentName', this.studentName)
        localStorage.setItem('token', data.token)
        this.loginInput = ''
        await this.fetchNotifications()
      } catch (e) { alert('Login failed: ' + e.message) }
    },
    logout() {
      this.studentId = ''; this.studentName = ''; this.token = ''
      this.notifications = []; this.dropdownOpen = false
      localStorage.removeItem('studentId'); localStorage.removeItem('studentName')
      localStorage.removeItem('token')
    },
    async fetchNotifications() {
      try {
        const res = await fetch(`/api/notifications/student/${this.studentId}`, {
          headers: { 'Authorization': `Bearer ${this.token}` }
        })
        if (res.ok) this.notifications = await res.json()
      } catch (e) {}
    },
    toggleDropdown() { this.dropdownOpen = !this.dropdownOpen; if (this.dropdownOpen) this.fetchNotifications() },
    closeDropdown() { this.dropdownOpen = false },
    clearNotifications() { this.notifications = []; this.dropdownOpen = false },
    formatTime(t) {
      if (!t) return ''
      const d = Math.floor((Date.now() - new Date(t)) / 1000)
      if (d < 60) return 'just now'
      if (d < 3600) return Math.floor(d / 60) + 'm ago'
      if (d < 86400) return Math.floor(d / 3600) + 'h ago'
      return new Date(t).toLocaleDateString()
    }
  }
}
</script>

<style>
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
body { background: #fafafa; font-family: 'Segoe UI', system-ui, -apple-system, sans-serif; }

/* Dark mode */
.dark body, .dark { background: #0f172a; color: #e2e8f0; }
.dark .navbar { background: #0f172a; border-color: #1e293b; }
.dark .nav-logo .logo-word { color: #f1f5f9; }
.dark .nav-links a { color: #94a3b8; }
.dark .nav-links a:hover { background: #1e293b; color: #f1f5f9; }
.dark .icon-btn { color: #94a3b8; }
.dark .icon-btn:hover { background: #1e293b; }
.dark .notif-panel { background: #1e293b; border-color: #334155; }
.dark .notif-item:hover { background: #334155; }
.dark .login-input { background: #1e293b; border-color: #334155; color: #f1f5f9; }

/* Navbar */
.navbar {
  position: sticky; top: 0; z-index: 200;
  background: #fff; border-bottom: 1px solid #e2e8f0;
  height: 52px;
}
.navbar-inner {
  max-width: 1200px; margin: 0 auto; padding: 0 24px;
  height: 100%; display: flex; align-items: center; gap: 16px;
}

/* Logo */
.nav-logo { display: flex; align-items: center; gap: 8px; text-decoration: none; flex-shrink: 0; }
.logo-mark { width: 20px; height: 20px; background: #0f172a; border-radius: 4px; display: block; }
.logo-word { font-size: 14px; font-weight: 600; color: #0f172a; letter-spacing: -0.3px; }

/* Links */
.nav-links { display: flex; align-items: center; gap: 2px; flex: 1; }
.nav-links a { color: #64748b; text-decoration: none; font-size: 13px; font-weight: 500; padding: 5px 10px; border-radius: 6px; transition: all 0.15s; white-space: nowrap; }
.nav-links a:hover { color: #0f172a; background: #f1f5f9; }
.nav-active { color: #0f172a !important; font-weight: 600 !important; }

/* Right */
.nav-right { display: flex; align-items: center; gap: 6px; flex-shrink: 0; }

/* Icon btn */
.icon-btn {
  background: none; border: none; cursor: pointer; color: #64748b;
  width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;
  border-radius: 6px; transition: all 0.15s; position: relative; flex-shrink: 0;
}
.icon-btn:hover { background: #f1f5f9; color: #0f172a; }

/* Notification dot */
.notif-dot {
  position: absolute; top: 5px; right: 5px;
  width: 6px; height: 6px; background: #3b82f6; border-radius: 50%;
  border: 1.5px solid #fff;
}

/* Bell wrap */
.bell-wrap { position: relative; }

/* Notif panel */
.notif-panel {
  position: absolute; right: 0; top: calc(100% + 6px);
  width: 300px; background: #fff; border: 1px solid #e2e8f0;
  border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  overflow: hidden; z-index: 999;
}
.notif-panel-head { display: flex; justify-content: space-between; align-items: center; padding: 12px 14px 10px; border-bottom: 1px solid #f1f5f9; }
.notif-panel-title { font-size: 13px; font-weight: 600; color: #0f172a; }
.notif-panel-count { font-size: 11px; color: #94a3b8; margin-top: 1px; }
.notif-list { max-height: 300px; overflow-y: auto; }
.notif-empty { padding: 20px; text-align: center; font-size: 13px; color: #94a3b8; }
.notif-item { display: flex; gap: 10px; padding: 10px 14px; border-bottom: 1px solid #f8fafc; transition: background 0.1s; }
.notif-item:last-child { border-bottom: none; }
.notif-item:hover { background: #f8fafc; }
.notif-bullet { width: 6px; height: 6px; background: #3b82f6; border-radius: 50%; flex-shrink: 0; margin-top: 5px; }
.notif-subject { font-size: 12px; font-weight: 600; color: #0f172a; }
.notif-body { font-size: 11px; color: #64748b; margin-top: 2px; line-height: 1.4; }
.notif-time { font-size: 10px; color: #94a3b8; margin-top: 3px; }

/* User chip */
.user-chip { display: flex; align-items: center; gap: 6px; padding: 3px 6px; border-radius: 8px; border: 1px solid #e2e8f0; }
.user-avatar { width: 22px; height: 22px; background: #0f172a; color: #fff; border-radius: 4px; font-size: 10px; font-weight: 700; display: flex; align-items: center; justify-content: center; }
.user-name { font-size: 12px; color: #475569; font-weight: 500; }
.logout-btn { background: none; border: none; font-size: 11px; color: #94a3b8; cursor: pointer; padding: 2px 4px; border-radius: 4px; }
.logout-btn:hover { color: #ef4444; background: #fef2f2; }

/* Login */
.login-form { display: flex; gap: 6px; }
.login-input { padding: 5px 10px; border: 1px solid #e2e8f0; border-radius: 6px; font-size: 12px; color: #0f172a; outline: none; width: 150px; }
.login-input:focus { border-color: #94a3b8; }
.login-btn { padding: 5px 14px; background: #0f172a; color: #fff; border: none; border-radius: 6px; font-size: 12px; font-weight: 600; cursor: pointer; }
.login-btn:hover { background: #1e293b; }
</style>
