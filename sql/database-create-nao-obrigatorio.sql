USE theater_db;
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

CREATE TABLE theater_capacity (
	theater_id BIGINT NOT NULL,
    capacity INT NOT NULL,
    PRIMARY KEY (theater_id),
    CONSTRAINT fk_bs_theater FOREIGN KEY (theater_id) REFERENCES theater(id)
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

