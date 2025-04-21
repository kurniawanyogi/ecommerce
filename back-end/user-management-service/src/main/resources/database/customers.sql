CREATE TABLE customers
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT    NOT NULL UNIQUE,
    full_name       VARCHAR(255),
    birth_date      DATE,
    address         TEXT,
    profile_picture VARCHAR(255),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    updated_by      BIGINT,
    version         BIGINT             DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
