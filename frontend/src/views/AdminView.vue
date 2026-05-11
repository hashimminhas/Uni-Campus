<template>
  <main class="container">
    <h1>Admin Dashboard - Library</h1>

    <div class="admin-section">
      <h2>Add a New Book</h2>
      
      <form @submit.prevent="addBook" class="add-book-form">
        <div class="form-group">
          <label for="title">Book Title</label>
          <input 
            type="text" 
            id="title" 
            v-model="newBook.title" 
            required 
            placeholder="Enter book title"
          />
        </div>

        <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
          {{ isSubmitting ? 'Adding...' : 'Add Book' }}
        </button>

        <div v-if="successMessage" class="success-msg">
          {{ successMessage }}
        </div>
        <div v-if="errorMessage" class="error-msg">
          {{ errorMessage }}
        </div>
      </form>
    <div class="admin-section">
      <h2>Dormitory Management - Add Room</h2>
      <form @submit.prevent="addRoom" class="admin-form">
        <div class="form-row">
          <div class="form-group">
            <label for="roomNumber">Room Number</label>
            <input type="text" id="roomNumber" v-model="newRoom.roomNumber" required placeholder="e.g. 101" />
          </div>
          <div class="form-group">
            <label for="building">Building</label>
            <input type="text" id="building" v-model="newRoom.building" required placeholder="e.g. North Hall" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label for="capacity">Capacity</label>
            <input type="number" id="capacity" v-model="newRoom.capacity" required min="1" />
          </div>
          <div class="form-group">
            <label for="amenities">Amenities (comma separated)</label>
            <input type="text" id="amenities" v-model="amenitiesInput" placeholder="e.g. Wi-Fi, AC" />
          </div>
        </div>
        <button type="submit" class="btn btn-dormitory" :disabled="isSubmittingDorm">
          {{ isSubmittingDorm ? 'Adding...' : 'Add Room' }}
        </button>
        <div v-if="dormSuccess" class="success-msg">{{ dormSuccess }}</div>
        <div v-if="dormError" class="error-msg">{{ dormError }}</div>
      </form>
    </div>

    <div class="admin-section rooms-list-section">
      <h2>All Rooms</h2>
      <button @click="fetchRooms" class="btn btn-primary" style="margin-bottom: 15px;">Refresh Rooms</button>
      <table v-if="rooms.length > 0" class="rooms-table">
        <thead>
          <tr>
            <th>Room Number</th>
            <th>Building</th>
            <th>Capacity</th>
            <th>Occupancy</th>
            <th>Amenities</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="room in rooms" :key="room.roomId">
            <td>{{ room.roomNumber }}</td>
            <td>{{ room.building }}</td>
            <td>{{ room.capacity }}</td>
            <td>{{ room.currentOccupancy }} / {{ room.capacity }}</td>
            <td>{{ room.amenities ? room.amenities.join(', ') : '' }}</td>
            <td>
              <span :class="room.isAvailable && room.currentOccupancy < room.capacity ? 'status-available' : 'status-full'">
                {{ room.isAvailable && room.currentOccupancy < room.capacity ? 'Available' : 'Full' }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else>No rooms found. Add a room above to get started.</p>
    </div>
  </div>
  </main>
</template>

<script>
export default {
  name: 'AdminView',
  data() {
    return {
      newBook: {
        title: ''
      },
      isSubmitting: false,
      successMessage: '',
      errorMessage: '',
      newRoom: {
        roomNumber: '',
        building: '',
        capacity: 1
      },
      amenitiesInput: '',
      isSubmittingDorm: false,
      dormSuccess: '',
      dormError: '',
      rooms: []
    }
  },
  mounted() {
    this.fetchRooms();
  },
  methods: {
    async addBook() {
      this.isSubmitting = true;
      this.successMessage = '';
      this.errorMessage = '';
      
      try {
        const response = await fetch('/api/library/books', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.newBook)
        });
        
        if (!response.ok) {
          let errorMsg = `HTTP error! status: ${response.status}`;
          try {
            const errorData = await response.json();
            if (errorData.message) errorMsg = errorData.message;
          } catch(e) { }
          throw new Error(errorMsg);
        }
        
        const addedBook = await response.json();
        this.successMessage = `Successfully added "${addedBook.title}" (ID: ${addedBook.bookId})`;
        this.newBook.title = ''; // Reset form
      } catch (error) {
        this.errorMessage = 'Failed to add book: ' + error.message;
        console.error('Error adding book:', error);
      } finally {
        this.isSubmitting = false;
      }
    },
    async addRoom() {
      this.isSubmittingDorm = true;
      this.dormSuccess = '';
      this.dormError = '';
      
      const payload = {
        ...this.newRoom,
        amenities: this.amenitiesInput.split(',').map(a => a.trim()).filter(a => a)
      };

      try {
        const response = await fetch('/api/dormitory/rooms', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(payload)
        });
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const addedRoom = await response.json();
        this.dormSuccess = `Successfully added Room ${addedRoom.roomNumber} in ${addedRoom.building}`;
        this.newRoom = {
          roomNumber: '',
          building: '',
          capacity: 1
        };
        this.amenitiesInput = '';
        this.fetchRooms();
      } catch (error) {
        this.dormError = 'Failed to add room: ' + error.message;
      } finally {
        this.isSubmittingDorm = false;
      }
    },
    async fetchRooms() {
      try {
        const response = await fetch('/api/dormitory/rooms');
        if (response.ok) {
          this.rooms = await response.json();
        }
      } catch (error) {
        console.error('Failed to fetch rooms:', error);
      }
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

h1 {
  color: #333;
  border-bottom: 3px solid #6c757d;
  padding-bottom: 10px;
  margin-bottom: 30px;
}

.admin-section {
  background-color: white;
  padding: 30px;
  margin-top: 1.1rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  border: 1px solid #eaeaea;
}

h2 {
  color: #555;
  margin-top: 0;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 8px;
  color: #444;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 16px;
  box-sizing: border-box;
}

input:focus {
  border-color: #007bff;
  outline: none;
  box-shadow: 0 0 0 3px rgba(0,123,255,0.25);
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

.btn-primary {
  background-color: #6c757d;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #5a6268;
}

.btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.success-msg {
  margin-top: 20px;
  padding: 15px;
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
  border-radius: 4px;
}

.error-msg {
  margin-top: 20px;
  padding: 15px;
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
}

.form-row .form-group {
  flex: 1;
}

.btn-dormitory {
  background-color: #6c5ce7;
  color: white;
}

.btn-dormitory:hover:not(:disabled) {
  background-color: #5b4bc4;
}

select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 16px;
}

.rooms-list-section {
  margin-top: 30px;
}

.rooms-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 15px;
}

.rooms-table th,
.rooms-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.rooms-table th {
  background-color: #f8f9fa;
  font-weight: bold;
  color: #333;
}

.status-available {
  color: #28a745;
  font-weight: bold;
}

.status-full {
  color: #dc3545;
  font-weight: bold;
}
</style>
