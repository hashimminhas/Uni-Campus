<template>
  <main class="container">
    <div class="hero">
      <h1>University Meal Plans</h1>
      <p>Fuel your studies with our flexible dining options.</p>
    </div>

    <!-- Student Subscription Section -->
    <section v-if="studentId" class="subscription-section">
      <div class="section-header">
        <h2>My Meal Plan</h2>
        <span class="badge" :class="isEligible ? 'eligible' : 'ineligible'">
          {{ isEligible ? 'Eligible to Subscribe' : 'Subscription Active' }}
        </span>
      </div>

      <div v-if="loadingSub" class="loading-state">
        <div class="spinner"></div>
        <p>Loading your subscription...</p>
      </div>

      <div v-else-if="activeSubscription" class="active-sub-card">
        <div class="card-content">
          <div class="plan-header">
            <div class="plan-name">{{ activeSubscription.planName }}</div>
            <div class="status-pill active">ACTIVE</div>
          </div>
          <div class="sub-details">
            <p><span>Subscribed On:</span> {{ formatDate(activeSubscription.startDate) }}</p>
            <p><span>Student ID:</span> {{ activeSubscription.studentId }}</p>
          </div>
          <button @click="cancelSubscription(activeSubscription.subscriptionId)" class="btn-cancel" :disabled="processing">
            {{ processing ? 'Processing...' : 'Cancel Subscription' }}
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <p>You are not currently subscribed to any meal plan.</p>
      </div>
    </section>

    <!-- Plans Catalog Section -->
    <section class="catalog-section">
      <div class="section-header">
        <h2>Available Plans</h2>
      </div>

      <div v-if="loadingPlans" class="loading-state">
        <div class="spinner"></div>
        <p>Loading available plans...</p>
      </div>

      <div v-else class="plans-grid">
        <div v-for="plan in plans" :key="plan.planId" class="plan-card">
          <div class="plan-info">
            <h3>{{ plan.name }}</h3>
            <div class="meals-badge">{{ plan.mealsPerWeek }} Meals / Week</div>
            <p class="price">${{ plan.price }} <small>/ semester</small></p>
            <p class="semester">Valid for: {{ plan.semester }}</p>
          </div>
          <div class="plan-actions">
            <button 
              v-if="isEligible && studentId" 
              @click="subscribe(plan)" 
              class="btn-subscribe"
              :disabled="processing"
            >
              {{ processing ? 'Processing...' : 'Subscribe Now' }}
            </button>
            <button v-else-if="!studentId" class="btn-disabled" disabled>Login to Subscribe</button>
            <button v-else class="btn-disabled" disabled>Already Subscribed</button>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script>
export default {
  name: 'MealPlanView',
  data() {
    return {
      studentId: localStorage.getItem('studentId') || '',
      plans: [],
      activeSubscription: null,
      isEligible: false,
      loadingPlans: true,
      loadingSub: false,
      processing: false,
      storageInterval: null
    }
  },
  mounted() {
    this.fetchPlans();
    if (this.studentId) {
      this.checkStatus();
    }
    this.storageInterval = setInterval(this.checkStorage, 1000);
  },
  beforeUnmount() {
    if (this.storageInterval) clearInterval(this.storageInterval);
  },
  methods: {
    checkStorage() {
      const currentId = localStorage.getItem('studentId') || '';
      if (this.studentId !== currentId) {
        this.studentId = currentId;
        if (this.studentId) {
          this.checkStatus();
        } else {
          this.activeSubscription = null;
          this.isEligible = false;
        }
      }
    },
    async fetchPlans() {
      this.loadingPlans = true;
      try {
        const response = await fetch('/api/meal-plan/plans');
        if (response.ok) {
          this.plans = await response.json();
        }
      } catch (error) {
        console.error('Error fetching plans:', error);
      } finally {
        this.loadingPlans = false;
      }
    },
    async checkStatus() {
      if (!this.studentId) return;
      this.loadingSub = true;
      try {
        // Check eligibility
        const eligRes = await fetch(`/api/meal-plan/students/${this.studentId}/eligibility`);
        if (eligRes.ok) {
          this.isEligible = await eligRes.json();
        }

        // Get active subscription
        const subRes = await fetch(`/api/meal-plan/subscriptions/student/${this.studentId}`);
        if (subRes.ok) {
          const subs = await subRes.json();
          this.activeSubscription = subs.find(s => s.status === 'ACTIVE') || null;
        }
      } catch (error) {
        console.error('Error checking status:', error);
      } finally {
        this.loadingSub = false;
      }
    },
    async subscribe(plan) {
      if (!confirm(`Subscribe to ${plan.name} for $${plan.price}?`)) return;
      
      this.processing = true;
      try {
        const response = await fetch(`/api/meal-plan/plans/${plan.planId}/subscribe`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ studentId: this.studentId })
        });

        if (response.ok) {
          alert('Successfully subscribed!');
          this.checkStatus();
        } else {
          const errMsg = await response.text();
          alert('Subscription failed: ' + errMsg);
        }
      } catch (error) {
        alert('Error: ' + error.message);
      } finally {
        this.processing = false;
      }
    },
    async cancelSubscription(subId) {
      if (!confirm('Are you sure you want to cancel your meal plan?')) return;
      
      this.processing = true;
      try {
        const response = await fetch(`/api/meal-plan/subscriptions/${subId}`, {
          method: 'DELETE'
        });

        if (response.ok) {
          alert('Subscription cancelled.');
          this.checkStatus();
        } else {
          alert('Failed to cancel subscription.');
        }
      } catch (error) {
        alert('Error: ' + error.message);
      } finally {
        this.processing = false;
      }
    },
    formatDate(date) {
      if (!date) return 'N/A';
      return new Date(date).toLocaleDateString();
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  font-family: 'Inter', sans-serif;
  color: #2d3436;
}

.hero {
  text-align: center;
  margin-bottom: 60px;
}

.hero h1 {
  font-size: 3rem;
  font-weight: 800;
  color: #d35400; /* Burnt Orange theme */
  margin-bottom: 10px;
}

.hero p {
  font-size: 1.2rem;
  color: #636e72;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  border-bottom: 2px solid #f1f2f6;
  padding-bottom: 10px;
}

.section-header h2 {
  font-size: 1.8rem;
  font-weight: 700;
}

.badge {
  padding: 6px 15px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.badge.eligible { background: #e3fcef; color: #00b894; }
.badge.ineligible { background: #fff5f5; color: #ff7675; }

.active-sub-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 10px 25px rgba(211, 84, 0, 0.1);
  border-left: 5px solid #d35400;
  margin-bottom: 40px;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.plan-name {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3436;
}

.status-pill {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 800;
}

.status-pill.active { background: #d35400; color: white; }

.sub-details p {
  margin: 10px 0;
  color: #636e72;
}

.sub-details span { font-weight: 600; color: #2d3436; }

.btn-cancel {
  margin-top: 20px;
  padding: 10px 20px;
  background: transparent;
  border: 1px solid #ff7675;
  color: #ff7675;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
}

.btn-cancel:hover { background: #fff5f5; }

.plans-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 30px;
}

.plan-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  transition: transform 0.3s ease;
}

.plan-card:hover { transform: translateY(-5px); }

.meals-badge {
  display: inline-block;
  background: #f1f2f6;
  padding: 5px 12px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 700;
  color: #636e72;
  margin-bottom: 15px;
}

.price {
  font-size: 2rem;
  font-weight: 800;
  margin: 10px 0;
}

.price small { font-size: 0.9rem; color: #b2bec3; font-weight: 400; }

.semester { font-size: 0.9rem; color: #636e72; margin-bottom: 25px; }

.btn-subscribe {
  width: 100%;
  padding: 14px;
  background: #d35400;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-subscribe:hover { background: #e67e22; }

.btn-disabled {
  width: 100%;
  padding: 14px;
  background: #f1f2f6;
  color: #b2bec3;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  cursor: not-allowed;
}

.loading-state, .empty-state {
  text-align: center;
  padding: 40px;
  background: #f8f9fa;
  border-radius: 12px;
  color: #b2bec3;
}

.spinner {
  width: 30px;
  height: 30px;
  border: 3px solid rgba(211, 84, 0, 0.1);
  border-left-color: #d35400;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin { to { transform: rotate(360deg); } }
</style>
