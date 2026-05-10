<template>
  <main class="container">
    <h1>UniCampus - Library</h1>

    <div v-if="loading" class="loading">
      <p>Loading books...</p>
    </div>

    <div v-else-if="error" class="error">
      <p>Error: {{ error }}</p>
    </div>

    <div v-else class="books-section">
      <h2>Available Books</h2>

      <div v-if="books.length === 0" class="no-books">
        <p>No books available in the library</p>
      </div>

      <div v-else class="books-list">
        <div v-for="book in books" :key="book.bookId" class="book-card">
          <div class="book-header">
            <h3>{{ book.title }}</h3>
            <span class="book-author">ID: {{ book.bookId }}</span>
          </div>

          <div class="book-details">
            <p><strong>Status:</strong> <span class="status" :class="book.isAvailable ? 'available' : 'unavailable'">{{ book.isAvailable ? 'Available' : 'Unavailable' }}</span></p>
          </div>

          <div class="book-actions">
            <button class="btn btn-primary" @click="borrowBook(book.bookId)"
                    v-if="book.isAvailable">
              Borrow
            </button>
            <button v-else class="btn btn-disabled" disabled>
              Unavailable
            </button>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
export default {
  name: 'LibraryView',
  data() {
    return {
      books: [],
      loading: true,
      error: null,
    }
  },
  mounted() {
    this.fetchBooks();
  },
  methods: {
    async fetchBooks() {
      try {
        this.loading = true;
        this.error = null;
        
        const response = await fetch('/api/library/books');
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        this.books = data;
        console.log('Books loaded:', this.books);
      } catch (error) {
        this.error = error.message || 'Failed to load books';
        console.error('Error fetching books:', error);
      } finally {
        this.loading = false;
      }
    },
    
    async borrowBook(bookId) {
      // Since we don't have login, we'll prompt for a UUID
      const studentId = prompt("Please enter your Student ID (UUID) to borrow this book:");
      
      if (!studentId) return; // User cancelled

      try {
        const response = await fetch(`/api/library/books/${bookId}/borrow`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ studentId })
        });
        
        if (!response.ok) {
          let errorMsg = `HTTP error! status: ${response.status}`;
          try {
            const errorData = await response.json();
            if (errorData.message) errorMsg = errorData.message;
          } catch(e) {
            // response was not JSON
          }
          throw new Error(errorMsg);
        }
        
        alert('Book borrowed successfully!');
        // Refresh the list to show updated availability
        this.fetchBooks();
      } catch (error) {
        alert('Failed to borrow book: ' + error.message);
        console.error('Error borrowing book:', error);
      }
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

h1 {
  color: #333;
  border-bottom: 3px solid #28a745;
  padding-bottom: 10px;
  margin-bottom: 30px;
}

h2 {
  color: #555;
  margin-bottom: 20px;
}

.loading, .error {
  text-align: center;
  padding: 40px;
  font-size: 16px;
}

.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}

.books-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.book-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
}

.book-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.book-header {
  margin-bottom: 15px;
}

.book-header h3 {
  margin: 0;
  color: #28a745;
  font-size: 18px;
}

.book-author {
  color: #666;
  font-style: italic;
  font-size: 14px;
  display: block;
  margin-top: 5px;
}

.book-details {
  margin-bottom: 15px;
  line-height: 1.8;
}

.book-details p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
}

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: bold;
  font-size: 12px;
}

.status.available {
  background-color: #d4edda;
  color: #155724;
}

.status.unavailable {
  background-color: #f8d7da;
  color: #721c24;
}

.book-actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  transition: background-color 0.3s ease;
  flex: 1;
  text-align: center;
}

.btn-primary {
  background-color: #28a745;
  color: white;
}

.btn-primary:hover {
  background-color: #218838;
}

.btn-disabled {
  background-color: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
}

.no-books {
  text-align: center;
  padding: 40px;
  color: #999;
  font-style: italic;
}
</style>
