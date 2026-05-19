<template>
  <div class="exam-page">

    <!-- Header -->
    <div class="exam-header">
      <div class="exam-header-inner">
        <div class="exam-eyebrow">EXAMINATIONS</div>
        <h1 class="exam-title">Exam Schedule</h1>
        <p class="exam-sub">View upcoming exams for your enrolled courses. Dates, locations and durations are listed below.</p>
      </div>
    </div>

    <div class="exam-body">

      <!-- Not logged in -->
      <div v-if="!studentId" class="exam-state">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#cbd5e1" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        <p>Log in to view your exam schedule.</p>
      </div>

      <div v-else-if="loading" class="exam-state">Loading your exams…</div>
      <div v-else-if="error" class="exam-state exam-state--error">{{ error }}</div>

      <div v-else class="exam-card">
        <div class="exam-card-head">
          <div>
            <div class="exam-card-title">Your Exams</div>
            <div class="exam-card-sub">{{ exams.length }} exam{{ exams.length !== 1 ? 's' : '' }} scheduled</div>
          </div>
        </div>

        <div class="exam-list">
          <div v-if="exams.length === 0" class="exam-empty">
            No exams scheduled yet. Exams appear here once your instructor creates them.
          </div>

          <div v-for="exam in sortedExams" :key="exam.examId" class="exam-row">
            <!-- Date block -->
            <div class="exam-date-block">
              <div class="exam-date-day">{{ formatDay(exam.examDate) }}</div>
              <div class="exam-date-month">{{ formatMonth(exam.examDate) }}</div>
            </div>

            <!-- Info -->
            <div class="exam-info">
              <div class="exam-course-name">{{ courseName(exam.courseId) }}</div>
              <div class="exam-meta">
                <span>{{ formatDateTime(exam.examDate) }}</span>
                <span class="meta-dot">·</span>
                <span>{{ exam.durationMinutes }} min</span>
              </div>
            </div>

            <!-- Location -->
            <div class="exam-location">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
              {{ exam.location || 'TBC' }}
            </div>

            <!-- Status badge -->
            <div class="exam-badge" :class="isUpcoming(exam.examDate) ? 'badge-upcoming' : 'badge-past'">
              {{ isUpcoming(exam.examDate) ? 'Upcoming' : 'Past' }}
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: 'ExamView',
  data() {
    return {
      exams: [],
      courses: [],
      studentId: localStorage.getItem('studentId') || '',
      loading: false,
      error: null,
    }
  },
  computed: {
    sortedExams() {
      return [...this.exams].sort((a, b) => new Date(a.examDate) - new Date(b.examDate))
    }
  },
  mounted() {
    if (this.studentId) this.fetchData()
    window.addEventListener('storage', this.onStorageChange)
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.onStorageChange)
  },
  methods: {
    onStorageChange() {
      const id = localStorage.getItem('studentId') || ''
      if (id !== this.studentId) {
        this.studentId = id
        this.exams = []
        if (id) this.fetchData()
      }
    },
    async fetchData() {
      try {
        this.loading = true
        this.error = null
        const [examRes, courseRes] = await Promise.all([
          fetch(`/api/exams/student/${this.studentId}`, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
          }),
          fetch('/api/courses')
        ])
        if (examRes.ok) this.exams = await examRes.json()
        if (courseRes.ok) this.courses = await courseRes.json()
      } catch (e) {
        this.error = 'Failed to load exam schedule.'
      } finally {
        this.loading = false
      }
    },
    courseName(courseId) {
      const c = this.courses.find(c => c.courseId === courseId)
      return c ? c.name : courseId
    },
    isUpcoming(date) {
      return new Date(date) > new Date()
    },
    formatDay(date) {
      return new Date(date).getDate()
    },
    formatMonth(date) {
      return new Date(date).toLocaleString('en', { month: 'short' }).toUpperCase()
    },
    formatDateTime(date) {
      return new Date(date).toLocaleString('en', {
        weekday: 'short', month: 'short', day: 'numeric',
        hour: '2-digit', minute: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.exam-page { min-height: 100vh; background: #fafafa; font-family: 'Segoe UI', system-ui, sans-serif; }

.exam-header { background: #fff; border-bottom: 1px solid #f1f5f9; padding: 40px 0 32px; }
.exam-header-inner { max-width: 900px; margin: 0 auto; padding: 0 32px; }
.exam-eyebrow { font-size: 11px; font-weight: 700; color: #94a3b8; letter-spacing: 1.5px; margin-bottom: 10px; }
.exam-title { font-size: 36px; font-weight: 800; color: #0f172a; margin: 0 0 10px; letter-spacing: -0.5px; }
.exam-sub { font-size: 14px; color: #64748b; line-height: 1.6; margin: 0; max-width: 480px; }

.exam-body { max-width: 900px; margin: 0 auto; padding: 32px; }

.exam-state { padding: 48px; text-align: center; font-size: 14px; color: #94a3b8; display: flex; flex-direction: column; align-items: center; gap: 12px; }
.exam-state--error { color: #dc2626; background: #fef2f2; border: 1px solid #fecaca; border-radius: 10px; }

.exam-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 14px; overflow: hidden; }
.exam-card-head { padding: 18px 24px 16px; border-bottom: 1px solid #f1f5f9; }
.exam-card-title { font-size: 15px; font-weight: 700; color: #0f172a; }
.exam-card-sub { font-size: 12px; color: #94a3b8; margin-top: 2px; }

.exam-empty { padding: 40px; text-align: center; font-size: 13px; color: #94a3b8; line-height: 1.6; }

.exam-row {
  display: flex; align-items: center; gap: 20px;
  padding: 16px 24px; border-bottom: 1px solid #f8fafc;
  transition: background 0.1s;
}
.exam-row:last-child { border-bottom: none; }
.exam-row:hover { background: #f8fafc; }

.exam-date-block {
  width: 44px; text-align: center; flex-shrink: 0;
  background: #f1f5f9; border-radius: 8px; padding: 6px 4px;
}
.exam-date-day { font-size: 18px; font-weight: 800; color: #0f172a; line-height: 1; }
.exam-date-month { font-size: 10px; font-weight: 600; color: #64748b; margin-top: 2px; letter-spacing: 0.5px; }

.exam-info { flex: 1; min-width: 0; }
.exam-course-name { font-size: 14px; font-weight: 600; color: #0f172a; }
.exam-meta { font-size: 12px; color: #64748b; margin-top: 3px; display: flex; align-items: center; gap: 6px; }
.meta-dot { color: #cbd5e1; }

.exam-location { font-size: 12px; color: #64748b; display: flex; align-items: center; gap: 4px; flex-shrink: 0; }

.exam-badge { font-size: 11px; font-weight: 600; padding: 4px 10px; border-radius: 20px; flex-shrink: 0; }
.badge-upcoming { background: #eff6ff; color: #3b82f6; }
.badge-past { background: #f1f5f9; color: #94a3b8; }
</style>
