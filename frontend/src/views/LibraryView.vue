<template>
  <div class="lib-page">

    <!-- Page header -->
    <div class="lib-header">
      <div class="lib-header-inner">
        <div class="lib-eyebrow">CAMPUS LIBRARY</div>
        <h1 class="lib-title">Catalog & Borrowing</h1>
        <p class="lib-sub">Browse the academic catalog and borrow titles for up to 14 days. You'll receive a notification each time you check out or return a book.</p>
      </div>
    </div>

    <div class="lib-body">

      <div v-if="loading" class="lib-state">Loading books…</div>
      <div v-else-if="error" class="lib-state lib-state--error">{{ error }}</div>

      <div v-else class="lib-card">
        <div class="lib-card-head">
          <div>
            <div class="lib-card-title">Available Books</div>
            <div v-if="studentId && activeLoans.length > 0" class="lib-card-sub">
              You currently have {{ activeLoans.length }} book{{ activeLoans.length !== 1 ? 's' : '' }} on loan
            </div>
          </div>
          <span class="lib-count">{{ books.length }} titles</span>
        </div>

        <div class="lib-book-list">
          <div v-if="books.length === 0" class="lib-empty">No books in the catalog.</div>
          <div v-for="book in books" :key="book.bookId" class="lib-book-row">

            <!-- Icon -->
            <div class="lib-book-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/>
                <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
              </svg>
            </div>

            <!-- Book info -->
            <div class="lib-book-info">
              <div class="lib-book-title">{{ book.title }}</div>
              <div class="lib-book-meta">
                <span v-if="book.author">{{ book.author }}</span>
                <span v-if="book.author && book.category"> · </span>
                <span v-if="book.category">{{ book.category }}</span>
              </div>
            </div>

            <!-- Right: availability + action -->
            <div class="lib-book-right">
              <div class="lib-book-avail">
                <template v-if="book.availableCopies !== undefined && book.totalCopies !== undefined">
                  {{ book.availableCopies }}/{{ book.totalCopies }} available
                </template>
                <template v-else>
                  {{ book.isAvailable ? 'Available' : 'Unavailable' }}
                </template>
              </div>

              <!-- Return if student has this on loan -->
              <button v-if="getLoan(book)" class="lib-btn lib-btn-return" @click="returnBook(getLoan(book).loanId)">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M9 14l-4-4 4-4"/><path d="M5 10h11a4 4 0 0 1 0 8h-1"/>
                </svg>
                Return
              </button>
              <!-- Borrow -->
              <button v-else-if="book.isAvailable" class="lib-btn lib-btn-borrow" @click="borrowBook(book.bookId)">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                Borrow
              </button>
              <!-- Unavailable -->
              <button v-else class="lib-btn lib-btn-unavail" disabled>
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                Unavailable
              </button>
            </div>

          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { apiUrl } from '../api'

export default {
  name: 'LibraryView',
  data() {
    return {
      books: [],
      activeLoans: [],
      studentId: localStorage.getItem('studentId') || '',
      loading: true,
      error: null,
    }
  },
  mounted() {
    this.fetchBooks()
    window.addEventListener('storage', this.handleStorageChange)
    this.storageInterval = setInterval(this.checkStorage, 1000)
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.handleStorageChange)
    if (this.storageInterval) clearInterval(this.storageInterval)
  },
  methods: {
    getLoan(book) {
      return this.activeLoans.find(l =>
        l.bookId === book.bookId || l.bookTitle === book.title
      ) || null
    },
    handleStorageChange(e) {
      if (e.key === 'studentId') this.checkStorage()
    },
    checkStorage() {
      const cur = localStorage.getItem('studentId') || ''
      if (this.studentId !== cur) {
        this.studentId = cur
        if (cur) this.fetchStudentLoans()
        else this.activeLoans = []
      }
    },
    isValidUUID(v) {
      return typeof v === 'string' && /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(v)
    },
    async fetchBooks() {
      try {
        this.loading = true
        this.error = null
        const res = await fetch(apiUrl('/api/library/books'))
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        this.books = await res.json()
        if (this.studentId) await this.fetchStudentLoans()
      } catch (e) {
        this.error = e.message || 'Failed to load books'
      } finally {
        this.loading = false
      }
    },
    async fetchStudentLoans() {
      if (!this.studentId) return
      try {
        const res = await fetch(apiUrl(`/api/library/loans/student/${this.studentId}`))
        if (res.ok) {
          const loans = await res.json()
          this.activeLoans = loans.filter(l => !l.returnedAt)
        }
      } catch (e) {}
    },
    async borrowBook(bookId) {
      let studentId = localStorage.getItem('studentId')
      if (!studentId) {
        const input = prompt('Please enter your Student ID (UUID) to borrow this book:')
        if (!input) return
        studentId = input.trim()
      }
      if (!this.isValidUUID(studentId)) {
        alert('Please provide a valid student UUID. Log in first or enter a valid ID.')
        return
      }
      try {
        const res = await fetch(apiUrl(`/api/library/books/${bookId}/borrow`), {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ studentId })
        })
        if (!res.ok) {
          const err = await res.json().catch(() => ({}))
          throw new Error(err.message || `HTTP ${res.status}`)
        }
        alert('Book borrowed successfully!')
        this.fetchBooks()
      } catch (e) {
        alert('Failed to borrow book: ' + e.message)
      }
    },
    async returnBook(loanId) {
      if (!confirm('Return this book?')) return
      try {
        const res = await fetch(apiUrl(`/api/library/loans/${loanId}/return`), {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' }
        })
        if (!res.ok) {
          const err = await res.json().catch(() => ({}))
          throw new Error(err.message || `HTTP ${res.status}`)
        }
        alert('Book returned successfully!')
        this.fetchBooks()
      } catch (e) {
        alert('Failed to return book: ' + e.message)
      }
    }
  }
}
</script>

<style scoped>
.lib-page { min-height: 100vh; background: #fafafa; font-family: 'Segoe UI', system-ui, sans-serif; }

/* Header */
.lib-header { background: #fff; border-bottom: 1px solid #f1f5f9; padding: 40px 0 32px; }
.lib-header-inner { max-width: 900px; margin: 0 auto; padding: 0 32px; }
.lib-eyebrow { font-size: 11px; font-weight: 700; color: #94a3b8; letter-spacing: 1.5px; margin-bottom: 10px; }
.lib-title { font-size: 36px; font-weight: 800; color: #0f172a; margin: 0 0 10px; letter-spacing: -0.5px; }
.lib-sub { font-size: 14px; color: #64748b; line-height: 1.6; margin: 0; max-width: 480px; }

/* Body */
.lib-body { max-width: 900px; margin: 0 auto; padding: 32px; }

.lib-state { padding: 48px; text-align: center; font-size: 14px; color: #94a3b8; }
.lib-state--error { color: #dc2626; background: #fef2f2; border: 1px solid #fecaca; border-radius: 10px; }

/* Card */
.lib-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 14px; overflow: hidden; }
.lib-card-head { display: flex; justify-content: space-between; align-items: center; padding: 18px 24px 16px; border-bottom: 1px solid #f1f5f9; }
.lib-card-title { font-size: 15px; font-weight: 700; color: #0f172a; }
.lib-card-sub { font-size: 12px; color: #94a3b8; margin-top: 2px; }
.lib-count { font-size: 12px; color: #94a3b8; font-weight: 500; }

/* Book list */
.lib-book-list { }
.lib-empty { padding: 40px; text-align: center; font-size: 13px; color: #94a3b8; }

.lib-book-row {
  display: flex; align-items: center; gap: 14px;
  padding: 14px 24px; border-bottom: 1px solid #f8fafc;
  transition: background 0.1s;
}
.lib-book-row:last-child { border-bottom: none; }
.lib-book-row:hover { background: #f8fafc; }

.lib-book-icon {
  width: 36px; height: 36px; background: #f1f5f9; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  color: #94a3b8; flex-shrink: 0;
}

.lib-book-info { flex: 1; min-width: 0; }
.lib-book-title { font-size: 14px; font-weight: 600; color: #0f172a; }
.lib-book-meta { font-size: 12px; color: #64748b; margin-top: 2px; }

.lib-book-right { display: flex; align-items: center; gap: 16px; flex-shrink: 0; }
.lib-book-avail { font-size: 12px; color: #94a3b8; text-align: right; min-width: 80px; }

/* Buttons */
.lib-btn {
  display: flex; align-items: center; gap: 5px;
  padding: 7px 14px; border-radius: 7px; border: none;
  font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.15s;
}
.lib-btn-borrow { background: #0f172a; color: #fff; }
.lib-btn-borrow:hover { background: #1e293b; }
.lib-btn-unavail { background: #f1f5f9; color: #94a3b8; cursor: not-allowed; }
.lib-btn-return { background: none; color: #475569; border: 1px solid #e2e8f0; }
.lib-btn-return:hover { background: #f8fafc; border-color: #cbd5e1; }
</style>
