<template>
  <div class="admin-page">

    <!-- Login screen -->
    <div v-if="!adminToken" class="login-screen">
      <div class="login-card">
        <div class="login-logo">
          <span class="logo-mark"></span>
          <span class="logo-word">UniCampus</span>
        </div>
        <div class="login-title">Admin Portal</div>
        <div class="login-sub">Sign in with your admin credentials</div>
        <div class="login-form">
          <input v-model="creds.username" placeholder="Username" class="login-input" @keyup.enter="adminLogin" />
          <input v-model="creds.password" type="password" placeholder="Password" class="login-input" @keyup.enter="adminLogin" />
          <div v-if="loginError" class="login-error">{{ loginError }}</div>
          <button @click="adminLogin" class="login-btn" :disabled="loginLoading">
            {{ loginLoading ? 'Signing in…' : 'Sign in' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Admin dashboard -->
    <div v-else class="dashboard">

      <!-- Header -->
      <div class="dash-header">
        <div>
          <div class="dash-label">UNICAMPUS · ADMIN</div>
          <div class="dash-title">Student Management</div>
        </div>
        <button class="sign-out-btn" @click="signOut">Sign out</button>
      </div>

      <!-- Stats row -->
      <div class="stats-row">
        <div class="stat-box">
          <div class="stat-n">{{ students.length }}</div>
          <div class="stat-l">Total Students</div>
        </div>
        <div class="stat-box">
          <div class="stat-n">{{ countByStatus('ACTIVE') }}</div>
          <div class="stat-l">Active</div>
        </div>
        <div class="stat-box">
          <div class="stat-n">{{ countByStatus('GRADUATED') }}</div>
          <div class="stat-l">Graduated</div>
        </div>
        <div class="stat-box">
          <div class="stat-n">{{ countByStatus('SUSPENDED') }}</div>
          <div class="stat-l">Suspended</div>
        </div>
        <div class="stat-box">
          <div class="stat-n">{{ countByStatus('ON_LEAVE') }}</div>
          <div class="stat-l">On Leave</div>
        </div>
      </div>

      <!-- Table -->
      <div class="table-card">
        <div class="table-head">
          <div class="table-title">All Students</div>
          <button class="refresh-btn" @click="fetchStudents" title="Refresh">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>
          </button>
        </div>

        <div v-if="loading" class="table-empty">Loading…</div>
        <div v-else-if="students.length === 0" class="table-empty">No students found.</div>

        <table v-else class="table">
          <thead>
            <tr>
              <th>STUDENT</th>
              <th>PROGRAM · YEAR</th>
              <th>STATUS</th>
              <th>CHANGE STATUS</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="s in students" :key="s.studentId" class="table-row">
              <td>
                <div class="student-cell">
                  <div class="avatar">{{ s.firstName[0] }}{{ s.lastName[0] }}</div>
                  <div>
                    <div class="cell-name">{{ s.firstName }} {{ s.lastName }}</div>
                    <div class="cell-email">{{ s.email }}</div>
                  </div>
                </div>
              </td>
              <td class="cell-prog">{{ s.program }} · {{ s.enrollmentYear }}</td>
              <td>
                <span class="badge" :class="badgeClass(s.academicStatus)">
                  <span class="badge-dot"></span>{{ formatStatus(s.academicStatus) }}
                </span>
              </td>
              <td>
                <div class="status-actions">
                  <button v-for="opt in statusOptions" :key="opt.value"
                    class="status-btn" :class="['status-btn--' + opt.cls, { 'status-btn--current': s.academicStatus === opt.value }]"
                    :disabled="s.academicStatus === opt.value || !!updating[s.studentId]"
                    @click="updateStatus(s, opt.value)">
                    {{ opt.label }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminView',
  data() {
    return {
      adminToken: '',
      creds: { username: '', password: '' },
      loginError: '',
      loginLoading: false,
      students: [],
      loading: false,
      updating: {},
      statusOptions: [
        { value: 'ACTIVE',    label: 'Active',    cls: 'active'    },
        { value: 'GRADUATED', label: 'Graduate',  cls: 'graduated' },
        { value: 'SUSPENDED', label: 'Suspend',   cls: 'suspended' },
        { value: 'ON_LEAVE',  label: 'On Leave',  cls: 'leave'     },
      ]
    }
  },
  mounted() {
    const t = localStorage.getItem('adminToken')
    if (t) { this.adminToken = t; this.fetchStudents() }
  },
  methods: {
    async adminLogin() {
      this.loginError = ''; this.loginLoading = true
      try {
        const res = await fetch('/api/students/auth/admin', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(this.creds)
        })
        if (!res.ok) throw new Error('Invalid credentials')
        const data = await res.json()
        this.adminToken = data.token
        localStorage.setItem('adminToken', data.token)
        await this.fetchStudents()
      } catch (e) {
        this.loginError = e.message
      } finally { this.loginLoading = false }
    },
    signOut() {
      this.adminToken = ''; this.students = []
      localStorage.removeItem('adminToken')
    },
    async fetchStudents() {
      this.loading = true
      try {
        const res = await fetch('/api/students', {
          headers: { 'Authorization': `Bearer ${this.adminToken}` }
        })
        this.students = res.ok ? await res.json() : []
      } catch (e) { this.students = [] }
      finally { this.loading = false }
    },
    async updateStatus(student, newStatus) {
      this.updating = { ...this.updating, [student.studentId]: true }
      try {
        const res = await fetch(`/api/students/${student.studentId}/status`, {
          method: 'PATCH',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.adminToken}`
          },
          body: JSON.stringify({ academicStatus: newStatus })
        })
        if (res.ok) {
          const updated = await res.json()
          const idx = this.students.findIndex(s => s.studentId === student.studentId)
          if (idx !== -1) this.students[idx] = updated
        }
      } catch (e) {}
      finally {
        const u = { ...this.updating }; delete u[student.studentId]; this.updating = u
      }
    },
    countByStatus(s) { return this.students.filter(st => st.academicStatus === s).length },
    badgeClass(s) {
      return { 'badge--active': s === 'ACTIVE', 'badge--graduated': s === 'GRADUATED', 'badge--suspended': s === 'SUSPENDED', 'badge--leave': s === 'ON_LEAVE' }
    },
    formatStatus(s) {
      return s === 'ON_LEAVE' ? 'On Leave' : s.charAt(0) + s.slice(1).toLowerCase()
    }
  }
}
</script>

<style scoped>
* { box-sizing: border-box; }
.admin-page { min-height: 100vh; background: #f8fafc; font-family: 'Segoe UI', system-ui, sans-serif; }

/* Login */
.login-screen { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f8fafc; }
.login-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 16px; padding: 40px 36px; width: 360px; }
.login-logo { display: flex; align-items: center; gap: 8px; margin-bottom: 24px; }
.logo-mark { width: 20px; height: 20px; background: #0f172a; border-radius: 4px; display: block; }
.logo-word { font-size: 14px; font-weight: 700; color: #0f172a; letter-spacing: -0.3px; }
.login-title { font-size: 20px; font-weight: 700; color: #0f172a; margin-bottom: 4px; }
.login-sub { font-size: 13px; color: #64748b; margin-bottom: 24px; }
.login-form { display: flex; flex-direction: column; gap: 10px; }
.login-input { padding: 10px 13px; border: 1px solid #e2e8f0; border-radius: 8px; font-size: 14px; outline: none; }
.login-input:focus { border-color: #94a3b8; }
.login-error { font-size: 13px; color: #dc2626; }
.login-btn { padding: 11px; background: #0f172a; color: #fff; border: none; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; margin-top: 4px; }
.login-btn:hover:not(:disabled) { background: #1e293b; }
.login-btn:disabled { opacity: 0.6; cursor: wait; }

/* Dashboard */
.dashboard { max-width: 1100px; margin: 0 auto; padding: 36px 28px; }
.dash-header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 24px; }
.dash-label { font-size: 11px; font-weight: 700; color: #94a3b8; letter-spacing: 1.5px; margin-bottom: 4px; }
.dash-title { font-size: 24px; font-weight: 700; color: #0f172a; }
.sign-out-btn { padding: 7px 16px; background: #fff; border: 1px solid #e2e8f0; border-radius: 8px; font-size: 13px; color: #64748b; cursor: pointer; }
.sign-out-btn:hover { background: #fef2f2; color: #dc2626; border-color: #fecaca; }

/* Stats */
.stats-row { display: grid; grid-template-columns: repeat(5, 1fr); gap: 12px; margin-bottom: 20px; }
.stat-box { background: #fff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 16px 18px; }
.stat-n { font-size: 24px; font-weight: 800; color: #0f172a; letter-spacing: -0.5px; }
.stat-l { font-size: 12px; color: #94a3b8; margin-top: 3px; }

/* Table */
.table-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
.table-head { display: flex; align-items: center; justify-content: space-between; padding: 16px 22px; border-bottom: 1px solid #f1f5f9; }
.table-title { font-size: 14px; font-weight: 700; color: #0f172a; }
.refresh-btn { width: 32px; height: 32px; background: #fff; border: 1px solid #e2e8f0; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #64748b; cursor: pointer; }
.refresh-btn:hover { background: #f8fafc; }

.table { width: 100%; border-collapse: collapse; }
.table th { padding: 9px 20px; font-size: 10px; font-weight: 700; color: #94a3b8; letter-spacing: 0.8px; text-align: left; background: #fafafa; }
.table-row { border-bottom: 1px solid #f1f5f9; transition: background 0.1s; }
.table-row:last-child { border-bottom: none; }
.table-row:hover { background: #f8fafc; }
.table td { padding: 11px 20px; vertical-align: middle; }
.table-empty { padding: 40px; text-align: center; color: #94a3b8; font-size: 13px; }

.student-cell { display: flex; align-items: center; gap: 10px; }
.avatar { width: 32px; height: 32px; border-radius: 50%; background: #e2e8f0; color: #475569; font-weight: 700; font-size: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.cell-name { font-weight: 600; font-size: 13px; color: #0f172a; }
.cell-email { font-size: 11px; color: #94a3b8; }
.cell-prog { font-size: 13px; color: #475569; }

.badge { display: inline-flex; align-items: center; gap: 5px; padding: 3px 9px; border-radius: 20px; font-size: 11px; font-weight: 500; }
.badge-dot { width: 5px; height: 5px; border-radius: 50%; flex-shrink: 0; }
.badge--active    { background: #f0fdf4; color: #16a34a; } .badge--active    .badge-dot { background: #16a34a; }
.badge--graduated { background: #f1f5f9; color: #64748b; } .badge--graduated .badge-dot { background: #94a3b8; }
.badge--suspended { background: #fef2f2; color: #dc2626; } .badge--suspended .badge-dot { background: #dc2626; }
.badge--leave     { background: #fffbeb; color: #d97706; } .badge--leave     .badge-dot { background: #d97706; }

/* Status action buttons */
.status-actions { display: flex; gap: 4px; flex-wrap: wrap; }
.status-btn { padding: 4px 10px; border-radius: 6px; font-size: 11px; font-weight: 500; border: 1px solid; cursor: pointer; transition: all 0.15s; }
.status-btn--current { opacity: 0.35; cursor: default; }
.status-btn:disabled { cursor: not-allowed; }
.status-btn--active    { border-color: #bbf7d0; color: #16a34a; background: #f0fdf4; }
.status-btn--active:hover:not(:disabled)    { background: #dcfce7; }
.status-btn--graduated { border-color: #e2e8f0; color: #64748b; background: #f8fafc; }
.status-btn--graduated:hover:not(:disabled) { background: #f1f5f9; }
.status-btn--suspended { border-color: #fecaca; color: #dc2626; background: #fef2f2; }
.status-btn--suspended:hover:not(:disabled) { background: #fee2e2; }
.status-btn--leave     { border-color: #fde68a; color: #d97706; background: #fffbeb; }
.status-btn--leave:hover:not(:disabled)     { background: #fef3c7; }
</style>
