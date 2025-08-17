CREATE TABLE verification_codes
(
    id         SERIAL PRIMARY KEY,
    code       VARCHAR   NOT NULL,
    type       VARCHAR   NOT NULL,
    verified   BOOLEAN   NOT NULL default false,
    user_id    BIGINT,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version    INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(id)
    CONSTRAINT uq_verification_code UNIQUE (code, type),
    INDEX      idx_code_type_verified (code, type, verified)
);