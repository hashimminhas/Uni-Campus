# UniCampus — Smart University Campus Management System

## Team
| Member       | Services                              |
|--------------|---------------------------------------|
| Hashim       | Student Service, Notification Service |
| Sudais       | Course Service, Exam Service          |
| Calvin       | Library Service, Billing Service      |
| Daboikiabo   | Dormitory Service, Meal Plan Service  |

## Tech Stack
- **Backend:** Java 21, Spring Boot 3.4.x
- **REST API:** Spring Web
- **Persistence:** Spring Data JPA (CrudRepository) + PostgreSQL
- **Async Messaging:** RabbitMQ (Spring AMQP)
- **Inter-service Calls:** WebClient (Spring WebFlux) — add dependency when needed
- **API Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Testing:** JUnit 5 + MockMvc + Mockito (@WebMvcTest)
- **Boilerplate:** Lombok (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor)
- **Frontend:** Vue.js 3 (Vite) + Vue Router + native fetch() API
- **Security:** Spring Security + JWT (Checkpoint 3)
- **Deployment:** Docker + Docker Compose

## How to Run

### 1. Start Infrastructure
```bash
docker-compose up -d
```
This starts PostgreSQL (with all 8 databases) and RabbitMQ.

### 2. Run Your Service
Open your service folder in IntelliJ or VS Code, then:
```bash
cd <your-service>/
mvn spring-boot:run
```

<<<<<<< HEAD
### 3. Run Service Tests
Each backend service can run its tests without starting PostgreSQL because tests use an in-memory H2 database:
```bash
cd <your-service>/
mvn test
```

> **What changed in the test setup (April 28 2026):**
> - Added H2 (in-memory DB) as a `test`-scoped dependency to every service `pom.xml`
> - Added `src/test/resources/application.yml` per service — overrides the PostgreSQL config during tests only; production config is unchanged
> - Upgraded frontend from **Vite 5 → Vite 8** and **@vitejs/plugin-vue 5 → 6** (fixed a known esbuild vulnerability)
> - Switched frontend `Dockerfile` from `Node 18 + npm install` to `Node 22 + npm ci` for reproducible installs
> - Added `frontend/package-lock.json` so everyone installs the exact same dependency versions
> - Removed obsolete `version: '3.8'` from `docker-compose.yml`

### 4. Run the Frontend
```bash
cd frontend/
npm ci
npm run dev
```

### 5. Access Swagger UI
Each service has Swagger UI at:
`http://localhost:<PORT>/swagger-ui.html`

### 6. RabbitMQ Management
=======
### 3. Access Swagger UI
Each service has Swagger UI at:
`http://localhost:<PORT>/swagger-ui.html`

### 4. RabbitMQ Management
>>>>>>> 88c5159d1d6c6c9986b072b09071f42a71155d35
`http://localhost:15672` (guest/guest)

## Port Assignments
| Service              | Port  |
|----------------------|-------|
| Student Service      | 8081  |
| Notification Service | 8082  |
| Course Service       | 8083  |
| Exam Service         | 8084  |
| Library Service      | 8085  |
| Billing Service      | 8086  |
| Dormitory Service    | 8087  |
| Meal Plan Service    | 8088  |
| PostgreSQL           | 5432  |
| RabbitMQ             | 5672  |
| RabbitMQ Management  | 15672 |
| Frontend (Dev)       | 5173  |

## Project Structure (per microservice)
Each microservice follows the Controller-Service-Repository (CSR) pattern:
```
<service-name>/
├── pom.xml
├── Dockerfile
└── src/main/java/com/unicampus/<service>/
    ├── <Service>Application.java
    ├── controller/    ← REST endpoints
    ├── dto/           ← Request/Response objects
    ├── service/       ← Business logic
    ├── repository/    ← Data access (CrudRepository)
    ├── domain/        ← JPA entities
    ├── exception/     ← Custom exceptions + @ControllerAdvice
    └── config/        ← OpenAPI, RabbitMQ, etc.
```

## Implementation Status

### Student Service
The Student Service is fully implemented. It covers all 6 endpoints required by Assignment 3:
register a student, get by ID, update profile, delete, update academic status, and validate.
All endpoints are tested (5 unit tests with `@WebMvcTest`, no database needed), documented on Swagger UI at `http://localhost:8081/swagger-ui.html`, and verified end-to-end through manual testing.

**How other services access student data:**
No service is allowed to query `student_db` directly. Instead, they call the Student Service over HTTP.
The main integration point is the validate endpoint before a student can enrol in a course, sit an exam, or borrow a book, the responsible service calls `GET /students/validate/{studentId}`.
It returns `valid: true` only if the student exists and their status is `ACTIVE`. If the student is suspended, graduated, or not found, the calling service gets a clear response and can reject the request with a meaningful error.
For cases where the full profile is needed (name, email, program), services call `GET /students/{studentId}` instead.
This pattern keeps each service independent they share data through APIs, not shared databases.

