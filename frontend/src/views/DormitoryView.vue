<template>
  <main class="container">
    <div class="hero">
      <h1>Campus Dormitories</h1>
      <p>Find your home away from home.</p>
    </div>

    <!-- Student Assignments Section -->
    <section v-if="studentId" class="assignments-section">
      <div class="section-header">
        <h2>My Room Assignments</h2>
        <span class="badge" :class="eligibility ? 'eligible' : 'ineligible'">
          {{ eligibility ? 'Eligible for Housing' : 'Not Eligible' }}
        </span>
      </div>

      <div v-if="assignmentsLoading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading your assignments...</p>
      </div>

      <div v-else-if="myAssignments.length > 0" class="assignments-grid">
        <div v-for="assignment in myAssignments" :key="assignment.assignmentId" class="assignment-card">
          <div class="card-glow"></div>
          <div class="card-content">
            <div class="room-number">Room {{ assignment.roomNumber }}</div>
            <div class="assignment-details">
              <p><span>Semester:</span> {{ assignment.semester }}</p>
              <p><span>Assigned Date:</span> {{ formatDate(assignment.assignedAt) }}</p>
            </div>
            <button @click="removeAssignment(assignment.assignmentId)" class="btn-cancel" :disabled="actionLoading">
              Cancel Assignment
            </button>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <p>You don't have any room assignments yet.</p>
      </div>
    </section>

    <!-- Available Rooms Section -->
    <section class="rooms-section">
      <div class="section-header">
        <h2>Available Rooms</h2>
        <div class="filters">
          <!-- Optional filters could go here -->
        </div>
      </div>

      <div v-if="roomsLoading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading available rooms...</p>
      </div>

      <div v-else class="rooms-grid">
        <div v-for="room in rooms" :key="room.roomId" class="room-card" :class="{ 'full': room.currentOccupancy >= room.capacity }">
          <div class="room-type">{{ room.type }}</div>
          <div class="room-info">
            <h3>Room {{ room.roomNumber }}</h3>
            <div class="occupancy-bar">
              <div class="bar-fill" :style="{ width: (room.currentOccupancy / room.capacity * 100) + '%' }"></div>
            </div>
            <p class="occupancy-text">{{ room.currentOccupancy }} / {{ room.capacity }} beds occupied</p>
            <p class="price">${{ room.pricePerSemester }} <small>/ semester</small></p>
          </div>
          <div class="room-actions">
            <button 
              v-if="room.currentOccupancy < room.capacity && eligibility" 
              @click="assignRoom(room)" 
              class="btn-assign"
              :disabled="actionLoading"
            >
              {{ actionLoading ? 'Processing...' : 'Assign Me' }}
            </button>
            <button v-else-if="!eligibility" class="btn-disabled" disabled>Not Eligible</button>
            <button v-else class="btn-full" disabled>Room Full</button>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script>
export default {
  name: 'DormitoryView',
  data() {
    return {
      rooms: [],
      myAssignments: [],
      studentId: localStorage.getItem('studentId') || '',
      eligibility: false,
      roomsLoading: true,
      assignmentsLoading: false,
      actionLoading: false,
      storageInterval: null
    }
  },
  mounted() {
    this.fetchRooms();
    if (this.studentId) {
      this.checkEligibility();
      this.fetchMyAssignments();
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
          this.checkEligibility();
          this.fetchMyAssignments();
        } else {
          this.myAssignments = [];
          this.eligibility = false;
        }
      }
    },
    async fetchRooms() {
      this.roomsLoading = true;
      try {
        const response = await fetch('/api/dormitory/rooms');
        if (response.ok) {
          this.rooms = await response.json();
        }
      } catch (error) {
        console.error('Error fetching rooms:', error);
      } finally {
        this.roomsLoading = false;
      }
    },
    async fetchMyAssignments() {
      if (!this.studentId) return;
      this.assignmentsLoading = true;
      try {
        const response = await fetch(`/api/dormitory/assignments/student/${this.studentId}`);
        if (response.ok) {
          this.myAssignments = await response.json();
        }
      } catch (error) {
        console.error('Error fetching assignments:', error);
      } finally {
        this.assignmentsLoading = false;
      }
    },
    async checkEligibility() {
      if (!this.studentId) return;
      try {
        const response = await fetch(`/api/dormitory/students/${this.studentId}/eligibility`);
        if (response.ok) {
          this.eligibility = await response.json();
        }
      } catch (error) {
        console.error('Error checking eligibility:', error);
      }
    },
    async assignRoom(room) {
      if (!this.studentId) {
        alert('Please login first.');
        return;
      }
      
      const semester = prompt('Enter semester (e.g., Fall 2024):', 'Fall 2024');
      if (!semester) return;

      this.actionLoading = true;
      try {
        const response = await fetch(`/api/dormitory/rooms/${room.roomId}/assign`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            studentId: this.studentId,
            semester: semester
          })
        });

        if (response.ok) {
          alert('Room assigned successfully!');
          this.fetchRooms();
          this.fetchMyAssignments();
        } else {
          const error = await response.text();
          alert('Failed to assign room: ' + error);
        }
      } catch (error) {
        alert('Error: ' + error.message);
      } finally {
        this.actionLoading = false;
      }
    },
    async removeAssignment(assignmentId) {
      if (!confirm('Are you sure you want to cancel this assignment?')) return;
      
      this.actionLoading = true;
      try {
        const response = await fetch(`/api/dormitory/assignments/${assignmentId}`, {
          method: 'DELETE'
        });

        if (response.ok) {
          alert('Assignment cancelled.');
          this.fetchRooms();
          this.fetchMyAssignments();
        } else {
          alert('Failed to cancel assignment.');
        }
      } catch (error) {
        alert('Error: ' + error.message);
      } finally {
        this.actionLoading = false;
      }
    },
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      return new Date(dateString).toLocaleDateString();
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
  color: #2d3436;
}

.hero {
  text-align: center;
  margin-bottom: 60px;
}

.hero h1 {
  font-size: 3.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #6c5ce7, #a29bfe);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 10px;
}

.hero p {
  font-size: 1.2rem;
  color: #636e72;
}

section {
  margin-bottom: 50px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-header h2 {
  font-size: 1.8rem;
  font-weight: 700;
  position: relative;
}

.section-header h2::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 40px;
  height: 4px;
  background: #6c5ce7;
  border-radius: 2px;
}

.badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.badge.eligible {
  background: #e1f5fe;
  color: #0288d1;
}

.badge.ineligible {
  background: #ffebee;
  color: #d32f2f;
}

/* Assignments Grid */
.assignments-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 25px;
}

.assignment-card {
  position: relative;
  background: #fff;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
  overflow: hidden;
  transition: transform 0.3s ease;
}

.assignment-card:hover {
  transform: translateY(-5px);
}

.card-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at center, rgba(108, 92, 231, 0.05) 0%, transparent 70%);
  pointer-events: none;
}

.room-number {
  font-size: 1.5rem;
  font-weight: 700;
  color: #6c5ce7;
  margin-bottom: 15px;
}

.assignment-details p {
  margin: 8px 0;
  font-size: 0.95rem;
  color: #636e72;
}

.assignment-details span {
  font-weight: 600;
  color: #2d3436;
}

.btn-cancel {
  margin-top: 20px;
  width: 100%;
  padding: 12px;
  border: 1px solid #fab1a0;
  background: transparent;
  color: #e17055;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #fff5f3;
}

/* Rooms Grid */
.rooms-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.room-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  border: 1px solid #f1f2f6;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.room-card:hover {
  box-shadow: 0 12px 40px rgba(0,0,0,0.1);
  border-color: #6c5ce7;
}

.room-type {
  background: #f8f9fa;
  padding: 8px 15px;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  color: #b2bec3;
  letter-spacing: 1px;
}

.room-info {
  padding: 20px;
  flex-grow: 1;
}

.room-info h3 {
  margin: 0 0 15px 0;
  font-size: 1.3rem;
}

.occupancy-bar {
  height: 8px;
  background: #f1f2f6;
  border-radius: 4px;
  margin-bottom: 10px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #6c5ce7, #a29bfe);
  border-radius: 4px;
}

.occupancy-text {
  font-size: 0.85rem;
  color: #636e72;
  margin-bottom: 15px;
}

.price {
  font-size: 1.4rem;
  font-weight: 700;
  color: #2d3436;
}

.price small {
  font-size: 0.9rem;
  color: #b2bec3;
  font-weight: 400;
}

.room-actions {
  padding: 20px;
  padding-top: 0;
}

.btn-assign {
  width: 100%;
  padding: 14px;
  background: #6c5ce7;
  color: #fff;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-assign:hover {
  background: #5b4bc4;
}

.btn-full, .btn-disabled {
  width: 100%;
  padding: 14px;
  background: #f1f2f6;
  color: #b2bec3;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  cursor: not-allowed;
}

.empty-state, .loading-state {
  text-align: center;
  padding: 60px;
  background: #f8f9fa;
  border-radius: 16px;
  color: #b2bec3;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(108, 92, 231, 0.1);
  border-left-color: #6c5ce7;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
