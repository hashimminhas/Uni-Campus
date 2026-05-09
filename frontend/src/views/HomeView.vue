<template>
  <main class="container">
    <h1>UniCampus - Courses</h1>

    <div v-if="loading" class="loading">
      <p>Loading courses...</p>
    </div>

    <div v-else-if="error" class="error">
      <p>Error: {{ error }}</p>
    </div>

    <div v-else class="courses-section">
      <h2>Available Courses</h2>

      <div v-if="courses.length === 0" class="no-courses">
        <p>No courses available</p>
      </div>

      <div v-else class="courses-list">
        <div v-for="course in courses" :key="course.courseId" class="course-card">
          <div class="course-header">
            <h3>{{ course.name }}</h3>
            <span class="course-id">ID: {{ course.courseId }}</span>
          </div>

          <div class="course-details">
            <p><strong>Instructor:</strong> {{ course.instructor }}</p>
            <p><strong>Credits:</strong> {{ course.credits }}</p>
            <p><strong>Semester:</strong> {{ course.semester }}</p>
            <p><strong>Status:</strong> <span class="status" :class="course.status.toLowerCase()">{{ course.status }}</span></p>
            <p><strong>Capacity:</strong> {{ course.enrolledCount }} / {{ course.capacity }}</p>
          </div>

          <div class="course-actions">
            <button class="btn btn-primary" @click="enrollCourse(course.courseId)"
                    v-if="course.status === 'OPEN' && course.enrolledCount < course.capacity">
              Enroll
            </button>
            <button v-else class="btn btn-disabled" disabled>
              {{ course.status === 'OPEN' ? 'Course Full' : 'Course ' + course.status }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
export default {
  name: 'HomeView',
  data() {
    return {
      courses: [],
      loading: true,
      error: null,
    }
  },
  mounted() {
    this.fetchCourses();
  },
  methods: {
    async fetchCourses() {
      try {
        this.loading = true;
        this.error = null;
        
        const response = await fetch('/courses');
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        this.courses = data;
        console.log('Courses loaded:', this.courses);
      } catch (error) {
        this.error = error.message || 'Failed to load courses';
        console.error('Error fetching courses:', error);
      } finally {
        this.loading = false;
      }
    },
    
    enrollCourse(courseId) {
      alert(`Enrollment for course ${courseId} not yet implemented`);
      // TODO: Implement enrollment with student ID
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
  border-bottom: 3px solid #007bff;
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

.courses-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.course-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
}

.course-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.course-header {
  margin-bottom: 15px;
}

.course-header h3 {
  margin: 0;
  color: #007bff;
  font-size: 18px;
}

.course-id {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
  display: block;
}

.course-details {
  margin-bottom: 15px;
  line-height: 1.8;
}

.course-details p {
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

.status.open {
  background-color: #d4edda;
  color: #155724;
}

.status.full {
  background-color: #fff3cd;
  color: #856404;
}

.status.closed {
  background-color: #f8d7da;
  color: #721c24;
}

.course-actions {
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
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-disabled {
  background-color: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
}

.no-courses {
  text-align: center;
  padding: 40px;
  color: #999;
  font-style: italic;
}
</style>
