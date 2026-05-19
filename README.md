# UniCampus — Smart University Campus Management System

## Team
| Member     | Services                              |
|------------|---------------------------------------|
| Hashim     | Student Service, Notification Service |
| Sudais     | Course Service, Exam Service          |
| Calvin     | Library Service, Billing Service      |
| Daboikiabo | Dormitory Service, Meal Plan Service  |

## Tech Stack
- **Backend:** Java 21, Spring Boot 3.4.x
- **REST API:** Spring Web (MVC)
- **Persistence:** Spring Data JPA + PostgreSQL
- **Async Messaging:** RabbitMQ (Spring AMQP) with Jackson2JsonMessageConverter
- **Inter-service Calls:** RestClient / RestTemplate / WebClient
- **API Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Security:** Spring Security 6 + JWT (stateless, per-service)
- **Testing:** JUnit 5 + MockMvc + Mockito (@WebMvcTest)
- **Boilerplate:** Lombok
- **Frontend:** Vue.js 3 (Vite) + Vue Router + native fetch()
- **Deployment:** Docker + Docker Compose (12 containers)

---

## How to Run

### Start Everything
```bash
docker compose up -d --build
```
Starts all 12 containers: PostgreSQL, RabbitMQ, API Gateway, 8 microservices, and the frontend.

### Stop Everything
```bash
docker compose down
```

### Rebuild a Single Service After Code Changes
```bash
docker compose build <service-name>
docker compose up -d <service-name>
```

---

## Access Points

| What | URL |
|---|---|
| **Frontend** | http://localhost:5173 |
| **API Gateway** | http://localhost:8080 |
| **RabbitMQ Management** | http://localhost:15672 (guest / guest) |

### Swagger UI (per service)
| Service | URL |
|---|---|
| Student Service | http://localhost:8081/swagger-ui.html |
| Notification Service | http://localhost:8082/swagger-ui.html |
| Course Service | http://localhost:8083/swagger-ui.html |
| Exam Service | http://localhost:8084/swagger-ui.html |
| Library Service | http://localhost:8085/swagger-ui.html |
| Billing Service | http://localhost:8086/swagger-ui.html |
| Dormitory Service | http://localhost:8087/swagger-ui.html |
| Meal Plan Service | http://localhost:8088/swagger-ui.html |

---

## Port Assignments
| Service | Port |
|---|---|
| API Gateway | 8080 |
| Student Service | 8081 |
| Notification Service | 8082 |
| Course Service | 8083 |
| Exam Service | 8084 |
| Library Service | 8085 |
| Billing Service | 8086 |
| Dormitory Service | 8087 |
| Meal Plan Service | 8088 |
| PostgreSQL | 5433 (host) / 5432 (internal) |
| RabbitMQ | 5672 |
| RabbitMQ Management | 15672 |
| Frontend | 5173 |

---

## Security

Every service uses stateless JWT authentication via Spring Security. Tokens are issued per service using a shared secret.

### Get an Admin Token (Student Service)
```
POST http://localhost:8081/students/auth/admin
{ "username": "admin", "password": "admin" }
```

### Get a Student Token
```
POST http://localhost:8081/students/auth/login
{ "studentId": "<uuid>" }
```

Use the returned token as `Authorization: Bearer <token>` on protected endpoints.
In Swagger UI, click **Authorize** and paste the token (no "Bearer " prefix needed).

---

## Frontend Login

1. Register a student at `http://localhost:5173/students`
2. Copy the returned UUID
3. Paste it in the login box in the navbar → Log in

---

## Seed Data (run after fresh start)

After `docker compose up -d --build`, the database is empty. Use the following to populate test data.

### Courses (admin token from course-service required)
```
POST http://localhost:8083/courses
{ "name": "Introduction to Computer Science", "instructor": "Dr. Ahmed Hassan", "capacity": 30, "semester": "Fall 2026", "credits": 3 }
{ "name": "Data Structures and Algorithms", "instructor": "Dr. Sara Malik", "capacity": 25, "semester": "Fall 2026", "credits": 4 }
{ "name": "Software Engineering Principles", "instructor": "Dr. James Lee", "capacity": 35, "semester": "Fall 2026", "credits": 3 }
```

### Exams (admin token from exam-service required)
```
POST http://localhost:8084/exams
{ "courseId": "<courseId>", "examDate": "2026-06-15T09:00:00", "location": "Hall A - Room 101", "durationMinutes": 120 }
{ "courseId": "<courseId>", "examDate": "2026-06-18T14:00:00", "location": "Hall B - Room 203", "durationMinutes": 150 }
{ "courseId": "<courseId>", "examDate": "2026-06-22T10:30:00", "location": "Hall C - Room 305", "durationMinutes": 90 }
```

### Dormitory Rooms (no auth required)
```
POST http://localhost:8087/dormitory/rooms
{ "roomId": "11111111-1111-1111-1111-111111111101", "roomNumber": "A101", "building": "Alpha", "capacity": 2, "currentOccupancy": 0, "amenities": ["WiFi","AC","Study Desk"], "isAvailable": true }
{ "roomId": "11111111-1111-1111-1111-111111111102", "roomNumber": "A102", "building": "Alpha", "capacity": 2, "currentOccupancy": 0, "amenities": ["WiFi","AC","Wardrobe"], "isAvailable": true }
{ "roomId": "11111111-1111-1111-1111-111111111103", "roomNumber": "B201", "building": "Beta",  "capacity": 1, "currentOccupancy": 0, "amenities": ["WiFi","Private Bathroom","AC"], "isAvailable": true }
{ "roomId": "11111111-1111-1111-1111-111111111104", "roomNumber": "B202", "building": "Beta",  "capacity": 3, "currentOccupancy": 0, "amenities": ["WiFi","Shared Kitchen","Study Room"], "isAvailable": true }
{ "roomId": "11111111-1111-1111-1111-111111111105", "roomNumber": "C301", "building": "Gamma", "capacity": 2, "currentOccupancy": 0, "amenities": ["WiFi","AC","Gym Access","Laundry"], "isAvailable": true }
```

### Meal Plans (no auth required)
```
POST http://localhost:8088/meal-plan/plans
{ "name": "Basic Plan",     "mealsPerWeek": 10, "price": 89.99,  "semester": "Fall 2026" }
{ "name": "Standard Plan",  "mealsPerWeek": 14, "price": 129.99, "semester": "Fall 2026" }
{ "name": "Premium Plan",   "mealsPerWeek": 21, "price": 179.99, "semester": "Fall 2026" }
{ "name": "Unlimited Plan", "mealsPerWeek": 28, "price": 219.99, "semester": "Fall 2026" }
```

### Library Books (admin token from course-service required)
```
POST http://localhost:8085/api/library/books
{ "title": "Clean Code", "isAvailable": true }
{ "title": "The Pragmatic Programmer", "isAvailable": true }
{ "title": "Introduction to Algorithms", "isAvailable": true }
{ "title": "Design Patterns", "isAvailable": true }
{ "title": "Computer Networks", "isAvailable": true }
{ "title": "Operating System Concepts", "isAvailable": true }
{ "title": "Database System Concepts", "isAvailable": true }
{ "title": "Artificial Intelligence: A Modern Approach", "isAvailable": true }
{ "title": "The Mythical Man-Month", "isAvailable": true }
{ "title": "Code Complete", "isAvailable": true }
{ "title": "Refactoring", "isAvailable": true }
{ "title": "Structure and Interpretation of Computer Programs", "isAvailable": true }
{ "title": "Software Engineering", "isAvailable": true }
{ "title": "Computer Organization and Architecture", "isAvailable": true }
{ "title": "Discrete Mathematics and Its Applications", "isAvailable": true }
```

---

## Architecture

```
Browser
  |
  v
API Gateway :8080  (Spring Cloud Gateway — routes /api/* to services, StripPrefix=1)
  |
  +-- /api/students/*     --> Student Service      :8081
  +-- /api/notifications/*--> Notification Service :8082
  +-- /api/courses/*      --> Course Service       :8083
  +-- /api/exams/*        --> Exam Service         :8084
  +-- /api/library/*      --> Library Service      :8085
  +-- /api/billing/*      --> Billing Service      :8086
  +-- /api/dormitory/*    --> Dormitory Service    :8087
  +-- /api/meal-plan/*    --> Meal Plan Service    :8088
```

### Async Events (RabbitMQ)
| Event | Exchange | Producer | Consumer |
|---|---|---|---|
| enrollment.confirmed | course.events | Course Service | Notification Service |
| course.dropped | course.events | Course Service | Notification Service |
| exam.scheduled | exam.events | Exam Service | Notification Service |
| book.overdue | library.events | Library Service | Billing + Notification |
| housing.fee.charged | billing.events | Dormitory Service | Billing Service |
| mealplan.fee.charged | billing.events | Meal Plan Service | Billing Service |

### Synchronous REST Calls
| Caller | Called | Purpose |
|---|---|---|
| Course Service | Student Service | Validate student before enrollment |
| Exam Service | Course Service | Get student enrolled courses |
| Library Service | Student Service | Validate student before borrow |
| Billing Service | Student + Course | Tuition calculation |
| Dormitory Service | Student Service | Validate student before room assign |
| Meal Plan Service | Student Service | Validate student before subscribe |

---

## Project Structure (per microservice)
```
<service-name>/
├── pom.xml
├── Dockerfile
└── src/main/java/com/unicampus/<service>/
    ├── <Service>Application.java
    ├── controller/    — REST endpoints
    ├── dto/           — Request/Response objects
    ├── service/       — Business logic
    ├── repository/    — Data access
    ├── domain/        — JPA entities
    ├── exception/     — Custom exceptions
    └── config/        — Security, RabbitMQ, OpenAPI
```
