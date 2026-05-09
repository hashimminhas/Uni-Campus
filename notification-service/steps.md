# Notification Service — Implementation Progress

## Step 1: Domain Layer ✅
- Created `NotificationChannel` enum (EMAIL, IN_APP)
- Created `NotificationStatus` enum (PENDING, SENT, FAILED)
- Created `Notification` JPA entity mapped to `notifications` table
  - Fields: notificationId (UUID PK), recipientId (UUID), channel (enum), subject, body (TEXT), status (enum, default PENDING), sentAt, createdAt
  - Auto-timestamp via @PrePersist, default status PENDING
- Created `NotificationTemplate` JPA entity mapped to `notification_templates` table
  - Fields: templateId (UUID PK), name (unique), subject, bodyTemplate (TEXT), createdAt, updatedAt
  - Auto-timestamps via @PrePersist and @PreUpdate

## Step 2: Repository ✅
- Created `NotificationRepository` extending `CrudRepository<Notification, UUID>`
  - Custom method: `findByRecipientId(UUID recipientId)` for fetching all notifications for a student
  - Inherits: findById, findAll, save, deleteById from CrudRepository
- Created `NotificationTemplateRepository` extending `CrudRepository<NotificationTemplate, UUID>`
  - No custom methods — standard CRUD is sufficient
## Step 3: DTOs ✅
- SendNotificationRequest — recipientId, channel, subject, body with validation
- NotificationResponse — full notification data returned to client
- NotificationStatusResponse — lightweight: notificationId, status, sentAt
- CreateTemplateRequest — name, subject, bodyTemplate with @NotBlank
- UpdateTemplateRequest — same fields, no validation (partial update)
- TemplateResponse — full template data returned to client

## Step 4: Exceptions ✅
- NotificationNotFoundException → 404 Not Found
- TemplateNotFoundException → 404 Not Found
- GlobalExceptionHandler (@ControllerAdvice) handles both + validation errors
- Consistent JSON error format: { status, error, message }
## Step 5: Service Layer ✅
- Created `NotificationService` with @Service and @Autowired repository
  - sendNotification — saves notification, simulates delivery (sets SENT + sentAt)
  - getNotificationById — finds or throws 404
  - getNotificationsByStudentId — returns list (empty if none)
  - getNotificationStatus — returns lightweight status response
  - Private mapToEntity and mapToResponse using @Builder
- Created `TemplateService` with @Service and @Autowired repository
  - createTemplate — saves and returns response
  - getAllTemplates — returns list of all templates
  - updateTemplate — partial update (only non-null fields), throws 404 if not found
  - Private mapToEntity and mapToResponse using @Builder
- Note: WebClient call to Student Service skipped for CP1 — will add in CP2
## Step 6: Controller ✅
- Created `NotificationController` with @RestController, @RequestMapping("/notifications"), @CrossOrigin
  - POST /notifications → 201 Created (with @Valid request body)
  - GET /notifications/{notificationId} → 200 OK
  - GET /notifications/student/{studentId} → 200 OK (returns list)
  - GET /notifications/{notificationId}/status → 200 OK
- Created `TemplateController` with @RestController, @RequestMapping("/notifications/templates"), @CrossOrigin
  - GET /notifications/templates → 200 OK (returns list)
  - POST /notifications/templates → 201 Created (with @Valid request body)
  - PUT /notifications/templates/{templateId} → 200 OK
- All 7 endpoints from Assignment 3 implemented
- @Tag and @Operation annotations added for Swagger
## Step 7: Swagger Testing ✅
- All 7 endpoints tested via Swagger UI at http://localhost:8082/swagger-ui.html
- Create template → 201 Created
- List templates → 200 OK with list
- Update template (partial) → 200 OK, unchanged fields preserved
- Send notification → 201 Created, status SENT, sentAt filled
- Send with different channel → 201 Created
- Missing fields → 400 Bad Request with field errors
- Get notification by ID → 200 OK
- Get with fake UUID → 404 Not Found
- Get by student → 200 OK with list of 2 notifications
- Get by unknown student → 200 OK with empty list
- Get status → 200 OK with SENT and timestamp
- Get status with fake UUID → 404 Not Found
## Step 8: Unit Tests ✅
- Created `NotificationControllerTest` with @WebMvcTest + MockMvc + @MockitoBean
  - Test 1: POST /notifications valid → 201 Created, status SENT
  - Test 2: GET /notifications/{id} exists → 200 OK
  - Test 3: GET /notifications/{id} not found → 404
  - Test 4: GET /notifications/student/{studentId} → 200 OK, list of 2
- Created `TemplateControllerTest` with @WebMvcTest + MockMvc + @MockitoBean
  - Test 1: POST /notifications/templates valid → 201 Created
  - Test 2: GET /notifications/templates → 200 OK, list of 2
  - Test 3: PUT /notifications/templates/{id} not found → 404
- All 7 tests use mocked services — no database needed
## Step 9: Swagger Polish ✅
- @Tag and @Operation annotations were added in Step 6 alongside the controllers
- NotificationController tagged "Notifications" with summaries on all 4 endpoints
- TemplateController tagged "Notification Templates" with summaries on all 3 endpoints
- Swagger UI at http://localhost:8082/swagger-ui.html shows all 7 endpoints with descriptions

## Step 10: Demo Prep ✅
- All 7 endpoints implemented, tested via Swagger (Step 7), and covered by unit tests (Step 8)
- Service starts cleanly with docker-compose (PostgreSQL on 5433, RabbitMQ on 5672)
- Demo flow: POST template → POST notification → GET notification → GET status

## CP2 Task 1: WebClient Integration ✅
- Added spring-boot-starter-webflux dependency to pom.xml
- Added student.service.base-url to application.yml
- Created StudentResponse DTO in client/ package (matches Student Service response)
- Created StudentServiceClient using WebClient to call GET /students/{id}
- StudentServiceClient throws StudentNotFoundException if Student Service returns 404
- Added StudentNotFoundException to exception/ package
- Added StudentNotFoundException handler to GlobalExceptionHandler (→ 404)
- Modified NotificationService.sendNotification to call Student Service first
- If student not found → 404 returned to caller, notification NOT saved
- If student exists → notification saved and marked SENT as before
- Updated NotificationControllerTest to @MockitoBean StudentServiceClient
- Integration: Notification Service now makes a real HTTP call to Student Service
