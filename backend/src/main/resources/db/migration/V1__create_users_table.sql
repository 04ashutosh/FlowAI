-- ============================================================
-- V1: Create users table
-- Phase 2 will implement the full auth logic.
-- We define the schema now to establish Flyway as the owner.
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
                                     id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    email       VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name   VARCHAR(255),
    role        VARCHAR(50)  NOT NULL DEFAULT 'USER',
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_users         PRIMARY KEY (id),
    CONSTRAINT uq_users_email   UNIQUE (email),
    CONSTRAINT chk_users_role   CHECK (role IN ('USER', 'ADMIN'))
    );

-- Index for login lookups (we always query by email)
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

COMMENT ON TABLE users IS 'FlowAI registered users';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password. NEVER store plaintext.';