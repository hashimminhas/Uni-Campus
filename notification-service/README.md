# Notification Service

The Notification Service is the communication backbone of UniCampus. Any time something important happens in the system — a student enrolls in a course, an exam is scheduled, a library book is overdue — this service is responsible for telling the student about it. It handles delivery tracking, reusable message templates, and in the full system, it listens to async events from every other service via RabbitMQ.

---

## What This Service Does

- Receives notification requests and delivers alerts via email or in-app messaging
- Stores every notification with full delivery tracking (`PENDING` → `SENT` → `FAILED`)
- Manages reusable message templates so other services can send consistent, pre-written messages
- For CP1, delivery is simulated — status is set to `SENT` immediately. Real delivery and a WebClient call to Student Service to fetch contact info will be added in CP2
- In the full system, this service consumes async events from Course Service (`enrollment.confirmed`, `course.dropped`), Exam Service (`exam.scheduled`), and Library Service (`book.overdue`) via RabbitMQ

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.5 |
| Database | PostgreSQL 16 (`notification_db`) |
| ORM | Spring Data JPA (CrudRepository) |
| Validation | Jakarta Validation (@NotBlank, @NotNull) |
| Messaging | Spring AMQP (RabbitMQ) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Boilerplate | Lombok |
| Tests | JUnit 5 + MockMvc + @WebMvcTest |

---

## Project Structure

```
notification-service/
├── src/main/java/com/unicampus/notification/
│   ├── controller/     NotificationController.java     ← Notification HTTP endpoints
│   │                   TemplateController.java         ← Template HTTP endpoints
│   ├── service/        NotificationService.java        ← Notification business logic
│   │                   TemplateService.java            ← Template business logic
│   ├── repository/     NotificationRepository.java     ← Database access for notifications
│   │                   NotificationTemplateRepository.java  ← Database access for templates
│   ├── domain/         Notification.java               ← Notification entity
│   │                   NotificationTemplate.java       ← Template entity
│   │                   NotificationChannel.java        ← Enum: EMAIL, IN_APP
│   │                   NotificationStatus.java         ← Enum: PENDING, SENT, FAILED
│   ├── dto/            SendNotificationRequest, NotificationResponse,
│   │                   NotificationStatusResponse, CreateTemplateRequest,
│   │                   UpdateTemplateRequest, TemplateResponse
│   ├── exception/      NotificationNotFoundException, TemplateNotFoundException,
│   │                   GlobalExceptionHandler          ← Error handling
│   └── config/         OpenApiConfig.java              ← Swagger setup
│                       RabbitMQConfig.java             ← RabbitMQ exchanges and queues
└── src/test/java/com/unicampus/notification/
    └── controller/     NotificationControllerTest.java ← 4 unit tests
                        TemplateControllerTest.java     ← 3 unit tests
```

---

## How to Run

**Prerequisites:** Docker must be running.

```bash
# 1. Start the database and RabbitMQ (from the UniCampus root folder)
docker compose up -d

# 2. Start the service
cd notification-service
mvn spring-boot:run
```

The service starts on **port 8082**.

Open Swagger UI: **http://localhost:8082/swagger-ui.html**

---

## API Endpoints

### 1. Send a Notification
```
POST /notifications
```
Sends a notification to a student. For CP1, the notification is saved and immediately marked as `SENT`.

**Request body:**
```json
{
  "recipientId": "cb89da4a-9f7b-4c91-8914-e7f5020c1798",
  "channel": "EMAIL",
  "subject": "Welcome to UniCampus",
  "body": "Your student account has been created."
}
```
Allowed channel values: `EMAIL`, `IN_APP`

**Response:** `201 Created` with the full notification including `notificationId`, `status: "SENT"`, and `sentAt` timestamp.

**Error cases:**
- `400 Bad Request` — if required fields are missing or null

---

### 2. Get Notification by ID
```
GET /notifications/{notificationId}
```
Returns the full details of one notification.

**Response:** `200 OK` with the notification, or `404 Not Found` if the ID does not exist.

---

### 3. Get All Notifications for a Student
```
GET /notifications/student/{studentId}
```
Returns every notification ever sent to a specific student.

**Response:** `200 OK` with a list — returns an **empty list** `[]` if no notifications exist for that student (never `404`).

---

### 4. Check Delivery Status
```
GET /notifications/{notificationId}/status
```
Lightweight endpoint — returns only the delivery state, not the full notification body.

**Response:** `200 OK`
```json
{
  "notificationId": "ecfa1840-617b-4bf2-98dc-f3bd4c0e62dd",
  "status": "SENT",
  "sentAt": "2026-05-01T23:38:52.347779"
}
```
- `404 Not Found` if the ID does not exist

---

### 5. List All Templates
```
GET /notifications/templates
```
Returns all stored message templates.

**Response:** `200 OK` with a list of templates (empty list if none exist).

---

### 6. Create a Template
```
POST /notifications/templates
```
Creates a reusable message blueprint. The `name` must be unique across all templates.

**Request body:**
```json
{
  "name": "enrollment-confirmation",
  "subject": "Enrollment Confirmed",
  "bodyTemplate": "Dear student, you have been enrolled in {courseName}."
}
```
**Response:** `201 Created` with the full template including `templateId` and timestamps.

**Error cases:**
- `400 Bad Request` — if any field is missing or blank

---

### 7. Update a Template
```
PUT /notifications/templates/{templateId}
```
Partial update — only send the fields you want to change. Anything left out stays exactly as it was.

**Request body (all fields optional):**
```json
{
  "bodyTemplate": "Dear student, your enrollment in {courseName} is confirmed. Good luck!"
}
```
**Response:** `200 OK` with the updated template.

**Error cases:**
- `404 Not Found` — if the template ID does not exist

---

## How Other Services Will Use This (CP2+)

Other services do not call this service's REST endpoints directly for routine notifications — instead, they publish events to RabbitMQ and this service picks them up automatically.

**Example flow — Course Service enrolling a student:**

```
Student enrolls in a course
        ↓
Course Service publishes enrollment.confirmed event to RabbitMQ
        ↓
Notification Service consumes the event from the queue
        ↓
Notification Service calls GET /students/{id} to get the student's contact info
        ↓
Notification Service creates the notification and delivers it
```

RabbitMQ queues already configured:
- `course.events` — enrollment and drop events
- `exam.events` — exam scheduling events
- `library.events` — overdue book events

---

## Error Response Format

All errors return a consistent JSON structure:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Notification not found with id: 00000000-0000-0000-0000-000000000000"
}
```

Validation errors (400) also include a field-level breakdown:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "recipientId": "must not be null",
    "subject": "must not be blank",
    "body": "must not be blank"
  }
}
```

---

## Running Tests

```bash
cd notification-service
mvn test
```

Expected output: `Tests run: 7, Failures: 0, Errors: 0`

The tests cover:
- Sending a valid notification → 201, status SENT
- Getting a notification that exists → 200
- Getting a notification that does not exist → 404
- Getting all notifications for a student → 200 with list of 2
- Creating a valid template → 201
- Getting all templates → 200 with list of 2
- Updating a template that does not exist → 404

---

## Built By

**Hashim** — Student Service, Notification Service  
Course: Enterprise System Integration (MTAT.03.229), University of Tartu
