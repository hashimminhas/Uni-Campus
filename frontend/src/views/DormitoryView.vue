<template>
  <div class="page">
    <!-- Breadcrumb -->
    <div class="breadcrumb-bar">
      <div class="breadcrumb-inner">
        <router-link to="/" class="bc-link">Home</router-link>
        <span class="bc-sep">/</span>
        <span class="bc-current">Dormitory</span>
      </div>
    </div>

    <div class="page-top">
      <h1 class="page-title">Campus Dormitories</h1>
      <p class="page-sub">Find your home away from home and manage your housing assignments.</p>
    </div>

    <div class="content">
      <!-- Student Assignments Section -->
      <div v-if="studentId" class="profile-card" style="margin-bottom: 24px;">
        <div class="profile-header">
          <div class="profile-avatar">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>
          </div>
          <div>
            <div class="profile-name">My Room Assignments</div>
            <div class="profile-sub">Your current housing details</div>
          </div>
          <span class="badge" :class="eligibility ? 'badge--active' : 'badge--suspended'" style="margin-left:auto">
            <span class="badge-dot"></span>{{ eligibility ? 'Eligible for Housing' : 'Not Eligible' }}
          </span>
        </div>

        <div v-if="assignmentsLoading" class="profile-loading">
          Loading your assignments...
        </div>
        <div v-else-if="myAssignments.length > 0" class="room-assignments-grid">
          <div v-for="assignment in myAssignments" :key="assignment.assignmentId" class="room-assignment-item">
            <div class="assignment-top">
              <div class="assignment-room">Room {{ assignment.roomNumber }}</div>
              <button @click="removeAssignment(assignment.assignmentId)" class="cancel-btn" :disabled="actionLoading" style="padding: 4px 8px; font-size: 11px;">
                Cancel
              </button>
            </div>
            <div class="assignment-details">
              <div class="pf">
                <div class="pf-label">SEMESTER</div>
                <div class="pf-val">{{ assignment.semester }}</div>
              </div>
              <div class="pf">
                <div class="pf-label">ASSIGNED DATE</div>
                <div class="pf-val">{{ formatDate(assignment.assignedAt) }}</div>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          You don't have any room assignments yet.
        </div>
      </div>

      <!-- Not logged in prompt -->
      <div v-if="!studentId" class="not-logged-in" style="margin-bottom: 24px;">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>
        <div class="not-logged-in-title">You are not logged in</div>
        <div class="not-logged-in-sub">Log in with your student UUID from the top-right corner to manage your housing assignments.</div>
      </div>

      <!-- Available Rooms Section -->
      <div class="section-title">Available Rooms</div>
      
      <div v-if="roomsLoading" class="empty-state">
        <span class="spinner"></span> Loading available rooms...
      </div>
      <div v-else class="rooms-grid">
        <div v-for="room in rooms" :key="room.roomId" class="room-card" :class="{ 'full': room.currentOccupancy >= room.capacity }">
          <div class="room-header">
            <div class="room-type">{{ room.type }}</div>
            <div class="room-price">${{ room.pricePerSemester }} <span style="font-size:10px; font-weight:normal; color:#94a3b8;">/ SEM</span></div>
          </div>
          <div class="room-body">
            <h3 class="room-number-lg">Room {{ room.roomNumber }}</h3>
            <div class="occupancy-wrap">
              <div class="occupancy-labels">
                <span>Occupancy</span>
                <span>{{ room.currentOccupancy }} / {{ room.capacity }}</span>
              </div>
              <div class="occupancy-bar">
                <div class="bar-fill" :style="{ width: (room.currentOccupancy / room.capacity * 100) + '%' }"></div>
              </div>
            </div>
          </div>
          <div class="room-footer">
            <button 
              v-if="room.currentOccupancy < room.capacity && eligibility && studentId" 
              @click="assignRoom(room)" 
              class="submit-btn" style="width: 100%; justify-content: center;"
              :disabled="actionLoading"
            >
              {{ actionLoading ? 'Processing...' : 'Assign Me' }}
            </button>
            <button v-else-if="!studentId" class="submit-btn" style="width: 100%; justify-content: center;" disabled>Log In First</button>
            <button v-else-if="!eligibility" class="submit-btn" style="width: 100%; justify-content: center;" disabled>Not Eligible</button>
            <button v-else class="submit-btn" style="width: 100%; justify-content: center;" disabled>Room Full</button>
          </div>
        </div>
      </div>

    </div>
  </div>
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
      
      const semester = prompt('Enter semester (e.g., Fall 2026):', 'Fall 2026');
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

/* My Profile / Assignments */
.profile-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 20px 24px; }
.profile-header { display: flex; align-items: center; gap: 14px; margin-bottom: 18px; }
.profile-avatar { width: 44px; height: 44px; background: #0f172a; color: #fff; border-radius: 8px; font-size: 15px; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.profile-name { font-size: 16px; font-weight: 700; color: #0f172a; }
.profile-sub { font-size: 12px; color: #94a3b8; margin-top: 2px; }
.profile-loading { font-size: 13px; color: #94a3b8; padding: 8px 0; }

.room-assignments-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 16px; }
.room-assignment-item { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px; padding: 16px; transition: background 0.15s; }
.room-assignment-item:hover { background: #f1f5f9; }
.assignment-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 14px; }
.assignment-room { font-size: 16px; font-weight: 700; color: #0f172a; }

.assignment-details { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.pf { display: flex; flex-direction: column; gap: 3px; }
.pf-label { font-size: 10px; font-weight: 700; color: #94a3b8; letter-spacing: 0.8px; }
.pf-val { font-size: 13px; color: #0f172a; }

/* Not logged in */
.not-logged-in { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 40px 24px; display: flex; flex-direction: column; align-items: center; gap: 12px; color: #94a3b8; }
.not-logged-in-title { font-size: 15px; font-weight: 600; color: #475569; }
.not-logged-in-sub { font-size: 13px; color: #94a3b8; text-align: center; max-width: 360px; line-height: 1.5; }

/* Badges */
.badge { display: inline-flex; align-items: center; gap: 5px; padding: 3px 9px; border-radius: 20px; font-size: 11px; font-weight: 500; }
.badge-dot { width: 5px; height: 5px; border-radius: 50%; flex-shrink: 0; }
.badge--active    { background: #f0fdf4; color: #16a34a; } .badge--active    .badge-dot { background: #16a34a; }
.badge--suspended { background: #fef2f2; color: #dc2626; } .badge--suspended .badge-dot { background: #dc2626; }

/* Buttons */
.cancel-btn { padding: 9px 18px; background: #fff; border: 1px solid #e2e8f0; border-radius: 7px; font-size: 13px; color: #64748b; cursor: pointer; }
.cancel-btn:hover:not(:disabled) { background: #fef2f2; color: #ef4444; border-color: #fca5a5; }
.cancel-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.submit-btn { display: inline-flex; align-items: center; gap: 7px; padding: 9px 22px; background: #0f172a; color: #fff; border: none; border-radius: 7px; font-size: 13px; font-weight: 600; cursor: pointer; transition: background 0.15s; }
.submit-btn:hover:not(:disabled) { background: #1e293b; }
.submit-btn:disabled { opacity: 0.4; cursor: not-allowed; background: #64748b; }

/* Rooms */
.section-title { font-size: 18px; font-weight: 700; color: #0f172a; margin: 10px 0 0; }
.rooms-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 16px; }
.room-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; display: flex; flex-direction: column; transition: box-shadow 0.15s, transform 0.15s; }
.room-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.05); transform: translateY(-2px); }
.room-header { padding: 16px 16px 10px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #f8fafc; }
.room-type { font-size: 10px; font-weight: 700; background: #f1f5f9; color: #475569; padding: 4px 8px; border-radius: 4px; letter-spacing: 0.5px; text-transform: uppercase; }
.room-price { font-size: 15px; font-weight: 700; color: #0f172a; }
.room-body { padding: 16px; flex-grow: 1; }
.room-number-lg { font-size: 20px; font-weight: 700; color: #0f172a; margin: 0 0 16px; }
.occupancy-wrap { display: flex; flex-direction: column; gap: 6px; }
.occupancy-labels { display: flex; justify-content: space-between; font-size: 11px; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; }
.occupancy-bar { height: 6px; background: #e2e8f0; border-radius: 3px; overflow: hidden; }
.bar-fill { height: 100%; background: #3b82f6; border-radius: 3px; }
.room-card.full .bar-fill { background: #ef4444; }
.room-footer { padding: 0 16px 16px; }

.empty-state { text-align: center; padding: 30px; font-size: 13px; color: #94a3b8; background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; }
.spinner { width: 13px; height: 13px; border: 2px solid rgba(0,0,0,0.1); border-top-color: #3b82f6; border-radius: 50%; animation: spin 0.6s linear infinite; display: inline-block; vertical-align: middle; margin-right: 6px; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>
