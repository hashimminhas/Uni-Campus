<template>
  <div class="page">

    <!-- Title -->
    <div class="page-top">
      <h1 class="page-title">Student Registry &amp; Records</h1>
      <p class="page-sub">Manage the academic lifecycle of currently enrolled and graduated students.<br>Access primary records, verify credentials, and track program progression across departments.</p>
    </div>

    <div class="content">

      <!-- Search -->
      <div class="search-box">
        <div class="search-label">FIND STUDENT BY UUID</div>
        <div class="search-row">
          <input v-model="find.studentId" placeholder="e.g. cb89da4a-9f7b-4c91-8914-e7f5020c1798" class="search-input" @keyup.enter="findStudent" />
          <button @click="findStudent" class="search-btn" :class="{ 'search-btn--loading': find.loading }">
            <span v-if="!find.loading">🔍 Search Record</span>
            <span v-else class="spinner"></span>
          </button>
        </div>
        <transition name="fade">
          <div v-if="find.result" class="search-result" :class="find.isError ? 'search-result--error' : 'search-result--success'">
            <template v-if="!find.isError">
              <div class="sr-header">
                <div class="avatar avatar--lg">{{ find.result.firstName[0] }}{{ find.result.lastName[0] }}</div>
                <div>
                  <div class="sr-name">{{ find.result.firstName }} {{ find.result.lastName }}</div>
                  <div class="sr-email">{{ find.result.email }}</div>
                </div>
                <span class="badge" :class="badgeClass(find.result.academicStatus)" style="margin-left:auto">
                  <span class="badge-dot"></span>{{ formatStatus(find.result.academicStatus) }}
                </span>
              </div>
              <div class="sr-grid">
                <div class="sr-field">
                  <span class="sr-label">STUDENT ID</span>
                  <span class="sr-val sr-val--mono">{{ find.result.studentId }}</span>
                </div>
                <div class="sr-field">
                  <span class="sr-label">PROGRAM</span>
                  <span class="sr-val">{{ find.result.program }}</span>
                </div>
                <div class="sr-field">
                  <span class="sr-label">ENROLLMENT YEAR</span>
                  <span class="sr-val">{{ find.result.enrollmentYear }}</span>
                </div>
                <div class="sr-field">
                  <span class="sr-label">PHONE</span>
                  <span class="sr-val">{{ find.result.phoneNumber || '—' }}</span>
                </div>
              </div>
            </template>
            <template v-else>
              <span class="error-text">⚠️ {{ find.result.message || 'Student not found.' }}</span>
            </template>
          </div>
        </transition>
      </div>

      <!-- Table -->
      <div class="table-card">
        <div class="table-header">
          <div>
            <div class="table-title">Registered Students</div>
            <div class="table-sub">All students currently in the system</div>
          </div>
          <button @click="fetchAllStudents" class="refresh-btn" :class="{ 'refresh-btn--loading': allStudents.loading }">
            <span v-if="!allStudents.loading">↻ Refresh</span>
            <span v-else class="spinner spinner--dark"></span>
          </button>
        </div>

        <table class="table">
          <thead>
            <tr>
              <th>STUDENT</th>
              <th>UUID</th>
              <th>PROGRAM · YEAR</th>
              <th>STATUS</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="allStudents.loading">
              <td colspan="4" class="table-empty">Loading...</td>
            </tr>
            <tr v-else-if="allStudents.list.length === 0">
              <td colspan="4" class="table-empty">No students registered yet</td>
            </tr>
            <tr v-for="s in pagedStudents" :key="s.studentId" class="table-row">
              <td>
                <div class="student-cell">
                  <div class="avatar">{{ s.firstName[0] }}{{ s.lastName[0] }}</div>
                  <div>
                    <div class="cell-name">{{ s.firstName }} {{ s.lastName }}</div>
                    <div class="cell-email">{{ s.email }}</div>
                  </div>
                </div>
              </td>
              <td class="cell-uuid">{{ s.studentId }}</td>
              <td class="cell-prog">{{ s.program }} · {{ s.enrollmentYear }}</td>
              <td>
                <span class="badge" :class="badgeClass(s.academicStatus)">
                  <span class="badge-dot"></span>{{ formatStatus(s.academicStatus) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="table-footer">
          <span class="footer-count">Showing {{ allStudents.list.length }} records</span>
          <div class="pagination">
            <button class="page-btn" :disabled="page === 0" @click="page--">Previous</button>
            <button class="page-btn" :disabled="(page + 1) * pageSize >= allStudents.list.length" @click="page++">Next</button>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: 'StudentsView',
  mounted() {
    this.fetchAllStudents()
  },
  data() {
    return {
      allStudents: { list: [], loading: false },
      find: { studentId: '', result: null, isError: false, loading: false },
      page: 0,
      pageSize: 8
    }
  },
  computed: {
    pagedStudents() {
      const start = this.page * this.pageSize
      return this.allStudents.list.slice(start, start + this.pageSize)
    }
  },
  methods: {
    async fetchAllStudents() {
      this.allStudents.loading = true
      this.page = 0
      try {
        const res = await fetch('/api/students')
        this.allStudents.list = await res.json()
      } catch (e) {
        this.allStudents.list = []
      } finally { this.allStudents.loading = false }
    },
    async findStudent() {
      if (!this.find.studentId.trim()) return
      this.find.result = null; this.find.loading = true
      try {
        const res = await fetch(`/api/students/${this.find.studentId.trim()}`)
        const data = await res.json()
        this.find.isError = !res.ok
        this.find.result = data
      } catch (e) {
        this.find.isError = true; this.find.result = { message: e.message }
      } finally { this.find.loading = false }
    },
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
.page { min-height: 100vh; background: #f5f5f4; font-family: 'Segoe UI', system-ui, sans-serif; }

.page-top { max-width: 960px; margin: 0 auto; padding: 48px 28px 0; }
.page-title { font-size: 32px; font-weight: 700; color: #0f172a; letter-spacing: -0.5px; margin: 0 0 12px; }
.page-sub { font-size: 15px; color: #64748b; line-height: 1.6; margin: 0; }

.content { max-width: 960px; margin: 0 auto; padding: 28px; display: flex; flex-direction: column; gap: 20px; }

/* Search */
.search-box { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 20px 24px; }
.search-label { font-size: 11px; font-weight: 700; color: #94a3b8; letter-spacing: 1px; margin-bottom: 10px; }
.search-row { display: flex; gap: 10px; }
.search-input {
  flex: 1; padding: 10px 14px; border: 1px solid #e2e8f0; border-radius: 8px;
  font-size: 14px; color: #0f172a; background: #f8fafc; outline: none;
  font-family: monospace; transition: border-color 0.15s;
}
.search-input:focus { border-color: #94a3b8; background: #fff; }
.search-input::placeholder { color: #94a3b8; font-family: 'Segoe UI', system-ui, sans-serif; }
.search-btn {
  padding: 10px 20px; background: #0f172a; color: #fff; border: none;
  border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer;
  white-space: nowrap; transition: background 0.15s; display: flex; align-items: center; gap: 6px;
}
.search-btn:hover { background: #1e293b; }
.search-btn--loading { opacity: 0.7; cursor: wait; }

.search-result { margin-top: 14px; border-radius: 8px; padding: 14px 16px; font-size: 14px; }
.search-result--success { background: #f0fdf4; border: 1px solid #bbf7d0; }
.search-result--error   { background: #fef2f2; border: 1px solid #fecaca; }
.sr-header { display: flex; align-items: center; gap: 14px; margin-bottom: 16px; }
.sr-name { font-weight: 700; font-size: 16px; color: #0f172a; }
.sr-email { font-size: 13px; color: #64748b; margin-top: 2px; }
.sr-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; background: rgba(0,0,0,0.03); border-radius: 8px; padding: 14px; }
.sr-field { display: flex; flex-direction: column; gap: 3px; }
.sr-label { font-size: 10px; font-weight: 700; color: #94a3b8; letter-spacing: 0.8px; }
.sr-val { font-size: 13px; color: #0f172a; }
.sr-val--mono { font-family: monospace; font-size: 12px; word-break: break-all; }
.error-text { color: #b91c1c; }

/* Table card */
.table-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }

.table-header { display: flex; align-items: flex-start; justify-content: space-between; padding: 20px 24px 0; }
.table-title { font-size: 15px; font-weight: 700; color: #0f172a; }
.table-sub { font-size: 13px; color: #94a3b8; margin-top: 2px; }

.refresh-btn {
  padding: 6px 14px; background: #fff; border: 1px solid #e2e8f0; border-radius: 8px;
  font-size: 13px; font-weight: 500; color: #475569; cursor: pointer; display: flex; align-items: center; gap: 6px;
  transition: background 0.15s;
}
.refresh-btn:hover { background: #f8fafc; }
.refresh-btn--loading { opacity: 0.7; cursor: wait; }

.table { width: 100%; border-collapse: collapse; margin-top: 16px; }
.table thead tr { border-top: 1px solid #f1f5f9; border-bottom: 1px solid #f1f5f9; }
.table th {
  padding: 10px 24px; font-size: 11px; font-weight: 700; color: #94a3b8;
  letter-spacing: 0.8px; text-align: left; background: #fafafa;
}
.table-row { border-bottom: 1px solid #f1f5f9; transition: background 0.1s; }
.table-row:last-child { border-bottom: none; }
.table-row:hover { background: #f8fafc; }
.table td { padding: 14px 24px; vertical-align: middle; }

.student-cell { display: flex; align-items: center; gap: 12px; }
.avatar {
  width: 36px; height: 36px; border-radius: 50%; background: #e2e8f0;
  color: #475569; font-weight: 700; font-size: 13px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.avatar--sm { width: 32px; height: 32px; font-size: 12px; }
.avatar--lg { width: 48px; height: 48px; font-size: 16px; font-weight: 700; }
.cell-name { font-weight: 600; font-size: 14px; color: #0f172a; }
.cell-email { font-size: 12px; color: #94a3b8; }
.cell-uuid { font-family: monospace; font-size: 12px; color: #94a3b8; }
.cell-prog { font-size: 14px; color: #475569; }

.badge { display: inline-flex; align-items: center; gap: 5px; padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.badge-dot { width: 6px; height: 6px; border-radius: 50%; }
.badge--active    { background: #f0fdf4; color: #16a34a; }
.badge--active    .badge-dot { background: #16a34a; }
.badge--graduated { background: #f1f5f9; color: #64748b; }
.badge--graduated .badge-dot { background: #94a3b8; }
.badge--suspended { background: #fef2f2; color: #dc2626; }
.badge--suspended .badge-dot { background: #dc2626; }
.badge--leave     { background: #fffbeb; color: #d97706; }
.badge--leave     .badge-dot { background: #d97706; }

.table-empty { text-align: center; padding: 40px; color: #94a3b8; font-size: 14px; }

.table-footer { display: flex; align-items: center; justify-content: space-between; padding: 14px 24px; border-top: 1px solid #f1f5f9; }
.footer-count { font-size: 13px; color: #94a3b8; }
.pagination { display: flex; gap: 8px; }
.page-btn {
  padding: 6px 14px; background: #fff; border: 1px solid #e2e8f0; border-radius: 8px;
  font-size: 13px; font-weight: 500; color: #475569; cursor: pointer; transition: background 0.15s;
}
.page-btn:hover:not(:disabled) { background: #f8fafc; }
.page-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.spinner { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; display: inline-block; }
.spinner--dark { border-color: rgba(0,0,0,0.1); border-top-color: #475569; }
@keyframes spin { to { transform: rotate(360deg); } }

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
