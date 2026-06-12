# Teatro Hexagonal - API Routes Summary

## Quick Reference

### Auth Service (`/api/auth/*` | `/api/user/*`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | ❌ | Login and get JWT token |
| POST | `/api/auth/register/customer` | ❌ | Register as customer |
| POST | `/api/auth/register/internal/admin` | ✅ ADMIN | Create admin user |
| GET | `/api/user/` | ✅ ADMIN | List all users |

---

### Theater Service (`/api/theater/*`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/theater/create` | ✅ ADMIN | Create new theater |
| GET | `/api/theater/listAll` | ❌ | List all theaters |
| GET | `/api/theater/id/{id}` | ❌ | Get theater by ID |

---

### Event Service (`/api/event/*`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/event/create` | ✅ ADMIN | Create new event |
| GET | `/api/event/listAll` | ❌ | List all events |
| GET | `/api/event/id/{id}` | ❌ | Get event by ID |
| POST | `/api/event/internal/theater/sync/capacity` | ❌ | Internal: Sync theater capacity |

---

### Reservation Service (`/api/reservation/*`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/reservation/reserve` | ✅ | Book seats for event |
| POST | `/api/reservation/internal/seats/initialize` | ❌ | Internal: Initialize event seats |
| GET | `/api/reservation/event/{eventId}/user/{userId}` | ✅ | Get user's reserved seats for event |
| GET | `/api/reservation/user/{userId}` | ✅ | Get all user's reserved seats |
| GET | `/api/reservation/event/seats/{eventId}` | ❌ | Get all seats for event |

---

## Authentication Legend

- ❌ **Public** - No authentication required
- ✅ **Authenticated** - Any logged-in user (ADMIN or CUSTOMER)
- ✅ **ADMIN** - ADMIN role required
- ✅ **CUSTOMER** - CUSTOMER role required

---

## Token Injection

The API Gateway automatically injects authenticated user context via headers:

```
X-User-Email: user@example.com
X-User-Role: ADMIN (or CUSTOMER)
```

These headers are available to backend services for additional authorization checks.

---

## Public Paths Whitelist

The API Gateway allows these paths without JWT validation:

1. `/api/auth/login`
2. `/api/auth/register/customer`
3. `/api/event/internal/theater/sync/capacity`
4. `/api/reservation/internal/seats/initialize`

All other endpoints require a valid JWT Bearer token.

---

## Common Error Scenarios

### 401 Unauthorized
- Missing `Authorization` header
- Token expired
- Invalid JWT signature

**Response:**
```json
{
  "timestamp": "2026-06-12T15:09:09Z",
  "status": 401,
  "error": "Token ausente ou formato inválido"
}
```

### 403 Forbidden (Access Denied)
- User lacks ADMIN role for admin-only endpoints

**Response:**
```json
{
  "timestamp": "2026-06-12T15:09:09Z",
  "status": 403,
  "error": "Acesso Proibido",
  "message": "Você não tem permissão para acessar este recurso. Apenas Administradores."
}
```

### 422 Unprocessable Entity (Validation Error)
- Invalid request body fields
- Missing required fields
- Invalid field values

**Response:**
```json
{
  "timestamp": "2026-06-12T15:09:09Z",
  "status": 422,
  "error": "Erro de Validação nos campos",
  "message": "Existem campos inválidos no envio.",
  "errors": ["Field error 1", "Field error 2"]
}
```

### 403 Forbidden (Reservation Failed)
- Seats unavailable
- Invalid event or user

**Response:**
```json
{
  "timestamp": "2026-06-12T15:09:09Z",
  "status": 403,
  "error": "Reserva não realizada",
  "message": "Seats not available for reservation"
}
```

---

## Request/Response Content-Type

All endpoints:
- **Accept:** `application/json`
- **Return:** `application/json`

---

## Typical User Journey

### 1️⃣ **New User Registration**
```
POST /api/auth/register/customer
├─ Input: name, email, password, role
└─ Output: User created with ID
```

### 2️⃣ **User Login**
```
POST /api/auth/login
├─ Input: email, password
└─ Output: JWT token (store in client)
```

### 3️⃣ **Browse Theaters**
```
GET /api/theater/listAll
├─ Auth: No
└─ Output: All theaters
```

### 4️⃣ **Browse Events**
```
GET /api/event/listAll
├─ Auth: No
└─ Output: All events with availability
```

### 5️⃣ **Check Seats for Event**
```
GET /api/reservation/event/seats/{eventId}
├─ Auth: No
└─ Output: All seats (D=Available, R=Reserved)
```

### 6️⃣ **Reserve Seats**
```
POST /api/reservation/reserve
├─ Auth: Yes (Bearer token)
├─ Input: eventId, userId, seatIds[]
└─ Output: Confirmation message
```

### 7️⃣ **Check User Reservations**
```
GET /api/reservation/user/{userId}
├─ Auth: Yes (Bearer token)
└─ Output: User's all reserved seats
```

---

## Typical Admin Journey

### 1️⃣ **Create Admin Account** (initial setup)
```
POST /api/auth/register/internal/admin
├─ Input: name, email, password, role=ADMIN
└─ Output: Admin user created
```

### 2️⃣ **Admin Login**
```
POST /api/auth/login
├─ Input: admin@example.com, adminPassword
└─ Output: Admin JWT token
```

### 3️⃣ **Create Theater**
```
POST /api/theater/create
├─ Auth: ADMIN
├─ Input: name, address, city, capacity
└─ Output: Theater created with ID
```

### 4️⃣ **Create Event**
```
POST /api/event/create
├─ Auth: ADMIN
├─ Input: theaterId, name, eventDate, totalSeats
└─ Output: Event created (seats auto-initialized)
```

### 5️⃣ **Create Customer Admin Account**
```
POST /api/auth/register/internal/admin
├─ Auth: ADMIN
├─ Input: name, email, password, role=ADMIN
└─ Output: New admin user created
```

### 6️⃣ **View All Users**
```
GET /api/user/
├─ Auth: ADMIN
└─ Output: All registered users
```

---

## Data Flow Diagram

```
┌─────────────┐
│   Client    │
│  (Browser)  │
└──────┬──────┘
       │
       │ HTTP/JSON
       ▼
┌──────────────────────┐
│   API Gateway        │
│   (Port 8080)        │
│                      │
│ ✓ JWT Validation     │
│ ✓ Request Routing    │
│ ✓ Header Injection   │
└──────────────────────┘
       │
       ├─────► Auth Service (8084)
       │       ├─ Login
       │       ├─ Register
       │       └─ User Management
       │
       ├─────► Theater Service (8081)
       │       ├─ Create Theater
       │       └─ List Theaters
       │
       ├─────► Event Service (8082)
       │       ├─ Create Event
       │       └─ List Events
       │
       └─────► Reservation Service (8083)
               ├─ Reserve Seats
               └─ List Reservations
```

---

## Important Notes

### Internal vs Public Endpoints
- **Internal endpoints** (e.g., `/internal/...`) are for service-to-service communication
- They are NOT protected by JWT validation (security by convention)
- They should only be called from trusted services

### Seat Status Codes
- `D` = Disponível (Available)
- `R` = Reservado (Reserved)

### Event Status
- `ACTIVE` = Accepting reservations
- `SOLD_OUT` = No seats available
- `CANCELLED` = Event cancelled

### Date Format
All date-times use ISO 8601 format: `yyyy-MM-dd'T'HH:mm:ss`

Example: `2026-07-15T19:30:00`

---

## Gateway Configuration

The API Gateway routes requests based on service name in the path:

```
/api/{service}/**  →  {service}_SERVICE_URL
```

**Service Mappings:**
- `/api/auth/**` → http://localhost:8084 (AUTH_SERVICE_URL)
- `/api/user/**` → http://localhost:8084 (AUTH_SERVICE_URL)
- `/api/theater/**` → http://localhost:8081 (THEATER_SERVICE_URL)
- `/api/event/**` → http://localhost:8082 (EVENT_SERVICE_URL)
- `/api/reservation/**` → http://localhost:8083 (RESERVATION_SERVICE_URL)

**Environment Variables:**
```
AUTH_SERVICE_URL=http://localhost:8084
THEATER_SERVICE_URL=http://localhost:8081
EVENT_SERVICE_URL=http://localhost:8082
RESERVATION_SERVICE_URL=http://localhost:8083
JWT_SECRET=<secret-key>
```

---

## For More Details

See `API_ROUTES.md` for complete endpoint documentation with request/response examples.
