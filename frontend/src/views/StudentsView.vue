<template>
  <div class="page">

    <div class="page-top">
      <h1 class="page-title">Student Registry</h1>
      <p class="page-sub">Register a new account or view your own profile.</p>
    </div>

    <div class="content">

      <!-- My Profile (only when logged in) -->
      <div v-if="studentId" class="profile-card">
        <div class="profile-header">
          <div class="profile-avatar">{{ initials }}</div>
          <div>
            <div class="profile-name">{{ studentName }}</div>
            <div class="profile-sub">Your profile</div>
          </div>
          <span class="badge" :class="profile ? badgeClass(profile.academicStatus) : ''" style="margin-left:auto">
            <span class="badge-dot"></span>{{ profile ? formatStatus(profile.academicStatus) : '…' }}
          </span>
        </div>
        <div v-if="profile" class="profile-grid">
          <div class="pf">
            <div class="pf-label">STUDENT ID</div>
            <div class="pf-val mono" @click="copyId(profile.studentId)" title="Click to copy">
              {{ profile.studentId }}
              <span v-if="copied === profile.studentId" class="copy-badge">Copied!</span>
            </div>
          </div>
          <div class="pf">
            <div class="pf-label">EMAIL</div>
            <div class="pf-val">{{ profile.email }}</div>
          </div>
          <div class="pf">
            <div class="pf-label">PROGRAM</div>
            <div class="pf-val">{{ profile.program }}</div>
          </div>
          <div class="pf">
            <div class="pf-label">ENROLLMENT YEAR</div>
            <div class="pf-val">{{ profile.enrollmentYear }}</div>
          </div>
          <div class="pf">
            <div class="pf-label">PHONE</div>
            <div class="pf-val">{{ profile.phoneNumber || '—' }}</div>
          </div>
        </div>
        <div v-else class="profile-loading">Loading profile…</div>
      </div>

      <!-- Not logged in: prompt or registration form -->
      <template v-else>

        <!-- Prompt card -->
        <div v-if="!showRegister" class="not-logged-in">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          <div class="not-logged-in-title">You are not logged in</div>
          <div class="not-logged-in-sub">Log in with your student UUID from the top-right corner, or register a new account below.</div>
          <button class="reg-trigger-btn" @click="showRegister = true">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="22" y1="11" x2="16" y2="11"/></svg>
            Register as New Student
          </button>
        </div>

        <!-- Registration form -->
        <div v-else class="reg-card">
          <div class="reg-header">
            <div>
              <div class="reg-title">New Student Registration</div>
              <div class="reg-sub">Fill in your details to create an account</div>
            </div>
            <button class="back-btn" @click="cancelRegister">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>
              Back
            </button>
          </div>

          <div class="reg-form">
            <div class="form-row">
              <div class="form-field">
                <label class="form-label">FIRST NAME <span class="req">*</span></label>
                <input v-model="form.firstName" class="form-input" :class="{ 'form-input--err': errors.firstName }" placeholder="e.g. Alice" @input="errors.firstName = ''" />
                <div v-if="errors.firstName" class="form-err">{{ errors.firstName }}</div>
              </div>
              <div class="form-field">
                <label class="form-label">LAST NAME <span class="req">*</span></label>
                <input v-model="form.lastName" class="form-input" :class="{ 'form-input--err': errors.lastName }" placeholder="e.g. Johnson" @input="errors.lastName = ''" />
                <div v-if="errors.lastName" class="form-err">{{ errors.lastName }}</div>
              </div>
            </div>

            <div class="form-field">
              <label class="form-label">EMAIL ADDRESS <span class="req">*</span></label>
              <input v-model="form.email" type="email" class="form-input" :class="{ 'form-input--err': errors.email }" placeholder="e.g. alice@university.edu" @input="errors.email = ''" />
              <div v-if="errors.email" class="form-err">{{ errors.email }}</div>
            </div>

            <div class="form-row">
              <div class="form-field">
                <label class="form-label">PROGRAM <span class="req">*</span></label>
                <input v-model="form.program" class="form-input" :class="{ 'form-input--err': errors.program }" placeholder="e.g. Computer Science" @input="errors.program = ''" />
                <div v-if="errors.program" class="form-err">{{ errors.program }}</div>
              </div>
              <div class="form-field">
                <label class="form-label">ENROLLMENT YEAR <span class="req">*</span></label>
                <input v-model.number="form.enrollmentYear" type="number" class="form-input" :class="{ 'form-input--err': errors.enrollmentYear }" placeholder="e.g. 2024" min="2000" max="2099" @input="errors.enrollmentYear = ''" />
                <div v-if="errors.enrollmentYear" class="form-err">{{ errors.enrollmentYear }}</div>
              </div>
            </div>

            <div class="form-field">
              <label class="form-label">PHONE NUMBER <span class="opt">(optional)</span></label>
              <input v-model="form.phoneNumber" class="form-input" placeholder="e.g. +372 5123 4567" />
            </div>

            <div v-if="regError" class="reg-error">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
              {{ regError }}
            </div>

            <div class="form-actions">
              <button class="cancel-btn" @click="cancelRegister" :disabled="regLoading">Cancel</button>
              <button class="submit-btn" @click="register" :disabled="regLoading">
                <span v-if="regLoading" class="spinner"></span>
                {{ regLoading ? 'Registering…' : 'Create Account' }}
              </button>
            </div>
          </div>
        </div>

      </template>

    </div>

    <!-- Success overlay -->
    <div v-if="showSuccess" class="success-overlay">
      <div class="success-card">
        <div class="success-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
        </div>
        <div class="success-title">Account Created!</div>
        <div class="success-sub">Welcome, {{ newStudentName }}. You are now logged in.</div>
        <div class="success-id-label">Your Student ID (save this to log in again):</div>
        <div class="success-id" @click="copyId(newStudentId)">
          {{ newStudentId }}
          <span v-if="copied === newStudentId" class="copy-badge">Copied!</span>
        </div>
        <button class="success-btn" @click="finishRegistration">Go to My Profile</button>
        <button class="success-dismiss-btn" @click="dismissSuccess">Stay on this page</button>
      </div>
    </div>

  </div>
</template>

<script>
import { apiUrl } from '../api'

export default {
  name: 'StudentsView',
  data() {
    return {
      studentId: localStorage.getItem('studentId') || '',
      studentName: localStorage.getItem('studentName') || '',
      profile: null,
      copied: null,
      showRegister: false,
      regLoading: false,
      regError: '',
      form: {
        firstName: '',
        lastName: '',
        email: '',
        program: '',
        enrollmentYear: '',
        phoneNumber: ''
      },
      errors: {
        firstName: '', lastName: '', email: '', program: '', enrollmentYear: ''
      },
      showSuccess: false,
      newStudentId: '',
      newStudentName: ''
    }
  },
  computed: {
    initials() {
      if (!this.studentName) return '?'
      return this.studentName.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase()
    }
  },
  mounted() {
    if (this.studentId) this.fetchProfile()
    window.addEventListener('storage', this.onStorageChange)
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.onStorageChange)
  },
  methods: {
    onStorageChange() {
      const newId = localStorage.getItem('studentId') || ''
      const newName = localStorage.getItem('studentName') || ''
      if (newId !== this.studentId) {
        this.studentId = newId
        this.studentName = newName
        this.profile = null
        if (newId) this.fetchProfile()
      }
    },
    async fetchProfile() {
      try {
        const res = await fetch(apiUrl(`/api/students/${this.studentId}`))
        if (res.ok) this.profile = await res.json()
      } catch (e) {}
    },
    cancelRegister() {
      this.showRegister = false
      this.regError = ''
      this.errors = { firstName: '', lastName: '', email: '', program: '', enrollmentYear: '' }
      this.form = { firstName: '', lastName: '', email: '', program: '', enrollmentYear: '', phoneNumber: '' }
    },
    validate() {
      let ok = true
      if (!this.form.firstName.trim()) { this.errors.firstName = 'First name is required'; ok = false }
      if (!this.form.lastName.trim()) { this.errors.lastName = 'Last name is required'; ok = false }
      if (!this.form.email.trim()) { this.errors.email = 'Email is required'; ok = false }
      else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) { this.errors.email = 'Enter a valid email address'; ok = false }
      if (!this.form.program.trim()) { this.errors.program = 'Program is required'; ok = false }
      if (!this.form.enrollmentYear) { this.errors.enrollmentYear = 'Enrollment year is required'; ok = false }
      else if (this.form.enrollmentYear < 2000 || this.form.enrollmentYear > 2099) { this.errors.enrollmentYear = 'Enter a valid year (2000–2099)'; ok = false }
      return ok
    },
    async register() {
      this.regError = ''
      if (!this.validate()) return
      this.regLoading = true
      try {
        const body = {
          firstName: this.form.firstName.trim(),
          lastName: this.form.lastName.trim(),
          email: this.form.email.trim(),
          program: this.form.program.trim(),
          enrollmentYear: this.form.enrollmentYear,
          phoneNumber: this.form.phoneNumber.trim() || null
        }
        const res = await fetch(apiUrl('/api/students'), {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(body)
        })
        if (!res.ok) {
          const err = await res.json().catch(() => ({}))
          throw new Error(err.message || 'Registration failed. Please check your details.')
        }
        const student = await res.json()

        // Auto-login with the new studentId
        const loginRes = await fetch(apiUrl('/api/students/auth/login'), {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ studentId: student.studentId })
        })
        if (loginRes.ok) {
          const loginData = await loginRes.json()
          localStorage.setItem('studentId', loginData.studentId)
          localStorage.setItem('studentName', `${loginData.firstName} ${loginData.lastName}`)
          localStorage.setItem('token', loginData.token)
        }

        this.newStudentId = student.studentId
        this.newStudentName = `${student.firstName} ${student.lastName}`
        this.showSuccess = true
      } catch (e) {
        this.regError = e.message
      } finally {
        this.regLoading = false
      }
    },
    finishRegistration() {
      window.location.reload()
    },
    dismissSuccess() {
      this.studentId = this.newStudentId
      this.studentName = this.newStudentName
      this.profile = null
      this.showSuccess = false
      this.fetchProfile()
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
      if (!s) return ''
      return s === 'ON_LEAVE' ? 'On Leave' : s.charAt(0) + s.slice(1).toLowerCase()
    }
  }
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f8fafc; font-family: 'Segoe UI', system-ui, sans-serif; }

.breadcrumb-bar { background: #fff; border-bottom: 1px solid #f1f5f9; }
.breadcrumb-inner { max-width: 860px; margin: 0 auto; padding: 10px 28px; display: flex; align-items: center; gap: 6px; }
.bc-link { font-size: 12px; color: #94a3b8; text-decoration: none; }
.bc-link:hover { color: #64748b; }
.bc-sep { font-size: 12px; color: #cbd5e1; }
.bc-current { font-size: 12px; color: #475569; font-weight: 500; }

.page-top { max-width: 860px; margin: 0 auto; padding: 28px 28px 0; }
.page-title { font-size: 26px; font-weight: 700; color: #0f172a; margin: 0 0 6px; }
.page-sub { font-size: 14px; color: #64748b; margin: 0; }

.content { max-width: 860px; margin: 0 auto; padding: 20px 28px 40px; display: flex; flex-direction: column; gap: 16px; }

/* My Profile */
.profile-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 20px 24px; }
.profile-header { display: flex; align-items: center; gap: 14px; margin-bottom: 18px; }
.profile-avatar { width: 44px; height: 44px; background: #0f172a; color: #fff; border-radius: 8px; font-size: 15px; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.profile-name { font-size: 16px; font-weight: 700; color: #0f172a; }
.profile-sub { font-size: 12px; color: #94a3b8; margin-top: 2px; }
.profile-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; background: #f8fafc; border-radius: 8px; padding: 14px; }
.pf { display: flex; flex-direction: column; gap: 3px; }
.pf-label { font-size: 10px; font-weight: 700; color: #94a3b8; letter-spacing: 0.8px; }
.pf-val { font-size: 13px; color: #0f172a; }
.pf-val.mono { font-family: monospace; font-size: 11px; cursor: pointer; word-break: break-all; }
.profile-loading { font-size: 13px; color: #94a3b8; padding: 8px 0; }

/* Not logged in */
.not-logged-in { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 40px 24px; display: flex; flex-direction: column; align-items: center; gap: 12px; color: #94a3b8; }
.not-logged-in-title { font-size: 15px; font-weight: 600; color: #475569; }
.not-logged-in-sub { font-size: 13px; color: #94a3b8; text-align: center; max-width: 360px; line-height: 1.5; }
.reg-trigger-btn { margin-top: 4px; display: inline-flex; align-items: center; gap: 7px; padding: 10px 20px; background: #0f172a; color: #fff; border: none; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; transition: background 0.15s; }
.reg-trigger-btn:hover { background: #1e293b; }

/* Registration form card */
.reg-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px; }
.reg-header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 22px; }
.reg-title { font-size: 16px; font-weight: 700; color: #0f172a; }
.reg-sub { font-size: 13px; color: #94a3b8; margin-top: 3px; }
.back-btn { display: inline-flex; align-items: center; gap: 5px; padding: 6px 12px; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 7px; font-size: 12px; color: #64748b; cursor: pointer; white-space: nowrap; }
.back-btn:hover { background: #f1f5f9; }

.reg-form { display: flex; flex-direction: column; gap: 14px; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.form-field { display: flex; flex-direction: column; gap: 5px; }
.form-label { font-size: 10px; font-weight: 700; color: #94a3b8; letter-spacing: 0.8px; }
.req { color: #dc2626; }
.opt { font-weight: 400; color: #cbd5e1; letter-spacing: 0; text-transform: none; font-size: 11px; }
.form-input { padding: 9px 12px; border: 1px solid #e2e8f0; border-radius: 7px; font-size: 13px; color: #0f172a; background: #f8fafc; outline: none; transition: border-color 0.15s; font-family: inherit; }
.form-input:focus { border-color: #94a3b8; background: #fff; }
.form-input--err { border-color: #fca5a5; background: #fff; }
.form-err { font-size: 11px; color: #dc2626; }

.reg-error { display: flex; align-items: center; gap: 8px; padding: 10px 14px; background: #fef2f2; border: 1px solid #fecaca; border-radius: 8px; font-size: 13px; color: #b91c1c; }

.form-actions { display: flex; justify-content: flex-end; gap: 8px; padding-top: 4px; }
.cancel-btn { padding: 9px 18px; background: #fff; border: 1px solid #e2e8f0; border-radius: 7px; font-size: 13px; color: #64748b; cursor: pointer; }
.cancel-btn:hover:not(:disabled) { background: #f8fafc; }
.cancel-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.submit-btn { display: inline-flex; align-items: center; gap: 7px; padding: 9px 22px; background: #0f172a; color: #fff; border: none; border-radius: 7px; font-size: 13px; font-weight: 600; cursor: pointer; transition: background 0.15s; }
.submit-btn:hover:not(:disabled) { background: #1e293b; }
.submit-btn:disabled { opacity: 0.6; cursor: wait; }

/* Success overlay */
.success-overlay { position: fixed; inset: 0; background: rgba(15,23,42,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
.success-card { background: #fff; border-radius: 16px; padding: 36px 32px; width: 440px; max-width: calc(100vw - 32px); display: flex; flex-direction: column; align-items: center; gap: 12px; text-align: center; }
.success-icon { width: 56px; height: 56px; background: #f0fdf4; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #16a34a; }
.success-title { font-size: 20px; font-weight: 700; color: #0f172a; }
.success-sub { font-size: 14px; color: #64748b; }
.success-id-label { font-size: 11px; font-weight: 700; color: #94a3b8; letter-spacing: 0.8px; margin-top: 8px; }
.success-id { font-family: monospace; font-size: 12px; color: #0f172a; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 10px 14px; cursor: pointer; word-break: break-all; position: relative; }
.success-btn { margin-top: 8px; padding: 10px 24px; background: #0f172a; color: #fff; border: none; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; }
.success-btn:hover { background: #1e293b; }
.success-dismiss-btn { background: none; border: 1px solid #e2e8f0; color: #64748b; padding: 8px 20px; border-radius: 8px; font-size: 13px; cursor: pointer; }
.success-dismiss-btn:hover { background: #f8fafc; }

.copy-badge { display: inline-block; margin-left: 6px; font-size: 10px; font-weight: 600; background: #0f172a; color: #fff; padding: 1px 6px; border-radius: 4px; }

.badge { display: inline-flex; align-items: center; gap: 5px; padding: 3px 9px; border-radius: 20px; font-size: 11px; font-weight: 500; }
.badge-dot { width: 5px; height: 5px; border-radius: 50%; flex-shrink: 0; }
.badge--active    { background: #f0fdf4; color: #16a34a; } .badge--active    .badge-dot { background: #16a34a; }
.badge--graduated { background: #f1f5f9; color: #64748b; } .badge--graduated .badge-dot { background: #94a3b8; }
.badge--suspended { background: #fef2f2; color: #dc2626; } .badge--suspended .badge-dot { background: #dc2626; }
.badge--leave     { background: #fffbeb; color: #d97706; } .badge--leave     .badge-dot { background: #d97706; }

.spinner { width: 13px; height: 13px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; display: inline-block; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>
