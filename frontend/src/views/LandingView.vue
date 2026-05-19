<template>
  <div class="landing">

    <!-- Hero -->
    <section class="hero">
      <div class="hero-inner">
        <div class="breadcrumb">UNICAMPUS · STUDENT PORTAL</div>
        <h1 class="hero-title">Everything you need to run your academic life.</h1>
        <p class="hero-sub">Records, courses, library, hostel, fees and exams a single calm workspace for students and the registrar's office.</p>
        <div class="hero-actions">
          <router-link to="/students" class="btn-primary">Explore services</router-link>
          <router-link to="/library" class="btn-ghost">Visit the library</router-link>
        </div>

      </div>
    </section>

    <!-- Services -->
    <section class="services">
      <div class="services-header">
        <div>
          <div class="section-label">SERVICES</div>
          <h2 class="section-title">Campus services</h2>
        </div>
        <span class="section-meta">Seven modules · one account</span>
      </div>
      <div class="services-grid">

        <router-link to="/students" class="service-card">
          <div class="service-top">
            <div class="service-icon-wrap service-icon-wrap--slate">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </div>
            <svg class="arrow-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </div>
          <div class="service-name">Student Registry</div>
          <div class="service-desc">Enrolment records, academic status and student credentials.</div>
        </router-link>

        <router-link to="/courses" class="service-card">
          <div class="service-top">
            <div class="service-icon-wrap service-icon-wrap--blue">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
            </div>
            <svg class="arrow-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </div>
          <div class="service-name">Course Enrollment</div>
          <div class="service-desc">Browse the course catalogue, enrol and track academic progress.</div>
        </router-link>

        <router-link to="/library" class="service-card">
          <div class="service-top">
            <div class="service-icon-wrap service-icon-wrap--green">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
            </div>
            <svg class="arrow-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </div>
          <div class="service-name">Campus Library</div>
          <div class="service-desc">Search the catalogue, borrow books and manage due-date reminders.</div>
        </router-link>

        <router-link to="/dormitory" class="service-card">
          <div class="service-top">
            <div class="service-icon-wrap service-icon-wrap--orange">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
            </div>
            <svg class="arrow-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </div>
          <div class="service-name">Dormitory Services</div>
          <div class="service-desc">Apply for on-campus housing, manage room assignments and stay records.</div>
        </router-link>

        <router-link to="/billing" class="service-card">
          <div class="service-top">
            <div class="service-icon-wrap service-icon-wrap--purple">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
            </div>
            <svg class="arrow-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </div>
          <div class="service-name">Fee & Billing</div>
          <div class="service-desc">View tuition invoices, pay dues and download official receipts.</div>
        </router-link>

        <router-link to="/mealplan" class="service-card">
          <div class="service-top">
            <div class="service-icon-wrap service-icon-wrap--teal">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 2v7c0 1.1.9 2 2 2h4a2 2 0 0 0 2-2V2"/><path d="M7 2v20"/><path d="M21 15V2a5 5 0 0 0-5 5v6c0 1.1.9 2 2 2h3zm0 0v7"/></svg>
            </div>
            <svg class="arrow-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </div>
          <div class="service-name">Meal Plan</div>
          <div class="service-desc">Subscribe to campus dining plans, manage meal credits and canteen access.</div>
        </router-link>

        <router-link to="/exam" class="service-card">
          <div class="service-top">
            <div class="service-icon-wrap service-icon-wrap--red">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/><line x1="9" y1="12" x2="15" y2="12"/><line x1="9" y1="16" x2="11" y2="16"/></svg>
            </div>
            <svg class="arrow-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </div>
          <div class="service-name">Examination Portal</div>
          <div class="service-desc">Check schedules, view results and submit grade appeals.</div>
        </router-link>

      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="footer-left">
        <span class="footer-brand">UNICAMPUS</span>
        <span class="footer-copy">Office of the Registrar · Academic Year 2025/26</span>
      </div>
      <div class="footer-links">
        <a href="#">Privacy</a>
        <a href="#">Accessibility</a>
        <a href="#">Contact</a>
      </div>
    </footer>

  </div>
</template>

<script>
export default { name: 'LandingView' }
</script>

<style scoped>
.landing { min-height: 100vh; background: #fafafa; font-family: 'Segoe UI', system-ui, sans-serif; display: flex; flex-direction: column; }

/* Hero */
.hero { background: #fff; border-bottom: 1px solid #f1f5f9; padding: 56px 0 48px; }
.hero-inner { max-width: 1100px; margin: 0 auto; padding: 0 32px; }
.breadcrumb { font-size: 11px; font-weight: 700; color: #94a3b8; letter-spacing: 1.5px; margin-bottom: 20px; }
.hero-title { font-size: 42px; font-weight: 800; color: #0f172a; line-height: 1.15; letter-spacing: -1px; margin: 0 0 14px; max-width: 600px; }
.hero-sub { font-size: 15px; color: #64748b; line-height: 1.7; margin: 0 0 28px; max-width: 520px; }
.hero-actions { display: flex; gap: 12px; align-items: center; margin-bottom: 40px; }
.btn-primary { background: #0f172a; color: #fff; padding: 11px 22px; border-radius: 8px; text-decoration: none; font-size: 14px; font-weight: 600; transition: background 0.15s; }
.btn-primary:hover { background: #1e293b; }
.btn-ghost { color: #475569; padding: 11px 18px; border-radius: 8px; text-decoration: none; font-size: 14px; font-weight: 500; border: 1px solid #e2e8f0; transition: all 0.15s; }
.btn-ghost:hover { background: #f1f5f9; border-color: #cbd5e1; }


/* Services */
.services { max-width: 1100px; margin: 0 auto; padding: 48px 32px 48px; flex: 1; }
.services-header { display: flex; align-items: flex-end; justify-content: space-between; margin-bottom: 20px; }
.section-label { font-size: 11px; font-weight: 700; color: #94a3b8; letter-spacing: 1.5px; margin-bottom: 4px; }
.section-title { font-size: 22px; font-weight: 700; color: #0f172a; margin: 0; }
.section-meta { font-size: 13px; color: #94a3b8; }

.services-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; }
.service-card {
  background: #fff; border: 1px solid #e2e8f0; border-radius: 12px;
  padding: 20px; text-decoration: none; display: flex; flex-direction: column; gap: 10px;
  transition: box-shadow 0.15s, border-color 0.15s;
}
.service-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.07); border-color: #cbd5e1; }
.service-top { display: flex; justify-content: space-between; align-items: flex-start; }
.arrow-icon { color: #cbd5e1; margin-top: 2px; }
.service-card:hover .arrow-icon { color: #94a3b8; }

/* Icon wraps */
.service-icon-wrap { width: 38px; height: 38px; border-radius: 9px; display: flex; align-items: center; justify-content: center; }
.service-icon-wrap--slate  { background: #f1f5f9; color: #475569; }
.service-icon-wrap--blue   { background: #eff6ff; color: #3b82f6; }
.service-icon-wrap--green  { background: #f0fdf4; color: #16a34a; }
.service-icon-wrap--orange { background: #fff7ed; color: #ea580c; }
.service-icon-wrap--purple { background: #faf5ff; color: #9333ea; }
.service-icon-wrap--red    { background: #fef2f2; color: #dc2626; }
.service-icon-wrap--teal   { background: #f0fdfa; color: #0d9488; }

.service-name { font-size: 15px; font-weight: 700; color: #0f172a; }
.service-desc { font-size: 13px; color: #64748b; line-height: 1.4; }

/* Footer */
.footer { border-top: 1px solid #e2e8f0; padding: 20px 32px; display: flex; justify-content: space-between; align-items: center; max-width: 1100px; margin: 0 auto; width: 100%; box-sizing: border-box; }
.footer-brand { font-size: 13px; font-weight: 800; color: #0f172a; letter-spacing: 1px; margin-right: 16px; }
.footer-copy { font-size: 12px; color: #94a3b8; }
.footer-links { display: flex; gap: 20px; }
.footer-links a { font-size: 12px; color: #94a3b8; text-decoration: none; }
.footer-links a:hover { color: #475569; }
</style>
