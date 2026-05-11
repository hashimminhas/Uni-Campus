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
            <label for="roomType">Type</label>
            <select id="roomType" v-model="newRoom.type" required>
              <option value="Single">Single</option>
              <option value="Double">Double</option>
              <option value="Suite">Suite</option>
            </select>
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label for="capacity">Capacity</label>
            <input type="number" id="capacity" v-model="newRoom.capacity" required min="1" />
          </div>
          <div class="form-group">
            <label for="price">Price / Semester</label>
            <input type="number" id="price" v-model="newRoom.pricePerSemester" required min="0" />
          </div>
        </div>
        <button type="submit" class="btn btn-dormitory" :disabled="isSubmittingDorm">
          {{ isSubmittingDorm ? 'Adding...' : 'Add Room' }}
        </button>
        <div v-if="dormSuccess" class="success-msg">{{ dormSuccess }}</div>
        <div v-if="dormError" class="error-msg">{{ dormError }}</div>
      </form>
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
        type: 'Single',
        capacity: 1,
        pricePerSemester: 0,
        isAvailable: true,
        currentOccupancy: 0
      },
      isSubmittingDorm: false,
      dormSuccess: '',
      dormError: ''
    }
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
      
      try {
        const response = await fetch('/api/dormitory/rooms', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.newRoom)
        });
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const addedRoom = await response.json();
        this.dormSuccess = `Successfully added Room ${addedRoom.roomNumber}`;
        this.newRoom = {
          roomNumber: '',
          type: 'Single',
          capacity: 1,
          pricePerSemester: 0,
          isAvailable: true,
          currentOccupancy: 0
        };
      } catch (error) {
        this.dormError = 'Failed to add room: ' + error.message;
      } finally {
        this.isSubmittingDorm = false;
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
</style>
