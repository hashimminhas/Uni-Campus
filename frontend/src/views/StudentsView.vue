<template>
  <div class="page">

    <!-- Breadcrumb -->
    <div class="breadcrumb-bar">
      <div class="breadcrumb-inner">
        <router-link to="/" class="bc-link">Home</router-link>
        <span class="bc-sep">/</span>
        <span class="bc-current">Students</span>
      </div>
    </div>

    <!-- Title -->
    <div class="page-top">
      <h1 class="page-title">Student Registry</h1>
      <p class="page-sub">Search individual records or browse the full list of registered students.</p>
    </div>

    <div class="content">

      <!-- Search -->
      <div class="search-box">
        <div class="search-label">FIND STUDENT BY UUID</div>
        <div class="search-row">
          <input v-model="find.studentId" placeholder="e.g. cb89da4a-9f7b-4c91-8914-e7f5020c1798" class="search-input" @keyup.enter="findStudent" />
          <button @click="findStudent" class="search-btn" :disabled="find.loading">
            <span v-if="!find.loading">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="display:inline;vertical-align:-2px;margin-right:5px"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>Search
            </span>
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
                  <span class="sr-val sr-val--mono" @click="copyId(find.result.studentId)" :title="copied === find.result.studentId ? 'Copied!' : 'Click to copy'" style="cursor:pointer">
                    {{ find.result.studentId }}
                    <span v-if="copied === find.result.studentId" class="copy-badge">Copied!</span>
                  </span>
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
              <div class="error-wrap">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                <span>{{ find.result.message || 'Student not found.' }}</span>
              </div>
            </template>
          </div>
        </transition>
      </div>

      <!-- Table -->
      <div class="table-card">
        <div class="table-header">
          <div>
            <div class="table-title">Registered Students</div>
            <div class="table-sub">{{ filteredStudents.length }} {{ activeFilter === 'ALL' ? 'total' : activeFilter.toLowerCase() }} records</div>
          </div>
          <div class="header-right">
            <!-- Filter chips -->
            <div class="filter-chips">
              <button v-for="f in filters" :key="f.value" class="chip" :class="{ 'chip--active': activeFilter === f.value }" @click="activeFilter = f.value; page = 0">
                {{ f.label }}
              </button>
            </div>
            <button @click="fetchAllStudents" class="refresh-btn" :class="{ 'refresh-btn--loading': allStudents.loading }" :title="'Refresh'">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>
            </button>
          </div>
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
            <tr v-else-if="filteredStudents.length === 0">
              <td colspan="4" class="table-empty">No students found</td>
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
              <td>
                <span class="cell-uuid" @click="copyId(s.studentId)" :title="copied === s.studentId ? 'Copied!' : 'Click to copy'">
                  {{ s.studentId }}
                  <span v-if="copied === s.studentId" class="copy-badge">Copied!</span>
                </span>
              </td>
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
          <span class="footer-count">Showing {{ pagedStudents.length }} of {{ filteredStudents.length }}</span>
          <div class="pagination">
            <button class="page-btn" :disabled="page === 0" @click="page--">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg>
              Previous
            </button>
            <button class="page-btn" :disabled="(page + 1) * pageSize >= filteredStudents.length" @click="page++">
              Next
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"/></svg>
            </button>
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
      pageSize: 8,
      activeFilter: 'ALL',
      copied: null,
      filters: [
        { label: 'All', value: 'ALL' },
        { label: 'Active', value: 'ACTIVE' },
        { label: 'Graduated', value: 'GRADUATED' },
        { label: 'On Leave', value: 'ON_LEAVE' },
      ]
    }
  },
  computed: {
    filteredStudents() {
      if (this.activeFilter === 'ALL') return this.allStudents.list
      return this.allStudents.list.filter(s => s.academicStatus === this.activeFilter)
    },
    pagedStudents() {
      const start = this.page * this.pageSize
      return this.filteredStudents.slice(start, start + this.pageSize)
    }
  },
  methods: {
    async fetchAllStudents() {
      this.allStudents.loading = true
      this.page = 0
      try {
        const token = localStorage.getItem('token') || ''
        const res = await fetch('/api/students', {
          headers: token ? { 'Authorization': `Bearer ${token}` } : {}
        })
        this.allStudents.list = res.ok ? await res.json() : []
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
    copyId(id) {
      navigator.clipboard.writeText(id).then(() => {
        this.copied = id
        setTimeout(() => { this.copied = null }, 1500)
      })
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
.page { min-height: 100vh; background: #f8fafc; font-family: 'Segoe UI', system-ui, sans-serif; }

/* Breadcrumb */
.breadcrumb-bar { background: #fff; border-bottom: 1px solid #f1f5f9; padding: 0; }
.breadcrumb-inner { max-width: 960px; margin: 0 auto; padding: 10px 28px; display: flex; align-items: center; gap: 6px; }
.bc-link { font-size: 12px; color: #94a3b8; text-decoration: none; }
.bc-link:hover { color: #64748b; }
.bc-sep { font-size: 12px; color: #cbd5e1; }
.bc-current { font-size: 12px; color: #475569; font-weight: 500; }

.page-top { max-width: 960px; margin: 0 auto; padding: 32px 28px 0; }
.page-title { font-size: 28px; font-weight: 700; color: #0f172a; letter-spacing: -0.5px; margin: 0 0 8px; }
.page-sub { font-size: 14px; color: #64748b; line-height: 1.6; margin: 0; }

.content { max-width: 960px; margin: 0 auto; padding: 20px 28px 40px; display: flex; flex-direction: column; gap: 16px; }

/* Search */
.search-box { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 18px 22px; }
.search-label { font-size: 10px; font-weight: 700; color: #94a3b8; letter-spacing: 1px; margin-bottom: 10px; }
.search-row { display: flex; gap: 8px; }
.search-input {
  flex: 1; padding: 9px 13px; border: 1px solid #e2e8f0; border-radius: 7px;
  font-size: 13px; color: #0f172a; background: #f8fafc; outline: none;
  font-family: monospace; transition: border-color 0.15s;
}
.search-input:focus { border-color: #94a3b8; background: #fff; }
.search-input::placeholder { color: #94a3b8; font-family: 'Segoe UI', system-ui, sans-serif; }
.search-btn {
  padding: 9px 18px; background: #0f172a; color: #fff; border: none;
  border-radius: 7px; font-size: 13px; font-weight: 600; cursor: pointer;
  white-space: nowrap; transition: background 0.15s; display: flex; align-items: center;
}
.search-btn:hover:not(:disabled) { background: #1e293b; }
.search-btn:disabled { opacity: 0.6; cursor: wait; }

.search-result { margin-top: 12px; border-radius: 8px; padding: 14px 16px; font-size: 14px; }
.search-result--success { background: #f0fdf4; border: 1px solid #bbf7d0; }
.search-result--error   { background: #fef2f2; border: 1px solid #fecaca; }
.sr-header { display: flex; align-items: center; gap: 12px; margin-bottom: 14px; }
.sr-name { font-weight: 700; font-size: 15px; color: #0f172a; }
.sr-email { font-size: 12px; color: #64748b; margin-top: 2px; }
.sr-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; background: rgba(0,0,0,0.03); border-radius: 8px; padding: 12px; }
.sr-field { display: flex; flex-direction: column; gap: 3px; }
.sr-label { font-size: 10px; font-weight: 700; color: #94a3b8; letter-spacing: 0.8px; }
.sr-val { font-size: 13px; color: #0f172a; }
.sr-val--mono { font-family: monospace; font-size: 11px; word-break: break-all; }
.error-wrap { display: flex; align-items: center; gap: 8px; color: #b91c1c; font-size: 14px; }

/* Copy badge */
.copy-badge { display: inline-block; margin-left: 6px; font-size: 10px; font-weight: 600; background: #0f172a; color: #fff; padding: 1px 6px; border-radius: 4px; vertical-align: middle; }

/* Table card */
.table-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
.table-header { display: flex; align-items: flex-start; justify-content: space-between; padding: 18px 22px 0; gap: 12px; }
.table-title { font-size: 14px; font-weight: 700; color: #0f172a; }
.table-sub { font-size: 12px; color: #94a3b8; margin-top: 2px; }

.header-right { display: flex; align-items: center; gap: 10px; flex-shrink: 0; }

/* Filter chips */
.filter-chips { display: flex; gap: 4px; }
.chip {
  padding: 5px 12px; border: 1px solid #e2e8f0; border-radius: 20px;
  font-size: 12px; font-weight: 500; color: #64748b; background: #fff;
  cursor: pointer; transition: all 0.15s; white-space: nowrap;
}
.chip:hover { background: #f8fafc; border-color: #cbd5e1; }
.chip--active { background: #0f172a; color: #fff; border-color: #0f172a; }

.refresh-btn {
  width: 32px; height: 32px; background: #fff; border: 1px solid #e2e8f0; border-radius: 8px;
  display: flex; align-items: center; justify-content: center; color: #64748b;
  cursor: pointer; transition: all 0.15s; flex-shrink: 0;
}
.refresh-btn:hover { background: #f8fafc; color: #0f172a; }
.refresh-btn--loading { opacity: 0.5; cursor: wait; }

.table { width: 100%; border-collapse: collapse; margin-top: 14px; }
.table thead tr { border-top: 1px solid #f1f5f9; border-bottom: 1px solid #f1f5f9; }
.table th {
  padding: 9px 22px; font-size: 10px; font-weight: 700; color: #94a3b8;
  letter-spacing: 0.8px; text-align: left; background: #fafafa;
}
.table-row { border-bottom: 1px solid #f1f5f9; transition: background 0.1s; }
.table-row:last-child { border-bottom: none; }
.table-row:hover { background: #f8fafc; }
.table td { padding: 11px 22px; vertical-align: middle; }

.student-cell { display: flex; align-items: center; gap: 10px; }
.avatar {
  width: 32px; height: 32px; border-radius: 50%; background: #e2e8f0;
  color: #475569; font-weight: 700; font-size: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.avatar--lg { width: 44px; height: 44px; font-size: 15px; }
.cell-name { font-weight: 600; font-size: 13px; color: #0f172a; }
.cell-email { font-size: 11px; color: #94a3b8; }
.cell-uuid {
  font-family: monospace; font-size: 11px; color: #94a3b8; cursor: pointer;
  transition: color 0.1s; display: inline-flex; align-items: center; gap: 4px;
}
.cell-uuid:hover { color: #475569; }
.cell-prog { font-size: 13px; color: #475569; }

.badge { display: inline-flex; align-items: center; gap: 5px; padding: 3px 9px; border-radius: 20px; font-size: 11px; font-weight: 500; }
.badge-dot { width: 5px; height: 5px; border-radius: 50%; flex-shrink: 0; }
.badge--active    { background: #f0fdf4; color: #16a34a; }
.badge--active    .badge-dot { background: #16a34a; }
.badge--graduated { background: #f1f5f9; color: #64748b; }
.badge--graduated .badge-dot { background: #94a3b8; }
.badge--suspended { background: #fef2f2; color: #dc2626; }
.badge--suspended .badge-dot { background: #dc2626; }
.badge--leave     { background: #fffbeb; color: #d97706; }
.badge--leave     .badge-dot { background: #d97706; }

.table-empty { text-align: center; padding: 36px; color: #94a3b8; font-size: 13px; }

.table-footer { display: flex; align-items: center; justify-content: space-between; padding: 12px 22px; border-top: 1px solid #f1f5f9; }
.footer-count { font-size: 12px; color: #94a3b8; }
.pagination { display: flex; gap: 6px; }
.page-btn {
  display: flex; align-items: center; gap: 4px;
  padding: 5px 12px; background: #fff; border: 1px solid #e2e8f0; border-radius: 7px;
  font-size: 12px; font-weight: 500; color: #475569; cursor: pointer; transition: background 0.15s;
}
.page-btn:hover:not(:disabled) { background: #f8fafc; }
.page-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.spinner { width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; display: inline-block; }
@keyframes spin { to { transform: rotate(360deg); } }

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
