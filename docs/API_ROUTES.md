# Teatro Hexagonal - API Routes Documentation

## Overview

This document describes all API endpoints in the Teatro Hexagonal microservices architecture. The API Gateway (port 8080) routes requests to backend services:

- **Auth Service** (8084): User authentication and management
- **Theater Service** (8081): Theater management
- **Event Service** (8082): Event management
- **Reservation Service** (8083): Seat reservations

---

## Authentication

### Public Endpoints
The following endpoints do NOT require JWT authentication:
- `POST /api/auth/login` - User login
- `POST /api/auth/register/customer` - Customer registration
- `POST /api/event/internal/theater/sync/capacity` - Internal theater sync
- `POST /api/reservation/internal/seats/initialize` - Internal seat initialization

### Protected Endpoints
All other endpoints require a valid JWT Bearer token in the `Authorization` header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Token Contents:**
- `sub` (subject): User email
- `role`: User role (ADMIN or CUSTOMER)

**Authentication Errors:**
- Missing or malformed token → `401 Unauthorized`
- Expired token → `401 Unauthorized` (with message "Token expirado")
- Invalid token → `401 Unauthorized` (with message "Token inválido")

---

## Auth Service Endpoints

### 1. Login (PUBLIC)
**POST** `/api/auth/login`

Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Request Validation:**
- `email`: Must be valid email format and non-empty
- `password`: Must be non-empty

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "usuario": "user@example.com",
  "role": "CUSTOMER"
}
```

**Error Response (422 Unprocessable Entity):**
```json
{
  "timestamp": "2026-06-12T15:09:09Z",
  "status": 422,
  "error": "Erro de Validação nos campos",
  "message": "Existem campos inválidos no envio.",
  "path": "/api/auth/login",
  "errors": [
    "O e-mail deve ser válido.",
    "O e-mail é obrigatório."
  ]
}
```

---

### 2. Register Customer (PUBLIC)
**POST** `/api/auth/register/customer`

Register a new customer account. Admin registration is not allowed via this endpoint.

**Request Body:**
```json
{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "MyPassword123",
  "role": "CUSTOMER"
}
```

**Request Validation:**
- `name`: Required, 3-255 characters
- `email`: Required, valid email format
- `password`: Required, minimum 6 characters
- `role`: Required, must be CUSTOMER (ADMIN will be rejected)

**Response (201 Created):**
```json
{
  "id": 2,
  "name": "João Silva",
  "email": "joao@example.com",
  "role": "CUSTOMER",
  "active": true
}
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2026-06-12T15:09:09Z",
  "status": 400,
  "error": "Requisição Inválida",
  "message": "Esta rota deve registrar apenas clientes!",
  "path": "/api/auth/register/customer"
}
```

---

### 3. Register Admin (ADMIN ONLY)
**POST** `/api/auth/register/internal/admin`

Create a new admin user. Requires ADMIN role authentication.

**Authorization:** ADMIN role required

**Request Body:**
```json
{
  "name": "Admin User",
  "email": "admin@example.com",
  "password": "AdminPassword123",
  "role": "ADMIN"
}
```

**Request Validation:** Same as customer registration

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Admin User",
  "email": "admin@example.com",
  "role": "ADMIN",
  "active": true
}
```

**Error Response (403 Forbidden):**
```json
{
  "timestamp": "2026-06-12T15:09:09Z",
  "status": 403,
  "error": "Acesso Proibido",
  "message": "Você não tem permissão para acessar este recurso. Apenas Administradores.",
  "path": "/api/auth/register/internal/admin"
}
```

---

### 4. List All Users (ADMIN ONLY)
**GET** `/api/user/`

Retrieve all registered users. Requires ADMIN role authentication.

**Authorization:** ADMIN role required

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Admin User",
    "email": "admin@example.com",
    "role": "ADMIN",
    "active": true
  },
  {
    "id": 2,
    "name": "João Silva",
    "email": "joao@example.com",
    "role": "CUSTOMER",
    "active": true
  }
]
```

**Query Parameters:** None

---

## Theater Service Endpoints

### 1. Create Theater (ADMIN ONLY)
**POST** `/api/theater/create`

Create a new theater. Requires ADMIN role authentication.

**Authorization:** ADMIN role required

**Request Body:**
```json
{
  "name": "Cinema Downtown",
  "address": "Rua das Flores, 123",
  "city": "São Paulo",
  "capacity": 500
}
```

**Request Validation:**
- `name`: Required, non-empty
- `address`: Required, non-empty
- `city`: Required, non-empty
- `capacity`: Required, minimum 1

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Cinema Downtown",
  "address": "Rua das Flores, 123",
  "city": "São Paulo",
  "capacity": 500
}
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2026-06-12T15:09:09Z",
  "status": 400,
  "error": "Requisição Inválida",
  "message": "A capacidade deve ser maior que 1",
  "path": "/api/theater/create"
}
```

---

### 2. List All Theaters (PUBLIC)
**GET** `/api/theater/listAll`

Retrieve all theaters. No authentication required.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Cinema Downtown",
    "address": "Rua das Flores, 123",
    "city": "São Paulo",
    "capacity": 500
  },
  {
    "id": 2,
    "name": "Cinema North",
    "address": "Avenida Norte, 456",
    "city": "Rio de Janeiro",
    "capacity": 300
  }
]
```

**Query Parameters:** None

---

### 3. Get Theater by ID (PUBLIC)
**GET** `/api/theater/id/{id}`

Retrieve a specific theater by its ID. No authentication required.

**Path Parameters:**
- `id` (required): Theater ID (long)

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Cinema Downtown",
  "address": "Rua das Flores, 123",
  "city": "São Paulo",
  "capacity": 500
}
```

**Error Response (404 Not Found):** Theater not found

---

## Event Service Endpoints

### 1. Create Event (ADMIN ONLY)
**POST** `/api/event/create`

Create a new event. Requires ADMIN role authentication.

**Authorization:** ADMIN role required

**Request Body:**
```json
{
  "theaterId": 1,
  "name": "Homem-Aranha: Sem Volta Para Casa",
  "eventDate": "2026-07-15T19:30:00",
  "totalSeats": 200
}
```

**Request Validation:**
- `theaterId`: Required, must exist
- `name`: Required, non-empty
- `eventDate`: Required, valid datetime (format: yyyy-MM-dd'T'HH:mm:ss)
- `totalSeats`: Required, must be greater than zero

**Response (201 Created):**
```json
{
  "id": 1,
  "theaterId": 1,
  "name": "Homem-Aranha: Sem Volta Para Casa",
  "eventDate": "2026-07-15T19:30:00",
  "totalSeats": 200,
  "availableSeats": 200,
  "status": "ACTIVE"
}
```

**Note:** Creating an event automatically initializes all seats as AVAILABLE.

---

### 2. List All Events (PUBLIC)
**GET** `/api/event/listAll`

Retrieve all events. No authentication required.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "theaterId": 1,
    "name": "Homem-Aranha: Sem Volta Para Casa",
    "eventDate": "2026-07-15T19:30:00",
    "totalSeats": 200,
    "availableSeats": 150,
    "status": "ACTIVE"
  },
  {
    "id": 2,
    "theaterId": 2,
    "name": "Duna: Parte Dois",
    "eventDate": "2026-08-20T20:00:00",
    "totalSeats": 150,
    "availableSeats": 150,
    "status": "ACTIVE"
  }
]
```

**Query Parameters:** None

---

### 3. Get Event by ID (PUBLIC)
**GET** `/api/event/id/{id}`

Retrieve a specific event by its ID. No authentication required.

**Path Parameters:**
- `id` (required): Event ID (long)

**Response (200 OK):**
```json
{
  "id": 1,
  "theaterId": 1,
  "name": "Homem-Aranha: Sem Volta Para Casa",
  "eventDate": "2026-07-15T19:30:00",
  "totalSeats": 200,
  "availableSeats": 150,
  "status": "ACTIVE"
}
```

---

### 4. Sync Theater Capacity (INTERNAL - PUBLIC)
**POST** `/api/event/internal/theater/sync/capacity`

Internal endpoint to sync theater capacity with event service. Used for inter-service communication.

**Request Body:**
```json
{
  "theaterId": 1,
  "capacity": 500
}
```

**Response (200 OK):** Empty response

**Note:** This is an internal endpoint. Accessible without authentication but intended for service-to-service communication.

---

## Reservation Service Endpoints

### 1. Reserve Seats (AUTHENTICATED)
**POST** `/api/reservation/reserve`

Book seats for an event. Requires authentication.

**Authorization:** Any authenticated user (ADMIN or CUSTOMER)

**Request Body:**
```json
{
  "eventId": 1,
  "userId": 2,
  "seatIds": [1, 2, 3, 5]
}
```

**Request Validation:**
- `eventId`: Required, must exist
- `userId`: Required, must be authenticated user
- `seatIds`: Required, cannot be empty, seats must be available

**Response (201 Created):**
```json
{
  "message": "Reserva(s) realizada e confirmada com sucesso!"
}
```

**Error Response (403 Forbidden):**
```json
{
  "timestamp": "2026-06-12T15:09:09Z",
  "status": 403,
  "error": "Reserva não realizada",
  "message": "Seats not available for reservation",
  "path": "/api/reservation/reserve"
}
```

---

### 2. Initialize Seats (INTERNAL - PUBLIC)
**POST** `/api/reservation/internal/seats/initialize`

Internal endpoint to create and initialize seats for an event. Used during event creation.

**Request Body:**
```json
{
  "eventId": 1,
  "totalSeats": 200
}
```

**Response (200 OK):** Empty response

**Note:** This endpoint creates all seats with status AVAILABLE (D). Used internally by event creation flow.

---

### 3. Get User Reserved Seats for Specific Event (AUTHENTICATED)
**GET** `/api/reservation/event/{eventId}/user/{userId}`

Get all seats reserved by a user for a specific event.

**Authorization:** Any authenticated user

**Path Parameters:**
- `eventId` (required): Event ID (long)
- `userId` (required): User ID (long)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "seatCode": "A-01",
    "eventId": 1
  },
  {
    "id": 2,
    "seatCode": "A-02",
    "eventId": 1
  }
]
```

---

### 4. Get All User Reserved Seats (AUTHENTICATED)
**GET** `/api/reservation/user/{userId}`

Get all seats reserved by a user across all events.

**Authorization:** Any authenticated user

**Path Parameters:**
- `userId` (required): User ID (long)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "seatCode": "A-01",
    "eventId": 1
  },
  {
    "id": 5,
    "seatCode": "B-05",
    "eventId": 2
  }
]
```

---

### 5. Get All Seats for Event (PUBLIC)
**GET** `/api/reservation/event/seats/{eventId}`

Get all seats for an event (available and reserved).

**Path Parameters:**
- `eventId` (required): Event ID (long)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "seatCode": "A-01",
    "status": "R",
    "reservedAt": "2026-06-12T14:30:00"
  },
  {
    "id": 2,
    "seatCode": "A-02",
    "status": "D",
    "reservedAt": null
  },
  {
    "id": 3,
    "seatCode": "A-03",
    "status": "R",
    "reservedAt": "2026-06-12T15:00:00"
  }
]
```

**Seat Status:**
- `D` = Available (Disponível)
- `R` = Reserved (Reservado)

---

## Error Handling

All errors follow a standard format:

```json
{
  "timestamp": "2026-06-12T15:09:09.849Z",
  "status": 400,
  "error": "Error Type",
  "message": "Detailed error message",
  "path": "/api/resource",
  "errors": ["Field error 1", "Field error 2"]
}
```

### HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | OK | Successful GET/POST/PUT |
| 201 | Created | Resource successfully created |
| 400 | Bad Request | Invalid input data |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | User lacks required permissions |
| 404 | Not Found | Resource does not exist |
| 422 | Unprocessable Entity | Validation errors on input fields |
| 500 | Internal Server Error | Server error |

---

## Request/Response Headers

### Required Request Headers
```
Authorization: Bearer <JWT_TOKEN>  (except public endpoints)
Content-Type: application/json
```

### Response Headers
```
Content-Type: application/json
```

---

## Data Types

### Enums

**Role:**
- `ADMIN` - Administrator user
- `CUSTOMER` - Regular customer user

**EventStatus:**
- `ACTIVE` - Event is active and accepting reservations
- `CANCELLED` - Event has been cancelled
- `SOLD_OUT` - Event is fully booked

**SeatStatus:**
- `D` - Available (Disponível)
- `R` - Reserved (Reservado)

**BookingStatus:**
- `CONFIRMED` - Reservation confirmed

---

## Example Usage Flows

### 1. User Registration and Login Flow

```bash
# 1. Register as customer
POST /api/auth/register/customer
{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "MyPassword123",
  "role": "CUSTOMER"
}

# 2. Login to get JWT token
POST /api/auth/login
{
  "email": "joao@example.com",
  "password": "MyPassword123"
}

# Response includes token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 2. Event Browsing and Reservation Flow

```bash
# 1. List all theaters
GET /api/theater/listAll

# 2. List all events
GET /api/event/listAll

# 3. Get specific event details
GET /api/event/id/1

# 4. Get available seats for event
GET /api/reservation/event/seats/1

# 5. Reserve seats (requires authentication)
POST /api/reservation/reserve
Authorization: Bearer <JWT_TOKEN>
{
  "eventId": 1,
  "userId": 2,
  "seatIds": [1, 2, 3]
}

# 6. Check user's reservations
GET /api/reservation/user/2
Authorization: Bearer <JWT_TOKEN>
```

### 3. Admin Theater/Event Management Flow

```bash
# 1. Create theater (requires ADMIN token)
POST /api/theater/create
Authorization: Bearer <ADMIN_JWT_TOKEN>
{
  "name": "Cinema Downtown",
  "address": "Rua das Flores, 123",
  "city": "São Paulo",
  "capacity": 500
}

# 2. Create event (requires ADMIN token)
POST /api/event/create
Authorization: Bearer <ADMIN_JWT_TOKEN>
{
  "theaterId": 1,
  "name": "Homem-Aranha",
  "eventDate": "2026-07-15T19:30:00",
  "totalSeats": 200
}

# 3. List users
GET /api/user/
Authorization: Bearer <ADMIN_JWT_TOKEN>
```

---

## Rate Limiting & Throttling

Currently, no rate limiting is implemented. All endpoints accept unlimited requests.

---

## Versioning

API is currently at version 1.0 (implicit). No version prefix in URLs.

---

## Contact & Support

For issues or questions about the API, please contact the development team.

**Last Updated:** 2026-06-12
**API Version:** 1.0
