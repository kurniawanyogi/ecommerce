CREATE TABLE role_permissions
(
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permission_role
        FOREIGN KEY (role_id)
            REFERENCES roles (id)
            ON DELETE RESTRICT,
    CONSTRAINT fk_role_permission_permission
        FOREIGN KEY (permission_id)
            REFERENCES permissions (id)
            ON DELETE RESTRICT
);
