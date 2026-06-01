# Sistema de Reservas de Teatro — Documentação de Arquitetura

> Stack: Java 26 · Spring Boot · Spring Security · JWT · MySQL · Redis · Docker  
> Padrões: Arquitetura Hexagonal · Microsserviços · CQRS

---

## 1. Visão Geral

O sistema é composto por **quatro microsserviços independentes**, cada um com domínio bem delimitado, comunicando-se via API Gateway. A autenticação usa **JWT stateless**: o Gateway valida o token localmente em cada requisição e injeta `X-User-Id` e `X-User-Role` como headers internos, eliminando roundtrips ao Auth Service. O `userId` chega ao `Reservation Service` via header e é persistido automaticamente na tabela `booking`.

```
Client → [Bearer JWT] → API Gateway → [valida JWT + injeta X-User-Id] → Microsserviços
```

---

## 2. Microsserviços

### 2.1 Auth Service (porta 8084) — NOVO
Responsável pelo ciclo de vida de usuários e tokens.

| Operação              | Endpoint                     | Auth? | Tipo    |
|-----------------------|------------------------------|-------|---------|
| Registrar usuário     | `POST /api/auth/register`    | Não   | Command |
| Login                 | `POST /api/auth/login`       | Não   | Command |
| Renovar token         | `POST /api/auth/refresh`     | Não   | Command |
| Logout (revoga token) | `POST /api/auth/logout`      | Sim   | Command |
| Dados do usuário      | `GET  /api/auth/me`          | Sim   | Query   |

### 2.2 Theater Service (porta 8081)
Responsável pelo cadastro e gerenciamento de teatros.

| Operação    | Endpoint                  | Role exigida | Tipo    |
|-------------|---------------------------|--------------|---------|
| Criar        | `POST /api/theaters`      | `ADMIN`      | Command |
| Listar       | `GET  /api/theaters`      | Qualquer     | Query   |
| Buscar por ID| `GET  /api/theaters/{id}` | Qualquer     | Query   |
| Atualizar    | `PUT  /api/theaters/{id}` | `ADMIN`      | Command |
| Remover      | `DELETE /api/theaters/{id}`| `ADMIN`     | Command |

### 2.3 Event Service (porta 8082)
Gerencia eventos e o mapa de assentos.

| Operação        | Endpoint                      | Role exigida | Tipo    |
|-----------------|-------------------------------|--------------|---------|
| Criar evento    | `POST /api/events`            | `ADMIN`      | Command |
| Listar          | `GET  /api/events`            | Qualquer     | Query   |
| Buscar por ID   | `GET  /api/events/{id}`       | Qualquer     | Query   |
| Mapa de assentos| `GET  /api/events/{id}/seats` | Qualquer     | Query   |
| Atualizar       | `PUT  /api/events/{id}`       | `ADMIN`      | Command |
| Remover         | `DELETE /api/events/{id}`     | `ADMIN`      | Command |

### 2.4 Reservation Service (porta 8083)
Controla reservas. O `userId` é extraído do header `X-User-Id` — nunca do body.

| Operação                      | Endpoint                        | Role exigida | Tipo    |
|-------------------------------|---------------------------------|--------------|---------|
| Criar reserva (hold + booking)| `POST /api/bookings`            | `CUSTOMER`   | Command |
| Confirmar reserva             | `PATCH /api/bookings/{id}/confirm`| `CUSTOMER` | Command |
| Cancelar reserva              | `PATCH /api/bookings/{id}/cancel` | `CUSTOMER` | Command |
| Listar minhas reservas        | `GET  /api/bookings/me`         | `CUSTOMER`   | Query   |
| Listar por evento (admin)     | `GET  /api/bookings?eventId=`   | `ADMIN`      | Query   |

---

## 3. Auth Service — Implementação

### 3.1 Estrutura hexagonal do Auth Service

```
auth-service/
└── src/main/java/com/theater/auth/
    ├── domain/
    │   ├── model/
    │   │   ├── User.java                 # Entidade de domínio
    │   │   └── UserRole.java             # Enum: ADMIN, CUSTOMER
    │   ├── port/
    │   │   ├── in/
    │   │   │   ├── RegisterUserUseCase.java
    │   │   │   ├── LoginUseCase.java
    │   │   │   └── RefreshTokenUseCase.java
    │   │   └── out/
    │   │       ├── UserRepository.java
    │   │       └── TokenRepository.java   # Refresh tokens no Redis
    │   └── service/
    │       ├── AuthCommandService.java
    │       └── TokenService.java
    ├── adapter/
    │   ├── in/web/
    │   │   ├── AuthController.java
    │   │   ├── request/
    │   │   │   ├── RegisterRequest.java
    │   │   │   └── LoginRequest.java
    │   │   └── response/
    │   │       └── AuthResponse.java
    │   └── out/
    │       ├── persistence/
    │       │   └── UserJpaRepository.java
    │       └── cache/
    │           └── RefreshTokenRedisAdapter.java
    └── infrastructure/
        ├── security/
        │   ├── JwtUtil.java              # Emissão e validação do JWT
        │   └── SecurityConfig.java
        └── config/
            └── PasswordConfig.java       # Bean BCryptPasswordEncoder
```

### 3.2 Dependências (pom.xml do auth-service)

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT (JJWT) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>

<!-- Redis para refresh tokens -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 3.3 Domínio — Entidade User

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.CUSTOMER;

    @Column(nullable = false)
    private boolean active = true;

    // factory method — senha já deve chegar hasheada
    public static User create(String name, String email,
                              String passwordHash, UserRole role) {
        User u = new User();
        u.name         = name;
        u.email        = email;
        u.passwordHash = passwordHash;
        u.role         = role;
        return u;
    }
}

public enum UserRole { ADMIN, CUSTOMER }
```

### 3.4 JwtUtil — Emissão e validação

```java
@Component
public class JwtUtil {

    // Chave lida de application.properties: jwt.secret (mínimo 256 bits)
    @Value("${jwt.secret}")
    private String secret;

    private static final Duration ACCESS_TTL  = Duration.ofMinutes(15);
    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    public String generateAccessToken(User user) {
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("role", user.getRole().name())
            .claim("email", user.getEmail())
            .issuedAt(new Date())
            .expiration(Date.from(Instant.now().plus(ACCESS_TTL)))
            .signWith(getKey())
            .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("type", "refresh")
            .issuedAt(new Date())
            .expiration(Date.from(Instant.now().plus(REFRESH_TTL)))
            .signWith(getKey())
            .compact();
    }

    // Retorna o userId extraído do token; lança JwtException se inválido
    public Long extractUserId(String token) {
        return Long.parseLong(
            Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(token).getPayload().getSubject()
        );
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build()
            .parseSignedClaims(token).getPayload();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
```

### 3.5 AuthCommandService — Login

```java
@Service
@Transactional
public class AuthCommandService implements LoginUseCase, RegisterUserUseCase {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;   // Redis
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse login(LoginCommand cmd) {
        User user = userRepository.findByEmail(cmd.email())
            .orElseThrow(() -> new InvalidCredentialsException());

        if (!user.isActive())
            throw new UserInactiveException();

        if (!passwordEncoder.matches(cmd.password(), user.getPasswordHash()))
            throw new InvalidCredentialsException();

        String accessToken  = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // Persiste refresh token no Redis com TTL de 7 dias
        tokenRepository.save(
            RefreshToken.of(user.getId(), refreshToken, Duration.ofDays(7))
        );

        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getRole());
    }

    @Override
    public void register(RegisterCommand cmd) {
        if (userRepository.existsByEmail(cmd.email()))
            throw new EmailAlreadyUsedException(cmd.email());

        String hash = passwordEncoder.encode(cmd.password());
        User user = User.create(cmd.name(), cmd.email(), hash, UserRole.CUSTOMER);
        userRepository.save(user);
    }
}
```

---

## 4. API Gateway — Filtro JWT

O Gateway **não chama o Auth Service** para validar tokens. Ele usa a mesma chave secreta (`jwt.secret`) para validar a assinatura localmente — zero latência extra por requisição.

```java
@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    // Rotas públicas — não passam pelo filtro
    private static final Set<String> PUBLIC_PATHS = Set.of(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/refresh"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
            .getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        try {
            String token  = authHeader.substring(7);
            Claims claims = jwtUtil.extractAllClaims(token);
            String userId = claims.getSubject();
            String role   = claims.get("role", String.class);

            // Injeta headers internos para os microsserviços downstream
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-User-Id",   userId)
                .header("X-User-Role", role)
                .build();

            return chain.filter(exchange.mutate().request(mutated).build());

        } catch (JwtException e) {
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override public int getOrder() { return -100; }
}
```

### Roteamento no application.yml do Gateway

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: http://auth-service:8084
          predicates: [Path=/api/auth/**]

        - id: theater-service
          uri: http://theater-service:8081
          predicates: [Path=/api/theaters/**]
          filters:
            - name: RequestRateLimiter
              args: { redis-rate-limiter.replenishRate: 50, redis-rate-limiter.burstCapacity: 100 }

        - id: event-service
          uri: http://event-service:8082
          predicates: [Path=/api/events/**]

        - id: reservation-service
          uri: http://reservation-service:8083
          predicates: [Path=/api/bookings/**]

  security:
    jwt:
      secret: ${JWT_SECRET}   # variável de ambiente obrigatória
```

---

## 5. Reservation Service — userId via Header

O `userId` **nunca** é enviado pelo cliente no body da requisição. Ele é extraído do header `X-User-Id` que o Gateway injetou após validar o JWT.

```java
// Adaptador de entrada — extrai userId do header
@RestController
@RequestMapping("/api/bookings")
public class ReservationController {

    private final CreateBookingUseCase createBookingUseCase;

    @PostMapping
    public ResponseEntity<BookingResponse> create(
        @RequestBody CreateBookingRequest req,
        @RequestHeader("X-User-Id") Long userId,          // injetado pelo Gateway
        @RequestHeader("X-User-Role") String role
    ) {
        CreateBookingCommand cmd = new CreateBookingCommand(
            req.eventId(),
            req.seatIds(),
            userId                                         // vem do JWT, não do body
        );
        BookingId id = createBookingUseCase.createBooking(cmd);
        return ResponseEntity.created(URI.create("/api/bookings/" + id.value())).build();
    }
}

// Comando atualizado com userId
public record CreateBookingCommand(
    Long eventId,
    List<Long> seatIds,
    Long userId           // antes: subscriberName/email/phone — agora vêm do User
) {}
```

> **Nota:** Com o sistema de login, `subscriber_name`, `subscriber_email` e `subscriber_phone` são removidos do body do booking — esses dados já existem no `user`. O `booking` armazena apenas `user_id`, `event_id`, `status` e as datas.

---

## 6. Banco de Dados — Schema Completo Atualizado

```sql
-- ─────────────────────────────────────────────────────────
-- TABELA: user (gerenciada pelo Auth Service)
-- ─────────────────────────────────────────────────────────
CREATE TABLE user (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('ADMIN', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────
-- TABELA: theater
-- ─────────────────────────────────────────────────────────
CREATE TABLE theater (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    address     VARCHAR(500),
    city        VARCHAR(100),
    capacity    INT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────
-- TABELA: event (FK → theater)
-- ─────────────────────────────────────────────────────────
CREATE TABLE event (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    theater_id       BIGINT NOT NULL,
    name             VARCHAR(255) NOT NULL,
    event_date       TIMESTAMP NOT NULL,
    total_seats      INT NOT NULL CHECK (total_seats <= 80),
    available_seats  INT NOT NULL,
    status           ENUM('ACTIVE', 'CANCELLED', 'SOLD_OUT') DEFAULT 'ACTIVE',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_theater FOREIGN KEY (theater_id) REFERENCES theater(id)
);

-- ─────────────────────────────────────────────────────────
-- TABELA: seat (FK → event)
-- ─────────────────────────────────────────────────────────
CREATE TABLE seat (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id     BIGINT NOT NULL,
    seat_code    VARCHAR(10) NOT NULL,
    status       ENUM('D','M','R') NOT NULL DEFAULT 'D',
    reserved_at  TIMESTAMP NULL,
    expires_at   TIMESTAMP NULL,
    CONSTRAINT fk_seat_event FOREIGN KEY (event_id) REFERENCES event(id),
    UNIQUE KEY uq_seat_per_event (event_id, seat_code)
);

-- ─────────────────────────────────────────────────────────
-- TABELA: booking (FK → event + FK → user)
-- ─────────────────────────────────────────────────────────
CREATE TABLE booking (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id    BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,                      -- FK para user (quem reservou)
    status      ENUM('PENDING','CONFIRMED','CANCELLED') DEFAULT 'PENDING',
    booked_at   TIMESTAMP NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_event FOREIGN KEY (event_id) REFERENCES event(id),
    CONSTRAINT fk_booking_user  FOREIGN KEY (user_id)  REFERENCES user(id)
);

-- ─────────────────────────────────────────────────────────
-- TABELA: booking_seat (associativa)
-- ─────────────────────────────────────────────────────────
CREATE TABLE booking_seat (
    booking_id  BIGINT NOT NULL,
    seat_id     BIGINT NOT NULL,
    PRIMARY KEY (booking_id, seat_id),
    CONSTRAINT fk_bs_booking FOREIGN KEY (booking_id) REFERENCES booking(id),
    CONSTRAINT fk_bs_seat    FOREIGN KEY (seat_id)    REFERENCES seat(id)
);

-- ─────────────────────────────────────────────────────────
-- ÍNDICES
-- ─────────────────────────────────────────────────────────
CREATE INDEX idx_event_theater ON event(theater_id);
CREATE INDEX idx_seat_event    ON seat(event_id);
CREATE INDEX idx_seat_status   ON seat(event_id, status);
CREATE INDEX idx_booking_event ON booking(event_id);
CREATE INDEX idx_booking_user  ON booking(user_id);     -- listagem "minhas reservas"
CREATE INDEX idx_user_email    ON user(email);           -- login rápido
```

---

## 7. Redis — Estruturas atualizadas com Auth

```
# Refresh tokens (TTL 7 dias — invalidados no logout)
refresh_token:{userId}  →  "<token_string>"   TTL: 604800s

# Mapa de assentos por evento
seat_map:{eventId}  →  HASH { "A1": "D", "A2": "R", ... }

# Assentos disponíveis (lookup O(1))
available_seats:{eventId}  →  SET { "A1", "A3", ... }

# Hold temporário de assento (TTL 10 min)
hold_seat:{seatId}  →  "<bookingId>"   TTL: 600s

# Projeção de evento para leitura
event_projection:{eventId}  →  JSON serializado
```

**Logout** revoga o refresh token simplesmente deletando a chave no Redis:

```java
public void logout(Long userId) {
    tokenRepository.deleteByUserId(userId); // DEL refresh_token:{userId}
}
```

---

## 8. Controle de Acesso por Role

| Role       | Pode fazer                                          |
|------------|-----------------------------------------------------|
| `ADMIN`    | CRUD de teatros e eventos; listar todos os bookings |
| `CUSTOMER` | Criar, confirmar e cancelar suas próprias reservas  |
| Anônimo    | Listar teatros, eventos e mapa de assentos          |

Verificação de posse da reserva no `BookingCommandService`:

```java
public void cancelBooking(Long bookingId, Long requestingUserId) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(BookingNotFoundException::new);

    if (!booking.getUserId().equals(requestingUserId)) {
        throw new ForbiddenException("Você não pode cancelar a reserva de outro usuário.");
    }

    booking.cancel();
    bookingRepository.save(booking);
}
```

---

## 9. Docker Compose Atualizado

```yaml
version: '3.9'

services:

  api-gateway:
    build: ./api-gateway
    ports: ["8080:8080"]
    environment:
      JWT_SECRET: ${JWT_SECRET}
      AUTH_SERVICE_URL:        http://auth-service:8084
      THEATER_SERVICE_URL:     http://theater-service:8081
      EVENT_SERVICE_URL:       http://event-service:8082
      RESERVATION_SERVICE_URL: http://reservation-service:8083
    depends_on: [auth-service, theater-service, event-service, reservation-service]

  auth-service:
    build: ./auth-service
    expose: ["8084"]
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/theater_db
      SPRING_REDIS_HOST: redis
      JWT_SECRET: ${JWT_SECRET}
    depends_on: [mysql, redis]

  theater-service:
    build: ./theater-service
    expose: ["8081"]
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/theater_db
      SPRING_REDIS_HOST: redis
    depends_on: [mysql, redis]

  event-service:
    build: ./event-service
    expose: ["8082"]
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/theater_db
      SPRING_REDIS_HOST: redis
    depends_on: [mysql, redis]

  reservation-service:
    build: ./reservation-service
    expose: ["8083"]
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/theater_db
      SPRING_REDIS_HOST: redis
    depends_on: [mysql, redis]

  mysql:
    image: mysql:8.4
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: theater_db
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports: ["3306:3306"]

  redis:
    image: redis:7.2-alpine
    command: redis-server --maxmemory 512mb --maxmemory-policy allkeys-lru
    ports: ["6379:6379"]
    volumes:
      - redis_data:/data

volumes:
  mysql_data:
  redis_data:
```

### .env (nunca commitar no git)

```env
JWT_SECRET=gera_aqui_uma_string_base64_de_256bits_minimo
```

---

## 10. Estrutura Multi-módulo Maven Atualizada

```
theater-reservation/
├── pom.xml                    (parent BOM)
├── api-gateway/               (Spring Cloud Gateway + filtro JWT)
├── auth-service/              (Spring Security + JWT + BCrypt)
├── theater-service/
├── event-service/
├── reservation-service/
├── shared-domain/             (records de eventos de domínio compartilhados)
└── docker-compose.yml
```

### Dependências adicionais no parent pom.xml

```xml
<!-- Spring Security (usado apenas no auth-service) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JJWT — compartilhado entre auth-service e api-gateway -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

---

## 11. Considerações para o Front-End React

Com o sistema de login, o React deve:

- Armazenar o `accessToken` em memória (`useState` / Zustand) — nunca em `localStorage` por segurança
- Armazenar o `refreshToken` em cookie `HttpOnly` (configurado no backend) para renovação silenciosa
- Enviar `Authorization: Bearer <accessToken>` em todas as requisições autenticadas
- Interceptar respostas `401` para tentar `POST /api/auth/refresh` antes de redirecionar para login

```json
// POST /api/auth/login — response
{
  "accessToken":  "eyJhbG...",
  "refreshToken": "eyJhbG...",
  "userId": 42,
  "role": "CUSTOMER"
}

// POST /api/bookings — body (sem dados pessoais, pois vêm do JWT)
{
  "eventId": 7,
  "seatIds": [23, 24, 25]
}
```
