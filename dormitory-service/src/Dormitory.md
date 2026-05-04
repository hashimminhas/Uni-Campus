# Checkpoint 1: Dormitory Service Implementation Plan

This plan outlines the steps to complete **Checkpoint 1** for the **Dormitory Service**, ensuring all requirements (Running Service, API Implementation, OpenAPI, Persistence, Testing, and API Demonstration) are fully met.

## User Review Required

> [!IMPORTANT]
> Please review the following technical decisions before execution:
> 1. **Database Selection**: I plan to use an **H2 in-memory database** for Checkpoint 1 to ensure immediate testability and persistence without requiring external infrastructure. If you prefer **PostgreSQL/MySQL**, please let me know.
> 2. **Service Communication**: I will use **RestTemplate** (or `RestClient` in Spring Boot 3.2+) for the synchronous call to the Student Service. OpenFeign is another option if you prefer.
> 3. **RabbitMQ Event Publishing**: Since Checkpoint 1 focuses on API and Persistence, I will implement the Message Broker publisher interface but mock or log the payload for `housing.fee.charged`. Let me know if you want the actual RabbitMQ integration completed in this phase.

## Proposed Changes

### Project Configuration
#### [MODIFY] `pom.xml`
Add necessary Spring Boot starters:
- `spring-boot-starter-web` (REST APIs)
- `spring-boot-starter-data-jpa` (Persistence)
- `h2` (Database)
- `spring-boot-starter-validation` (Input validation)
- `springdoc-openapi-starter-webmvc-ui` (Swagger/OpenAPI documentation)
- `spring-boot-starter-test` (Testing framework)

#### [MODIFY] `application.properties`
Configure server port, database connection (H2 console enabled), and JPA properties.

---

### Data Model & Persistence
Implementation of the database schema as defined in the assignment (`dormitory_db`).

#### [NEW] `src/main/java/com/unicampus/dormitory/model/Room.java`
Entity representing the Room aggregate root:
- `roomId` (UUID, PK)
- `roomNumber` (String), `building` (String), `capacity` (Integer)
- `currentOccupancy` (Integer)
- `amenities` (List<String>)
- `isAvailable` (Boolean)

#### [NEW] `src/main/java/com/unicampus/dormitory/model/RoomAssignment.java`
Entity representing the RoomAssignment:
- `assignmentId` (UUID, PK)
- `roomId` (UUID, FK)
- `studentId` (UUID)
- `semester` (String), `assignmentDate` (Date)
- `status` (Enum: ACTIVE, COMPLETED, CANCELLED)

#### [NEW] `src/main/java/com/unicampus/dormitory/repository/RoomRepository.java`
#### [NEW] `src/main/java/com/unicampus/dormitory/repository/RoomAssignmentRepository.java`
Spring Data JPA repositories for database operations.

---

### Service Layer & Integration
#### [NEW] `src/main/java/com/unicampus/dormitory/service/StudentServiceClient.java`
A component responsible for synchronously calling the Student Service `GET /students/validate/{id}` to verify identity.

#### [NEW] `src/main/java/com/unicampus/dormitory/service/DormitoryService.java`
Contains the core business logic. Specifically handles Workflow 2 (Dormitory Room Assignment):
1. Verifies student via `StudentServiceClient`.
2. Checks room capacity and availability.
3. Creates a `RoomAssignment` and updates `Room.isAvailable`.
4. (Simulated) publishes `housing.fee.charged` event.

---

### Controllers (API Endpoints)
#### [NEW] `src/main/java/com/unicampus/dormitory/controller/DormitoryController.java`
Implements all required REST endpoints matching the Base URL `/api/dormitory`:
- `POST /rooms` (Add new room)
- `GET /rooms` (Retrieve all rooms)
- `GET /rooms/{roomId}` (Room details)
- `POST /rooms/{roomId}/assign` (Assign room to student - interacts with Student Service)
- `GET /assignments/student/{studentId}` (Student's assignment)
- `DELETE /assignments/{assignmentId}` (Remove assignment)
- `GET /rooms/{roomId}/availability` (Check availability)
- `GET /students/{studentId}/eligibility` (Check eligibility)

---

### Testing
#### [NEW] `src/main/java/com/unicampus/dormitory/controller/DormitoryControllerTest.java`
Implements the required `@WebMvcTest`. 
- **Target Endpoint**: `POST /rooms/{roomId}/assign`
- **Dependencies Mocked**: `DormitoryService` (or `StudentServiceClient` if testing the service layer directly).
- **Happy Path Test**: Simulates Student Service returning `200 OK` (active student). Verifies the response returns `201 Created` with assignment details.
- **Error Case Test**: Simulates Student Service returning `404 Not Found` or `403 Forbidden` (inactive student). Verifies the response returns `400 Bad Request` or `403 Forbidden` and no assignment is created.

## Verification Plan

### Automated Tests
- Run `mvn clean test` to ensure all `@WebMvcTest` cases pass successfully.

### Manual Verification
1. Start the application locally via `mvn spring-boot:run`.
2. Access the **Swagger UI** at `http://localhost:8080/swagger-ui.html`.
3. Manually test endpoints using the Swagger UI:
   - Create a room.
   - Attempt to assign a student to the room.
   - Verify the database saves the assignment (can be viewed via H2 console).
