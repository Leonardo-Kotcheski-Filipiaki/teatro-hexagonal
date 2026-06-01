# Sistema de Reservas de Teatro — Documentação de Arquitetura

> Stack: Java 26 · Spring Boot · MySQL · Redis · Docker  
> Padrões: Arquitetura Hexagonal · Microsserviços · CQRS

---

## 1. Visão Geral

O sistema é composto por **três microsserviços independentes**, cada um com seu próprio domínio bem delimitado, comunicando-se via API Gateway. Toda a lógica de negócio fica isolada no **Domain Core** (hexágono), protegida por Ports & Adapters. O CQRS separa operações de escrita (MySQL) de leitura (Redis), garantindo performance e escalabilidade.

---

## 2. Microsserviços

### 2.1 Theater Service
Responsável pelo cadastro e gerenciamento de teatros.

| Operação | Endpoint | Tipo |
|---|---|---|
| Criar teatro | `POST /api/theaters` | Command |
| Listar teatros | `GET /api/theaters` | Query |
| Buscar por ID | `GET /api/theaters/{id}` | Query |
| Atualizar | `PUT /api/theaters/{id}` | Command |
| Remover | `DELETE /api/theaters/{id}` | Command |

### 2.2 Event Service
Gerencia eventos vinculados a teatros e controla o mapa de assentos.

| Operação | Endpoint | Tipo |
|---|---|---|
| Criar evento | `POST /api/events` | Command |
| Listar por teatro | `GET /api/events?theaterId={id}` | Query |
| Buscar por ID | `GET /api/events/{id}` | Query |
| Mapa de assentos | `GET /api/events/{id}/seats` | Query |
| Atualizar | `PUT /api/events/{id}` | Command |
| Remover | `DELETE /api/events/{id}` | Command |

### 2.3 Reservation Service
Controla inscrições, reservas momentâneas (hold) e confirmações.

| Operação | Endpoint | Tipo |
|---|---|---|
| Criar inscrição + reservar assentos | `POST /api/bookings` | Command |
| Confirmar reserva | `PATCH /api/bookings/{id}/confirm` | Command |
| Cancelar reserva | `PATCH /api/bookings/{id}/cancel` | Command |
| Listar por evento | `GET /api/bookings?eventId={id}` | Query |
| Buscar por ID | `GET /api/bookings/{id}` | Query |

---

## 3. Arquitetura Hexagonal — Estrutura de Pastas

```
reservation-service/
├── src/main/java/com/theater/reservation/
│   ├── domain/                          # Hexágono — NÚCLEO
│   │   ├── model/
│   │   │   ├── Booking.java             # Entidade de domínio
│   │   │   ├── Seat.java
│   │   │   └── SeatStatus.java          # Enum: AVAILABLE, MOMENTARILY_RESERVED, RESERVED
│   │   ├── port/
│   │   │   ├── in/                      # Portas de entrada (Use Cases)
│   │   │   │   ├── CreateBookingUseCase.java
│   │   │   │   ├── ConfirmBookingUseCase.java
│   │   │   │   └── GetBookingQuery.java
│   │   │   └── out/                     # Portas de saída (SPI)
│   │   │       ├── BookingRepository.java
│   │   │       ├── SeatRepository.java
│   │   │       └── BookingReadRepository.java
│   │   └── service/                     # Casos de uso implementados
│   │       ├── BookingCommandService.java
│   │       └── BookingQueryService.java
│   │
│   ├── adapter/                         # Adaptadores — FORA DO HEXÁGONO
│   │   ├── in/
│   │   │   └── web/
│   │   │       ├── ReservationController.java
│   │   │       ├── request/
│   │   │       │   └── CreateBookingRequest.java
│   │   │       └── response/
│   │   │           └── BookingResponse.java
│   │   └── out/
│   │       ├── persistence/
│   │       │   ├── BookingJpaRepository.java
│   │       │   ├── SeatJpaRepository.java
│   │       │   └── BookingPersistenceAdapter.java
│   │       └── cache/
│   │           ├── BookingRedisRepository.java
│   │           └── BookingCacheAdapter.java
│   │
│   └── infrastructure/
│       ├── config/
│       │   ├── RedisConfig.java
│       │   └── SwaggerConfig.java
│       └── event/
│           └── SeatStatusEventListener.java
```

> A mesma estrutura se aplica aos demais microsserviços (`theater-service`, `event-service`).

---

## 4. CQRS — Separação de Comandos e Consultas

### Command Side (Escrita → MySQL)

```java
// Porta de entrada para comandos
public interface CreateBookingUseCase {
    BookingId createBooking(CreateBookingCommand command);
}

// Comando imutável
public record CreateBookingCommand(
    Long eventId,
    String subscriberName,
    String subscriberEmail,
    String subscriberPhone,
    List<Long> seatIds
) {}

// Handler (service de domínio)
@Service
@Transactional
public class BookingCommandService implements CreateBookingUseCase {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public BookingId createBooking(CreateBookingCommand cmd) {
        // 1. Validar assentos (apenas AVAILABLE)
        List<Seat> seats = seatRepository.findAllById(cmd.seatIds());
        seats.forEach(seat -> {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatNotAvailableException(seat.getId());
            }
        });

        // 2. Marcar como MOMENTARILY_RESERVED (M)
        seats.forEach(seat -> seat.holdFor(Duration.ofMinutes(10)));
        seatRepository.saveAll(seats);

        // 3. Criar booking
        Booking booking = Booking.create(cmd.eventId(), cmd.subscriberName(),
                                         cmd.subscriberEmail(), cmd.subscriberPhone(), seats);
        Booking saved = bookingRepository.save(booking);

        // 4. Publicar evento de domínio → sincronizar Redis
        eventPublisher.publishEvent(new BookingCreatedEvent(saved));

        return saved.getId();
    }
}
```

### Query Side (Leitura → Redis)

```java
// Projeção otimizada para leitura
public record SeatMapProjection(
    Long eventId,
    String eventName,
    List<SeatView> seats
) {}

public record SeatView(Long id, String code, SeatStatus status) {}

// Query handler
@Service
public class EventQueryService implements GetSeatMapQuery {

    private final BookingReadRepository redisReadRepo;
    private final EventJpaRepository fallbackRepo; // fallback se cache miss

    @Override
    public SeatMapProjection getSeatMap(Long eventId) {
        return redisReadRepo.findSeatMap(eventId)
            .orElseGet(() -> {
                // Cache miss: vai ao MySQL e repopula Redis
                SeatMapProjection projection = fallbackRepo.buildSeatMap(eventId);
                redisReadRepo.cacheSeatMap(eventId, projection);
                return projection;
            });
    }
}
```

---

## 5. Banco de Dados

### 5.1 MySQL — Schema (Write Model)

```sql
CREATE TABLE theater (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    address      VARCHAR(500),
    city         VARCHAR(100),
    capacity     INT NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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

CREATE TABLE seat (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id     BIGINT NOT NULL,
    seat_code    VARCHAR(10) NOT NULL,   -- ex: "A1", "B12"
    status       ENUM('D','M','R') NOT NULL DEFAULT 'D',
    reserved_at  TIMESTAMP NULL,
    expires_at   TIMESTAMP NULL,         -- para reservas momentâneas (M)
    CONSTRAINT fk_seat_event FOREIGN KEY (event_id) REFERENCES event(id),
    UNIQUE KEY uq_seat_per_event (event_id, seat_code)
);

CREATE TABLE booking (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id         BIGINT NOT NULL,
    subscriber_name  VARCHAR(255) NOT NULL,
    subscriber_email VARCHAR(255) NOT NULL,
    subscriber_phone VARCHAR(20),
    status           ENUM('PENDING','CONFIRMED','CANCELLED') DEFAULT 'PENDING',
    booked_at        TIMESTAMP NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_event FOREIGN KEY (event_id) REFERENCES event(id)
);

CREATE TABLE booking_seat (
    booking_id  BIGINT NOT NULL,
    seat_id     BIGINT NOT NULL,
    PRIMARY KEY (booking_id, seat_id),
    CONSTRAINT fk_bs_booking FOREIGN KEY (booking_id) REFERENCES booking(id),
    CONSTRAINT fk_bs_seat    FOREIGN KEY (seat_id)    REFERENCES seat(id)
);

-- Índices de performance
CREATE INDEX idx_event_theater  ON event(theater_id);
CREATE INDEX idx_seat_event     ON seat(event_id);
CREATE INDEX idx_seat_status    ON seat(event_id, status);
CREATE INDEX idx_booking_event  ON booking(event_id);
```

### 5.2 Por que Redis como banco de leitura?

Redis foi escolhido por:

- **Latência sub-milissegundo** — leituras do mapa de assentos são frequentíssimas; Redis entrega em ~0.1ms vs ~5–20ms do MySQL
- **Estruturas nativas** — `HASH` para projeções de eventos, `SET` para IDs de assentos disponíveis, `SORTED SET` para filas
- **TTL nativo** — expiração automática das reservas momentâneas (M) sem cron job
- **Pub/Sub** — notificações em tempo real para o front-end React via WebSocket/SSE
- **Eventual consistency aceitável** — o delta de inconsistência é mínimo: o listener de eventos do Spring sincroniza Redis na mesma transação de negócio

#### Estruturas Redis utilizadas

```
# Mapa completo de assentos de um evento (HASH)
seat_map:{eventId}  →  { "A1": "D", "A2": "R", "B5": "M", ... }

# Assentos disponíveis (SET — O(1) para verificação)
available_seats:{eventId}  →  { "A1", "A3", "B2", ... }

# Expiração de reservas momentâneas (KEY com TTL)
hold_seat:{seatId}  →  "bookingId"   TTL: 600s (10 min)

# Projeção do evento (STRING com JSON serializado)
event_projection:{eventId}  →  { id, name, date, theaterName, availableSeats }
```

---

## 6. Regras de Negócio dos Assentos

| Status | Código | Transições permitidas |
|---|---|---|
| Disponível | `D` | → `M` (hold), → `R` (direto, fluxo admin) |
| Reservado Momentaneamente | `M` | → `R` (confirmação), → `D` (expiração TTL) |
| Reservado | `R` | → `D` (cancelamento) |

**Regras críticas:**
- Máximo de 80 assentos por evento (`CHECK` no banco + validação de domínio)
- Uma pessoa pode reservar múltiplos assentos num único booking, desde que estejam com status `D`
- Reservas `M` expiram automaticamente em 10 minutos via TTL do Redis + job de reconciliação no MySQL
- Dois usuários não podem reservar o mesmo assento: controle via `SELECT FOR UPDATE` no MySQL (pessimistic lock) durante o command

```java
// Exemplo de pessimistic lock no repository
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM Seat s WHERE s.id IN :ids")
List<Seat> findAllByIdWithLock(@Param("ids") List<Long> ids);
```

---

## 7. Expiração de Reservas Momentâneas

A expiração usa dois mecanismos complementares:

1. **Redis TTL** — `hold_seat:{seatId}` expira em 600s; um `KeyExpirationEventMessageListener` do Spring captura o evento e publica uma mensagem interna para reverter o assento para `D` no MySQL.

2. **Job de reconciliação** (fallback) — `@Scheduled` a cada 5 minutos verifica assentos com `status = 'M'` e `expires_at < NOW()` e os reverte para `D`. Garante consistência mesmo em caso de falha do listener Redis.

```java
@Component
public class SeatExpirationJob {

    private final SeatRepository seatRepository;

    @Scheduled(fixedDelay = 300_000) // 5 minutos
    @Transactional
    public void expireHeldSeats() {
        List<Seat> expired = seatRepository
            .findByStatusAndExpiresAtBefore(SeatStatus.MOMENTARILY_RESERVED, Instant.now());
        expired.forEach(Seat::release);
        seatRepository.saveAll(expired);
    }
}
```

---

## 8. Docker Compose

```yaml
version: '3.9'

services:

  api-gateway:
    build: ./api-gateway
    ports: ["8080:8080"]
    environment:
      THEATER_SERVICE_URL: http://theater-service:8081
      EVENT_SERVICE_URL:   http://event-service:8082
      RESERVATION_SERVICE_URL: http://reservation-service:8083
    depends_on: [theater-service, event-service, reservation-service]

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
    command: redis-server --maxmemory 256mb --maxmemory-policy allkeys-lru
    ports: ["6379:6379"]
    volumes:
      - redis_data:/data

volumes:
  mysql_data:
  redis_data:
```

---

## 9. Escalabilidade

Para suportar grandes volumes de acesso:

### Horizontal Scaling
- Todos os microsserviços são **stateless** → múltiplas réplicas via Docker Swarm/Kubernetes
- O API Gateway faz load balancing entre réplicas

### Redis como escudo do MySQL
- **>95% das leituras** são servidas pelo Redis — o MySQL fica protegido de leituras pesadas
- Assentos disponíveis são lidos do Redis em O(1)

### Controle de concorrência
- `SELECT FOR UPDATE` no MySQL garante que dois usuários não reservem o mesmo assento simultaneamente
- Para escala extrema, substituir por **Redis SETNX** (optimistic lock distribuído) ou **Kafka** para processar reservas em fila

### Kafka (evolução futura)
Quando a carga justificar, o `ApplicationEventPublisher` do Spring pode ser substituído por Kafka:
- Produtor: `ReservationService` publica `BookingCreatedEvent`
- Consumidor: `SeatProjectionConsumer` atualiza Redis assincronamente
- Garante ordering por `eventId` como partition key

---

## 10. Estrutura Multi-módulo Maven

```
theater-reservation/
├── pom.xml                    (parent BOM)
├── api-gateway/
│   └── pom.xml
├── theater-service/
│   └── pom.xml
├── event-service/
│   └── pom.xml
├── reservation-service/
│   └── pom.xml
├── shared-domain/             (entidades e eventos compartilhados)
│   └── pom.xml
└── docker-compose.yml
```

### Dependências principais (pom.xml pai)

```xml
<properties>
    <java.version>26</java.version>
    <spring-boot.version>3.5.0</spring-boot.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.5.0</version>
    </dependency>
</dependencies>
```

---

## 11. Considerações para o Front-End React

A API foi projetada para suportar o front-end com:

- **CORS** configurado no API Gateway para `http://localhost:3000`
- **Endpoint de seat map** retorna grid pronto para renderização com status por assento
- **Polling ou SSE** no endpoint `GET /api/events/{id}/seats/stream` para atualização em tempo real do mapa (Redis Pub/Sub → SSE)
- **Payload compacto** — o mapa de assentos retorna array simples `[{ code, status }]` para render eficiente do grid

```json
// GET /api/events/42/seats
{
  "eventId": 42,
  "eventName": "Hamlet",
  "totalSeats": 80,
  "availableSeats": 34,
  "seats": [
    { "id": 1, "code": "A1", "status": "R" },
    { "id": 2, "code": "A2", "status": "D" },
    { "id": 3, "code": "A3", "status": "M" }
  ]
}
```
