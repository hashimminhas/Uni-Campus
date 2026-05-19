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

### 1. Start Everything (Backend)
```bash
docker compose up -d
```
This starts **all services** — PostgreSQL, RabbitMQ, API Gateway, and all 8 microservices. No `mvn spring-boot:run` needed.

To rebuild a specific service after code changes:
```bash
docker compose up -d --build <service-name>
# e.g. docker compose up -d --build student-service
```

To stop everything:
```bash
docker compose down
```

### 2. Start the Frontend
```bash
cd frontend
npm install       # first time only
npm run dev
```
Open `http://localhost:5173` in your browser.
If `localhost` fails on your machine, use `http://127.0.0.1:5173/` instead.

### 3. Access Swagger UI
Each service exposes Swagger UI directly:
| Service              | Swagger URL                              |
|----------------------|------------------------------------------|
| Student Service      | http://localhost:8081/swagger-ui.html    |
| Notification Service | http://localhost:8082/swagger-ui.html    |
| Course Service       | http://localhost:8083/swagger-ui.html    |
| Exam Service         | http://localhost:8084/swagger-ui.html    |
| Library Service      | http://localhost:8085/swagger-ui.html    |
| Billing Service      | http://localhost:8086/swagger-ui.html    |
| Dormitory Service    | http://localhost:8087/swagger-ui.html    |
| Meal Plan Service    | http://localhost:8088/swagger-ui.html    |

### 4. RabbitMQ Management
`http://localhost:15672` — login: `guest / guest`

## Port Assignments
| Service              | Port  |
|----------------------|-------|
| API Gateway          | 8080  |
| Student Service      | 8081  |
| Notification Service | 8082  |
| Course Service       | 8083  |
| Exam Service         | 8084  |
| Library Service      | 8085  |
| Billing Service      | 8086  |
| Dormitory Service    | 8087  |
| Meal Plan Service    | 8088  |
| PostgreSQL           | 5433  |
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

---

## How to Send Notifications from Your Service (RabbitMQ)

The Notification Service listens on RabbitMQ queues automatically. When something happens in your service (enroll, borrow, pay, etc.), publish a message to the right exchange and the student will see it in the bell dropdown on the frontend. **You do not call the Notification Service directly.**

### Step 1 — Add dependency to your `pom.xml` (if not already there)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

### Step 2 — Add JSON converter + RabbitTemplate to your `RabbitMQConfig.java`
```java
@Bean
public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
}

@Bean
public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jsonMessageConverter());
    return template;
}
```

### Step 3 — Inject RabbitTemplate into your service class
```java
private final RabbitTemplate rabbitTemplate;
```

### Step 4 — Publish the event inside your method

Use the exact exchange name, routing key, and JSON fields shown below for your service.

---

### Sudais — Course Service
Publish when a student **enrolls** in a course:
```java
rabbitTemplate.convertAndSend(
    "course.events",
    "enrollment.confirmed",
    Map.of(
        "studentId", studentId,         // UUID
        "courseCode", "CS301",          // String
        "courseName", "Algorithms",     // String
        "action", "ENROLLED"            // String — use "DROPPED" when dropping
    )
);
```
Publish when a student **drops** a course — same code but `"action", "DROPPED"`.

### Sudais — Exam Service
Publish when an exam is **scheduled** for a student:
```java
rabbitTemplate.convertAndSend(
    "exam.events",
    "exam.scheduled",
    Map.of(
        "studentId",  studentId,        // UUID
        "courseCode", "CS301",          // String
        "courseName", "Algorithms",     // String
        "examDate",   "2026-06-05",     // String
        "roomNumber", "A2"              // String
    )
);
```

### Daboikiabo — Dormitory & Billing
These queues are not wired yet. Message Hashim and he will add the bindings in 5 minutes. Then publish like this:

**Dormitory** — when a room is assigned:
```java
rabbitTemplate.convertAndSend(
    "dormitory.events",
    "room.assigned",
    Map.of(
        "studentId",  studentId,        // UUID
        "roomNumber", "B204",           // String
        "startDate",  "2026-09-01"      // String
    )
);
```

**Billing** — when an invoice is created:
```java
rabbitTemplate.convertAndSend(
    "billing.events",
    "invoice.created",
    Map.of(
        "studentId", studentId,         // UUID
        "amount",    "500.00",          // String
        "dueDate",   "2026-06-01"       // String
    )
);
```

### How to verify it works
1. Run your service: `docker compose up -d --build <your-service-name>`
2. Call your endpoint (e.g. enroll a student)
3. Open `http://localhost:15672` → Queues — the message count will briefly spike then drop to 0 (consumed instantly)
4. Log in to `http://localhost:5173` with that student's UUID → click the bell — notification appears

