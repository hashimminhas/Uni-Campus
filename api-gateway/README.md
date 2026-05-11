# API Gateway — UniCampus

Single entry point for all UniCampus microservices. Built with **Spring Cloud Gateway** (reactive, Netty-based).

## Features
- Centralised routing to all 8 backend services
- Global CORS configuration (frontend origins: `localhost:5173`, `localhost:3000`)
- Circuit breaker per upstream service (Resilience4j)
- Request / response logging with timing
- Health endpoint via Spring Actuator
- Docker-ready

## Route Table

| Gateway Path            | Upstream Service       | Upstream URL          |
|-------------------------|------------------------|-----------------------|
| `/api/students/**`      | student-service        | `http://localhost:8081` |
| `/api/notifications/**` | notification-service   | `http://localhost:8082` |
| `/api/courses/**`       | course-service         | `http://localhost:8083` |
| `/api/exams/**`         | exam-service           | `http://localhost:8084` |
| `/api/library/**`       | library-service        | `http://localhost:8085` |
| `/api/billing/**`       | billing-service        | `http://localhost:8086` |
| `/api/dormitory/**`     | dormitory-service      | `http://localhost:8087` |
| `/api/mealplan/**`      | mealplan-service       | `http://localhost:8088` |

> **Path rewriting:** Most routes use `StripPrefix=1` to remove the `/api` segment before forwarding.
> The library-service route does **not** strip the prefix because its controller already maps to `/api/library`.

## How to Run

### Locally (Maven)
```bash
cd api-gateway
mvn spring-boot:run
```
The gateway starts on **port 8080**. Make sure the downstream services you need are also running.

### With Docker Compose
```bash
docker-compose up -d --build api-gateway
```

## Health Check
```bash
curl http://localhost:8080/actuator/health
```

## Example Requests (via Gateway)

### Create a student
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@uni.edu","program":"CS"}'
```

### Get all books
```bash
curl http://localhost:8080/api/library/books
```

### List courses
```bash
curl http://localhost:8080/api/courses
```

## Circuit Breaker
When an upstream service is down, the circuit breaker trips and returns:
```json
{
  "status": 503,
  "error": "Service Unavailable",
  "message": "The upstream service is temporarily unavailable. Please try again later.",
  "timestamp": "2026-05-10T12:00:00Z"
}
```

## Configuration
Override upstream URLs via environment variables (useful for Docker):
```
STUDENT_SERVICE_URL=http://student-service:8081
COURSE_SERVICE_URL=http://course-service:8083
```
See `application.yml` for the full list.
