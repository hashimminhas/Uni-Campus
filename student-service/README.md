# Student Service

The Student Service is the identity foundation of UniCampus. Every student in the system lives here their profile, their academic status, and whether they are allowed to use other university services. Every other microservice (Course, Exam, Library, etc.) depends on this service to verify a student before doing anything with them.

---

## What This Service Does

- Registers new students and stores their profile in a dedicated PostgreSQL database (`student_db`)
- Lets you look up, update, and delete students
- Tracks each student's academic status: `ACTIVE`, `SUSPENDED`, `GRADUATED`, or `ON_LEAVE`
- Exposes a **validate endpoint** so other services can check if a student is allowed to enrol in courses, sit exams, borrow books, etc.


## How to Run

**Prerequisites:** Docker must be running.

```bash
# 1. Start the database (from the UniCampus root folder)
docker compose up -d

# 2. Start the service
cd student-service
mvn spring-boot:run
```

The service starts on **port 8081**.

Open Swagger UI: **http://localhost:8081/swagger-ui.html**

---

## API Endpoints

### 1. Register a New Student
```
POST /students
```
Creates a student profile. Email must be unique. Status is set to `ACTIVE` automatically.

**Request body:**
```json
{
  "firstName": "Hashim",
  "lastName": "Ali",
  "email": "hashim@ut.ee",
  "phoneNumber": "+372123456",
  "program": "Computer Science",
  "enrollmentYear": 2024
}
```
**Response:** `201 Created` with the full student profile including the generated `studentId`.

**Error cases:**
- `400 Bad Request` — if required fields are missing or email format is wrong
- `409 Conflict` — if the email is already registered

---

### 2. Get Student by ID
```
GET /students/{studentId}
```
Returns the full profile of one student.

**Response:** `200 OK` with student profile, or `404 Not Found` if the ID does not exist.

---

### 3. Update Student Profile
```
PUT /students/{studentId}
```
Updates a student's profile. You only need to send the fields you want to change everything else stays the same.

**Request body (all fields optional):**
```json
{
  "phoneNumber": "+372999999",
  "program": "Data Science"
}
```
**Response:** `200 OK` with the updated profile.

**Error cases:**
- `404 Not Found` — student does not exist
- `409 Conflict` — if the new email is already taken by someone else

---

### 4. Delete a Student
```
DELETE /students/{studentId}
```
Permanently removes a student from the system.

**Response:** `204 No Content` (empty body), or `404 Not Found`.

---

### 5. Update Academic Status
```
PATCH /students/{studentId}/status
```
Changes a student's academic status. This is a separate endpoint because status changes are administrative actions, not routine profile edits.

**Request body:**
```json
{
  "academicStatus": "SUSPENDED"
}
```
Allowed values: `ACTIVE`, `SUSPENDED`, `GRADUATED`, `ON_LEAVE`

**Response:** `200 OK` with the updated profile.

---

### 6. Validate a Student (for other services)
```
GET /students/validate/{studentId}
```
This is the integration endpoint. Other microservices call this before allowing a student to do anything enrol in a course, book an exam slot, borrow a library book, etc.

**Response:** `200 OK`
```json
{
  "studentId": "cb89da4a-9f7b-4c91-8914-e7f5020c1798",
  "valid": true,
  "academicStatus": "ACTIVE"
}
```

- `valid: true` only when status is `ACTIVE`
- `valid: false` when status is `SUSPENDED`, `GRADUATED`, or `ON_LEAVE`
- `404 Not Found` if no student exists with that ID

---

## How Other Services Use This

Other microservices do **not** access the student database directly. Instead they call the validate endpoint over HTTP before processing any student request.

**Example flow Course Service enrolling a student:**

```
Student clicks "Enrol" in the frontend
        ↓
Course Service receives the request
        ↓
Course Service calls GET /students/validate/{studentId}  ← calls this service
        ↓
If valid=true  → proceed with enrolment
If valid=false → return error to student ("Your account is suspended")
If 404         → return error ("Student not found")
```

This keeps each service independent Course Service never needs to know how students are stored, only whether they are valid.

---

## Error Response Format

All errors return a consistent JSON structure:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Student not found with id: 00000000-0000-0000-0000-000000000000"
}
```

Validation errors (400) also include a field-level breakdown:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "email": "must be a well-formed email address",
    "firstName": "must not be blank"
  }
}
```

---

## Running Tests

Tests use an in-memory H2 database no Docker needed.

```bash
cd student-service
mvn test
```

Expected output: `Tests run: 5, Failures: 0, Errors: 0`

The tests cover:
- Creating a student → 201
- Getting a student that exists → 200
- Getting a student that does not exist → 404
- Validating an active student → 200, valid=true
- Validating a student that does not exist → 404
