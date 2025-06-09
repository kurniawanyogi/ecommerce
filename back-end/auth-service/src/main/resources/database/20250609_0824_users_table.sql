CREATE TABLE users
(
    id           SERIAL PRIMARY KEY,
    email        VARCHAR(255) UNIQUE,
    password     VARCHAR(255),
    phone_number VARCHAR(50),
    first_name   VARCHAR(100),
    last_name    VARCHAR(100),
    birth_date   DATE,
    gender       VARCHAR(20),
    id_number    VARCHAR(100),
    id_type      VARCHAR(50),
    status       VARCHAR(50),
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,
    updated_by   BIGINT,
    version      BIGINT
);
