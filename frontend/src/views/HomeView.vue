<template>
  <div class="courses-page">

    <!-- Header -->
    <div class="courses-header">
      <div class="courses-header-inner">
        <div class="courses-eyebrow">ACADEMICS</div>
        <h1 class="courses-title">Courses & Enrolment</h1>
        <p class="courses-sub">Browse the catalog by department, view seat availability, and enrol in courses for the current semester.</p>
      </div>
    </div>

    <div class="courses-body">
      <div v-if="loading" class="courses-state">Loading courses…</div>
      <div v-else-if="error" class="courses-state courses-state--error">{{ error }}</div>

      <div v-else>
        <div v-if="courses.length === 0" class="courses-state">No courses available.</div>
        <div v-else class="courses-grid">
          <div v-for="course in courses" :key="course.courseId" class="course-card">

            <!-- Card top -->
            <div class="card-top">
              <div class="card-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/>
                </svg>
              </div>
              <span class="card-semester">{{ course.semester }}</span>
            </div>

            <!-- Name & meta -->
            <div class="card-name">{{ course.name }}</div>
            <div class="card-meta">{{ course.instructor }} · {{ course.credits }} credits</div>

            <!-- Details -->
            <div class="card-details">
              <div class="card-detail-row">
                <span class="card-detail-label">Status</span>
                <span class="card-detail-value" :class="course.status === 'OPEN' ? 'status-open' : 'status-closed'">{{ course.status }}</span>
              </div>
              <div class="card-detail-row">
                <span class="card-detail-label">Seats left</span>
                <span class="card-detail-value" :class="seatsLeft(course) <= 5 ? 'seats-low' : ''">{{ seatsLeft(course) }}</span>
              </div>
            </div>

            <!-- Action -->
            <button v-if="isEnrolled(course.courseId)" class="card-btn card-btn--drop" @click="dropCourse(course.courseId)">
              Drop Course
            </button>
            <button v-else-if="course.status === 'OPEN' && seatsLeft(course) > 0" class="card-btn card-btn--enrol" @click="enrollCourse(course.courseId)">
              + Enrol
            </button>
            <button v-else class="card-btn card-btn--unavail" disabled>
              {{ course.status !== 'OPEN' ? course.status : 'Course Full' }}
            </button>

          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import { apiUrl } from '../api'

export default {
  name: 'HomeView',
  data() {
    return {
      courses: [],
      enrolledCourseIds: [],
      loading: true,
      error: null,
    }
  },
  mounted() {
    this.fetchCourses()
    window.addEventListener('storage', this.onStorageChange)
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.onStorageChange)
  },
  methods: {
    onStorageChange() {
      this.fetchEnrolledCourses()
    },
    seatsLeft(course) {
      return Math.max(0, course.capacity - course.enrolledCount)
    },
    isEnrolled(courseId) {
      return this.enrolledCourseIds.includes(courseId)
    },
    async fetchCourses() {
      try {
        this.loading = true
        this.error = null
        const res = await fetch(apiUrl('/api/courses'))
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        this.courses = await res.json()
        await this.fetchEnrolledCourses()
      } catch (e) {
        this.error = e.message || 'Failed to load courses'
      } finally {
        this.loading = false
      }
    },
    async fetchEnrolledCourses() {
      const studentId = localStorage.getItem('studentId')
      if (!studentId) { this.enrolledCourseIds = []; return }
      try {
        const res = await fetch(apiUrl(`/api/courses/student/${studentId}`), {
          headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        })
        if (res.ok) {
          const data = await res.json()
          this.enrolledCourseIds = data.map(c => c.courseId)
        }
      } catch (e) {}
    },
    async dropCourse(courseId) {
      const studentId = localStorage.getItem('studentId')
      if (!studentId) return
      if (!confirm('Are you sure you want to drop this course?')) return
      try {
        const res = await fetch(apiUrl(`/api/courses/${courseId}/enroll/${studentId}`), {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        })
        if (!res.ok) {
          const err = await res.json().catch(() => ({}))
          throw new Error(err.message || `Error ${res.status}`)
        }
        await this.fetchCourses()
      } catch (e) {
        alert('Drop failed: ' + e.message)
      }
    },
    async enrollCourse(courseId) {
      const studentId = localStorage.getItem('studentId')
      if (!studentId) { alert('Please log in first to enrol in a course.'); return }
      try {
        const res = await fetch(apiUrl(`/api/courses/${courseId}/enroll`), {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
          body: JSON.stringify({ studentId })
        })
        if (!res.ok) {
          const err = await res.json().catch(() => ({}))
          throw new Error(err.message || `Error ${res.status}`)
        }
        await this.fetchCourses()
      } catch (e) {
        alert('Enrolment failed: ' + e.message)
      }
    }
  }
}
</script>

<style scoped>
.courses-page { min-height: 100vh; background: #fafafa; font-family: 'Segoe UI', system-ui, sans-serif; }

/* Header */
.courses-header { background: #fff; border-bottom: 1px solid #f1f5f9; padding: 40px 0 32px; }
.courses-header-inner { max-width: 1100px; margin: 0 auto; padding: 0 32px; }
.courses-eyebrow { font-size: 11px; font-weight: 700; color: #94a3b8; letter-spacing: 1.5px; margin-bottom: 10px; }
.courses-title { font-size: 36px; font-weight: 800; color: #0f172a; margin: 0 0 10px; letter-spacing: -0.5px; }
.courses-sub { font-size: 14px; color: #64748b; line-height: 1.6; margin: 0; max-width: 500px; }

/* Body */
.courses-body { max-width: 1100px; margin: 0 auto; padding: 32px; }

.courses-state { padding: 48px; text-align: center; font-size: 14px; color: #94a3b8; }
.courses-state--error { color: #dc2626; background: #fef2f2; border: 1px solid #fecaca; border-radius: 10px; }

/* Grid */
.courses-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }

/* Card */
.course-card {
  background: #fff; border: 1px solid #e2e8f0; border-radius: 14px;
  padding: 20px; display: flex; flex-direction: column; gap: 10px;
  transition: box-shadow 0.15s, border-color 0.15s;
}
.course-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.07); border-color: #cbd5e1; }

.card-top { display: flex; justify-content: space-between; align-items: center; }
.card-icon { width: 36px; height: 36px; background: #f1f5f9; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #64748b; }
.card-semester { font-size: 11px; font-weight: 600; color: #94a3b8; letter-spacing: 0.5px; }

.card-name { font-size: 16px; font-weight: 700; color: #0f172a; line-height: 1.3; }
.card-meta { font-size: 12px; color: #64748b; }

.card-details { display: flex; flex-direction: column; gap: 6px; margin: 4px 0; }
.card-detail-row { display: flex; justify-content: space-between; align-items: center; }
.card-detail-label { font-size: 12px; color: #94a3b8; }
.card-detail-value { font-size: 12px; font-weight: 600; color: #475569; }
.status-open { color: #16a34a; }
.status-closed { color: #dc2626; }
.seats-low { color: #ea580c; }

/* Buttons */
.card-btn {
  margin-top: auto; width: 100%; padding: 10px; border-radius: 8px; border: none;
  font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.15s;
  display: flex; align-items: center; justify-content: center; gap: 6px;
}
.card-btn--enrol { background: #0f172a; color: #fff; }
.card-btn--enrol:hover { background: #1e293b; }
.card-btn--enrolled { background: #f1f5f9; color: #64748b; cursor: default; }
.card-btn--drop { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
.card-btn--drop:hover { background: #fee2e2; border-color: #fca5a5; }
.card-btn--unavail { background: #f1f5f9; color: #94a3b8; cursor: not-allowed; }
</style>
