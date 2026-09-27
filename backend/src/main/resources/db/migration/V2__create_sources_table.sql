-- ============================================================
-- V2: Create sources table
-- ============================================================

CREATE TABLE IF NOT EXISTS sources (
                                       id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    host        VARCHAR(255) NOT NULL,
    port        INTEGER      NOT NULL,
    database_name VARCHAR(100) NOT NULL,
    username    VARCHAR(100) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_sources         PRIMARY KEY (id),
    CONSTRAINT fk_sources_user    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_sources_type   CHECK (type IN ('POSTGRESQL', 'MYSQL'))
    );

CREATE INDEX IF NOT EXISTS idx_sources_user_id ON sources(user_id);