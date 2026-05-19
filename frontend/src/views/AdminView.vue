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

    <!-- Admin Dashboard -->
    <div v-else class="dashboard">

      <!-- Header -->
      <div class="dash-header">
        <div>
          <div class="dash-label">UNICAMPUS · ADMIN PANEL</div>
          <div class="dash-title">Academic & Resource Console</div>
        </div>
        <button class="sign-out-btn" @click="signOut">Sign out</button>
      </div>

      <!-- Navigation Tabs -->
      <div class="nav-tabs">
        <button 
          class="tab-btn" 
          :class="{ 'tab-btn--active': activeTab === 'students' }" 
          @click="switchTab('students')"
        >
          Student Registry
        </button>
        <button 
          class="tab-btn" 
          :class="{ 'tab-btn--active': activeTab === 'library' }" 
          @click="switchTab('library')"
        >
          Library Inventory
        </button>
        <button 
          class="tab-btn" 
          :class="{ 'tab-btn--active': activeTab === 'dormitory' }" 
          @click="switchTab('dormitory')"
        >
          Dormitory Management
        </button>
        <button 
          class="tab-btn" 
          :class="{ 'tab-btn--active': activeTab === 'mealPlan' }" 
          @click="switchTab('mealPlan')"
        >
          Meal Plan Configuration
        </button>
      </div>

      <!-- TAB 1: Student Management -->
      <div v-if="activeTab === 'students'" class="tab-content">
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
            <div class="table-title">All Registered Students</div>
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

      <!-- TAB 2: Library Inventory Management -->
      <div v-if="activeTab === 'library'" class="tab-content">
        <div class="library-grid">
          
          <!-- Column Left: Book Creator -->
          <div class="creator-card">
            <h3 class="card-subtitle">Add New Resource</h3>
            <p class="card-desc">Expand the university catalog database. Newly indexed items are instantly set as 'Available' for loan reservations.</p>
            
            <form @submit.prevent="createBook" class="creator-form">
              <div class="form-group">
                <label for="bookTitle">Book Title</label>
                <input 
                  id="bookTitle"
                  v-model="newBookTitle"
                  type="text" 
                  placeholder="e.g. Introduction to Quantum Computing" 
                  required 
                  class="form-input"
                />
              </div>
              <button type="submit" class="btn-submit-book" :disabled="bookSubmitLoading">
                {{ bookSubmitLoading ? 'Saving to Database...' : 'Register Book' }}
              </button>
            </form>

            <transition name="fade">
              <div v-if="bookMessage" class="status-message" :class="{ 'status-message--error': bookMessageError }">
                {{ bookMessage }}
              </div>
            </transition>
          </div>

          <!-- Column Right: Book Catalog List -->
          <div class="table-card inventory-list">
            <div class="table-head">
              <div class="table-title">University Catalog ({{ books.length }} total)</div>
              <button class="refresh-btn" @click="fetchBooks" title="Refresh Inventory">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>
              </button>
            </div>

            <div v-if="booksLoading" class="table-empty">Loading book catalog ledger...</div>
            <div v-else-if="books.length === 0" class="table-empty">No library items indexed yet.</div>

            <table v-else class="table">
              <thead>
                <tr>
                  <th>RESOURCE DESCRIPTION</th>
                  <th>AVAILABILITY</th>
                  <th class="text-right">MANAGE INVENTORY</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="b in books" :key="b.bookId" class="table-row">
                  <td class="book-details">
                    <span class="book-title-txt">{{ b.title }}</span>
                    <span class="book-id-txt">ID: {{ b.bookId }}</span>
                  </td>
                  <td>
                    <span class="badge" :class="b.isAvailable ? 'badge--available' : 'badge--borrowed'">
                      <span class="badge-dot"></span>
                      {{ b.isAvailable ? 'Available' : 'Loaned Out' }}
                    </span>
                  </td>
                  <td class="text-right">
                    <button 
                      class="toggle-btn" 
                      :class="b.isAvailable ? 'toggle-btn--disable' : 'toggle-btn--enable'"
                      @click="toggleBookAvailability(b)"
                      :disabled="!!catalogUpdating[b.bookId]"
                    >
                      {{ b.isAvailable ? 'Flag Unavailable' : 'Flag Restocked' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

        </div>
      </div>

      <!-- TAB 3: Dormitory Management -->
      <div v-if="activeTab === 'dormitory'" class="tab-content">
        <div class="library-grid">
          
          <!-- Column Left: Dormitory Creator -->
          <div class="creator-card">
            <h3 class="card-subtitle">Add New Room</h3>
            <p class="card-desc">Register a new room in the dormitory system. Upon creation, it will be available for student assignments.</p>
            
            <form @submit.prevent="createDorm" class="creator-form">
              <div class="form-group">
                <label for="dormBuilding">Building</label>
                <input id="dormBuilding" v-model="newDorm.building" type="text" placeholder="e.g. North Hall" required class="form-input" />
              </div>
              <div class="form-group">
                <label for="dormRoomNumber">Room Number</label>
                <input id="dormRoomNumber" v-model="newDorm.roomNumber" type="text" placeholder="e.g. A101" required class="form-input" />
              </div>
              <div class="form-group">
                <label for="dormCapacity">Capacity</label>
                <input id="dormCapacity" v-model.number="newDorm.capacity" type="number" min="1" required class="form-input" />
              </div>
              <div class="form-group">
                <label for="dormAmenities">Amenities (comma separated)</label>
                <input id="dormAmenities" v-model="newDorm.amenities" type="text" placeholder="e.g. WiFi, AC" class="form-input" />
              </div>
              <button type="submit" class="btn-submit-book" :disabled="dormSubmitLoading">
                {{ dormSubmitLoading ? 'Saving to Database...' : 'Register Room' }}
              </button>
            </form>

            <transition name="fade">
              <div v-if="dormMessage" class="status-message" :class="{ 'status-message--error': dormMessageError }">
                {{ dormMessage }}
              </div>
            </transition>
          </div>

          <!-- Column Right: Room List -->
          <div class="table-card inventory-list">
            <div class="table-head">
              <div class="table-title">Dormitory Rooms ({{ dorms.length }} total)</div>
              <button class="refresh-btn" @click="fetchDorms" title="Refresh Rooms">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>
              </button>
            </div>

            <div v-if="dormsLoading" class="table-empty">Loading dormitory records...</div>
            <div v-else-if="dorms.length === 0" class="table-empty">No rooms registered yet.</div>

            <table v-else class="table">
              <thead>
                <tr>
                  <th>ROOM DETAILS</th>
                  <th>CAPACITY</th>
                  <th class="text-right">STATUS</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="d in dorms" :key="d.roomId" class="table-row">
                  <td class="book-details">
                    <span class="book-title-txt">{{ d.building }} - {{ d.roomNumber }}</span>
                    <span class="book-id-txt">Amenities: {{ d.amenities ? d.amenities.join(', ') : 'None' }}</span>
                  </td>
                  <td>
                    {{ d.currentOccupancy }} / {{ d.capacity }}
                  </td>
                  <td class="text-right">
                    <span class="badge" :class="d.isAvailable && d.currentOccupancy < d.capacity ? 'badge--available' : 'badge--suspended'">
                      <span class="badge-dot"></span>
                      {{ d.isAvailable && d.currentOccupancy < d.capacity ? 'Available' : 'Full / Unavailable' }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- TAB 4: Meal Plan Configuration -->
      <div v-if="activeTab === 'mealPlan'" class="tab-content">
        <div class="library-grid">
          
          <!-- Column Left: Meal Plan Creator -->
          <div class="creator-card">
            <h3 class="card-subtitle">Create Meal Plan</h3>
            <p class="card-desc">Add a new meal plan package for the upcoming semester.</p>
            
            <form @submit.prevent="createMealPlan" class="creator-form">
              <div class="form-group">
                <label for="planName">Plan Name</label>
                <input id="planName" v-model="newPlan.name" type="text" placeholder="e.g. Premium Plan" required class="form-input" />
              </div>
              <div class="form-group">
                <label for="planSemester">Semester</label>
                <input id="planSemester" v-model="newPlan.semester" type="text" placeholder="e.g. Fall 2026" required class="form-input" />
              </div>
              <div class="form-group">
                <label for="planMeals">Meals Per Week</label>
                <input id="planMeals" v-model.number="newPlan.mealsPerWeek" type="number" min="1" required class="form-input" />
              </div>
              <div class="form-group">
                <label for="planPrice">Price ($)</label>
                <input id="planPrice" v-model.number="newPlan.price" type="number" min="0" step="0.01" required class="form-input" />
              </div>
              <button type="submit" class="btn-submit-book" :disabled="planSubmitLoading">
                {{ planSubmitLoading ? 'Saving...' : 'Create Plan' }}
              </button>
            </form>

            <transition name="fade">
              <div v-if="planMessage" class="status-message" :class="{ 'status-message--error': planMessageError }">
                {{ planMessage }}
              </div>
            </transition>
          </div>

          <!-- Column Right: Meal Plan List -->
          <div class="table-card inventory-list">
            <div class="table-head">
              <div class="table-title">Available Meal Plans ({{ mealPlans.length }} total)</div>
              <button class="refresh-btn" @click="fetchMealPlans" title="Refresh Plans">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>
              </button>
            </div>

            <div v-if="plansLoading" class="table-empty">Loading meal plans...</div>
            <div v-else-if="mealPlans.length === 0" class="table-empty">No meal plans created yet.</div>

            <table v-else class="table">
              <thead>
                <tr>
                  <th>PLAN DETAILS</th>
                  <th>OFFERING</th>
                  <th class="text-right">MANAGE</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="p in mealPlans" :key="p.planId" class="table-row">
                  <td class="book-details">
                    <span class="book-title-txt">{{ p.name }}</span>
                    <span class="book-id-txt">{{ p.semester }}</span>
                  </td>
                  <td>
                    {{ p.mealsPerWeek }} meals/wk · ${{ p.price }}
                  </td>
                  <td class="text-right">
                    <button 
                      class="toggle-btn" 
                      :class="p.isActive ? 'toggle-btn--disable' : 'toggle-btn--enable'"
                      @click="togglePlanStatus(p)"
                      :disabled="!!planUpdating[p.planId]"
                    >
                      {{ p.isActive ? 'Deactivate' : 'Activate' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
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
      activeTab: 'students', // 'students' or 'library'
      creds: { username: '', password: '' },
      loginError: '',
      loginLoading: false,
      
      // Student Tab variables
      students: [],
      loading: false,
      updating: {},
      statusOptions: [
        { value: 'ACTIVE',    label: 'Active',    cls: 'active'    },
        { value: 'GRADUATED', label: 'Graduate',  cls: 'graduated' },
        { value: 'SUSPENDED', label: 'Suspend',   cls: 'suspended' },
        { value: 'ON_LEAVE',  label: 'On Leave',  cls: 'leave'     },
      ],

      // Library Tab variables
      books: [],
      booksLoading: false,
      newBookTitle: '',
      bookSubmitLoading: false,
      bookMessage: '',
      bookMessageError: false,
      catalogUpdating: {},

      // Dormitory Tab variables
      dorms: [],
      dormsLoading: false,
      newDorm: { roomNumber: '', building: '', capacity: 1, amenities: '' },
      dormSubmitLoading: false,
      dormMessage: '',
      dormMessageError: false,

      // Meal Plan Tab variables
      mealPlans: [],
      plansLoading: false,
      newPlan: { name: '', mealsPerWeek: 10, price: 0, semester: 'Fall 2026' },
      planSubmitLoading: false,
      planMessage: '',
      planMessageError: false,
      planUpdating: {}
    }
  },
  mounted() {
    const t = localStorage.getItem('adminToken')
    if (t) { 
      this.adminToken = t; 
      this.fetchStudents();
      this.fetchBooks();
      this.fetchDorms();
      this.fetchMealPlans();
    }
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
        await Promise.all([
          this.fetchStudents(),
          this.fetchBooks(),
          this.fetchDorms(),
          this.fetchMealPlans()
        ]);
      } catch (e) {
        this.loginError = e.message
      } finally { this.loginLoading = false }
    },
    signOut() {
      this.adminToken = ''; 
      this.students = [];
      this.books = [];
      this.dorms = [];
      this.mealPlans = [];
      localStorage.removeItem('adminToken')
    },
    switchTab(tab) {
      this.activeTab = tab;
      this.bookMessage = '';
      this.dormMessage = '';
      this.planMessage = '';
      if (tab === 'students') this.fetchStudents();
      if (tab === 'library') this.fetchBooks();
      if (tab === 'dormitory') this.fetchDorms();
      if (tab === 'mealPlan') this.fetchMealPlans();
    },
    
    // ── Student Management Operations ──────────────────────────────────
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
    },

    // ── Library Inventory Operations ───────────────────────────────────
    async fetchBooks() {
      this.booksLoading = true;
      try {
        const res = await fetch('/api/library/books');
        if (res.ok) {
          this.books = await res.json();
        }
      } catch (e) {
        console.error('Error fetching book inventory catalog:', e);
      } finally {
        this.booksLoading = false;
      }
    },
    async createBook() {
      if (!this.newBookTitle.trim()) return;
      this.bookSubmitLoading = true;
      this.bookMessage = '';
      this.bookMessageError = false;
      try {
        const res = await fetch('/api/library/books', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.adminToken}`
          },
          body: JSON.stringify({ title: this.newBookTitle.trim() })
        });
        
        if (res.ok) {
          this.bookMessage = `Successfully indexed "${this.newBookTitle}" into standard availability.`;
          this.newBookTitle = '';
          await this.fetchBooks();
        } else {
          throw new Error('Server rejected book indexing transaction.');
        }
      } catch (e) {
        this.bookMessage = e.message || 'Connection failure to library catalog service.';
        this.bookMessageError = true;
      } finally {
        this.bookSubmitLoading = false;
      }
    },
    async toggleBookAvailability(book) {
      this.catalogUpdating = { ...this.catalogUpdating, [book.bookId]: true };
      try {
        const updatedStatus = !book.isAvailable;
        const res = await fetch(`/api/library/books/${book.bookId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.adminToken}`
          },
          body: JSON.stringify({
            title: book.title,
            isAvailable: updatedStatus
          })
        });
        if (res.ok) {
          const updatedBook = await res.json();
          const idx = this.books.findIndex(b => b.bookId === book.bookId);
          if (idx !== -1) {
            this.books[idx] = updatedBook;
          }
        }
      } catch (e) {
        console.error('Error toggling resource state:', e);
      } finally {
        const u = { ...this.catalogUpdating }; delete u[book.bookId]; this.catalogUpdating = u;
      }
    },

    // ── Dormitory Management Operations ──────────────────────────────────
    async fetchDorms() {
      this.dormsLoading = true;
      try {
        const res = await fetch('/api/dormitory/rooms', {
          headers: { 'Authorization': `Bearer ${this.adminToken}` }
        });
        if (res.ok) this.dorms = await res.json();
      } catch (e) {
        console.error('Error fetching rooms:', e);
      } finally {
        this.dormsLoading = false;
      }
    },
    async createDorm() {
      if (!this.newDorm.building || !this.newDorm.roomNumber) return;
      this.dormSubmitLoading = true;
      this.dormMessage = '';
      this.dormMessageError = false;
      try {
        const payload = {
          ...this.newDorm,
          amenities: this.newDorm.amenities.split(',').map(s => s.trim()).filter(s => s)
        };
        const res = await fetch('/api/dormitory/rooms', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.adminToken}`
          },
          body: JSON.stringify(payload)
        });
        
        if (res.ok) {
          this.dormMessage = `Successfully added room ${this.newDorm.roomNumber} in ${this.newDorm.building}.`;
          this.newDorm = { roomNumber: '', building: '', capacity: 1, amenities: '' };
          await this.fetchDorms();
        } else {
          throw new Error('Failed to register room.');
        }
      } catch (e) {
        this.dormMessage = e.message || 'Error communicating with dormitory service';
        this.dormMessageError = true;
      } finally {
        this.dormSubmitLoading = false;
      }
    },

    // ── Meal Plan Configuration Operations ───────────────────────────────
    async fetchMealPlans() {
      this.plansLoading = true;
      try {
        const res = await fetch('/api/meal-plan/plans', {
          headers: { 'Authorization': `Bearer ${this.adminToken}` }
        });
        if (res.ok) this.mealPlans = await res.json();
      } catch (e) {
        console.error('Error fetching meal plans:', e);
      } finally {
        this.plansLoading = false;
      }
    },
    async createMealPlan() {
      if (!this.newPlan.name || !this.newPlan.semester) return;
      this.planSubmitLoading = true;
      this.planMessage = '';
      this.planMessageError = false;
      try {
        const res = await fetch('/api/meal-plan/plans', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.adminToken}`
          },
          body: JSON.stringify(this.newPlan)
        });
        
        if (res.ok) {
          this.planMessage = `Successfully created meal plan "${this.newPlan.name}".`;
          this.newPlan = { name: '', mealsPerWeek: 10, price: 0, semester: 'Fall 2026' };
          await this.fetchMealPlans();
        } else {
          throw new Error('Failed to create meal plan.');
        }
      } catch (e) {
        this.planMessage = e.message || 'Error communicating with meal plan service';
        this.planMessageError = true;
      } finally {
        this.planSubmitLoading = false;
      }
    },
    async togglePlanStatus(plan) {
      this.planUpdating = { ...this.planUpdating, [plan.planId]: true };
      try {
        const res = await fetch(`/api/meal-plan/plans/${plan.planId}/toggle`, {
          method: 'PATCH',
          headers: { 'Authorization': `Bearer ${this.adminToken}` }
        });
        if (res.ok) {
          const updatedPlan = await res.json();
          const idx = this.mealPlans.findIndex(p => p.planId === plan.planId);
          if (idx !== -1) this.mealPlans[idx] = updatedPlan;
        }
      } catch (e) {
        console.error('Error toggling meal plan status:', e);
      } finally {
        const u = { ...this.planUpdating }; delete u[plan.planId]; this.planUpdating = u;
      }
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

/* Dashboard Layout */
.dashboard { max-width: 1100px; margin: 0 auto; padding: 36px 28px; }
.dash-header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 24px; }
.dash-label { font-size: 11px; font-weight: 700; color: #94a3b8; letter-spacing: 1.5px; margin-bottom: 4px; }
.dash-title { font-size: 24px; font-weight: 700; color: #0f172a; }
.sign-out-btn { padding: 7px 16px; background: #fff; border: 1px solid #e2e8f0; border-radius: 8px; font-size: 13px; color: #64748b; cursor: pointer; }
.sign-out-btn:hover { background: #fef2f2; color: #dc2626; border-color: #fecaca; }

/* Nav Tabs */
.nav-tabs { display: flex; gap: 8px; margin-bottom: 28px; border-bottom: 1px solid #e2e8f0; padding-bottom: 12px; }
.tab-btn { background: none; border: none; padding: 8px 16px; font-size: 14px; font-weight: 600; color: #64748b; cursor: pointer; border-radius: 6px; transition: all 0.15s; }
.tab-btn:hover { background: #f1f5f9; color: #0f172a; }
.tab-btn--active { background: #0f172a; color: #fff; }
.tab-btn--active:hover { background: #1e293b; color: #fff; }

/* Stats (Student tab) */
.stats-row { display: grid; grid-template-columns: repeat(5, 1fr); gap: 12px; margin-bottom: 20px; }
.stat-box { background: #fff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 16px 18px; }
.stat-n { font-size: 24px; font-weight: 800; color: #0f172a; letter-spacing: -0.5px; }
.stat-l { font-size: 12px; color: #94a3b8; margin-top: 3px; }

/* Library Layout Columns */
.library-grid { display: grid; grid-template-columns: 1fr 1.8fr; gap: 24px; align-items: start; }
.creator-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px; }
.card-subtitle { font-size: 16px; font-weight: 700; color: #0f172a; margin: 0 0 6px; }
.card-desc { font-size: 13px; color: #64748b; margin: 0 0 20px; line-height: 1.5; }
.creator-form { display: flex; flex-direction: column; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group label { font-size: 11px; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; }
.form-input { padding: 10px 14px; border: 1px solid #e2e8f0; border-radius: 8px; font-size: 13px; outline: none; transition: border-color 0.15s; }
.form-input:focus { border-color: #0f172a; }
.btn-submit-book { background: #0f172a; color: #fff; padding: 11px; font-size: 13px; font-weight: 700; border: none; border-radius: 8px; cursor: pointer; transition: background 0.15s; }
.btn-submit-book:hover:not(:disabled) { background: #1e293b; }
.btn-submit-book:disabled { opacity: 0.6; cursor: wait; }

.status-message { font-size: 12px; font-weight: 600; color: #16a34a; background: #f0fdf4; border: 1px solid #dcfce7; padding: 10px; border-radius: 6px; margin-top: 14px; text-align: center; }
.status-message--error { color: #dc2626; background: #fef2f2; border-color: #fee2e2; }

/* Book Inventory List Row Detail */
.book-details { display: flex; flex-direction: column; gap: 2px; }
.book-title-txt { font-weight: 600; color: #334155; }
.book-id-txt { font-size: 10px; color: #94a3b8; font-family: monospace; }
.text-right { text-align: right; }

.toggle-btn { padding: 4px 10px; font-size: 11px; font-weight: 600; border-radius: 6px; cursor: pointer; border: 1px solid; background: none; transition: all 0.15s; }
.toggle-btn:disabled { opacity: 0.5; cursor: wait; }
.toggle-btn--disable { border-color: #fecaca; color: #dc2626; }
.toggle-btn--disable:hover:not(:disabled) { background: #fef2f2; }
.toggle-btn--enable { border-color: #bbf7d0; color: #16a34a; }
.toggle-btn--enable:hover:not(:disabled) { background: #f0fdf4; }

/* Table general styles */
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
.badge--active      { background: #f0fdf4; color: #16a34a; } .badge--active      .badge-dot { background: #16a34a; }
.badge--graduated   { background: #f1f5f9; color: #64748b; } .badge--graduated   .badge-dot { background: #94a3b8; }
.badge--suspended   { background: #fef2f2; color: #dc2626; } .badge--suspended   .badge-dot { background: #dc2626; }
.badge--leave       { background: #fffbeb; color: #d97706; } .badge--leave       .badge-dot { background: #d97706; }
.badge--available   { background: #f0fdf4; color: #16a34a; } .badge--available   .badge-dot { background: #16a34a; }
.badge--borrowed    { background: #fff7ed; color: #ea580c; } .badge--borrowed    .badge-dot { background: #ea580c; }

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

/* Transitions */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
