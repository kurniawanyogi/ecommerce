CREATE TABLE roles
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100),
    description VARCHAR(255),
    status      VARCHAR(50),
    version     BIGINT
);
