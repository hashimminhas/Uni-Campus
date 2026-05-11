# Library Service

Part of the **UniCampus Student Information System**. Manages the book catalog, borrowing lifecycle, and overdue tracking.

**Owner:** Calvin  
**Port:** 8085  
**Base URL:** `/api/library`  
**Database:** `library_db` (PostgreSQL)

---

## REST Endpoints

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/api/library/books` | Add a new book | 201 |
| PUT | `/api/library/books/{id}` | Update book details/availability | 200 |
| GET | `/api/library/books` | Browse the book catalog | 200 |
| POST | `/api/library/books/{id}/borrow` | Borrow a book | 201 |
| PUT | `/api/library/loans/{id}/return` | Return a borrowed book | 200 |
| GET | `/api/library/loans/student/{studentId}` | View a student's active loans | 200 |

---

## Data Model

**books**
| Column | Type |
|--------|------|
| book_id (PK) | UUID |
| title | VARCHAR |
| is_available | BOOLEAN |

**book_loans**
| Column | Type |
|--------|------|
| loan_id (PK) | UUID |
| book_id (FK) | UUID |
| student_id | UUID |
| due_date | DATE |
| returned_at | TIMESTAMP |

---

## Inter-Service Communication

### Synchronous (REST)
- Before borrowing, calls **Student Service** `GET /students/validate/{studentId}` via WebClient to verify the student exists and is active. Returns 403 if validation fails.

### Asynchronous (RabbitMQ)
- Publishes `book.overdue` events to the `library.events` topic exchange via a daily scheduled job.
- Consumed by **Billing Service** (creates a fine) and **Notification Service** (sends overdue alert).

---

## Testing

8 unit tests using `@WebMvcTest` + MockMvc + Mockito (no database required):

| Test | Asserts |
|------|---------|
| `addBook_valid_returns201` | 201, response has title |
| `getAllBooks_returns200` | 200, array response |
| `borrowBook_valid_returns201` | 201, response has loanId |
| `borrowBook_studentInvalid_returns403` | 403, error message |
| `borrowBook_bookNotAvailable_returns409` | 409, error message |
| `borrowBook_bookNotFound_returns404` | 404, error message |
| `returnBook_valid_returns200` | 200, response has returnedAt |
| `getStudentLoans_returns200` | 200, array response |

Run tests:
```bash
mvn test
```

---

## Tech Stack

- Java 21, Spring Boot 3.4.5
- Spring Data JPA + PostgreSQL
- Spring WebFlux (WebClient for sync REST calls)
- Spring AMQP (RabbitMQ)
- Lombok, Springdoc OpenAPI (Swagger)
- JUnit 5 + Mockito + MockMvc

---

## Running Locally

### Prerequisites
- Java 21
- PostgreSQL running on port 5433 with a `library_db` database
- RabbitMQ running on port 5672 (for overdue events)
- Student Service running on port 8081 (for borrow validation)

### Start the service
```bash
mvn spring-boot:run
```

### Swagger UI
```
http://localhost:8085/swagger-ui.html
```

### Docker
```bash
docker build -t library-service .
docker run -p 8085:8085 library-service
```
