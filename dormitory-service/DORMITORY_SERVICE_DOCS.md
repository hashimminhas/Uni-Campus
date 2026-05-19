# Dormitory Service: Complete Implementation Guide

This document explains every single change, addition, and implementation decision made to build the **Dormitory Service** from scratch to a fully functional Checkpoint 1 state.

---

## 1. Project Configuration & Dependencies

### `pom.xml` Updates
To give the barebones Spring Boot app the tools it needed, I added several key dependencies:
- **`spring-boot-starter-web`**: To create REST APIs (Controllers, HTTP responses).
- **`spring-boot-starter-data-jpa`**: To handle database interactions (Repositories, saving/loading data).
- **`postgresql`**: The official database driver to allow Spring to talk to your `unicampus-postgres` container.
- **`springdoc-openapi-starter-webmvc-ui`**: To automatically generate the Swagger UI documentation at `/swagger-ui.html`.
- **`lombok`**: To auto-generate getters, setters, and constructors (using `@Data`, `@RequiredArgsConstructor`, etc.) to keep the code clean.
- **`maven-compiler-plugin` config**: Configured specifically to ensure Lombok works correctly with the latest Java versions.

### `application.properties`
Configured the app to connect to your PostgreSQL Docker container instead of an in-memory database:
- Set the connection URL to `jdbc:postgresql://localhost:5433/dormitory_db`.
- Configured Hibernate's `ddl-auto=update` so Spring automatically generates your database tables (`rooms`, `room_assignments`, `room_amenities`) without you writing SQL scripts.
- Disabled `open-in-view` (a best practice) to prevent unintended database connections while sending JSON to the user.

---

## 2. Database Entities (The Data Model)

I created two primary database models in the `com.unicampus.dormitory.model` package.

### `Room.java`
Represents a physical room in a dormitory.
- Uses `@Entity` and `@Table(name = "rooms")` to map it to the database.
- **Key Fields**: `roomId` (UUID), `roomNumber`, `building`, `capacity`, `currentOccupancy`, and `isAvailable`.
- **Amenities**: Used `@ElementCollection(fetch = FetchType.EAGER)` for the `amenities` list. *I used `EAGER` fetching to fix the `500 Internal Server Error` you encountered earlier. It forces Hibernate to load the amenities immediately alongside the room, so it doesn't crash when converting to JSON.*

### `RoomAssignment.java`
Represents the link between a student and a room.
- **Key Fields**: `assignmentId` (UUID), `roomId` (foreign key concept), `studentId`, `semester`, and `status`.
- Uses an `enum` for status (`ACTIVE`, `COMPLETED`, `CANCELLED`) to easily track whether the student currently lives there.

---

## 3. Repositories (Database Access)

I created two interfaces extending `JpaRepository` in `com.unicampus.dormitory.repository`. Spring Data JPA automatically implements the SQL queries behind the scenes.
- **`RoomRepository`**: Gives us `save()`, `findById()`, and `findAll()` for Rooms.
- **`RoomAssignmentRepository`**: I added custom methods here:
  - `findByStudentId(UUID)`: To find all assignments for a specific student.
  - `findByStudentIdAndStatus(UUID, Status)`: Specifically used to check if a student already has an *active* assignment.

---

## 4. Cross-Service Communication

The Dormitory Service is not allowed to assign a room to a fake or suspended student. It must ask the **Student Service** for permission.

### `RestTemplateConfig.java`
A simple configuration class that provides a `RestTemplate` Bean. This is Spring's tool for making external HTTP requests.

### `StudentServiceClient.java`
A dedicated class acting as a messenger.
- It makes an HTTP `GET` request to `http://localhost:8081/students/validate/{studentId}`.
- **Bug Fix**: Initially, I set the URL to `/api/students/validate/`, which caused a 404 error. I fixed the URL and created a specific `ValidateStudentResponse` DTO (Data Transfer Object) to safely read the JSON (`{"isValid": true}`) returned by the Student Service.

---

## 5. Core Business Logic (The Service Layer)

The brain of the operation lives in **`DormitoryService.java`**. I added several methods here:

### Room Creation (`addRoom`)
- Intercepts the room payload from the user.
- **Overrides the user's input**: It forces a new, secure `UUID` for the `roomId`, forces `isAvailable` to `true`, and forces `currentOccupancy` to `0`. It then saves it to the database.

### Room Assignment Workflow (`assignRoom`)
This is the most complex part of the system (Workflow 2 from your assignment):
1. Calls `studentServiceClient` to verify the student exists and is active.
2. Checks `RoomAssignmentRepository` to ensure the student doesn't already have an active room.
3. Finds the requested Room and checks if it's full (`currentOccupancy >= capacity`) or unavailable.
4. Creates a new `ACTIVE` RoomAssignment.
5. Increments the room's `currentOccupancy`. If the room hits capacity, it flips `isAvailable = false`.
6. Saves both the assignment and the updated room to PostgreSQL.

### Assignment Removal (`removeAssignment`)
- Finds the active assignment and marks it as `CANCELLED`.
- Finds the linked Room, decrements the `currentOccupancy`, and flips `isAvailable = true` so the room can be booked again.

---

## 6. API Endpoints (The Controller)

Finally, I exposed the business logic to the internet via **`DormitoryController.java`**.
- Mapped to `/api/dormitory`.
- Converts HTTP requests (like Postman or cURL) into Java method calls, and converts the Java results back into JSON responses.
- **Endpoints Implemented**:
  - `POST /rooms` (Add a room)
  - `GET /rooms` & `GET /rooms/{roomId}` (View rooms)
  - `GET /rooms/{roomId}/availability` (Check if a room is open)
  - `POST /rooms/{roomId}/assign` (Book a room for a student)
  - `GET /assignments/student/{studentId}` (See where a student lives)
  - `DELETE /assignments/{assignmentId}` (Cancel an assignment)

---

## 7. Testing 

I wrote a suite of automated tests in **`DormitoryControllerTest.java`**.
- Uses `@WebMvcTest` to test the API endpoints without booting up the entire database.
- It *mocks* the `DormitoryService` (pretends to be it) to test exactly how the Controller responds to Success (201 Created), Bad Requests (400 - Student Invalid), and Conflicts (409 - Room Full).

*(To test this manually, I also created the `api-requests.http` file for you, which contains ready-to-click HTTP requests for your IDE!)*
