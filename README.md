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

### 3. Access Swagger UI
Each service has Swagger UI at:
`http://localhost:<PORT>/swagger-ui.html`

### 4. RabbitMQ Management
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

## Checkpoints
- **CP1 (May 5):** 1 microservice fully implemented per student (backend only)
- **CP2 (May 12):** 2nd service + integration + basic frontend
- **CP3 (May 19):** Full system + security + Docker + async messaging
