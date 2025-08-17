CREATE TABLE verification_codes (
                                    id SERIAL PRIMARY KEY,
                                    code VARCHAR NOT NULL,
                                    type VARCHAR NOT NULL,
                                    verified BOOLEAN NOT NULL DEFAULT FALSE,
                                    user_id BIGINT,
                                    expires_at TIMESTAMP NOT NULL,
                                    created_at TIMESTAMP,
                                    updated_at TIMESTAMP,
                                    version INTEGER,

                                    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
                                    CONSTRAINT uq_verification_code UNIQUE (code, type)
);

CREATE INDEX idx_code_type_verified ON verification_codes (code, type, verified);