CREATE TABLE sellers
(
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT    NOT NULL UNIQUE,
    store_name        VARCHAR(255),
    store_description TEXT,
    address           TEXT,
    profile_picture   VARCHAR(255),
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    updated_by        BIGINT,
    version           BIGINT             DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
