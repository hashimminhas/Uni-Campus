<template>
  <div class="billing-container">
    <!-- Header Section -->
    <header class="page-header">
      <div class="header-content">
        <div class="breadcrumb">UNICAMPUS · STUDENT FINANCES</div>
        <h1 class="page-title">Fee & Billing Dashboard</h1>
        <p class="page-subtitle">Track your aggregated university charges, calculate tuition credits, and manage payments in a single portal.</p>
      </div>
      <div class="header-badge">
        <span class="active-badge" v-if="studentId">Active Student Account</span>
        <span class="inactive-badge" v-else>Identity Needed</span>
      </div>
    </header>

    <!-- Main Content Guarded by Login Status -->
    <div v-if="!studentId" class="auth-fallback">
      <div class="fallback-card">
        <div class="fallback-icon">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
        </div>
        <h3>Secure Access Required</h3>
        <p>Please log in using your unique Student UUID in the navigation bar above to view your academic bills, invoice history, and make payments.</p>
      </div>
    </div>

    <div v-else class="dashboard-grid">
      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Retrieving financial ledger records...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <div class="error-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
        </div>
        <div class="error-msg">
          <h4>Ledger Synchronization Error</h4>
          <p>{{ error }}</p>
          <button @click="loadAll" class="btn btn-secondary btn-sm">Try Again</button>
        </div>
      </div>

      <!-- Main Dashboard Content -->
      <div v-else class="main-dashboard">
        <!-- 1. Stats and Overview Strip -->
        <section class="overview-section">
          <div class="overview-grid">
            <div class="overview-card outstanding" :class="{ 'outstanding--zero': status.outstandingBalance <= 0 }">
              <div class="card-inner">
                <span class="card-label">Outstanding Balance</span>
                <span class="card-value">${{ formatMoney(status.outstandingBalance) }}</span>
                <span class="card-status" v-if="status.outstandingBalance > 0">Dues Pending Payment</span>
                <span class="card-status status-good" v-else>Fully Paid - Thank You!</span>
              </div>
            </div>
            <div class="overview-card">
              <div class="card-inner">
                <span class="card-label">Billed Items</span>
                <span class="card-value">{{ status.chargeCount }}</span>
                <span class="card-status">Aggregated Fees</span>
              </div>
            </div>
            <div class="overview-card">
              <div class="card-inner">
                <span class="card-label">Payments Applied</span>
                <span class="card-value">{{ status.paymentCount }}</span>
                <span class="card-status">Transaction History</span>
              </div>
            </div>
          </div>
        </section>

        <!-- 2. Interactive Calculation & Quick Tools -->
        <section class="tools-section">
          <div class="section-header">
            <h2 class="section-title">Tuition Orchestration</h2>
            <span class="section-subtitle">Tuition calculation based on current course enrollment credits ($500.00/credit).</span>
          </div>

          <div class="tools-card">
            <div class="tools-content">
              <div class="tools-details">
                <h4>Course Enrollment Tuition Invoicing</h4>
                <p>Calculates tuition fee charges in real-time by checking your current active credit hours registered in the Course Service registry. This operation validates student profile status before posting.</p>
              </div>
              <div class="tools-actions">
                <button class="btn btn-primary" @click="calculateTuition" :disabled="tuitionLoading">
                  <span v-if="tuitionLoading" class="btn-spinner"></span>
                  <span>{{ tuitionLoading ? 'Re-calculating...' : 'Verify & Calculate Tuition' }}</span>
                </button>
              </div>
            </div>

            <!-- Tuition Result Alert -->
            <transition name="fade">
              <div v-if="tuitionResult" class="result-alert">
                <div class="alert-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                </div>
                <div class="alert-text">
                  <strong>Tuition successfully billed!</strong> Registered credit load: <strong>{{ tuitionResult.totalCredits }} credits</strong>. Total calculated amount: <strong>${{ formatMoney(tuitionResult.tuitionAmount) }}</strong> has been added to outstanding charges.
                </div>
              </div>
            </transition>
          </div>
        </section>

        <!-- 3. Financial Columns Layout: Left = Charge History, Right = Payment Terminal -->
        <div class="financial-columns">
          <!-- Charges Column -->
          <section class="charges-column">
            <div class="column-header">
              <h2 class="section-title">Fee Ledger & Charges</h2>
              <span class="badge-count">{{ charges.length }} items</span>
            </div>

            <div class="table-container">
              <div v-if="charges.length === 0" class="empty-table">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                <p>No billing charges have been posted to this account yet.</p>
              </div>

              <table v-else class="ledgertable">
                <thead>
                  <tr>
                    <th>Bill Category</th>
                    <th>Details</th>
                    <th>Invoice Date</th>
                    <th class="text-right">Amount</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="c in charges" :key="c.chargeId" class="ledger-row">
                    <td>
                      <span class="badge" :class="'badge--' + c.chargeType.toLowerCase()">
                        {{ c.chargeType }}
                      </span>
                    </td>
                    <td class="ledger-desc">
                      <span class="desc-text">{{ c.description }}</span>
                      <span class="uuid-text">Ref: {{ c.chargeId.substring(0, 8) }}…</span>
                    </td>
                    <td class="ledger-date">{{ formatDate(c.createdAt) }}</td>
                    <td class="ledger-amount text-right">${{ formatMoney(c.amount) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>

          <!-- Payment Column -->
          <section class="payment-column">
            <div class="column-header">
              <h2 class="section-title">Payment Simulator</h2>
              <span class="badge-count text-blue">Gateway Active</span>
            </div>

            <div class="payment-card">
              <div class="terminal-header">
                <div class="chip-graphic"></div>
                <span class="terminal-brand">UniCampus PayTerminal™</span>
              </div>

              <form @submit.prevent="submitPayment" class="payment-form">
                <div class="form-group">
                  <label for="amountInput">Simulate Payment Amount ($)</label>
                  <div class="input-currency-wrapper">
                    <span class="currency-symbol">$</span>
                    <input
                      id="amountInput"
                      v-model.number="payForm.amount"
                      type="number"
                      step="0.01"
                      min="0.01"
                      placeholder="0.00"
                      required
                      class="form-control"
                    />
                  </div>
                </div>

                <div class="form-group">
                  <label for="methodSelect">Payment Gateway Channel</label>
                  <select id="methodSelect" v-model="payForm.method" required class="form-control select-control">
                    <option value="" disabled selected>Select method...</option>
                    <option value="CREDIT_CARD">Credit Card / Debit Card</option>
                    <option value="BANK_TRANSFER">Direct Academic Wire (Bank Transfer)</option>
                    <option value="CASH">Registrar Cashier Cash Deposit</option>
                  </select>
                </div>

                <button type="submit" class="btn btn-payment-action" :disabled="payLoading">
                  <span v-if="payLoading" class="btn-spinner"></span>
                  <span>{{ payLoading ? 'Processing Settlement...' : 'Simulate Payment Settlement' }}</span>
                </button>
              </form>

              <!-- Payment Success Alert -->
              <transition name="slide-up">
                <div v-if="payResult" class="payment-alert">
                  <div class="pay-alert-title">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                    <span>Settlement Approved!</span>
                  </div>
                  <div class="pay-alert-details">
                    <p>Amount Settled: <strong>${{ formatMoney(payResult.amount) }}</strong> via {{ payResult.method }}</p>
                    <p>Remaining Outstanding Balance: <strong>${{ formatMoney(payResult.newBalance) }}</strong></p>
                    <span class="pay-alert-id">Auth Code: {{ payResult.paymentId }}</span>
                  </div>
                </div>
              </transition>
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { apiUrl } from '../api'

export default {
  name: 'BillingView',
  data() {
    return {
      studentId: localStorage.getItem('studentId') || '',
      token: localStorage.getItem('token') || '',
      loading: true,
      error: null,
      status: { outstandingBalance: 0, chargeCount: 0, paymentCount: 0 },
      charges: [],
      // Tuition calculator variables
      tuitionLoading: false,
      tuitionResult: null,
      // Payment terminal variables
      payForm: { amount: null, method: '' },
      payLoading: false,
      payResult: null,
      // Auto storage synchronizer interval
      storageInterval: null,
    }
  },
  mounted() {
    if (this.studentId) {
      this.loadAll();
    } else {
      this.loading = false;
    }
    // Watch for login state changes on current tab dynamically
    this.storageInterval = setInterval(this.checkStorage, 1000);
  },
  beforeUnmount() {
    if (this.storageInterval) {
      clearInterval(this.storageInterval);
    }
  },
  methods: {
    checkStorage() {
      const currentStudentId = localStorage.getItem('studentId') || '';
      const currentToken = localStorage.getItem('token') || '';
      
      if (this.studentId !== currentStudentId) {
        this.studentId = currentStudentId;
        this.token = currentToken;
        this.tuitionResult = null;
        this.payResult = null;
        this.payForm = { amount: null, method: '' };
        
        if (this.studentId) {
          this.loadAll();
        } else {
          this.status = { outstandingBalance: 0, chargeCount: 0, paymentCount: 0 };
          this.charges = [];
          this.loading = false;
        }
      }
    },
    async loadAll() {
      this.loading = true;
      this.error = null;
      try {
        const headers = {};
        if (this.token) {
          headers['Authorization'] = `Bearer ${this.token}`;
        }

        const [statusRes, chargesRes] = await Promise.all([
          fetch(apiUrl(`/api/billing/${this.studentId}/status`), { headers }),
          fetch(apiUrl(`/api/billing/${this.studentId}/charges`), { headers })
        ]);

        if (statusRes.ok) {
          this.status = await statusRes.json();
        } else if (statusRes.status === 404) {
          // If no billing account exists in DB yet, display zero balance gracefully (it will be created on charge/tuition call)
          this.status = { studentId: this.studentId, outstandingBalance: 0, chargeCount: 0, paymentCount: 0 };
        } else {
          throw new Error(`Failed to load billing status. HTTP code: ${statusRes.status}`);
        }

        if (chargesRes.ok) {
          const rawCharges = await chargesRes.json();
          if (Array.isArray(rawCharges)) {
            // Sort charges with latest first
            this.charges = rawCharges.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
          } else {
            console.error('Expected array of charges, but got:', rawCharges);
            throw new Error(rawCharges.message || 'Failed to parse billing charge records.');
          }
        } else if (chargesRes.status === 404) {
          this.charges = [];
        } else {
          throw new Error(`Failed to fetch student charge list. HTTP code: ${chargesRes.status}`);
        }
      } catch (err) {
        this.error = err.message || 'An unexpected error occurred while communicating with the Billing API.';
        console.error('Error fetching billing data:', err);
      } finally {
        this.loading = false;
      }
    },
    async calculateTuition() {
      this.tuitionLoading = true;
      this.tuitionResult = null;
      try {
        const headers = {
          'Content-Type': 'application/json'
        };
        if (this.token) {
          headers['Authorization'] = `Bearer ${this.token}`;
        }

        const response = await fetch(apiUrl(`/api/billing/${this.studentId}/tuition`), {
          method: 'POST',
          headers
        });

        if (!response.ok) {
          let errorMsg = 'Failed to request tuition assessment calculation';
          try {
            const errData = await response.json();
            if (errData.message) errorMsg = errData.message;
          } catch (e) {
            const rawText = await response.text();
            if (rawText) errorMsg = rawText;
          }
          throw new Error(errorMsg);
        }

        this.tuitionResult = await response.json();
        // Reload all data so that ledger, outstanding balance, and billed statistics are updated
        await this.loadAll();
      } catch (err) {
        alert(`Tuition Invoicing Failed:\n${err.message}`);
        console.error('Error calculating tuition:', err);
      } finally {
        this.tuitionLoading = false;
      }
    },
    async submitPayment() {
      if (!this.payForm.amount || !this.payForm.method) return;
      
      this.payLoading = true;
      this.payResult = null;
      try {
        const headers = {
          'Content-Type': 'application/json'
        };
        if (this.token) {
          headers['Authorization'] = `Bearer ${this.token}`;
        }

        const response = await fetch(apiUrl(`/api/billing/${this.studentId}/pay`), {
          method: 'POST',
          headers,
          body: JSON.stringify(this.payForm)
        });

        if (!response.ok) {
          let errorMsg = 'Payment settlement denied';
          try {
            const errData = await response.json();
            if (errData.message) errorMsg = errData.message;
          } catch (e) {
            const rawText = await response.text();
            if (rawText) errorMsg = rawText;
          }
          throw new Error(errorMsg);
        }

        this.payResult = await response.json();
        // Reset payment form inputs
        this.payForm = { amount: null, method: '' };
        // Sync stats
        await this.loadAll();
      } catch (err) {
        alert(`Payment Emulation Failed:\n${err.message}`);
        console.error('Error executing payment:', err);
      } finally {
        this.payLoading = false;
      }
    },
    formatMoney(val) {
      return Number(val || 0).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  }
}
</script>

<style scoped>
.billing-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 40px 24px;
  font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
  color: #334155;
  background: #fafafa;
  min-height: calc(100vh - 52px);
  box-sizing: border-box;
}

/* Page Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e2e8f0;
}
.breadcrumb {
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
  letter-spacing: 1.5px;
  margin-bottom: 6px;
}
.page-title {
  font-size: 28px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.5px;
  margin: 0 0 6px 0;
}
.page-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  max-width: 650px;
  line-height: 1.5;
}
.header-badge {
  flex-shrink: 0;
  margin-top: 10px;
}
.active-badge {
  background: #f0fdf4;
  color: #16a34a;
  font-size: 11px;
  font-weight: 700;
  padding: 6px 12px;
  border-radius: 9999px;
  border: 1px solid #dcfce7;
  text-transform: uppercase;
}
.inactive-badge {
  background: #f1f5f9;
  color: #64748b;
  font-size: 11px;
  font-weight: 700;
  padding: 6px 12px;
  border-radius: 9999px;
  border: 1px solid #e2e8f0;
  text-transform: uppercase;
}

/* Fallback Guard */
.auth-fallback {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px 20px;
}
.fallback-card {
  max-width: 480px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
}
.fallback-icon {
  width: 72px;
  height: 72px;
  background: #f8fafc;
  color: #64748b;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px auto;
  border: 1px solid #e2e8f0;
}
.fallback-card h3 {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 12px 0;
}
.fallback-card p {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0;
}

/* Spinner / States */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #64748b;
}
.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e2e8f0;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-state {
  display: flex;
  gap: 16px;
  background: #fef2f2;
  border: 1px solid #fee2e2;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  align-items: flex-start;
}
.error-icon {
  color: #ef4444;
  flex-shrink: 0;
  background: #fff;
  padding: 8px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.error-msg h4 {
  font-size: 15px;
  font-weight: 700;
  color: #991b1b;
  margin: 0 0 4px 0;
}
.error-msg p {
  font-size: 13px;
  color: #b91c1c;
  margin: 0 0 12px 0;
  line-height: 1.5;
}

/* Dashboard Grid */
.dashboard-grid {
  display: flex;
  flex-direction: column;
  gap: 32px;
}
.main-dashboard {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* Overview Strip */
.overview-section {
  width: 100%;
}
.overview-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.overview-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}
.overview-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
  border-color: #cbd5e1;
}
.overview-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: #64748b;
}
.overview-card.outstanding::before {
  background: #ea580c;
}
.overview-card.outstanding {
  background: #fff7ed;
  border-color: #ffedd5;
}
.overview-card.outstanding--zero {
  background: #f0fdf4;
  border-color: #dcfce7;
}
.overview-card.outstanding--zero::before {
  background: #16a34a;
}
.card-inner {
  display: flex;
  flex-direction: column;
}
.card-label {
  font-size: 11px;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 8px;
}
.overview-card.outstanding .card-label {
  color: #c2410c;
}
.overview-card.outstanding--zero .card-label {
  color: #15803d;
}
.card-value {
  font-size: 32px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -1px;
  line-height: 1;
  margin-bottom: 6px;
}
.overview-card.outstanding .card-value {
  color: #9a3412;
}
.overview-card.outstanding--zero .card-value {
  color: #166534;
}
.card-status {
  font-size: 12px;
  color: #64748b;
}
.overview-card.outstanding .card-status {
  color: #ea580c;
  font-weight: 600;
}
.overview-card.outstanding--zero .card-status.status-good {
  color: #16a34a;
  font-weight: 600;
}

/* Tools Section */
.tools-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.section-header {
  margin-bottom: 4px;
}
.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 2px 0;
}
.section-subtitle {
  font-size: 12px;
  color: #64748b;
}
.tools-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
}
.tools-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 40px;
}
.tools-details h4 {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 6px 0;
}
.tools-details p {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
  margin: 0;
}
.tools-actions {
  flex-shrink: 0;
}

/* Result Alert */
.result-alert {
  display: flex;
  gap: 12px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 10px;
  padding: 16px;
  margin-top: 20px;
  align-items: center;
}
.alert-icon {
  color: #16a34a;
  flex-shrink: 0;
}
.alert-text {
  font-size: 13px;
  color: #14532d;
  line-height: 1.4;
}

/* Table Column */
.financial-columns {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 32px;
  align-items: start;
}
.column-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #e2e8f0;
}
.badge-count {
  background: #e2e8f0;
  color: #475569;
  font-size: 11px;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 9999px;
}
.badge-count.text-blue {
  background: #eff6ff;
  color: #2563eb;
}
.table-container {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.02);
}
.empty-table {
  padding: 60px 20px;
  text-align: center;
  color: #94a3b8;
}
.empty-table svg {
  margin: 0 auto 12px auto;
  opacity: 0.6;
}
.empty-table p {
  font-size: 13px;
  margin: 0;
}

.ledgertable {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 13px;
}
.ledgertable th {
  background: #f8fafc;
  color: #475569;
  font-weight: 700;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: 14px 16px;
  border-bottom: 1px solid #e2e8f0;
}
.ledgertable td {
  padding: 16px;
  border-bottom: 1px solid #f1f5f9;
  vertical-align: middle;
}
.ledger-row:last-child td {
  border-bottom: none;
}
.ledger-desc {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.desc-text {
  font-weight: 600;
  color: #334155;
}
.uuid-text {
  font-size: 11px;
  color: #94a3b8;
  font-family: monospace;
}
.ledger-date {
  color: #64748b;
  font-size: 12px;
  white-space: nowrap;
}
.ledger-amount {
  font-weight: 700;
  color: #0f172a;
  font-size: 14px;
}
.text-right {
  text-align: right;
}

/* Badges */
.badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}
.badge--tuition {
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #dbeafe;
}
.badge--housing {
  background: #fdf2f8;
  color: #db2777;
  border: 1px solid #fce7f3;
}
.badge--mealplan {
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #dcfce7;
}
.badge--fine {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fee2e2;
}

/* Terminal Card (Payment simulation) */
.payment-card {
  background: #1e293b;
  border-radius: 16px;
  padding: 24px;
  color: #f1f5f9;
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.15);
  border: 1px solid #334155;
  position: relative;
}
.terminal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  border-bottom: 1px solid #334155;
  padding-bottom: 14px;
}
.chip-graphic {
  width: 32px;
  height: 24px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border-radius: 4px;
  border: 1px solid #f1f5f9;
  opacity: 0.85;
}
.terminal-brand {
  font-size: 10px;
  font-weight: 800;
  text-transform: uppercase;
  color: #94a3b8;
  letter-spacing: 1px;
}
.payment-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.form-group label {
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.input-currency-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}
.currency-symbol {
  position: absolute;
  left: 14px;
  font-size: 16px;
  font-weight: 700;
  color: #94a3b8;
}
.form-control {
  background: #0f172a;
  border: 1px solid #334155;
  border-radius: 10px;
  padding: 12px 14px;
  color: #fff;
  font-size: 14px;
  width: 100%;
  box-sizing: border-box;
  outline: none;
  transition: border-color 0.15s;
}
.form-control:focus {
  border-color: #3b82f6;
}
.input-currency-wrapper .form-control {
  padding-left: 28px;
  font-size: 16px;
  font-weight: 700;
}
.select-control {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%2394a3b8' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
  padding-right: 40px;
}

.payment-alert {
  background: #064e3b;
  border: 1px solid #059669;
  border-radius: 10px;
  padding: 16px;
  margin-top: 20px;
  color: #a7f3d0;
}
.pay-alert-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  font-size: 13px;
  margin-bottom: 6px;
}
.pay-alert-details {
  font-size: 12px;
  line-height: 1.5;
}
.pay-alert-details p {
  margin: 2px 0;
}
.pay-alert-id {
  display: block;
  font-family: monospace;
  font-size: 10px;
  color: #34d399;
  margin-top: 6px;
}

/* Button UI */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  border: none;
  transition: all 0.15s;
  white-space: nowrap;
}
.btn-sm {
  padding: 6px 12px;
  font-size: 11px;
  border-radius: 6px;
}
.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.btn-primary {
  background: #0f172a;
  color: #fff;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.btn-primary:hover:not(:disabled) {
  background: #1e293b;
}
.btn-secondary {
  background: #e2e8f0;
  color: #475569;
}
.btn-secondary:hover:not(:disabled) {
  background: #cbd5e1;
}
.btn-payment-action {
  background: #2563eb;
  color: #fff;
  font-size: 14px;
  padding: 12px;
  width: 100%;
  box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.2);
}
.btn-payment-action:hover:not(:disabled) {
  background: #3b82f6;
  box-shadow: 0 10px 15px -3px rgba(37, 99, 235, 0.3);
}

.btn-spinner {
  width: 12px;
  height: 12px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

/* Animations */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.slide-up-enter-active {
  transition: all 0.3s ease-out;
}
.slide-up-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

/* Dark Mode Theme Overrides */
:global(.dark) .billing-container {
  background: #0f172a;
  color: #cbd5e1;
}
:global(.dark) .page-header {
  border-color: #1e293b;
}
:global(.dark) .page-title {
  color: #f1f5f9;
}
:global(.dark) .page-subtitle {
  color: #94a3b8;
}
:global(.dark) .fallback-card {
  background: #1e293b;
  border-color: #334155;
}
:global(.dark) .fallback-icon {
  background: #0f172a;
  border-color: #334155;
  color: #94a3b8;
}
:global(.dark) .fallback-card h3 {
  color: #f1f5f9;
}
:global(.dark) .fallback-card p {
  color: #94a3b8;
}
:global(.dark) .overview-card {
  background: #1e293b;
  border-color: #334155;
}
:global(.dark) .overview-card.outstanding {
  background: #2d1e10;
  border-color: #452a0a;
}
:global(.dark) .overview-card.outstanding--zero {
  background: #062f1c;
  border-color: #044b2c;
}
:global(.dark) .overview-card.outstanding .card-label {
  color: #f97316;
}
:global(.dark) .overview-card.outstanding--zero .card-label {
  color: #4ade80;
}
:global(.dark) .card-value {
  color: #f1f5f9;
}
:global(.dark) .overview-card.outstanding .card-value {
  color: #ffedd5;
}
:global(.dark) .overview-card.outstanding--zero .card-value {
  color: #dcfce7;
}
:global(.dark) .tools-card {
  background: #1e293b;
  border-color: #334155;
}
:global(.dark) .tools-details h4 {
  color: #f1f5f9;
}
:global(.dark) .tools-details p {
  color: #94a3b8;
}
:global(.dark) .btn-primary {
  background: #f1f5f9;
  color: #0f172a;
}
:global(.dark) .btn-primary:hover:not(:disabled) {
  background: #e2e8f0;
}
:global(.dark) .result-alert {
  background: #062f1c;
  border-color: #044b2c;
  color: #a7f3d0;
}
:global(.dark) .result-alert strong {
  color: #fff;
}
:global(.dark) .column-header {
  border-color: #334155;
}
:global(.dark) .section-title {
  color: #f1f5f9;
}
:global(.dark) .badge-count {
  background: #334155;
  color: #cbd5e1;
}
:global(.dark) .badge-count.text-blue {
  background: #1e3a8a;
  color: #93c5fd;
}
:global(.dark) .table-container {
  background: #1e293b;
  border-color: #334155;
}
:global(.dark) .ledgertable th {
  background: #0f172a;
  color: #94a3b8;
  border-color: #334155;
}
:global(.dark) .ledgertable td {
  border-color: #334155;
}
:global(.dark) .desc-text {
  color: #e2e8f0;
}
:global(.dark) .ledger-date {
  color: #94a3b8;
}
:global(.dark) .ledger-amount {
  color: #f1f5f9;
}
:global(.dark) .badge--tuition {
  background: #1e3a8a;
  color: #93c5fd;
  border-color: #2563eb;
}
:global(.dark) .badge--housing {
  background: #4c0519;
  color: #fbcfe8;
  border-color: #db2777;
}
:global(.dark) .badge--mealplan {
  background: #064e3b;
  color: #a7f3d0;
  border-color: #16a34a;
}
:global(.dark) .badge--fine {
  background: #7f1d1d;
  color: #fecaca;
  border-color: #dc2626;
}

/* Small Screens / Responsive styling */
@media (max-width: 900px) {
  .financial-columns {
    grid-template-columns: 1fr;
    gap: 32px;
  }
  .overview-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  .tools-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  .tools-actions {
    width: 100%;
  }
  .btn-primary {
    width: 100%;
  }
  .page-header {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
